package com.firebyte.elearning;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.firebyte.elearning.databinding.ActivityTugasDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class TugasDetailActivity extends AppCompatActivity {

    private ActivityTugasDetailBinding binding;
    private FirebaseFirestore db;
    private String tugasId;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTugasDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        tugasId = getIntent().getStringExtra("TUGAS_ID");

        if (tugasId != null && currentUser != null) {
            loadTugasDetails();
            checkSubmissionStatus();
            setupAdminFeatures(); // Memanggil fungsi untuk admin
        } else {
            Toast.makeText(this, "Gagal memuat data.", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.btnKirimJawaban.setOnClickListener(v -> {
            String linkJawaban = binding.etLinkJawaban.getText().toString().trim();
            if (linkJawaban.isEmpty() || !android.util.Patterns.WEB_URL.matcher(linkJawaban).matches()) {
                Toast.makeText(this, "Masukkan link jawaban yang valid", Toast.LENGTH_SHORT).show();
                return;
            }
            saveJawabanToFirestore(linkJawaban);
        });
    }

    private void loadTugasDetails() {
        db.collection("tugas").document(tugasId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Tugas tugas = doc.toObject(Tugas.class);
                if (tugas != null) {
                    binding.tvDetailJudulTugas.setText(tugas.getMataKuliah());
                    binding.tvDetailDeskripsiTugas.setText(tugas.getDeskripsi());

                    // PERBAIKAN: Hanya tampilkan tombol jika ada link
                    String lampiranUrl = tugas.getLampiranUrl();
                    if(lampiranUrl != null && !lampiranUrl.isEmpty()) {
                        binding.btnBukaLinkTugas.setVisibility(View.VISIBLE);
                        binding.btnBukaLinkTugas.setOnClickListener(v -> {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lampiranUrl));
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(this, "Tidak dapat membuka link tugas.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        binding.btnBukaLinkTugas.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void checkSubmissionStatus() {
        DocumentReference jawabanRef = db.collection("tugas").document(tugasId)
                .collection("jawaban").document(currentUser.getUid());

        jawabanRef.get().addOnSuccessListener(doc -> {
            if (binding == null) return;
            if (doc.exists()) {
                String linkTerkirim = doc.getString("fileUrl");
                // PERBAIKAN: Menggunakan ID yang benar
                binding.tvStatusJawaban.setText("Anda sudah mengirim jawaban:");
                binding.etLinkJawaban.setText(linkTerkirim);
                binding.etLinkJawaban.setEnabled(false);
                binding.btnKirimJawaban.setEnabled(false);
                binding.btnKirimJawaban.setText("Jawaban Sudah Terkirim");
            }
        });
    }

    private void saveJawabanToFirestore(String linkJawaban) {
        String userId = currentUser.getUid();
        Map<String, Object> jawaban = new HashMap<>();
        jawaban.put("userId", userId);
        jawaban.put("userName", currentUser.getDisplayName());
        jawaban.put("fileUrl", linkJawaban);
        jawaban.put("submittedAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("tugas").document(tugasId)
                .collection("jawaban").document(userId).set(jawaban)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TugasDetailActivity.this, "Jawaban berhasil dikirim!", Toast.LENGTH_SHORT).show();
                    checkSubmissionStatus();
                });
    }

    private void setupAdminFeatures() {
        UserManager.checkAdminStatus(isAdmin -> {
            if (binding != null && isAdmin) {
                binding.btnLihatRekapJawaban.setVisibility(View.VISIBLE);
                binding.btnLihatRekapJawaban.setOnClickListener(v -> {
                    Intent intent = new Intent(this, RekapTugasActivity.class);
                    intent.putExtra("TUGAS_ID", tugasId);
                    startActivity(intent);
                });
            }
        });
    }
}