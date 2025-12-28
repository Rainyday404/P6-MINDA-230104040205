# Proyek Aplikasi MINDA - Catatan Jurnal Lokal

Aplikasi **MINDA** adalah proyek praktikum mata kuliah **Mobile Programming** yang berfokus pada implementasi database lokal menggunakan **Room ORM** dan penyimpanan preferensi pengguna dengan **DataStore**. Aplikasi ini dirancang menggunakan **Jetpack Compose** untuk antarmuka pengguna modern dan navigasi yang dinamis.

## 👤 Identitas Mahasiswa

* **Nama:** Ivan Dwika Bagaskara (Hujan/Rain)
* **NIM:** 230104040205
* **Role:** Mahasiswa
* **Mata Kuliah:** Mobile Programming 20251

---

## 📝 Deskripsi Proyek

Proyek ini (Modul 6) bertujuan untuk membangun aplikasi jurnal harian yang memprioritaskan privasi (*privacy-by-design*), di mana seluruh data disimpan secara offline di perangkat pengguna. Fitur utama mencakup alur *onboarding* pengguna baru, penyimpanan nama pengguna, serta fungsi CRUD (Create, Read, Update, Delete) untuk catatan jurnal.

### Fitur Utama:

1. **Multi-step Onboarding:** Alur pengenalan aplikasi mulai dari *Welcome Screen* hingga input nama pengguna.
2. **Local Data Persistence:** Menggunakan **Room Database (SQLite)** untuk mengelola data jurnal.
3. **User Preferences:** Menyimpan nama pengguna dan status *onboarding* menggunakan **Jetpack DataStore**.
4. **Modern UI/UX:** Dibangun sepenuhnya menggunakan **Jetpack Compose** tanpa XML legacy.
5. **Reactive Navigation:** Perpindahan antar layar (Home, Detail, Edit, Calendar) menggunakan **Navigation Compose**.

---

## 🛠️ Teknologi yang Digunakan

* **Bahasa Pemrograman:** Kotlin
* **UI Framework:** Jetpack Compose
* **Database:** Room Persistence Library (SQLite)
* **Data Storage:** Jetpack DataStore (Preferences)
* **Architecture:** Repository Pattern
* **Tools:** Android Studio

---

## 📂 Struktur Proyek

Sesuai dengan panduan praktikum, struktur utama aplikasi mencakup:

* **Data Layer:** Entity, DAO, dan AppDatabase untuk Room.
* **Repository:** Sebagai jembatan antara sumber data dan UI.
* **UI Layer:** Composable functions untuk setiap screen (Onboarding, Home, Journal, dsb).
* **Navigation:** `AppNavHost` untuk mengatur logika perpindahan layar.

---

## 📸 Tampilan Aplikasi

1. **Onboarding 1-4:** Perkenalan dan Input Nama.
2. **HomeScreen:** Menampilkan daftar jurnal dan sapaan personal ("Hi, Rain!").
3. **Journal Screen:** Form untuk menambah atau mengubah catatan.

---

## ⚙️ Cara Menjalankan

1. Clone repository ini.
2. Buka proyek menggunakan **Android Studio** (Versi terbaru disarankan).
3. Tunggu proses *Gradle Sync* selesai.
4. Jalankan aplikasi pada Emulator atau Perangkat Android fisik.

---

> **Catatan:** Seluruh data jurnal tersimpan hanya di perangkat (offline) untuk memastikan keamanan dan privasi data pengguna.

---
