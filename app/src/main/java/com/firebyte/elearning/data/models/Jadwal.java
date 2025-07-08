package com.firebyte.elearning.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "jadwal_table")
public class Jadwal {

    @PrimaryKey
    @NonNull
    private String id = "";

    private String mataKuliah;
    private String dosen;
    private String hari;
    private String tanggal;
    private String waktu;
    private String ruang; // <-- INI YANG TERLEWAT SEBELUMNYA
    private Date createdAt;

    public Jadwal() {}

    public Jadwal(String mataKuliah, String dosen, String hari, String tanggal, String waktu, String ruang) {
        this.mataKuliah = mataKuliah;
        this.dosen = dosen;
        this.hari = hari;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.ruang = ruang;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getMataKuliah() { return mataKuliah; }
    public String getDosen() { return dosen; }
    public String getHari() { return hari; }
    public String getTanggal() { return tanggal; }
    public String getWaktu() { return waktu; }
    public String getRuang() { return ruang; }
    public Date getCreatedAt() { return createdAt; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setMataKuliah(String mataKuliah) { this.mataKuliah = mataKuliah; }
    public void setDosen(String dosen) { this.dosen = dosen; }
    public void setHari(String hari) { this.hari = hari; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public void setWaktu(String waktu) { this.waktu = waktu; }
    public void setRuang(String ruang) { this.ruang = ruang; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}