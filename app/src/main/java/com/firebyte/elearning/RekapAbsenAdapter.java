package com.firebyte.elearning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.databinding.ItemRekapAbsenBinding;

public class RekapAbsenAdapter extends FirestoreRecyclerAdapter<AbsensiMahasiswa, RekapAbsenAdapter.RekapViewHolder> {

    public RekapAbsenAdapter(@NonNull FirestoreRecyclerOptions<AbsensiMahasiswa> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RekapViewHolder holder, int position, @NonNull AbsensiMahasiswa model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public RekapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rekap_absen, parent, false);
        return new RekapViewHolder(view);
    }

    class RekapViewHolder extends RecyclerView.ViewHolder {
        private final ItemRekapAbsenBinding binding;

        public RekapViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRekapAbsenBinding.bind(itemView);
        }

        void bind(AbsensiMahasiswa absensi) {
            binding.tvNamaMahasiswa.setText(absensi.getNamaUser());
            binding.tvStatusMahasiswa.setText("Status: " + absensi.getStatus());

            String keterangan = absensi.getKeterangan();
            if (keterangan != null && !keterangan.isEmpty()) {
                binding.tvKeteranganMahasiswa.setVisibility(View.VISIBLE);
                binding.tvKeteranganMahasiswa.setText("Keterangan: " + keterangan);
            } else {
                binding.tvKeteranganMahasiswa.setVisibility(View.GONE);
            }
        }
    }
}