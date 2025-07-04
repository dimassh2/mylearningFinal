package com.firebyte.elearning;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.databinding.ActivityRekapAbsenBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

public class RekapAbsenActivity extends AppCompatActivity {

    private ActivityRekapAbsenBinding binding;
    private FirebaseFirestore db;
    private RekapAbsenAdapter adapter;
    private String sesiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRekapAbsenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        sesiId = getIntent().getStringExtra("SESI_ID");

        if (sesiId == null || sesiId.isEmpty()) {
            Toast.makeText(this, "ID Sesi tidak valid.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSesiInfo();
        setupRecyclerView();
        setupAdminFeatures();
    }

    private void loadSesiInfo() {
        db.collection("sesi_absensi").document(sesiId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        AbsensiSesi sesi = documentSnapshot.toObject(AbsensiSesi.class);
                        if (sesi != null) {
                            binding.tvRekapSubtitle.setText("Mata Kuliah: " + sesi.getMataKuliah());
                        }
                    }
                });
    }

    private void setupRecyclerView() {
        Query query = db.collection("sesi_absensi").document(sesiId)
                .collection("mahasiswa").orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<AbsensiMahasiswa> options = new FirestoreRecyclerOptions.Builder<AbsensiMahasiswa>()
                .setQuery(query, AbsensiMahasiswa.class)
                .build();

        adapter = new RekapAbsenAdapter(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (binding != null) {
                    binding.tvNoRekap.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }
            }
        };

        binding.recyclerViewRekap.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewRekap.setAdapter(adapter);
    }

    private void setupAdminFeatures() {
        UserManager.checkAdminStatus(isAdmin -> {
            if (binding != null && isAdmin) {
                binding.btnHapusSesi.setVisibility(View.VISIBLE);
                binding.btnHapusSesi.setOnClickListener(v -> konfirmasiHapusSesi());
            }
        });
    }

    private void konfirmasiHapusSesi() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Sesi Absensi")
                .setMessage("Apakah Anda yakin ingin menghapus seluruh sesi absensi ini beserta datanya? Tindakan ini tidak bisa dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> hapusSesiIni())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void hapusSesiIni() {
        // Hapus semua dokumen di sub-koleksi 'mahasiswa' terlebih dahulu
        db.collection("sesi_absensi").document(sesiId).collection("mahasiswa")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> batch.delete(doc.getReference()));

                    batch.commit().addOnSuccessListener(aVoid -> {
                        // Setelah semua data mahasiswa di dalamnya terhapus, hapus dokumen sesi utama
                        db.collection("sesi_absensi").document(sesiId).delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(this, "Sesi berhasil dihapus.", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    }).addOnFailureListener(e -> Toast.makeText(this, "Gagal menghapus data absen.", Toast.LENGTH_SHORT).show());
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}