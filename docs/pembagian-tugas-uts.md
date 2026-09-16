# Pembagian Tugas Menuju UTS

**Kelompok 6 · WaktuKu** · keadaan per 16 September 2026

Dokumen ini menjawab tiga hal: siapa memegang bagian mana, apa yang masih
harus dikerjakan, dan apa yang harus dikuasai tiap orang saat ditanya dosen.

---

## 1. Anggota dan wilayah kode

| Kode | Nama | NIM | Peran | Folder dan berkas yang dipegang |
|---|---|---|---|---|
| **M1** | Hafizh Naufal Raditya | H1D024061 | UI/UX (Jetpack Compose), pemilik repo | `ui/screens/`, `ui/components/`, `ui/theme/`, `res/drawable/`, `res/values/` |
| **M2** | Biladi Amna | H1D024074 | State & logika (ViewModel) | `ui/viewmodel/`, `app/src/test/` |
| **M3** | Muhammad Abu Umar | H1D024084 | Penyimpanan lokal (Room) | `model/`, `data/`, `app/schemas/`, `app/src/androidTest/` |
| **M4** | Afkar Aufaa Farros | *menyusul* | Navigasi & integrasi sistem | `ui/navigation/`, `ui/WaktuKuApp.kt`, `MainActivity.kt`, `WaktuKuApplication.kt`, `AndroidManifest.xml`, `notification/`\*, `data/preferences/`\* |

\* Belum ada. Folder ini dibuat saat tugasnya dikerjakan.

Kode M1–M4 sama dengan komentar `PENANGGUNG JAWAB: Mahasiswa N` di kepala
setiap berkas, jadi tidak ada berkas yang perlu diubah. Urutan M2–M4 mengikuti
urutan nama. Boleh ditukar, asal sebelum mulai mengerjakan.

Beban kode yang harus dikuasai **saat ini** (setelah kedua PR yang terbuka
digabung):

| | Berkas | Baris |
|---|---|---|
| M1 | 8 | 1.656 |
| M2 | 4 (2 di antaranya berkas uji) | 1.010 |
| M3 | 10 (termasuk `MigrationTest`) | 823 |
| M4 | 6 + manifest | 475 |

Karena itu M4 mendapat fitur sistem yang paling sulit (F4 Notifikasi), dan M3
mendapat keputusan skema database untuk F6.

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
   tiap keputusan.
