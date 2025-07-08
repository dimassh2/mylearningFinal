package com.firebyte.elearning.data.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Materi {
    private String mataKuliah;
    private String judul;
    private String dosen; // <-- FIELD BARU
    private String lampiranUrl;
    private String tipeLampiran;

    @ServerTimestamp
    private Date createdAt;

    public Materi() {}

    public Materi(String mataKuliah, String judul, String dosen, String lampiranUrl, String tipeLampiran) {
        this.mataKuliah = mataKuliah;
        this.judul = judul;
        this.dosen = dosen; // <-- Diinisialisasi
        this.lampiranUrl = lampiranUrl;
        this.tipeLampiran = tipeLampiran;
    }

    // Getters
    public String getMataKuliah() { return mataKuliah; }
    public String getJudul() { return judul; }
    public String getDosen() { return dosen; } // <-- GETTER BARU
    public String getLampiranUrl() { return lampiranUrl; }
    public String getTipeLampiran() { return tipeLampiran; }
    public Date getCreatedAt() { return createdAt; }
}