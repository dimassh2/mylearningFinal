package com.firebyte.elearning;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JadwalRepository {

    private final JadwalDao jadwalDao;
    private final LiveData<List<Jadwal>> allJadwal;
    private final FirebaseFirestore db;
    private final ExecutorService executorService;

    public JadwalRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        jadwalDao = database.jadwalDao();
        allJadwal = jadwalDao.getAllJadwal();
        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Metode ini akan dipanggil oleh ViewModel untuk mendapatkan data
    public LiveData<List<Jadwal>> getAllJadwal() {
        return allJadwal;
    }

    // Metode untuk menyinkronkan data dari Firebase ke Room
    public void refreshJadwal() {
        db.collection("jadwal").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Jadwal> jadwalList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Jadwal jadwal = document.toObject(Jadwal.class);
                    jadwal.setId(document.getId()); // Penting: set ID dari dokumen
                    jadwalList.add(jadwal);
                }
                // Jalankan operasi database di thread terpisah
                executorService.execute(() -> {
                    jadwalDao.deleteAll(); // Hapus data lama
                    jadwalDao.insertAll(jadwalList); // Masukkan data baru
                });
            } else {
                Log.w("JadwalRepository", "Error getting documents.", task.getException());
            }
        });
    }
}