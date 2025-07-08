package com.firebyte.elearning;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.databinding.FragmentTugasBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TugasFragment extends Fragment {

    private FragmentTugasBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tugasRef = db.collection("tugas");
    private TugasAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTugasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupAdminFeatures();
    }

    private void setupRecyclerView() {
        Query query = tugasRef.orderBy("createdAt", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Tugas> options = new FirestoreRecyclerOptions.Builder<Tugas>()
                .setQuery(query, Tugas.class).build();

        adapter = new TugasAdapter(options);

        adapter.setOnTugasItemClickListener(new TugasAdapter.OnTugasItemClickListener() {
            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot) {
                if (isAdded() && getContext() != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Hapus Tugas")
                            .setMessage("Apakah Anda yakin ingin menghapus tugas ini?")
                            .setPositiveButton("Hapus", (dialog, which) -> {
                                documentSnapshot.getReference().delete()
                                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Tugas dihapus", Toast.LENGTH_SHORT).show());
                            })
                            .setNegativeButton("Batal", null)
                            .show();
                }
            }

            @Override
            public void onEditClick(DocumentSnapshot documentSnapshot) {
                Tugas tugas = documentSnapshot.toObject(Tugas.class);
                showTugasDialog(documentSnapshot, tugas);
            }
        });

        if (getContext() != null) {
            binding.recyclerViewTugas.setLayoutManager(new SafeLinearLayoutManager(getContext()));
        }
        binding.recyclerViewTugas.setAdapter(adapter);
    }

    private void setupAdminFeatures() {
        UserManager.checkAdminStatus(isAdmin -> {
            if (binding != null && isAdded()) {
                if (isAdmin) {
                    binding.fabAddTugas.setVisibility(View.VISIBLE);
                    binding.fabAddTugas.setOnClickListener(v -> showTugasDialog(null, null));
                } else {
                    binding.fabAddTugas.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showTugasDialog(@Nullable DocumentSnapshot snapshot, @Nullable Tugas tugas) {
        if (getContext() == null || !isAdded()) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_tugas, null);
        final EditText etMataKuliah = dialogView.findViewById(R.id.et_mata_kuliah_tugas);
        final EditText etDeskripsi = dialogView.findViewById(R.id.et_deskripsi_tugas);
        final EditText etLink = dialogView.findViewById(R.id.et_link_lampiran_tugas);
        final Button btnSimpan = dialogView.findViewById(R.id.btn_simpan_tugas);

        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        if (tugas != null) {
            etMataKuliah.setText(tugas.getMataKuliah());
            etDeskripsi.setText(tugas.getDeskripsi());
            etLink.setText(tugas.getLampiranUrl());
        }

        btnSimpan.setOnClickListener(v -> {
            String mataKuliah = etMataKuliah.getText().toString().trim();
            String deskripsi = etDeskripsi.getText().toString().trim();
            String linkLampiran = etLink.getText().toString().trim();

            if (mataKuliah.isEmpty() || deskripsi.isEmpty()) {
                Toast.makeText(getContext(), "Mata Kuliah dan Deskripsi wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            saveTugasToFirestore(snapshot, mataKuliah, deskripsi, linkLampiran, dialog);
        });
        dialog.show();
    }

    private void saveTugasToFirestore(@Nullable DocumentSnapshot snapshot, String mataKuliah, String deskripsi, String url, AlertDialog dialog) {
        String tipe = (url != null && !url.isEmpty()) ? "LINK" : null;
        Tugas tugasBaru = new Tugas(mataKuliah, deskripsi, url, tipe);

        if (snapshot == null) {
            // Mode Tambah
            tugasRef.add(tugasBaru).addOnSuccessListener(aVoid -> {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Tugas berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });
        } else {
            // Mode Edit
            snapshot.getReference().set(tugasBaru).addOnSuccessListener(aVoid -> {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Tugas berhasil diperbarui", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}