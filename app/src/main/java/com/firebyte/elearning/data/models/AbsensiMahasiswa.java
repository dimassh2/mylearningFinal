package com.firebyte.elearning.data.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class AbsensiMahasiswa {
    private String userId;
    private String namaUser;
    private String status;
    private String keterangan;

    @ServerTimestamp
    private Date timestamp;

    public AbsensiMahasiswa() {} // Wajib

    public AbsensiMahasiswa(String userId, String namaUser, String status, String keterangan) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.status = status;
        this.keterangan = keterangan;
    }

    // GETTER YANG BENAR
    public String getUserId() { return userId; }
    public String getNamaUser() { return namaUser; }
    public String getStatus() { return status; }
    public String getKeterangan() { return keterangan; }
    public Date getTimestamp() { return timestamp; }
}