package com.firebyte.elearning;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserManager {

    // Interface untuk menangani hasil pengecekan yang tidak instan (asynchronous)
    public interface AdminCheckListener {
        void onResult(boolean isAdmin);
    }

    // Method baru untuk mengecek status admin dari Firestore
    public static void checkAdminStatus(AdminCheckListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            listener.onResult(false); // Jika tidak ada user, bukan admin
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Cek apakah field 'role' ada dan nilainya "admin"
                    String role = document.getString("role");
                    if (role != null && role.equals("admin")) {
                        listener.onResult(true); // Ini adalah admin
                    } else {
                        listener.onResult(false); // Bukan admin
                    }
                } else {
                    listener.onResult(false); // Dokumen tidak ditemukan
                }
            } else {
                listener.onResult(false); // Gagal mengambil data
            }
        });
    }
}
