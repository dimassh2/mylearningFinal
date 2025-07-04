package com.firebyte.elearning;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Tugas {
    private String mataKuliah;
    private String deskripsi;
    private String lampiranUrl;
    private String tipeLampiran;

    @ServerTimestamp
    private Date createdAt;

    public Tugas() {}

    public Tugas(String mataKuliah, String deskripsi, String lampiranUrl, String tipeLampiran) {
        this.mataKuliah = mataKuliah;
        this.deskripsi = deskripsi;
        this.lampiranUrl = lampiranUrl;
        this.tipeLampiran = tipeLampiran;
    }

    // GETTER YANG BENAR
    public String getMataKuliah() { return mataKuliah; }
    public String getDeskripsi() { return deskripsi; }
    public String getLampiranUrl() { return lampiranUrl; }
    public String getTipeLampiran() { return tipeLampiran; }
    public Date getCreatedAt() { return createdAt; }
}