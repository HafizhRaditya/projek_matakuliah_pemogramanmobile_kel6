# Pembagian Tugas Menuju UTS

**Kelompok 6 · WaktuKu** · keadaan per 16 September 2026, diperbarui
17 September 2026 dengan ketentuan teknis project dari dosen (bagian 3), dan
1 Oktober 2026 dengan keputusan lingkup UTS (bagian 3, "Status 1 Oktober 2026"),
dan 3 Oktober 2026 dengan ketentuan ujian lisan dan perbaikan bug (bagian 3,
"Status 3 Oktober 2026")

Dokumen ini menjawab tiga hal: siapa memegang bagian mana, apa yang masih
harus dikerjakan, dan apa yang harus dikuasai tiap orang saat ditanya dosen.

---

## 1. Anggota dan wilayah kode

| Kode | Nama | NIM | Peran | Folder dan berkas yang dipegang |
|---|---|---|---|---|
| **M1** | Hafizh Naufal Raditya | H1D024061 | UI/UX (Jetpack Compose), pemilik repo | `ui/screens/`, `ui/components/`, `ui/theme/`, `res/drawable/`, `res/values/` |
| **M2** | Biladi Amna | H1D024074 | State & logika (ViewModel) | `ui/viewmodel/`, `app/src/test/` |
| **M3** | Muhammad Abu Umar | H1D024084 | Penyimpanan lokal (Room) | `model/`, `data/`, `app/schemas/`, `app/src/androidTest/` |
| **M4** | Afkar Aufaa Farros | H1D024085 | Navigasi & integrasi sistem | `ui/navigation/`, `ui/WaktuKuApp.kt`, `MainActivity.kt`, `WaktuKuApplication.kt`, `AndroidManifest.xml`, `notification/`\*, `data/preferences/`\* |

\* Belum ada. Kedua folder ini milik F4 dan F7 yang ditunda sampai setelah UTS.

Kode M1–M4 sama dengan komentar `PENANGGUNG JAWAB: Mahasiswa N` di kepala
setiap berkas, jadi tidak ada berkas yang perlu diubah. Urutan M2–M4 mengikuti
urutan nama. Boleh ditukar, asal sebelum mulai mengerjakan.

Beban kode yang harus dikuasai per 3 Oktober 2026:

| | Berkas | Baris |
|---|---|---|
| M1 | 8 | 2.370 |
| M2 | 6 (3 di antaranya berkas uji) | 1.705 |
| M3 | 10 (termasuk `MigrationTest`) | 823 |
| M4 | 6 + manifest | 498 |

F4 dan F6 ditunda sampai setelah UTS, jadi beban ini tidak bertambah lagi
sebelum pengumpulan. Yang perlu dikejar sekarang adalah pemahaman, bukan baris
kode baru.

---

## 2. Kenapa dibagi per folder

Kriteria penilaian UTS dari eLDirU:

| Komponen | Jenis | Bobot |
|---|---|---|
| Video demo aplikasi | Kelompok | 20% |
| Source code & struktur proyek | Kelompok | 16% |
| Ketepatan waktu pengumpulan | Kelompok | 4% |
| **Pemahaman kode** | **Individu** | **30%** |
| Evaluasi & tanya jawab | Individu | 18% |
| Penyampaian & komunikasi | Individu | 12% |

**60% nilai bersifat individu**, dan dosen akan menanyakan source code serta
struktur folder secara langsung. Maka:

1. Setiap orang harus bisa menjelaskan **foldernya sendiri** sampai ke alasan
   tiap keputusan. Sejak 3 Oktober diketahui bahwa **bagian kode yang
   ditanyakan dipilih dosen dari seluruh proyek**, jadi folder anggota lain
   juga wajib dipelajari (lihat bagian 7).
