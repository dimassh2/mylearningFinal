package com.firebyte.elearning.ui.tugas;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebyte.elearning.R;
import com.firebyte.elearning.data.models.Jawaban;
import com.firebyte.elearning.databinding.ItemRekapJawabanBinding;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RekapJawabanAdapter extends FirestoreRecyclerAdapter<Jawaban, RekapJawabanAdapter.JawabanViewHolder> {

    private Context context;

    public RekapJawabanAdapter(@NonNull FirestoreRecyclerOptions<Jawaban> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull JawabanViewHolder holder, int position, @NonNull Jawaban model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public JawabanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rekap_jawaban, parent, false);
        return new JawabanViewHolder(view);
    }

    class JawabanViewHolder extends RecyclerView.ViewHolder {
        private final ItemRekapJawabanBinding binding;

        public JawabanViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRekapJawabanBinding.bind(itemView);
        }

        void bind(Jawaban jawaban) {
            binding.tvNamaMahasiswaJawaban.setText(jawaban.getUserName());

            if (jawaban.getSubmittedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                binding.tvWaktuKirim.setText("Dikirim: " + sdf.format(jawaban.getSubmittedAt()));
            } else {
                binding.tvWaktuKirim.setText("Waktu tidak tersedia");
            }

            binding.btnLihatJawaban.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jawaban.getFileUrl()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Tidak dapat membuka link jawaban.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}