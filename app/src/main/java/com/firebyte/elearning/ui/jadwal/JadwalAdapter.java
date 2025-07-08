package com.firebyte.elearning.ui.jadwal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.firebyte.elearning.R;
import com.firebyte.elearning.core.UserManager;
import com.firebyte.elearning.data.models.Jadwal;

public class JadwalAdapter extends ListAdapter<Jadwal, JadwalAdapter.JadwalViewHolder> {

    private OnItemClickListener listener;

    public JadwalAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Jadwal> DIFF_CALLBACK = new DiffUtil.ItemCallback<Jadwal>() {
        @Override
        public boolean areItemsTheSame(@NonNull Jadwal oldItem, @NonNull Jadwal newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Jadwal oldItem, @NonNull Jadwal newItem) {
            return oldItem.getMataKuliah().equals(newItem.getMataKuliah()) &&
                    oldItem.getDosen().equals(newItem.getDosen()) &&
                    oldItem.getWaktu().equals(newItem.getWaktu());
        }
    };

    @NonNull
    @Override
    public JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
        return new JadwalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalViewHolder holder, int position) {
        Jadwal currentJadwal = getItem(position);
        holder.tvMataKuliah.setText(currentJadwal.getMataKuliah());
        holder.tvDosen.setText("Dosen: " + currentJadwal.getDosen());
        holder.tvRuang.setText("Ruang: " + currentJadwal.getRuang());
        String waktuLengkap = currentJadwal.getHari() + ", " + (currentJadwal.getTanggal() != null && !currentJadwal.getTanggal().isEmpty() ? currentJadwal.getTanggal() : "") + " | " + currentJadwal.getWaktu();
        holder.tvWaktu.setText(waktuLengkap);

        UserManager.checkAdminStatus(isAdmin -> {
            holder.btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            holder.btnEdit.setVisibility(isAdmin ? View.VISIBLE : View.GONE); // Tampilkan tombol edit untuk admin
        });
    }

    public Jadwal getJadwalAt(int position) {
        return getItem(position);
    }

    class JadwalViewHolder extends RecyclerView.ViewHolder {
        TextView tvMataKuliah, tvDosen, tvRuang, tvWaktu;
        ImageButton btnDelete, btnEdit; // Tambahkan btnEdit

        public JadwalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMataKuliah = itemView.findViewById(R.id.tv_mata_kuliah);
            tvDosen = itemView.findViewById(R.id.tv_dosen);
            tvRuang = itemView.findViewById(R.id.tv_ruang);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            btnDelete = itemView.findViewById(R.id.delete_button);
            btnEdit = itemView.findViewById(R.id.edit_button); // Inisialisasi btnEdit

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getItem(position));
                }
            });

            // Tambahkan listener untuk tombol edit
            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(Jadwal jadwal);
        void onEditClick(Jadwal jadwal); // Tambahkan metode baru
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}