package com.firebyte.elearning;

public class User {
    private String uid;
    private String nama;
    private String email;
    private String fotoUrl;
    private String nim;
    private String jurusan;
    private String kelas;
    private String bio;
    private String alamat;
    private String role = "mahasiswa"; // Default role

    public User() {} // Diperlukan oleh Firestore

    public User(String uid, String nama, String email, String fotoUrl) {
        this.uid = uid;
        this.nama = nama;
        this.email = email;
        this.fotoUrl = fotoUrl;
        this.nim = "";
        this.jurusan = "";
        this.kelas = "";
        this.bio = "";
        this.alamat = "";
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getFotoUrl() { return fotoUrl; }
    public String getNim() { return nim; }
    public String getJurusan() { return jurusan; }
    public String getKelas() { return kelas; }
    public String getBio() { return bio; }
    public String getAlamat() { return alamat; }
    public String getRole() { return role; }
}