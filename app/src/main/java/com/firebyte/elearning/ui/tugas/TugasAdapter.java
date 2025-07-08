package com.firebyte.elearning.ui.tugas;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.R;
import com.firebyte.elearning.core.UserManager;
import com.firebyte.elearning.data.models.Tugas;
import com.firebyte.elearning.databinding.ItemTugasBinding;
import com.google.firebase.firestore.DocumentSnapshot;

public class TugasAdapter extends FirestoreRecyclerAdapter<Tugas, TugasAdapter.TugasViewHolder> {

    private OnTugasItemClickListener listener;

    public interface OnTugasItemClickListener {
        void onDeleteClick(DocumentSnapshot documentSnapshot);
        void onEditClick(DocumentSnapshot documentSnapshot); // Metode edit baru
    }

    public void setOnTugasItemClickListener(OnTugasItemClickListener listener) {
        this.listener = listener;
    }

    public TugasAdapter(@NonNull FirestoreRecyclerOptions<Tugas> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TugasViewHolder holder, int position, @NonNull Tugas model) {
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
            binding = ItemTugasBinding.bind(itemView);
        }

        void bind(Tugas tugas, String tugasId) {
            binding.tvNamaPelajaran.setText(tugas.getMataKuliah());
            binding.tvNamaGuru.setText(tugas.getDeskripsi());

            if (tugas.getLampiranUrl() != null && !tugas.getLampiranUrl().isEmpty()) {
                binding.tvSisaWaktu.setText("Ada lampiran (" + tugas.getTipeLampiran() + ")");
            } else {
                binding.tvSisaWaktu.setText("Tidak ada lampiran");
            }

            UserManager.checkAdminStatus(isAdmin -> {
                binding.deleteButtonTugas.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
                binding.editButtonTugas.setVisibility(isAdmin ? View.VISIBLE : View.GONE); // Menampilkan tombol edit
                if (isAdmin) {
                    binding.deleteButtonTugas.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener != null) {
                            listener.onDeleteClick(getSnapshots().getSnapshot(position));
                        }
                    });
                    binding.editButtonTugas.setOnClickListener(v -> { // Listener untuk edit
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener != null) {
                            listener.onEditClick(getSnapshots().getSnapshot(position));
                        }
                    });
                } else {
                    binding.deleteButtonTugas.setVisibility(View.GONE);
                }
            });

            binding.btnKerjakan.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), TugasDetailActivity.class);
                intent.putExtra("TUGAS_ID", tugasId);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}