2. **Semua orang** harus bisa menjelaskan struktur folder secara keseluruhan
   (tabel di atas) dan satu alur data utuh dari layar sampai database
   ([DOKUMENTASI.md bagian 3](../DOKUMENTASI.md#3-arsitektur-aplikasi)).
3. **Commit dari akun masing-masing.** Per 3 Oktober 2026 Biladi, Abu Umar,
   dan Afkar sudah punya commit dari akunnya sendiri. Riwayat Git adalah bukti siapa mengerjakan apa.

---

## 3. Posisi proyek sekarang

| Fitur | Prioritas | Status |
|---|---|---|
| F1 Beranda | P0 | **Selesai** di `main` |
| F2 Navigasi | P0 | **Selesai** di `main`, sudah Type-Safe Navigation. Dua uji di HP belum dilakukan (tombol kembali, rotasi layar) |
| F3 Timer Pomodoro | P0 | **Selesai** di `main` dengan 12 uji unit, termasuk mode demo (B-1) |
| F4 Notifikasi | P0 | **Ditunda** sampai setelah UTS |
| F5 Detail / edit tugas | P1 | **Selesai** di `main` dengan 6 uji unit. Belum diuji di HP |
| F6 Statistik | P2 | **Ditunda** sampai setelah UTS |
| F7 Pengaturan | P3 | **Ditunda** sampai setelah UTS |

Keadaan `main` per 4 Oktober 2026: `assembleDebug` berhasil dan 24 uji unit
lulus.

### Ketentuan teknis dari dosen

Aplikasi wajib menerapkan **minimal 5 dari 7** materi Native Android dengan
Jetpack Compose. Hasil pengecekan kode per 1 Oktober 2026:

| # | Materi | Status sekarang | Ditutup oleh |
|---|---|---|---|
| 1 | UI & Layout Dasar (Column, Row, Box, Modifier) | **Terpenuhi** | — |
| 2 | Material Design 3 (Color, Typography, Button, OutlinedTextField, Card) | **Terpenuhi**: palet warna sendiri, komponen M3, dan `Type.kt` berisi 11 token | H-7 ✅ |
| 3 | State Management & UDF (`remember`, `rememberSaveable`, state hoisting, UDF) | **Terpenuhi**: `rememberSaveable` di `HomeScreen`, `AddTaskDialog`, dan `TaskDetailScreen` | H-8 ✅ |
| 4 | Lazy Layouts (`LazyColumn`/`LazyGrid` + `key`) | **Terpenuhi**: `LazyColumn` dengan `key = { task.id }` | — |
| 5 | Networking & API (Retrofit/Ktor) | **Tidak dikerjakan.** WaktuKu aplikasi luring; sinkronisasi cloud di luar lingkup ([PRD bagian 4.3](../PRD.md)) | — |
| 6 | Arsitektur MVVM (`ViewModel` + UiState Loading/Success/Error) | **Terpenuhi**: `HomeUiState` dan `TaskDetailUiState` berpola Loading/Success/Error, dengan `.catch` | B-8 ✅, H-9 ✅, B-3 ✅ |
| 7 | Navigation Compose (min. 3 layar, Type-Safe Navigation, kirim data, BottomNavigation/Scaffold) | **Terpenuhi**: rute `@Serializable` + `toRoute()`; tiga layar: Beranda, Fokus, Detail Tugas | A-6 ✅, H-2 ✅, A-3 ✅ |

**Tercapai: 6 dari 7** (materi 1, 2, 3, 4, 6, 7), semuanya sudah di `main`.
Satu materi cadangan di atas syarat minimal, tanpa mengubah konsep luring di
PRD.

---

### Status 29 September 2026

Tugas bertanda ✅ di tabel atas dikerjakan dalam lima cabang berikut. Semuanya
**sudah di-merge ke `main`** pada 1 Oktober 2026 (PR #8 sampai #12), dengan
urutan ini karena cabangnya bertumpuk:

| Urutan | Cabang | Isi |
|---|---|---|
| 1 | `feat/h7-tipografi` | H-7 |
| 2 | `feat/h8-remembersaveable` | H-8 |
| 3 | `feat/b8-h9-uistate` | B-8, H-9 |
| 4 | `feat/a6-typesafe-nav` | A-6 |
| 5 | `feat/b3-h2-detail-tugas` | B-3, H-2, A-3 |

Kelima cabang sudah diuji digabung bersamaan: tanpa konflik, `assembleDebug`
berhasil, dan 20 uji unit lulus.

> **Penting untuk UTS.** B-8, B-3, A-6, dan A-3 dikerjakan Hafizh dengan
> bantuan AI (Claude) supaya aplikasi memenuhi syarat dosen tepat waktu.
> Kodenya tetap menjadi **tanggung jawab pemilik folder**: Biladi wajib
> menguasai `TaskViewModel.kt` dan `TaskDetailViewModel.kt` beserta ujinya,
> Afkar wajib menguasai seluruh `ui/navigation/`. Setiap berkas sudah diberi
> komentar penjelas. Bagian ini juga wajib ditulis jujur di Formulir
> Deklarasi Penggunaan AI (B-7).

### Status 1 Oktober 2026

**Tenggat: Senin, 5 Oktober 2026 pukul 16.00**, untuk tautan source code dan
Formulir Deklarasi Penggunaan AI di eLDirU.

**Lingkup dipotong.** F4 Notifikasi, F6 Statistik, dan F7 Pengaturan ditunda
sampai setelah UTS. Tab Statistik, ikon Pengaturan, dan `PlaceholderScreen`
sudah dihapus dari aplikasi, jadi yang didemokan hanya layar yang benar-benar
jadi: Beranda, Fokus, dan Detail Tugas. Alasan lengkapnya ada di
[PRD bagian 9](../PRD.md#9-prioritas-dan-urutan-potong). Ringkasnya: ketiganya
belum dimulai, memakai hal di luar materi kuliah sampai Pertemuan 6
(`AlarmManager`, `BroadcastReceiver`, DataStore, `Canvas`), dan UTS berbentuk
tanya jawab lisan tentang kode sendiri.

Tugas yang ikut ditunda: H-3, H-5, B-2, B-4, B-5, U-3, U-4, U-5, A-2, A-4.

**Sisa pekerjaan sampai tenggat:**

| Siapa | Tugas | Catatan |
|---|---|---|
| Hafizh | H-1, H-4, H-6 | H-6 dikerjakan terakhir, setelah video jadi |
| Biladi | **B-1**, B-6, B-7 | B-1 (mode demo) **menahan video**: tanpa itu sesi 25 menit tidak bisa ditunjukkan selesai |
| Abu Umar | U-1, U-6, U-7 | U-6: `DOKUMENTASI.md` bagian 2 dan 8 sudah usang |
| Afkar | A-1, A-5 | Kirim NIM untuk README, dokumen ini, dan formulir deklarasi |
| Semua | Terima undangan *collaborator*, `git clone`, build di laptop sendiri | Lalu latihan tanya jawab memakai bagian 7 |

**Yang belum pernah diuji di HP:** layar Detail Tugas, navigasi setelah migrasi
Type-Safe (A-1), dan `MigrationTest` (U-1).

### Status 3 Oktober 2026

**Ketentuan proses UTS dari dosen:**

- UTS **bukan kegiatan kelompok** dan **bukan demo aplikasi**
- Ujian lisan: setiap orang menjelaskan bagian kode yang dibuat bersama
  kelompok, dan **bagian kodenya dipilih dosen**
- Tidak ada sesi tanya jawab saat proses UTS

Artinya setiap orang perlu memahami **seluruh** kode, bukan hanya foldernya.
Daftar di bagian 7 tetap berguna sebagai urutan belajar.

**Bug yang diperbaiki** (cabang `fix/uts-final`):

| Bug | Penyebab | Perbaikan |
|---|---|---|
| Tab Beranda tidak bisa dibuka dari layar Fokus (laporan Afkar) | `saveState`/`restoreState` menyimpan layar Fokus atas nama Beranda, lalu memulihkannya saat tab Beranda ditekan | `navigateToTab` hanya memakai `popUpTo` + `launchSingleTop` |
| Timer hilang saat tombol kembali ditekan di layar Fokus, dan bisa ada dua timer berjalan | `PomodoroViewModel` terikat pada layar Fokus | `PomodoroViewModel` dibuat sekali di `WaktuKuNavHost`, milik Activity |
| Sesi bisa tercatat atas nama tugas lain | Tugas di timer bisa diganti di tengah sesi | `pilihTugas` diabaikan selama sesi berjalan atau dijeda |
| Aplikasi tertutup paksa bila tugas yang sedang di timer dihapus | Foreign key menolak sesi untuk tugas yang sudah tidak ada | Timer mengamati tugasnya dan kosong kembali bila tugas dihapus |
| Layar kosong bila tombol kembali di Detail terpanggil dua kali | `popBackStack()` kedua membuang Beranda | `popBackStack(HomeRoute, inclusive = false)` |
| Tugas baru langsung mulai di "sesi 4 dari 4" (audit akhir) | Hitungan siklus tugas sebelumnya terbawa | `pilihTugas` membuat `TimerUiState` baru |
| Tidak ada penjelasan saat tombol putar tugas lain ditekan di tengah sesi (audit akhir) | Tugas memang dikunci selama sesi | Layar Fokus menampilkan pesan; tugas yang dipilih dipasang begitu sesi dihentikan |
| Tanggal tenggat bisa mundur sehari di zona waktu negatif (audit akhir) | DatePicker menyimpan 00.00 UTC, ditampilkan dengan zona HP | Tanggal dibaca dengan zona UTC |
| Filter Belum selesai menampilkan "Semua tugas sudah selesai" saat belum ada tugas (audit akhir) | Pesan hanya melihat filter | Pesan juga melihat jumlah tugas |
| Layar Fokus terpotong dan tidak bisa digulir saat HP landscape (laporan Afkar) | Kolom isi layar tidak diberi `verticalScroll` | Layar Fokus dan dialog tambah tugas bisa digulir |

**Sudah selesai sejak status 1 Oktober:** B-1 mode demo (Biladi), U-6
`DOKUMENTASI.md` (Abu Umar, fakta-faktanya sudah dicocokkan dengan kode), NIM
Afkar (Afkar), H-1 dan H-4 (Hafizh). Uji unit kini 24, semua lulus (per audit akhir 3 Oktober).

**Sisa pekerjaan sampai Senin 5 Oktober 16.00:**

| Siapa | Tugas | Catatan |
|---|---|---|
| Afkar | A-5 video demo, A-1 uji ulang di HP | Video direkam dengan `MODE_DEMO = true` di `WaktuKuNavHost.kt` (fokus 5 detik), lalu dikembalikan ke `false` |
| Abu Umar | U-7 unggah teks forum, U-1 `MigrationTest` | Teks forum diunggah bersama tautan video |
| Biladi | B-7 Formulir Deklarasi AI | Isi jujur, termasuk bagian yang dikerjakan dengan bantuan AI |
| Hafizh | H-6 tangkapan layar, unggah tautan repo | Setelah video jadi |
| Semua | Pelajari seluruh kode | Mulai dari bagian 7, lalu alur data di `DOKUMENTASI.md` bagian 3 |

### Petunjuk tugas mandiri sebelum tenggat

Tugas di bawah dikerjakan sendiri oleh pemiliknya dan di-push dari akun
masing-masing. Bila memakai alat bantu AI, cantumkan di Formulir Deklarasi AI.

**Sebelum mulai (semua orang):**

```bash
git checkout main
git pull origin main
git log --oneline -1
```

Baris terakhir harus menampilkan commit terbaru di GitHub. Cabang lama seperti
`docs/u6-sinkronisasi-dokumentasi` sudah digabung dan dihapus; `git pull` dari
cabang itu tidak membawa perubahan apa pun. Tanda aplikasi versi terbaru: di
layar Fokus, di bawah empat titik, ada tulisan "Istirahat panjang setelah 4
sesi fokus".

**Abu Umar — U-1, menjalankan `MigrationTest`**

1. `git checkout -b test/u1-migration-test`
2. Sambungkan HP dengan USB debugging, lalu jalankan
   `./gradlew :app:connectedDebugAndroidTest` (di Windows: `gradlew.bat`).
   Perintah ini memasang aplikasi uji lalu mencopotnya lagi, sehingga data
   WaktuKu di HP itu bisa terhapus. Jalankan sebelum mengisi data untuk video.
3. Buka laporannya di `app/build/reports/androidTests/connected/`.
4. Catat hasilnya (tanggal, tipe HP, jumlah uji yang lulus) di
   `DOKUMENTASI.md` bagian 6, pada baris "Validasi Migrasi Room".
5. Commit, push, lalu buka Pull Request. Bila ujinya gagal, kirim pesan
   galatnya ke grup; jangan diubah menjadi "lulus".

*Opsional untuk Abu Umar:* uji DAO di `app/src/androidTest/`, memakai
`Room.inMemoryDatabaseBuilder`. Tiga hal yang layak diuji: tugas yang ditambah
muncul di `observeAll()`; menghapus tugas ikut menghapus sesinya (CASCADE);
`insertCompletedSession` menambah `completed_pomodoros`.

**Afkar — A-1, uji navigasi di HP**

1. Dari layar Fokus, tekan tab Beranda: harus pindah ke Beranda.
2. Mulai sesi, tekan tombol kembali, lalu buka tab Fokus: timer harus masih
   berjalan.
3. Putar HP di layar Fokus dan di layar Detail: harus tetap di layar yang sama.
4. Centang hasilnya di [PRD bagian F2](../PRD.md), commit di cabang
   `docs/a1-uji-navigasi`, lalu buka Pull Request.

*Opsional untuk Afkar:* ikon aplikasi masih ikon Android bawaan. Di Android
Studio: klik kanan folder `app` → New → Image Asset → Launcher Icons, pilih
gambar dan warna latar, Finish. Kerjakan di cabang `feat/ikon-aplikasi`.

**Biladi — B-7, Formulir Deklarasi AI**

Draf formulir sudah ada di Hafizh. Lengkapi bagian dalam kurung siku (alat AI
yang dipakai tiap anggota), minta setiap anggota membaca isinya, lalu unggah
ke eLDirU.

**Aturan yang tetap berlaku:** jangan `git push --force`, jangan push langsung
ke `main`, dan jangan meng-commit `MODE_DEMO = true`.

## 4. Tugas per orang

Setiap tugas punya kode (H, B, U, A) supaya mudah disebut di grup dan dipakai
sebagai nama cabang, misalnya `feat/b1-mode-demo`.

Tanda di kolom kode: **✅** selesai dan sudah di `main` · **⏸** ditunda sampai
setelah UTS · tanpa tanda berarti masih harus dikerjakan sebelum tenggat.

### M1 · Hafizh — UI

| Kode | Fitur | Tugas | Menunggu |
|---|---|---|---|
| H-0 ✅ | — | Gabungkan PR `design/tema-dan-perbaikan-ui` dan PR `feat/f3-timer`. Undang Biladi, Abu Umar, dan Afkar sebagai *collaborator* | — |
| H-1 ✅ | F3 | Rapikan `TimerScreen`: ikon Jeda dan Hentikan (Vector Asset, seperti ikon tab), angka timer lebih besar, warna berbeda untuk fokus dan istirahat, keterangan "istirahat panjang setelah 4 sesi" | H-0 |
| H-2 ✅ | F5 | `TaskDetailScreen`: judul, catatan, chip prioritas, `DatePicker` tenggat, tombol −/+ target sesi, riwayat sesi, Simpan, Hapus dengan dialog konfirmasi | B-3 |
| H-3 ⏸ | F6 | `StatsScreen`: kartu total sesi dan menit hari ini, diagram batang 7 hari digambar dengan `Canvas`, tampilan kosong | B-4 |
| H-4 ✅ | — | Layar pembuka ikut mode gelap (`themes.xml`), centang pada chip prioritas di `AddTaskDialog` | — |
| H-5 ⏸ | F7 | *Opsional:* `SettingsScreen` | A-4 |
| H-6 | UTS | Tangkapan layar setiap layar (terang dan gelap). Sebagai perwakilan, unggah tautan repo dan tangkapan layar ke eLDirU | semua |
| H-7 ✅ | Materi 2 | Tulis `Type.kt` sungguhan: minimal `headlineMedium`, `titleLarge`, `titleMedium`, `bodyLarge`, `bodyMedium`, `labelLarge`, `labelSmall` (fontFamily, fontWeight, fontSize, lineHeight, letterSpacing). Tidak ada `fontSize` hardcoded di layar | H-0 |
| H-8 ✅ | Materi 3 | Pakai `rememberSaveable` untuk state UI yang harus bertahan saat layar diputar: `showAddDialog` di `HomeScreen`, serta `title`, `priority`, `sudahDisentuh` di `AddTaskDialog`. Perbarui komentar di `AddTaskDialog` yang menyebut `rememberSaveable` tidak dipakai. Uji: putar HP saat dialog terbuka, isian harus tetap ada | H-0 |
| H-9 ✅ | Materi 6 | `HomeScreen` membaca UiState baru dari B-8: `when (uiState)` untuk Loading, Success, dan Error. Tambah tampilan `ErrorState` dan perbarui Preview | B-8 |

H-2 juga memenuhi syarat **minimal 3 layar** pada materi 7, jadi layar Detail
tidak boleh dipotong lagi seperti rencana potong di PRD bagian 9.

Layar bisa mulai dibangun lewat `@Preview` dengan UiState buatan begitu M2
menetapkan bentuk data class-nya. Tidak perlu menunggu ViewModel selesai.

### M2 · Biladi — ViewModel

| Kode | Fitur | Tugas | Menunggu |
|---|---|---|---|
| B-1 ✅ | F3 | **Mode demo:** durasi fase bisa dipersingkat (misal 10 detik). Wajib untuk video. Sekarang 25/5/15 menit tertulis tetap di enum `PomodoroPhase` | H-0 |
| B-2 ⏸ | F3 | Simpan `targetEndMillis` ke `SavedStateHandle` supaya timer tidak hilang saat Android mematikan proses aplikasi (batasan ini tercatat di PRD bagian F3) | H-0 |
| B-3 ✅ | F5 | `TaskDetailViewModel`: muat tugas lewat `observeTask(id)`, state form, validasi judul kosong, simpan, hapus. **Tetapkan `TaskDetailUiState` di hari pertama** supaya H-2 bisa jalan. Pakai pola Loading/Success/Error seperti B-8 | — |
| B-4 ⏸ | F6 | `StatsViewModel`: gabungkan query menjadi `StatsUiState`. **Tetapkan bentuknya di hari pertama**. Pakai pola Loading/Success/Error seperti B-8 | U-3 |
| B-5 ⏸ | F4 | Panggil penjadwal notifikasi dari `PomodoroViewModel`: jadwalkan saat Mulai/Lanjut, batalkan saat Jeda/Hentikan | A-2, U-5 |
| B-6 | — | Uji unit untuk B-1 sampai B-4. `./gradlew :app:testDebugUnitTest` harus hijau sebelum push | — |
| B-7 | UTS | Koordinator **Formulir Deklarasi Penggunaan AI**: kumpulkan isian dari semua anggota, lalu unggah | semua |
| B-8 ✅ | Materi 6 | Ubah `HomeUiState` menjadi pola **Loading / Success / Error** (rincian di bawah). Perbarui `TaskViewModelTest` dan tambah satu uji untuk keadaan Error. **Tetapkan bentuknya di hari pertama** supaya H-9 bisa jalan | H-0 |

Rincian B-8:

```kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val tasks: List<Task>,
        val filter: TaskFilter,
        val totalCount: Int,
        val doneCount: Int,
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
```

- Nilai awal `stateIn` adalah `HomeUiState.Loading`
- Tambahkan `.catch { emit(HomeUiState.Error("Gagal memuat tugas")) }` sebelum
  `stateIn`, supaya kegagalan membaca Room tampil sebagai pesan, bukan crash
- Kerjakan di atas `main` setelah H-0, karena PR desain sudah mengubah
  `TaskViewModel` (penghapusan dengan Urungkan)

### M3 · Abu Umar — Data

| Kode | Fitur | Tugas | Menunggu |
|---|---|---|---|
| U-1 | — | Jalankan `MigrationTest` dengan HP tersambung: `./gradlew :app:connectedDebugAndroidTest`. Uji ini sudah ditulis tapi **belum pernah dijalankan** | — |
| U-2 | F5 | Periksa `observeTask` dan `observeSessionsForTask` dengan data contoh, termasuk urutan riwayat sesinya | — |
| U-3 ⏸ | F6 | Query rekap sesi **per hari** untuk 7 hari terakhir. Yang ada sekarang baru total per rentang waktu (`observeCompletedCount`, `observeTotalMinutes`) | — |
| U-4 ⏸ | F6 | **Putuskan:** "tugas selesai minggu ini" butuh kolom `completed_at` yang belum ada di `Task`. Pilih: tambah kolom lewat Migration 2→3 beserta ujinya, atau ubah kriterianya menjadi "total tugas selesai" dan catat di PRD | — |
| U-5 ⏸ | F4 | Pasang penjadwal notifikasi buatan A-2 di `AppContainer` | A-2 |
| U-6 ✅ | — | Perbarui [DOKUMENTASI.md](../DOKUMENTASI.md) bagian 2 dan 8. Daftar berkas dan daftar "yang belum dikerjakan" di sana sudah usang | — |
| U-7 | UTS | Teks forum eLDirU: perkenalan kelompok dan peran (tabel bagian 1), latar belakang ([PRD bagian 2](../PRD.md)), deskripsi aplikasi ([PRD bagian 1](../PRD.md)) | — |

### M4 · Afkar — Navigasi & sistem

| Kode | Fitur | Tugas | Menunggu |
|---|---|---|---|
| A-1 | F2 | Uji di HP: (1) pindah Beranda → Fokus, tekan kembali sekali, harus kembali ke Beranda dan timer tetap berjalan; (2) dari layar Fokus, tekan tab Beranda, harus pindah ke Beranda; (3) putar HP di layar Fokus, harus tetap di layar Fokus. Centang di PRD | — |
| A-2 ⏸ | F4 | **Notifikasi** — rincian di bawah | — |
| A-3 ✅ | F5, F6 | Ganti placeholder Detail dan Statistik (dan Pengaturan bila F7 dikerjakan) di `WaktuKuNavHost` dengan layar sungguhan. Hapus `PlaceholderScreen` setelah semuanya terganti, bersama M1 | H-2, H-3, A-6 |
| A-4 ⏸ | F7 | *Opsional:* DataStore untuk Pengaturan di `data/preferences/` (perlu dependensi baru) | — |
| A-5 | UTS | Rekam dan sunting **video demo** sesuai [skenario PRD bagian 11](../PRD.md). Setiap anggota menarasikan bagiannya sendiri | B-1, semua |
| A-6 ✅ | Materi 7 | **Migrasi ke Type-Safe Navigation** — rincian di bawah. Kerjakan **sebelum A-3**, supaya rute layar baru langsung memakai cara baru | H-0 |

Rincian A-2:

- `NotificationChannel` dibuat di `WaktuKuApplication`
- Izin `POST_NOTIFICATIONS` ditulis di manifest dan diminta saat runtime
  (Android 13 ke atas; HP uji kita Android 16)
- Aplikasi tetap berjalan wajar bila izin ditolak
- Notifikasi dijadwalkan dengan `AlarmManager` + `BroadcastReceiver` pada
  waktu `targetEndMillis`, dan dibatalkan saat sesi dijeda atau dihentikan
- Mengetuk notifikasi membuka layar Timer

> **Jangan memicu notifikasi dari ticker di ViewModel.** Ticker ikut tertidur
> saat layar mati, sehingga notifikasi baru muncul ketika HP dinyalakan lagi.
> Alarm sistem tetap berjalan walau aplikasi tidak aktif.

Rincian A-6:

- Tambah plugin `org.jetbrains.kotlin.plugin.serialization` (versi sama dengan
  Kotlin di `libs.versions.toml`) dan library `kotlinx-serialization-json`.
  Navigation Compose 2.10.0 yang dipakai sekarang sudah mendukung cara ini
- Ganti `WaktuKuRoutes` (rute berupa teks) dengan objek rute:

```kotlin
@Serializable data object Home
@Serializable data class Timer(val taskId: Long = -1L)   // -1 = belum memilih tugas
@Serializable data class TaskDetail(val taskId: Long)
@Serializable data object Stats
@Serializable data object Settings
```

- Di `WaktuKuNavHost`: `composable<TaskDetail> { entry -> val rute = entry.toRoute<TaskDetail>() }`
- Berpindah layar: `navController.navigate(TaskDetail(taskId = id))`
- Tab aktif di `WaktuKuBottomBar`: `destination.hierarchy.any { it.hasRoute(Home::class) }`
- Nilai bawaan `taskId = -1L` sama dengan `NO_TASK_ID` sekarang, jadi
  `TimerScreen` dan `PomodoroViewModel` tidak perlu diubah
- Uji ulang A-1 (tombol kembali dan rotasi layar) setelah migrasi

---

## 5. Titik temu antar anggota

Tiga fitur melewati lebih dari satu folder. Urutan di bawah mencegah orang
saling menunggu tanpa tahu.

| Fitur | Urutan |
|---|---|
| F4 Notifikasi | **Ditunda.** A-2 membuat penjadwal → U-5 memasangnya di `AppContainer` → B-5 memanggilnya dari `PomodoroViewModel` |
| F5 Detail | B-3 menetapkan `TaskDetailUiState` → H-2 membangun layar (mulai dari Preview) → A-3 menyambungkan rute. U-2 dikerjakan di awal |
| F6 Statistik | **Ditunda.** U-3 dan U-4 menyiapkan query → B-4 menetapkan `StatsUiState` → H-3 membangun layar → A-3 menyambungkan rute |
| Materi 6 UiState | B-8 menetapkan `HomeUiState` baru → H-9 menyesuaikan `HomeScreen`. Keduanya digabung dalam waktu berdekatan supaya build tidak rusak lama |
| Materi 7 Type-safe | A-6 memigrasi rute → A-3 menyambungkan layar baru dengan objek rute. Layar (H-2, H-3) tidak terpengaruh karena hanya menerima lambda |

`AppContainer.kt` (M3) dan `WaktuKuNavHost.kt` (M4) adalah dua berkas yang
paling sering disentuh orang lain. **Bilang dulu di grup** sebelum mengubahnya.

---

## 6. Urutan kerja

> **Tenggat pengumpulan:** Senin, 5 Oktober 2026 pukul 16.00 (eLDirU,
> Pertemuan 8). Urutan di bawah adalah rencana awal. Sisa pekerjaan yang
> berlaku sekarang ada di bagian 3, "Status 1 Oktober 2026".

1. **Hari ini** — H-0: gabungkan dua PR dan undang collaborator. Semua orang
   `git clone`, lalu `./gradlew :app:assembleDebug` harus hijau di laptop
   masing-masing.
2. **P0 lengkap** — B-1, B-2, A-1, A-2, U-1, U-5, B-5, H-1. Tanpa ini aplikasi
   bukan WaktuKu.
3. **Ketentuan teknis** — dikerjakan bersamaan dengan langkah 2: H-7, H-8, B-8
   lalu H-9, dan A-6. Tanpa ini project tidak memenuhi syarat 5 dari 7 materi.
4. **F5 dan F6** — U-2, U-3, U-4, B-3, B-4, H-2, H-3, A-3. H-2 wajib selesai
   (syarat minimal 3 layar).
5. **F7 dan polesan** — H-4, lalu H-5 dan A-4 bila waktu cukup. Bila mepet,
   potong dari bawah: F7 → F6. F1–F5 dan ketentuan teknis tidak boleh dipotong.
6. **Pengumpulan** — jalankan skenario demo PRD bagian 11 sampai tanpa error,
   lalu A-5 (video), H-6 (tangkapan layar dan tautan repo), U-7 (teks forum),
   B-7 (deklarasi AI).
7. **Latihan tanya jawab** — setiap orang menjelaskan foldernya kepada tiga
   orang lainnya, lalu gantian bertanya memakai daftar di bagian 7.

---

## 7. Persiapan tanya jawab

Bahan jawaban tersedia di komentar setiap berkas, serta di
[DOKUMENTASI.md](../DOKUMENTASI.md) bagian 4 dan 10.

**Wajib dikuasai semua orang** (bagian kode dipilih dosen dari seluruh proyek,
jadi daftar pertanyaan anggota lain juga perlu dibaca):

- Struktur folder dan pemilik setiap folder (bagian 1)
- Alur data saat pengguna mencentang tugas, dari `TaskCard` sampai Room lalu
  kembali ke layar
- Beda MVVM dengan MVC

### M1 · Hafizh

Berkas: `HomeScreen.kt`, `TimerScreen.kt`, `TaskCard.kt`, `AddTaskDialog.kt`,
`Color.kt`, `Theme.kt`, `Type.kt`, `res/drawable/ic_*.xml`

- Kenapa `HomeScreen` dipecah menjadi versi stateful dan stateless?
- Apa itu *state hoisting*? Tunjukkan contohnya di `TaskCard`.
- Kapan state cukup disimpan dengan `remember`, kapan perlu `rememberSaveable`,
  dan kapan harus di ViewModel?
- Apa yang diatur `Type.kt`, dan kenapa `fontSize` tidak ditulis langsung di layar?
- Kenapa `collectAsStateWithLifecycle`, bukan `collectAsState`?
- Apa fungsi `key = { task.id }` pada `LazyColumn`?
- Dari mana warna tema berasal, dan kenapa *dynamic color* dimatikan?

### M2 · Biladi

Berkas: `TaskViewModel.kt`, `PomodoroViewModel.kt`,
`PomodoroViewModelTest.kt`, `TaskViewModelTest.kt`

- Kenapa timer memakai `targetEndMillis`, bukan menghitung mundur per detik?
  *(paling mungkin ditanyakan)*
- Apa gunanya `nowMillis` disuntikkan dari luar? Bagaimana uji unit memajukan
  jam tanpa menunggu 25 menit?
- Apa fungsi `combine` dan `stateIn(WhileSubscribed(5_000))`?
- Kenapa satu UiState, bukan banyak `StateFlow` terpisah?
- Kenapa UiState dibuat sebagai `sealed interface` Loading/Success/Error, dan
  apa fungsi `.catch { }`?
- Kenapa penghapusan tugas ditunda sampai Snackbar hilang?
- Kenapa ViewModel butuh `Factory`?
- Kenapa `PomodoroViewModel` dibuat di `WaktuKuNavHost`, bukan di
  `TimerScreen`? Apa yang terjadi pada timer bila tugasnya dihapus?
- Bagaimana mode demo bekerja (`DURASI_DEMO`, `DemoFactory`)?

### M3 · Abu Umar

Berkas: `Task.kt`, `PomodoroSession.kt`, `TaskDao.kt`, `PomodoroDao.kt`,
`TaskConverters.kt`, `TaskRepository.kt`, `PomodoroRepository.kt`,
`WaktuKuDatabase.kt`, `AppContainer.kt`, `app/schemas/*.json`,
`MigrationTest.kt`

- Kenapa fungsi tulis di DAO ditandai `suspend`, sedangkan fungsi baca
  mengembalikan `Flow`?
- Kenapa tenggat disimpan sebagai `Long`, bukan `String`?
- Apa guna `TypeConverter`?
- Jelaskan relasi `tasks` dan `pomodoro_sessions`: *foreign key*, index, dan
  `ON DELETE CASCADE`.
- Kenapa `insertCompletedSession` memakai `@Transaction`?
- Apa itu Migration, dan bagaimana kalian membuktikan Migration 1→2 benar?
- Kenapa Repository berbentuk interface? Kenapa DI manual, bukan Hilt?

### M4 · Afkar

Berkas: `WaktuKuDestinations.kt`, `WaktuKuBottomBar.kt`, `WaktuKuNavHost.kt`,
`WaktuKuApp.kt`, `MainActivity.kt`, `WaktuKuApplication.kt`,
`AndroidManifest.xml`

- Apa itu Type-Safe Navigation? Apa kelebihannya dibanding rute berupa teks
  seperti `"task/{taskId}"`?
- Bagaimana `taskId` dikirim ke layar Detail (`TaskDetail(taskId)`,
  `toRoute()`), dan bagaimana `Timer` dibuka tanpa memilih tugas?
- Apa fungsi `popUpTo` dan `launchSingleTop`? Kenapa `saveState` dan
  `restoreState` sengaja tidak dipakai? *(bug yang ditemukan Afkar)*
- Kenapa tab aktif diperiksa lewat `hierarchy`?
- Kenapa bottom bar ada di `WaktuKuApp` dan disembunyikan di Detail?
- Apa itu pola *single-activity*? Apa peran kelas `Application`?
- Kenapa perlu `consumeWindowInsets`?
- Kenapa Notifikasi, Statistik, dan Pengaturan ditunda? Kalau layar Statistik
  dibuat nanti, apa saja yang perlu ditambahkan di `WaktuKuDestinations.kt` dan
  `WaktuKuNavHost.kt`?

---

## 8. Aturan kerja

Selengkapnya ada di [README bagian Panduan untuk anggota kelompok](../README.md#panduan-untuk-anggota-kelompok).
Ringkasnya:

- Satu cabang per tugas, dinamai dengan kode tugas: `feat/a2-notifikasi`
- `git pull` di `main` sebelum membuat cabang. Jangan push langsung ke `main`
- Build dan uji unit harus hijau sebelum push, lalu buka Pull Request
- Commit dari akun sendiri
- Menyentuh berkas milik anggota lain? Bilang dulu di grup