2. **Semua orang** harus bisa menjelaskan struktur folder secara keseluruhan
   (tabel di atas) dan satu alur data utuh dari layar sampai database
   ([DOKUMENTASI.md bagian 3](../DOKUMENTASI.md#3-arsitektur)).
3. **Commit dari akun masing-masing.** Saat ini seluruh 16 commit berasal dari
   akun Hafizh. Riwayat Git adalah bukti siapa mengerjakan apa.

---

## 3. Posisi proyek sekarang

| Fitur | Prioritas | Status |
|---|---|---|
| F1 Beranda | P0 | Selesai di `main`. Perbaikan tampilan menunggu PR `design/tema-dan-perbaikan-ui` |
| F2 Navigasi | P0 | Selesai. Dua uji di HP belum dilakukan (tombol kembali, rotasi layar) |
| F3 Timer Pomodoro | P0 | Kode dan 8 uji unit selesai di PR `feat/f3-timer`. **Belum ada mode demo** |
| F4 Notifikasi | P0 | **Belum dimulai** |
| F5 Detail / edit tugas | P1 | Masih placeholder. Query datanya sudah ada |
| F6 Statistik | P2 | Masih placeholder. Dua dari tiga query sudah ada |
| F7 Pengaturan | P3 | Masih placeholder. DataStore belum dipasang |

Kedua PR yang terbuka sudah diuji digabung bersamaan: **tidak ada konflik**,
`assembleDebug` berhasil, dan 12 uji unit lulus.

---

## 4. Tugas per orang

Setiap tugas punya kode (H, B, U, A) supaya mudah disebut di grup dan dipakai
sebagai nama cabang, misalnya `feat/a2-notifikasi`.

### M1 · Hafizh — UI

| Kode | Fitur | Tugas | Menunggu |
|---|---|---|---|
| H-0 | — | Gabungkan PR `design/tema-dan-perbaikan-ui` dan PR `feat/f3-timer`. Undang Biladi, Abu Umar, dan Afkar sebagai *collaborator* | — |
| H-1 | F3 | Rapikan `TimerScreen`: ikon Jeda dan Hentikan (Vector Asset, seperti ikon tab), angka timer lebih besar, warna berbeda untuk fokus dan istirahat, keterangan "istirahat panjang setelah 4 sesi" | H-0 |
| H-2 | F5 | `TaskDetailScreen`: judul, catatan, chip prioritas, `DatePicker` tenggat, tombol −/+ target sesi, riwayat sesi, Simpan, Hapus dengan dialog konfirmasi | B-3 |
| H-3 | F6 | `StatsScreen`: kartu total sesi dan menit hari ini, diagram batang 7 hari digambar dengan `Canvas`, tampilan kosong | B-4 |
| H-4 | — | Layar pembuka ikut mode gelap (`themes.xml`), centang pada chip prioritas di `AddTaskDialog`, rapikan sisa templat di `Type.kt` | — |
| H-5 | F7 | *Opsional:* `SettingsScreen` | A-4 |
| H-6 | UTS | Tangkapan layar setiap layar (terang dan gelap). Sebagai perwakilan, unggah tautan repo dan tangkapan layar ke eLDirU | semua |

Layar bisa mulai dibangun lewat `@Preview` dengan UiState buatan begitu M2
menetapkan bentuk data class-nya. Tidak perlu menunggu ViewModel selesai.

### M2 · Biladi — ViewModel

| Kode | Fitur | Tugas | Menunggu |
|---|---|---|---|
| B-1 | F3 | **Mode demo:** durasi fase bisa dipersingkat (misal 10 detik). Wajib untuk video. Sekarang 25/5/15 menit tertulis tetap di enum `PomodoroPhase` | H-0 |
| B-2 | F3 | Simpan `targetEndMillis` ke `SavedStateHandle` supaya timer tidak hilang saat Android mematikan proses aplikasi (batasan ini tercatat di PRD bagian F3) | H-0 |
| B-3 | F5 | `TaskDetailViewModel`: muat tugas lewat `observeTask(id)`, state form, validasi judul kosong, simpan, hapus. **Tetapkan `TaskDetailUiState` di hari pertama** supaya H-2 bisa jalan | — |
| B-4 | F6 | `StatsViewModel`: gabungkan query menjadi `StatsUiState`. **Tetapkan bentuknya di hari pertama** | U-3 |
| B-5 | F4 | Panggil penjadwal notifikasi dari `PomodoroViewModel`: jadwalkan saat Mulai/Lanjut, batalkan saat Jeda/Hentikan | A-2, U-5 |
| B-6 | — | Uji unit untuk B-1 sampai B-4. `./gradlew :app:testDebugUnitTest` harus hijau sebelum push | — |
| B-7 | UTS | Koordinator **Formulir Deklarasi Penggunaan AI**: kumpulkan isian dari semua anggota, lalu unggah | semua |

### M3 · Abu Umar — Data

| Kode | Fitur | Tugas | Menunggu |
|---|---|---|---|
| U-1 | — | Jalankan `MigrationTest` dengan HP tersambung: `./gradlew :app:connectedDebugAndroidTest`. Uji ini sudah ditulis tapi **belum pernah dijalankan** | — |
| U-2 | F5 | Periksa `observeTask` dan `observeSessionsForTask` dengan data contoh, termasuk urutan riwayat sesinya | — |
| U-3 | F6 | Query rekap sesi **per hari** untuk 7 hari terakhir. Yang ada sekarang baru total per rentang waktu (`observeCompletedCount`, `observeTotalMinutes`) | — |
| U-4 | F6 | **Putuskan:** "tugas selesai minggu ini" butuh kolom `completed_at` yang belum ada di `Task`. Pilih: tambah kolom lewat Migration 2→3 beserta ujinya, atau ubah kriterianya menjadi "total tugas selesai" dan catat di PRD | — |
| U-5 | F4 | Pasang penjadwal notifikasi buatan A-2 di `AppContainer` | A-2 |
| U-6 | — | Perbarui [DOKUMENTASI.md](../DOKUMENTASI.md) bagian 2 dan 8. Daftar berkas dan daftar "yang belum dikerjakan" di sana sudah usang | — |
| U-7 | UTS | Teks forum eLDirU: perkenalan kelompok dan peran (tabel bagian 1), latar belakang ([PRD bagian 2](../PRD.md)), deskripsi aplikasi ([PRD bagian 1](../PRD.md)) | — |

### M4 · Afkar — Navigasi & sistem

| Kode | Fitur | Tugas | Menunggu |
|---|---|---|---|
| A-1 | F2 | Uji di HP: (1) pindah Beranda → Fokus → Statistik, tekan kembali sekali, aplikasi harus langsung tertutup; (2) putar HP di tab Statistik, harus tetap di Statistik. Centang di PRD | — |
| A-2 | F4 | **Notifikasi** — rincian di bawah | — |
| A-3 | F5, F6 | Ganti placeholder Detail dan Statistik (dan Pengaturan bila F7 dikerjakan) di `WaktuKuNavHost` dengan layar sungguhan. Hapus `PlaceholderScreen` setelah semuanya terganti, bersama M1 | H-2, H-3 |
| A-4 | F7 | *Opsional:* DataStore untuk Pengaturan di `data/preferences/` (perlu dependensi baru) | — |
| A-5 | UTS | Rekam dan sunting **video demo** sesuai [skenario PRD bagian 11](../PRD.md). Setiap anggota menarasikan bagiannya sendiri | B-1, semua |

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

---

## 5. Titik temu antar anggota

Tiga fitur melewati lebih dari satu folder. Urutan di bawah mencegah orang
saling menunggu tanpa tahu.

| Fitur | Urutan |
|---|---|
| F4 Notifikasi | A-2 membuat penjadwal → U-5 memasangnya di `AppContainer` → B-5 memanggilnya dari `PomodoroViewModel` |
| F5 Detail | B-3 menetapkan `TaskDetailUiState` → H-2 membangun layar (mulai dari Preview) → A-3 menyambungkan rute. U-2 dikerjakan di awal |
| F6 Statistik | U-3 dan U-4 menyiapkan query → B-4 menetapkan `StatsUiState` → H-3 membangun layar → A-3 menyambungkan rute |

`AppContainer.kt` (M3) dan `WaktuKuNavHost.kt` (M4) adalah dua berkas yang
paling sering disentuh orang lain. **Bilang dulu di grup** sebelum mengubahnya.

---

## 6. Urutan kerja

> **Tenggat pengumpulan:** `______________` — belum tercantum di halaman
> eLDirU Pertemuan 8. Tanyakan, lalu isi di sini.

1. **Hari ini** — H-0: gabungkan dua PR dan undang collaborator. Semua orang
   `git clone`, lalu `./gradlew :app:assembleDebug` harus hijau di laptop
   masing-masing.
2. **P0 lengkap** — B-1, B-2, A-1, A-2, U-1, U-5, B-5, H-1. Tanpa ini aplikasi
   bukan WaktuKu.
3. **F5 dan F6** — U-2, U-3, U-4, B-3, B-4, H-2, H-3, A-3.
4. **F7 dan polesan** — H-4, lalu H-5 dan A-4 bila waktu cukup. Bila mepet,
   potong dari bawah: F7 → F6 → F5. F1–F4 tidak boleh dipotong.
5. **Pengumpulan** — jalankan skenario demo PRD bagian 11 sampai tanpa error,
   lalu A-5 (video), H-6 (tangkapan layar dan tautan repo), U-7 (teks forum),
   B-7 (deklarasi AI).
6. **Latihan tanya jawab** — setiap orang menjelaskan foldernya kepada tiga
   orang lainnya, lalu gantian bertanya memakai daftar di bagian 7.

---

## 7. Persiapan tanya jawab

Bahan jawaban tersedia di komentar setiap berkas, serta di
[DOKUMENTASI.md](../DOKUMENTASI.md) bagian 4 dan 10.

**Wajib dikuasai semua orang:**

- Struktur folder dan pemilik setiap folder (bagian 1)
- Alur data saat pengguna mencentang tugas, dari `TaskCard` sampai Room lalu
  kembali ke layar
- Beda MVVM dengan MVC

### M1 · Hafizh

Berkas: `HomeScreen.kt`, `TimerScreen.kt`, `TaskCard.kt`, `AddTaskDialog.kt`,
`Color.kt`, `Theme.kt`, `Type.kt`, `res/drawable/ic_*.xml`

- Kenapa `HomeScreen` dipecah menjadi versi stateful dan stateless?
- Apa itu *state hoisting*? Tunjukkan contohnya di `TaskCard`.
- Kapan state cukup disimpan dengan `remember`, kapan harus di ViewModel?
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
- Kenapa satu data class UiState, bukan banyak `StateFlow` terpisah?
- Kenapa penghapusan tugas ditunda sampai Snackbar hilang?
- Kenapa ViewModel butuh `Factory`?

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
`AndroidManifest.xml`, dan berkas `notification/` yang akan dibuat

- Apa beda argumen wajib `task/{taskId}` dan argumen opsional
  `timer?taskId={taskId}`?
- Apa fungsi `popUpTo`, `saveState`, `restoreState`, dan `launchSingleTop`?
- Kenapa tab aktif diperiksa lewat `hierarchy`?
- Kenapa bottom bar ada di `WaktuKuApp` dan disembunyikan di Detail? Kenapa
  Pengaturan bukan tab?
- Apa itu pola *single-activity*? Apa peran kelas `Application`?
- Kenapa perlu `consumeWindowInsets`?
- Kenapa izin notifikasi diminta saat runtime, dan kenapa notifikasi
  dijadwalkan lewat `AlarmManager`?

---

## 8. Aturan kerja

Selengkapnya ada di [README bagian Panduan untuk anggota kelompok](../README.md#panduan-untuk-anggota-kelompok).
Ringkasnya:

- Satu cabang per tugas, dinamai dengan kode tugas: `feat/a2-notifikasi`
- `git pull` di `main` sebelum membuat cabang. Jangan push langsung ke `main`
- Build dan uji unit harus hijau sebelum push, lalu buka Pull Request
- Commit dari akun sendiri
- Menyentuh berkas milik anggota lain? Bilang dulu di grup
