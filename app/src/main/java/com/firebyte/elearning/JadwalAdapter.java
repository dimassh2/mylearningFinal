package com.firebyte.elearning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class JadwalAdapter extends FirestoreRecyclerAdapter<Jadwal, JadwalAdapter.JadwalViewHolder> {

    private OnItemClickListener listener;

    public JadwalAdapter(@NonNull FirestoreRecyclerOptions<Jadwal> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull JadwalViewHolder holder, int position, @NonNull Jadwal model) {
        holder.tvMataKuliah.setText(model.getMataKuliah());
        holder.tvDosen.setText("Dosen: " + model.getDosen());
        holder.tvRuang.setText("Ruang: " + model.getRuang());
        String waktuLengkap = model.getHari() + ", " + model.getTanggal() + " | " + model.getWaktu();
        holder.tvWaktu.setText(waktuLengkap);


        // Menampilkan tombol hapus hanya jika pengguna adalah admin
// Menampilkan tombol hapus hanya jika pengguna adalah admin
        UserManager.checkAdminStatus(isAdmin -> {
            if (isAdmin) {
                holder.btnDelete.setVisibility(View.VISIBLE);
            } else {
                holder.btnDelete.setVisibility(View.GONE);
            }
        });
    }

    @NonNull
    @Override
    public JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
        return new JadwalViewHolder(view);
    }

    class JadwalViewHolder extends RecyclerView.ViewHolder {
        TextView tvMataKuliah, tvDosen, tvRuang, tvWaktu;
        ImageButton btnDelete;

        public JadwalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMataKuliah = itemView.findViewById(R.id.tv_mata_kuliah);
            tvDosen = itemView.findViewById(R.id.tv_dosen);
            tvRuang = itemView.findViewById(R.id.tv_ruang);
            tvWaktu = itemView.findViewById(R.id.tv_waktu);
            btnDelete = itemView.findViewById(R.id.delete_button);

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(getSnapshots().getSnapshot(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(DocumentSnapshot documentSnapshot);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
