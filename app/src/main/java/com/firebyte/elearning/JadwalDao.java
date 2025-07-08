package com.firebyte.elearning;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface JadwalDao {

    /**
     * Memasukkan daftar jadwal ke dalam database.
     * Jika ada jadwal dengan ID (primary key) yang sama, data lama akan digantikan.
     * @param jadwalList Daftar objek Jadwal yang akan dimasukkan.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Jadwal> jadwalList);

    /**
     * Mengambil semua data jadwal dari tabel, diurutkan berdasarkan waktu pembuatan (terbaru dulu).
     * Menggunakan LiveData agar UI bisa otomatis diperbarui saat ada perubahan data.
     * @return LiveData yang berisi daftar semua Jadwal.
     */
    @Query("SELECT * FROM jadwal_table ORDER BY createdAt DESC")
    LiveData<List<Jadwal>> getAllJadwal();

    /**
     * Menghapus semua data dari tabel jadwal.
     * Berguna untuk membersihkan data lama sebelum melakukan sinkronisasi baru.
     */
    @Query("DELETE FROM jadwal_table")
    void deleteAll();
}