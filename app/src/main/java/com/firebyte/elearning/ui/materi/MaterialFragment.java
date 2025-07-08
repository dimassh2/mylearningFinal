package com.firebyte.elearning.ui.materi;

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
import com.firebyte.elearning.R;
import com.firebyte.elearning.core.UserManager;
import com.firebyte.elearning.data.models.Materi;
import com.firebyte.elearning.databinding.FragmentMaterialBinding;
import com.firebyte.elearning.utils.SafeLinearLayoutManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MaterialFragment extends Fragment {
    private FragmentMaterialBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference materiRef = db.collection("materi");
    private MateriAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMaterialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupAdminFeatures();
    }

    private void setupRecyclerView() {
        Query query = materiRef.orderBy("createdAt", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Materi> options = new FirestoreRecyclerOptions.Builder<Materi>()
                .setQuery(query, Materi.class).build();

        if (getContext() == null) return;

        adapter = new MateriAdapter(options, requireContext());
        adapter.setOnItemClickListener(new MateriAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot) {
                if (isAdded() && getContext() != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Hapus Materi")
                            .setMessage("Apakah Anda yakin ingin menghapus materi ini?")
                            .setPositiveButton("Hapus", (dialog, which) -> {
                                documentSnapshot.getReference().delete()
                                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Materi dihapus", Toast.LENGTH_SHORT).show());
                            })
                            .setNegativeButton("Batal", null)
                            .show();
                }
            }

            @Override
            public void onEditClick(DocumentSnapshot documentSnapshot) {
                // Panggil dialog edit dengan data yang ada
                Materi materi = documentSnapshot.toObject(Materi.class);
                showMateriDialog(documentSnapshot, materi);
            }
        });
        binding.recyclerViewMateri.setLayoutManager(new SafeLinearLayoutManager(getContext()));
        binding.recyclerViewMateri.setAdapter(adapter);
    }

    private void setupAdminFeatures() {
        UserManager.checkAdminStatus(isAdmin -> {
            if (binding != null && isAdded() && isAdmin) {
                binding.fabAddMateri.setVisibility(View.VISIBLE);
                binding.fabAddMateri.setOnClickListener(v -> showMateriDialog(null, null));
            } else if (binding != null) {
                binding.fabAddMateri.setVisibility(View.GONE);
            }
        });
    }

    private void showMateriDialog(@Nullable DocumentSnapshot snapshot, @Nullable Materi materi) {
        if (getContext() == null || !isAdded()) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_materi, null);
        final EditText etMataKuliah = dialogView.findViewById(R.id.et_mata_kuliah_materi);
        final EditText etJudul = dialogView.findViewById(R.id.et_judul_materi);
        final EditText etDosen = dialogView.findViewById(R.id.et_dosen_materi);
        final EditText etLink = dialogView.findViewById(R.id.et_link_lampiran);
        final Button btnSimpan = dialogView.findViewById(R.id.btn_simpan_materi);

        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        if (materi != null) {
            etMataKuliah.setText(materi.getMataKuliah());
            etJudul.setText(materi.getJudul());
            etDosen.setText(materi.getDosen());
            etLink.setText(materi.getLampiranUrl());
        }

        btnSimpan.setOnClickListener(v -> {
            String mataKuliah = etMataKuliah.getText().toString().trim();
            String judul = etJudul.getText().toString().trim();
            String dosen = etDosen.getText().toString().trim();
            String linkLampiran = etLink.getText().toString().trim();
            if (mataKuliah.isEmpty() || judul.isEmpty() || dosen.isEmpty() || linkLampiran.isEmpty()) {
                Toast.makeText(getContext(), "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            saveMateriToFirestore(snapshot, mataKuliah, judul, dosen, linkLampiran, dialog);
        });
        dialog.show();
    }

    private void saveMateriToFirestore(@Nullable DocumentSnapshot snapshot, String mataKuliah, String judul, String dosen, String url, AlertDialog dialog) {
        Materi materiBaru = new Materi(mataKuliah, judul, dosen, url, "LINK");

        if (snapshot == null) {
            // Mode Tambah
            materiRef.add(materiBaru).addOnSuccessListener(aVoid -> {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Materi berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });
        } else {
            // Mode Edit
            snapshot.getReference().set(materiBaru).addOnSuccessListener(aVoid -> {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Materi berhasil diperbarui", Toast.LENGTH_SHORT).show();
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