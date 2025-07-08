package com.firebyte.elearning.ui.jadwal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.firebyte.elearning.R;
import com.firebyte.elearning.core.UserManager;
import com.firebyte.elearning.data.models.Jadwal;
import com.firebyte.elearning.databinding.FragmentScheduleBinding;
import com.firebyte.elearning.utils.AlarmReceiver;
import com.firebyte.elearning.utils.SafeLinearLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private JadwalViewModel jadwalViewModel;
    private JadwalAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkAndRequestAlarmPermission();
        setupRecyclerView();
        setupAdminFeatures();

        jadwalViewModel = new ViewModelProvider(this).get(JadwalViewModel.class);
        jadwalViewModel.getAllJadwal().observe(getViewLifecycleOwner(), jadwalList -> {
            adapter.submitList(jadwalList);
            binding.tvEmpty.setVisibility(jadwalList.isEmpty() ? View.VISIBLE : View.GONE);
            if (binding.swipeRefreshLayout.isRefreshing()) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
            scheduleRemindersForAll(jadwalList);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> jadwalViewModel.refresh());
        jadwalViewModel.refresh();
    }

    private void setupRecyclerView() {
        binding.recyclerViewJadwal.setLayoutManager(new SafeLinearLayoutManager(getContext()));
        binding.recyclerViewJadwal.setHasFixedSize(true);
        adapter = new JadwalAdapter();
        binding.recyclerViewJadwal.setAdapter(adapter);

        adapter.setOnItemClickListener(new JadwalAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(Jadwal jadwal) {
                if (getContext() == null) return;
                new AlertDialog.Builder(getContext())
                        .setTitle("Hapus Jadwal")
                        .setMessage("Apakah Anda yakin ingin menghapus jadwal ini?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            db.collection("jadwal").document(jadwal.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Jadwal dihapus", Toast.LENGTH_SHORT).show();
                                        jadwalViewModel.refresh();
                                    });
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }

            @Override
            public void onEditClick(Jadwal jadwal) {
                // Panggil dialog dengan data yang ada
                showJadwalDialog(jadwal);
            }
        });
    }

    private void setupAdminFeatures() {
        UserManager.checkAdminStatus(isAdmin -> {
            if (binding != null && isAdded() && isAdmin) {
                binding.fabAddJadwal.setVisibility(View.VISIBLE);
                // Panggil dialog tanpa data (untuk membuat jadwal baru)
                binding.fabAddJadwal.setOnClickListener(v -> showJadwalDialog(null));
            } else if (binding != null) {
                binding.fabAddJadwal.setVisibility(View.GONE);
            }
        });
    }

    // --- FUNGSI DIALOG BARU YANG BISA TAMBAH & EDIT ---
    private void showJadwalDialog(@Nullable Jadwal jadwal) {
        if (getContext() == null || !isAdded()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_jadwal, null);
        builder.setView(dialogView);

        final Spinner spinnerHari = dialogView.findViewById(R.id.spinner_hari);
        final EditText etMataKuliah = dialogView.findViewById(R.id.et_mata_kuliah);
        final EditText etDosen = dialogView.findViewById(R.id.et_dosen);
        final EditText etTanggal = dialogView.findViewById(R.id.et_tanggal);
        final EditText etWaktu = dialogView.findViewById(R.id.et_waktu);
        final EditText etRuang = dialogView.findViewById(R.id.et_ruang);
        final Button btnSimpan = dialogView.findViewById(R.id.btn_simpan);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.hari_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHari.setAdapter(spinnerAdapter);

        // Jika ini adalah mode EDIT, isi field dengan data yang ada
        if (jadwal != null) {
            etMataKuliah.setText(jadwal.getMataKuliah());
            etDosen.setText(jadwal.getDosen());
            etTanggal.setText(jadwal.getTanggal());
            etWaktu.setText(jadwal.getWaktu());
            etRuang.setText(jadwal.getRuang());
            // Set pilihan spinner
            int spinnerPosition = spinnerAdapter.getPosition(jadwal.getHari());
            spinnerHari.setSelection(spinnerPosition);
        }

        final AlertDialog dialog = builder.create();

        btnSimpan.setOnClickListener(v -> {
            String mataKuliah = etMataKuliah.getText().toString().trim();
            String dosen = etDosen.getText().toString().trim();
            String hari = spinnerHari.getSelectedItem().toString();
            String tanggal = etTanggal.getText().toString().trim();
            String waktu = etWaktu.getText().toString().trim();
            String ruang = etRuang.getText().toString().trim();

            if (mataKuliah.isEmpty() || waktu.isEmpty()) {
                Toast.makeText(getContext(), "Mata Kuliah dan Waktu wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            Jadwal jadwalBaru = new Jadwal(mataKuliah, dosen, hari, tanggal, waktu, ruang);

            if (jadwal == null) {
                // Mode TAMBAH: Buat dokumen baru
                db.collection("jadwal").add(jadwalBaru)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Jadwal berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            jadwalViewModel.refresh();
                            dialog.dismiss();
                        });
            } else {
                // Mode EDIT: Update dokumen yang sudah ada
                db.collection("jadwal").document(jadwal.getId()).set(jadwalBaru)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Jadwal berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            jadwalViewModel.refresh();
                            dialog.dismiss();
                        });
            }
        });

        dialog.show();
    }

    // --- SISA KODE (Alarm, dll.) TETAP SAMA ---
    private void scheduleRemindersForAll(List<Jadwal> jadwalList) {
        if (getContext() == null) return;
        for (Jadwal j : jadwalList) {
            scheduleReminder(getContext(), j);
        }
    }

    private void scheduleReminder(Context context, Jadwal jadwal) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (jadwal.getWaktu() == null || !jadwal.getWaktu().contains(":")) return;

        int targetDayOfWeek = getDayOfWeekFromString(jadwal.getHari());
        String waktuMulaiString = jadwal.getWaktu().split("-")[0].trim();
        int targetHour, targetMinute;
        try {
            targetHour = Integer.parseInt(waktuMulaiString.split(":")[0]);
            targetMinute = Integer.parseInt(waktuMulaiString.split(":")[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return;
        }
        if (targetDayOfWeek == -1) return;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, targetHour);
        calendar.set(Calendar.MINUTE, targetMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (targetDayOfWeek < currentDayOfWeek || (targetDayOfWeek == currentDayOfWeek && System.currentTimeMillis() >= calendar.getTimeInMillis())) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }
        calendar.set(Calendar.DAY_OF_WEEK, targetDayOfWeek);
        calendar.add(Calendar.HOUR_OF_DAY, -1);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_TITLE, "Pengingat Kuliah: " + jadwal.getMataKuliah());
        intent.putExtra(AlarmReceiver.EXTRA_MESSAGE, "Kelas akan dimulai 1 jam lagi di " + jadwal.getRuang());

        int pendingIntentId = jadwal.getId().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) return;
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private int getDayOfWeekFromString(String day) {
        if (day == null) return -1;
        switch (day.toLowerCase(Locale.ROOT)) {
            case "minggu": return Calendar.SUNDAY;
            case "senin": return Calendar.MONDAY;
            case "selasa": return Calendar.TUESDAY;
            case "rabu": return Calendar.WEDNESDAY;
            case "kamis": return Calendar.THURSDAY;
            case "jumat": return Calendar.FRIDAY;
            case "sabtu": return Calendar.SATURDAY;
            default: return -1;
        }
    }

    private void checkAndRequestAlarmPermission() {
        if (getContext() == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Izin Diperlukan")
                        .setMessage("Aplikasi ini memerlukan izin untuk mengatur alarm agar dapat memberikan pengingat jadwal. Aktifkan izin ini di pengaturan.")
                        .setPositiveButton("Buka Pengaturan", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}