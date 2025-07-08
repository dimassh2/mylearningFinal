package com.firebyte.elearning.ui.absen;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.R;
import com.firebyte.elearning.core.UserManager;
import com.firebyte.elearning.data.models.AbsensiMahasiswa;
import com.firebyte.elearning.data.models.AbsensiSesi;
import com.firebyte.elearning.databinding.FragmentAbsenBinding;
import com.firebyte.elearning.utils.SafeLinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AbsenFragment extends Fragment {

    private FragmentAbsenBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AbsenSesiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAbsenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance(); // Inisialisasi FirebaseAuth
        setupRecyclerView();
        setupAdminFeatures();
    }

    private void setupRecyclerView() {
        Query query = db.collection("sesi_absensi").orderBy("waktuBuka", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<AbsensiSesi> options = new FirestoreRecyclerOptions.Builder<AbsensiSesi>()
                .setQuery(query, AbsensiSesi.class)
                .build();

        AbsenSesiAdapter.OnItemClickListener listener = new AbsenSesiAdapter.OnItemClickListener() {
            @Override
            public void onAbsenClick(DocumentSnapshot documentSnapshot) {
                AbsensiSesi sesi = documentSnapshot.toObject(AbsensiSesi.class);
                if (sesi != null) {
                    showAbsenDialog(documentSnapshot.getId(), sesi.getMataKuliah());
                }
            }

            @Override
            public void onRekapClick(DocumentSnapshot documentSnapshot) {
                if (getContext() != null) {
                    Intent intent = new Intent(getContext(), RekapAbsenActivity.class);
                    intent.putExtra("SESI_ID", documentSnapshot.getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot) {
                if (getContext() != null) {
                    konfirmasiHapusSesi(documentSnapshot.getId());
                }
            }
        };

        if (getContext() != null) {
            adapter = new AbsenSesiAdapter(options, getContext(), listener);
            binding.recyclerViewAbsen.setLayoutManager(new SafeLinearLayoutManager(getContext())); // Gunakan SafeLinearLayoutManager
            binding.recyclerViewAbsen.setAdapter(adapter);
        }
    }

    private void setupAdminFeatures() {
        UserManager.checkAdminStatus(isAdmin -> {
            if (binding != null && isAdded()) {
                binding.fabAddAbsen.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
                if (isAdmin) {
                    binding.fabAddAbsen.setOnClickListener(v -> showAddAbsenSesiDialog());
                }
            }
        });
    }

    // ===================================================================
    // == METODE YANG HILANG DAN SEKARANG DITAMBAHKAN KEMBALI ==
    // ===================================================================

    private void showAddAbsenSesiDialog() {
        if (getContext() == null || !isAdded()) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_absen_sesi, null);
        final EditText etMatkul = dialogView.findViewById(R.id.et_matkul_sesi);
        final EditText etDurasi = dialogView.findViewById(R.id.et_durasi_sesi);

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setTitle("Buka Sesi Absensi")
                .setPositiveButton("Buka", (dialog, which) -> {
                    String matkul = etMatkul.getText().toString().trim();
                    String durasiStr = etDurasi.getText().toString().trim();
                    if (matkul.isEmpty() || durasiStr.isEmpty()) {
                        Toast.makeText(getContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        int durasiMenit = Integer.parseInt(durasiStr);
                        Calendar cal = Calendar.getInstance();
                        Date waktuBuka = cal.getTime();
                        cal.add(Calendar.MINUTE, durasiMenit);
                        Date waktuTutup = cal.getTime();

                        Map<String, Object> sesi = new HashMap<>();
                        sesi.put("mataKuliah", matkul);
                        sesi.put("waktuBuka", waktuBuka);
                        sesi.put("waktuTutup", waktuTutup);

                        db.collection("sesi_absensi").add(sesi)
                                .addOnSuccessListener(aVoid -> {
                                    if (isAdded() && getContext() != null) {
                                        Toast.makeText(getContext(), "Sesi absensi untuk " + matkul + " dibuka!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Durasi harus berupa angka.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showAbsenDialog(String sesiId, String mataKuliah) {
        if (getContext() == null || !isAdded()) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_absen_keterangan, null);
        final RadioGroup rgStatus = dialogView.findViewById(R.id.rg_status);
        final EditText etKeterangan = dialogView.findViewById(R.id.et_keterangan_absen);

        rgStatus.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isIzinOrSakit = checkedId == R.id.rb_izin || checkedId == R.id.rb_sakit;
            etKeterangan.setVisibility(isIzinOrSakit ? View.VISIBLE : View.GONE);
        });

        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setTitle("Absensi: " + mataKuliah)
                .setPositiveButton("Kirim", (dialog, which) -> {
                    int selectedId = rgStatus.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(getContext(), "Pilih status kehadiran.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String status;
                    if (selectedId == R.id.rb_hadir) {
                        status = "Hadir";
                    } else if (selectedId == R.id.rb_izin) {
                        status = "Izin";
                    } else {
                        status = "Sakit";
                    }

                    String keterangan = etKeterangan.getText().toString().trim();
                    if ((status.equals("Izin") || status.equals("Sakit")) && keterangan.isEmpty()) {
                        Toast.makeText(getContext(), "Keterangan wajib diisi untuk Izin/Sakit.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendAbsenData(sesiId, status, keterangan);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void sendAbsenData(String sesiId, String status, String keterangan) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        AbsensiMahasiswa absensi = new AbsensiMahasiswa(user.getUid(), user.getDisplayName(), status, keterangan);
        db.collection("sesi_absensi").document(sesiId)
                .collection("mahasiswa").document(user.getUid()).set(absensi)
                .addOnSuccessListener(aVoid -> {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Absen berhasil direkam", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Anda sudah absen di sesi ini.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void konfirmasiHapusSesi(String sesiId) {
        if (getContext() == null || !isAdded()) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Sesi Absensi")
                .setMessage("Yakin ingin menghapus sesi ini dan semua datanya? Tindakan ini tidak bisa dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> hapusSesiIni(sesiId))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void hapusSesiIni(String sesiId) {
        if (getContext() == null || !isAdded()) return;
        // Hapus semua dokumen di sub-koleksi 'mahasiswa' terlebih dahulu
        db.collection("sesi_absensi").document(sesiId).collection("mahasiswa")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            batch.delete(doc.getReference());
                        }
                        batch.commit().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                // Setelah semua data mahasiswa di dalamnya terhapus, hapus dokumen sesi utama
                                db.collection("sesi_absensi").document(sesiId).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            if (isAdded() && getContext() != null) {
                                                Toast.makeText(getContext(), "Sesi berhasil dihapus.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                });
    }


    // ===================================================================
    // == Lifecycle Methods ==
    // ===================================================================

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