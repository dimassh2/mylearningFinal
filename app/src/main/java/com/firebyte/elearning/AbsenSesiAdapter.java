package com.firebyte.elearning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.databinding.ItemSesiAbsenBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AbsenSesiAdapter extends FirestoreRecyclerAdapter<AbsensiSesi, AbsenSesiAdapter.AbsenSesiViewHolder> {

    private final OnItemClickListener listener;
    private final Context context;

    public interface OnItemClickListener {
        void onAbsenClick(DocumentSnapshot documentSnapshot);
        void onRekapClick(DocumentSnapshot documentSnapshot);
        void onDeleteClick(DocumentSnapshot documentSnapshot);
    }

    public AbsenSesiAdapter(@NonNull FirestoreRecyclerOptions<AbsensiSesi> options, Context context, OnItemClickListener listener) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull AbsenSesiViewHolder holder, int position, @NonNull AbsensiSesi model) {
        holder.bind(model, getSnapshots().getSnapshot(position));
    }

    @NonNull
    @Override
    public AbsenSesiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sesi_absen, parent, false);
        return new AbsenSesiViewHolder(view);
    }

    class AbsenSesiViewHolder extends RecyclerView.ViewHolder {
        private final ItemSesiAbsenBinding binding;

        public AbsenSesiViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSesiAbsenBinding.bind(itemView);
        }

        void bind(AbsensiSesi sesi, DocumentSnapshot snapshot) {
            binding.tvMatkulSesi.setText(sesi.getMataKuliah());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            binding.tvWaktuBuka.setText("Dibuka: " + sdf.format(sesi.getWaktuBuka()));

            boolean isOpen = new Date().before(sesi.getWaktuTutup());

            if (isOpen) {
                binding.tvStatusSesi.setText("Status: DIBUKA");
                binding.tvStatusSesi.setTextColor(ContextCompat.getColor(context, R.color.green_primary));
                binding.btnLakukanAbsen.setEnabled(true);
            } else {
                binding.tvStatusSesi.setText("Status: DITUTUP");
                binding.tvStatusSesi.setTextColor(Color.RED);
                binding.btnLakukanAbsen.setEnabled(false);
            }

            UserManager.checkAdminStatus(isAdmin -> {
                binding.btnRekapAbsen.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
                binding.btnHapusSesi.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
                binding.btnLakukanAbsen.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
            });

            binding.btnLakukanAbsen.setOnClickListener(v -> listener.onAbsenClick(snapshot));
            binding.btnRekapAbsen.setOnClickListener(v -> listener.onRekapClick(snapshot));
            binding.btnHapusSesi.setOnClickListener(v -> listener.onDeleteClick(snapshot));
        }
    }
}