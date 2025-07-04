package com.firebyte.elearning;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.databinding.ActivityRekapTugasBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RekapTugasActivity extends AppCompatActivity {

    private ActivityRekapTugasBinding binding;
    private FirebaseFirestore db;
    private RekapJawabanAdapter adapter;
    private String tugasId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRekapTugasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        tugasId = getIntent().getStringExtra("TUGAS_ID");

        if (tugasId == null || tugasId.isEmpty()) {
            Toast.makeText(this, "ID Tugas tidak valid.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTugasInfo();
        setupRecyclerView();
    }

    private void loadTugasInfo() {
        db.collection("tugas").document(tugasId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Tugas tugas = documentSnapshot.toObject(Tugas.class);
                        if (tugas != null && binding != null) {
                            binding.tvJudulTugasRekap.setText(tugas.getMataKuliah());
                            binding.tvDeskripsiTugasRekap.setText(tugas.getDeskripsi());
                        }
                    }
                });
    }

    private void setupRecyclerView() {
        Query query = db.collection("tugas").document(tugasId)
                .collection("jawaban").orderBy("submittedAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Jawaban> options = new FirestoreRecyclerOptions.Builder<Jawaban>()
                .setQuery(query, Jawaban.class)
                .build();

        adapter = new RekapJawabanAdapter(options, this) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (binding != null) {
                    binding.tvNoJawaban.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }
            }
        };

        binding.recyclerViewRekapTugas.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewRekapTugas.setAdapter(adapter);
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