# MyLearning E-Learning App

<p align="center">
  <img src="https://placehold.co/1200x400/10B981/FFFFFF?text=MyLearning+App&font=inter" alt="MyLearning Banner"/>
</p>

**MyLearning** adalah aplikasi e-learning berbasis Android yang dirancang untuk memfasilitasi kegiatan belajar mengajar antara dosen dan mahasiswa. Aplikasi ini menyediakan platform terpusat untuk mengelola jadwal, materi, tugas, dan absensi secara efisien dan *real-time*.

---

## ✨ Fitur Utama

Aplikasi ini dilengkapi dengan berbagai fitur untuk mendukung proses pembelajaran digital:

* **Login & Otentikasi**: Sistem login yang aman menggunakan Google Sign-In (Firebase Authentication).
* **Manajemen Jadwal**: Mahasiswa dapat melihat jadwal perkuliahan. Dosen (Admin) dapat menambah, mengedit, dan menghapus jadwal. Dilengkapi dengan pengingat notifikasi 1 jam sebelum kelas dimulai.
* **Manajemen Materi**: Dosen dapat mengunggah materi perkuliahan dalam bentuk link (misalnya Google Drive), dan mahasiswa dapat mengaksesnya kapan saja.
* **Manajemen Tugas**: Dosen dapat membuat tugas, dan mahasiswa dapat mengumpulkan jawaban dalam bentuk link. Dosen juga dapat merekapitulasi jawaban yang masuk.
* **Absensi Online**: Dosen dapat membuka sesi absensi dalam durasi tertentu. Mahasiswa dapat melakukan absensi (Hadir, Izin, Sakit) dan dosen dapat melihat rekapnya.
* **Profil Pengguna**: Pengguna dapat melihat dan mengedit informasi profil mereka, termasuk NIM, jurusan, dan data pribadi lainnya.
* **Sistem Berbasis Peran (Role)**: Aplikasi membedakan antara peran `mahasiswa` dan `admin` (dosen) untuk memberikan hak akses yang sesuai.

---

## 🛠️ Teknologi & Library

Proyek ini dibangun menggunakan teknologi modern dan library populer di ekosistem Android.

* **Bahasa Pemrograman**: Java
* **Backend & Database**: Firebase
    * **Firestore**: Sebagai database NoSQL utama untuk data dinamis.
    * **Authentication**: Untuk otentikasi pengguna melalui Google.
    * **Cloud Messaging**: Untuk mengirim notifikasi.
* **Database Lokal**: Room Persistence Library untuk menyimpan data jadwal secara offline.
* **Arsitektur**: Mengadopsi prinsip MVVM (Model-View-ViewModel) dengan Repository Pattern.
* **UI & Desain**:
    * Material Design 3
    * ViewBinding
    * RecyclerView dengan FirebaseUI Adapter
* **Library Pendukung**:
    * **Glide**: Untuk memuat gambar secara efisien.
    * **CircleImageView**: Untuk menampilkan gambar profil.

---

## 🚀 Setup & Instalasi

Untuk menjalankan proyek ini di lingkungan lokal Anda, ikuti langkah-langkah berikut:

1.  **Clone Repositori**
    ```bash
    git clone [https://github.com/kotkaaja/mylearningFinal.git](https://github.com/kotkaaja/mylearningFinal.git)
    ```

2.  **Buka di Android Studio**
    * Buka Android Studio.
    * Pilih `Open an Existing Project` dan arahkan ke direktori yang baru saja Anda clone.

3.  **Hubungkan ke Firebase**
    * Buka [Firebase Console](https://console.firebase.google.com/).
    * Buat proyek Firebase baru.
    * Tambahkan aplikasi Android ke proyek Firebase Anda dengan `package name`: `com.firebyte.elearning`.
    * Unduh file `google-services.json` dan letakkan di dalam direktori `app/`.
    * Di Firebase Console, aktifkan **Authentication** (dengan provider Google) dan **Firestore Database**.

4.  **Build & Jalankan**
    * Tunggu hingga Gradle selesai melakukan sinkronisasi.
    * Klik `Run 'app'` untuk membangun dan menjalankan aplikasi di emulator atau perangkat fisik.

---

## 🤝 Kontribusi

Dibuat dengan ❤️ oleh **FireByte Class - Kelompok 1**
