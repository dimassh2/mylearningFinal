package com.firebyte.elearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter; // <-- IMPORT BARU YANG HILANG
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner; // <-- IMPORT BARU YANG HILANG
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.databinding.FragmentScheduleBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference jadwalRef = db.collection("jadwal");
    private JadwalAdapter adapter;

    public ScheduleFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView();

        // Menggunakan sistem pengecekan admin yang baru
        UserManager.checkAdminStatus(isAdmin -> {
            if (isAdmin) {
                binding.fabAddJadwal.setVisibility(View.VISIBLE);
                binding.fabAddJadwal.setOnClickListener(v -> showAddJadwalDialog());
            } else {
                binding.fabAddJadwal.setVisibility(View.GONE);
            }
        });
    }

    private void setUpRecyclerView() {
        Query query = jadwalRef.orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Jadwal> options = new FirestoreRecyclerOptions.Builder<Jadwal>()
                .setQuery(query, Jadwal.class)
                .build();

        adapter = new JadwalAdapter(options);

        binding.recyclerViewJadwal.setHasFixedSize(true);
        binding.recyclerViewJadwal.setLayoutManager(new SafeLinearLayoutManager(getContext()));
        binding.recyclerViewJadwal.setAdapter(adapter);

        adapter.setOnItemClickListener(documentSnapshot -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Hapus Jadwal")
                    .setMessage("Apakah Anda yakin ingin menghapus jadwal ini?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        documentSnapshot.getReference().delete();
                        Toast.makeText(getContext(), "Jadwal dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void showAddJadwalDialog() {
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

        // Setup Adapter untuk Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.hari_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHari.setAdapter(spinnerAdapter);

        final AlertDialog dialog = builder.create();

        btnSimpan.setOnClickListener(v -> {
            String mataKuliah = etMataKuliah.getText().toString().trim();
            String dosen = etDosen.getText().toString().trim();
            String hari = spinnerHari.getSelectedItem().toString();
            String tanggal = etTanggal.getText().toString().trim();
            String waktu = etWaktu.getText().toString().trim();
            String ruang = etRuang.getText().toString().trim();

            if (mataKuliah.isEmpty() || waktu.isEmpty()) {
                Toast.makeText(getContext(), "Harap isi semua field wajib", Toast.LENGTH_SHORT).show();
                return;
            }

            jadwalRef.add(new Jadwal(mataKuliah, dosen, hari, tanggal, waktu, ruang));
            Toast.makeText(getContext(), "Jadwal berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
