package com.firebyte.elearning;

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
import com.firebyte.elearning.databinding.ItemMateriBinding;
import com.google.firebase.firestore.DocumentSnapshot;

public class MateriAdapter extends FirestoreRecyclerAdapter<Materi, MateriAdapter.MateriViewHolder> {

    private final Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(DocumentSnapshot documentSnapshot);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MateriAdapter(@NonNull FirestoreRecyclerOptions<Materi> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MateriViewHolder holder, int position, @NonNull Materi model) {
        if (holder.getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
            DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
            holder.bind(model, snapshot);
        }
    }

    @NonNull
    @Override
    public MateriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_materi, parent, false);
        return new MateriViewHolder(view);
    }

    class MateriViewHolder extends RecyclerView.ViewHolder {
        private final ItemMateriBinding binding;

        public MateriViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMateriBinding.bind(itemView);
        }

        void bind(Materi materi, DocumentSnapshot snapshot) {
            // PERBAIKAN: Memberi nilai default jika data null untuk mencegah error
            binding.tvJudulMateri.setText(materi.getJudul() != null ? materi.getJudul() : "Tanpa Judul");
            binding.tvMatkulMateri.setText(materi.getMataKuliah() != null ? materi.getMataKuliah() : "Mata Kuliah Umum");
            binding.tvDosenMateri.setText("Dosen: " + (materi.getDosen() != null ? materi.getDosen() : "-"));

            binding.btnBukaMateri.setOnClickListener(v -> {
                String url = materi.getLampiranUrl();
                if (url != null && !url.isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, "Tidak dapat membuka link materi.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Tidak ada lampiran untuk materi ini.", Toast.LENGTH_SHORT).show();
                }
            });

            UserManager.checkAdminStatus(isAdmin -> {
                binding.deleteButtonMateri.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
                if (isAdmin) {
                    binding.deleteButtonMateri.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onDeleteClick(snapshot);
                        }
                    });
                }
            });
        }
    }
}