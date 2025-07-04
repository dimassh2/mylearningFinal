package com.firebyte.elearning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.databinding.FragmentHomeBinding;
import com.firebyte.elearning.databinding.ItemTugasHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Tugas, TugasHomeViewHolder> tugasAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserData();
        setupMenuListeners();
        setupTugasRecyclerView();
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || binding == null) return;

        db.collection("users").document(user.getUid()).get().addOnSuccessListener(doc -> {
            if (isAdded() && getContext() != null && binding != null) {
                if (doc.exists()) {
                    User userData = doc.toObject(User.class);
                    if (userData != null) {
                        binding.tvNamaHome.setText(userData.getNama());
                        binding.tvNimHome.setText("NIM: " + userData.getNim());
                        String jurusanDanKelas = userData.getJurusan() + " - " + userData.getKelas();
                        binding.tvJurusanKelasHome.setText(jurusanDanKelas);
                        Glide.with(requireContext()).load(userData.getFotoUrl()).placeholder(R.drawable.ic_profile).into(binding.profileImageHome);
                    }
                }
            }
        });
    }

    private void setupMenuListeners() {
        if (binding == null) return;

        binding.menuJadwal.setOnClickListener(v -> {
            if (isAdded()) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ScheduleFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Tombol Absen sekarang membuka AbsenFragment (yang sebelumnya sudah kita buat)
        binding.menuAbsen.setOnClickListener(v -> {
            if (isAdded()) {
                // Navigasi ke AbsenFragment, bukan ke MainActivity lagi.
                // Pastikan AbsenFragment sudah dibuat dan MainActivity sudah diupdate untuk menanganinya.
                // Jika belum, Anda bisa sementara kembali ke logika lama atau implementasikan AbsenFragment.
                // Untuk sekarang, kita asumsikan akan ke AbsenFragment.
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AbsenFragment()) // Ganti dengan fragment absen yang benar
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setupTugasRecyclerView() {
        if (binding == null || getContext() == null) return;

        Query query = db.collection("tugas").orderBy("createdAt", Query.Direction.DESCENDING).limit(10);
        FirestoreRecyclerOptions<Tugas> options = new FirestoreRecyclerOptions.Builder<Tugas>()
                .setQuery(query, Tugas.class)
                .build();

        tugasAdapter = new FirestoreRecyclerAdapter<Tugas, TugasHomeViewHolder>(options) {
            @NonNull
            @Override
            public TugasHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tugas_home, parent, false);
                return new TugasHomeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TugasHomeViewHolder holder, int position, @NonNull Tugas model) {
                if (holder.getAbsoluteAdapterPosition() != -1) {
                    holder.bind(model, getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId());
                }
            }

            @Override
            public void onDataChanged() {
                if(binding != null && isAdded()) {
                    binding.tvNoTugas.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }
            }
        };

        // Menggunakan SafeLinearLayoutManager untuk mencegah crash
        binding.recyclerViewTugasHome.setLayoutManager(new SafeLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewTugasHome.setAdapter(tugasAdapter);
    }

    // Kelas ViewHolder sekarang dapat menemukan RecyclerView.ViewHolder
    static class TugasHomeViewHolder extends RecyclerView.ViewHolder {
        private final ItemTugasHomeBinding itemBinding;

        public TugasHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemTugasHomeBinding.bind(itemView);
        }

        void bind(Tugas tugas, String tugasId) {
            itemBinding.tvMatkulTugas.setText(tugas.getMataKuliah());
            if (tugas.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                itemBinding.tvDeadlineTugas.setText("Dibuat: " + sdf.format(tugas.getCreatedAt()));
            } else {
                itemBinding.tvDeadlineTugas.setText("Baru saja");
            }

            itemBinding.btnKerjakanTugas.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, TugasDetailActivity.class);
                intent.putExtra("TUGAS_ID", tugasId);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (tugasAdapter != null) {
            tugasAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tugasAdapter != null) {
            tugasAdapter.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}