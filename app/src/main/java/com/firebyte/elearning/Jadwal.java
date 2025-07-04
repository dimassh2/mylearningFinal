package com.firebyte.elearning;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Jadwal {
    // Properti harus public atau memiliki getter agar Firestore bisa mengaksesnya
    private String id;
    private String mataKuliah;
    private String dosen;
    private String hari; // Field baru untuk hari
    private String tanggal;
    private String waktu;
    private String ruang;

    @ServerTimestamp
    private Date createdAt;

    // Constructor kosong diperlukan oleh Firestore
    public Jadwal() {}

    // INI BAGIAN YANG DIPERBAIKI: Constructor sekarang menerima 6 parameter
    public Jadwal(String mataKuliah, String dosen, String hari, String tanggal, String waktu, String ruang) {
        this.mataKuliah = mataKuliah;
        this.dosen = dosen;
        this.hari = hari; // Menyimpan data hari
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.ruang = ruang;
    }

    // Getters
    public String getId() { return id; }
    public String getMataKuliah() { return mataKuliah; }
    public String getDosen() { return dosen; }
    public String getHari() { return hari; } // Getter untuk hari
    public String getTanggal() { return tanggal; }
    public String getWaktu() { return waktu; }
    public String getRuang() { return ruang; }
    public Date getCreatedAt() { return createdAt; }

    // Setter untuk ID
    public void setId(String id) { this.id = id; }
}
