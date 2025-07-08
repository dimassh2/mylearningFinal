package com.firebyte.elearning.data.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Absensi {
    private String userId;
    private String namaUser;
    private String status; // "Hadir", "Sakit", "Izin"
    @ServerTimestamp
    private Date tanggal;

    public Absensi() {} // Diperlukan oleh Firestore

    public Absensi(String userId, String namaUser, String status) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.status = status;
    }

    // --- GETTERS ---
    public String getUserId() { return userId; }
    public String getNamaUser() { return namaUser; }
    public String getStatus() { return status; }
    public Date getTanggal() { return tanggal; }
}