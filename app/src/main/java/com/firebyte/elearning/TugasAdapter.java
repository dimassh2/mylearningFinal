package com.firebyte.elearning;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.databinding.ItemTugasBinding;
import com.google.firebase.firestore.DocumentSnapshot;

public class TugasAdapter extends FirestoreRecyclerAdapter<Tugas, TugasAdapter.TugasViewHolder> {

    private OnTugasItemClickListener listener;

    public interface OnTugasItemClickListener {
        void onDeleteClick(DocumentSnapshot documentSnapshot);
    }

    public void setOnTugasItemClickListener(OnTugasItemClickListener listener) {
        this.listener = listener;
    }

    public TugasAdapter(@NonNull FirestoreRecyclerOptions<Tugas> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TugasViewHolder holder, int position, @NonNull Tugas model) {
        // Mengirim ID dokumen ke ViewHolder agar bisa digunakan saat tombol diklik
        String docId = getSnapshots().getSnapshot(position).getId();
        holder.bind(model, docId);
    }

    @NonNull
    @Override
    public TugasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tugas, parent, false);
        return new TugasViewHolder(view);
    }

    class TugasViewHolder extends RecyclerView.ViewHolder {
        private final ItemTugasBinding binding;

        public TugasViewHolder(@NonNull View itemView) {
            super(itemView);
            // Menggunakan ViewBinding untuk mengakses semua view dengan aman
            binding = ItemTugasBinding.bind(itemView);
        }

        void bind(Tugas tugas, String tugasId) {
            // Menggunakan ID yang benar dari layout
            binding.tvNamaPelajaran.setText(tugas.getMataKuliah());
            binding.tvNamaGuru.setText(tugas.getDeskripsi()); // Menampilkan deskripsi di sini

            if (tugas.getLampiranUrl() != null && !tugas.getLampiranUrl().isEmpty()) {
                binding.tvSisaWaktu.setText("Ada lampiran (" + tugas.getTipeLampiran() + ")");
            } else {
                binding.tvSisaWaktu.setText("Tidak ada lampiran");
            }

            // Menampilkan tombol hapus untuk admin
            UserManager.checkAdminStatus(isAdmin -> {
                if (isAdmin) {
                    binding.deleteButtonTugas.setVisibility(View.VISIBLE);
                    binding.deleteButtonTugas.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener != null) {
                            listener.onDeleteClick(getSnapshots().getSnapshot(position));
                        }
                    });
                } else {
                    binding.deleteButtonTugas.setVisibility(View.GONE);
                }
            });

            // Memberi aksi pada tombol "Kerjakan"
            binding.btnKerjakan.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), TugasDetailActivity.class);
                intent.putExtra("TUGAS_ID", tugasId);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}