# WaktuKu

Aplikasi Android **luring** (offline) untuk perencanaan pribadi dan timer Pomodoro.
Proyek mata kuliah Pemrograman Mobile - Kelompok 6, Teknik Informatika.

> **Dokumen proyek:**
> - [PRD.md](PRD.md) - ruang lingkup MVP, user story, kriteria penerimaan, rencana 4 minggu
> - [DOKUMENTASI.md](DOKUMENTASI.md) - penjelasan tiap file, diagram alur MVVM,
>   alasan di balik setiap keputusan teknis, dan bahan tanya-jawab presentasi

## Arsitektur

Mengikuti panduan resmi [Android App Architecture](https://developer.android.com/topic/architecture/intro)
dengan pola **MVVM** dan struktur direktori bergaya
[android/compose-samples](https://github.com/android/compose-samples).

```
UI (Compose)  ->  ViewModel (StateFlow)  ->  Repository  ->  DAO  ->  Room
     ^                                                                  |
     +---------------- Flow: perubahan data mengalir naik --------------+
```

Aliran datanya satu arah (*Unidirectional Data Flow*): kejadian dari pengguna
turun ke bawah lewat pemanggilan fungsi, sedangkan data naik ke atas lewat `Flow`.

## Struktur direktori

```
app/src/main/java/com/kelompok/waktuku/
├── WaktuKuApplication.kt      # Titik masuk, memegang AppContainer
├── MainActivity.kt            # Satu-satunya Activity (single-activity)
│
├── model/                     # [Mahasiswa 3]
│   └── Task.kt                # Data class + @Entity + enum TaskPriority
│
├── data/                      # [Mahasiswa 3]
│   ├── TaskDao.kt             # Kumpulan query SQL (Room)
│   ├── TaskConverters.kt      # Penerjemah enum <-> TEXT
│   ├── TaskRepository.kt      # Interface + OfflineTaskRepository
│   ├── WaktuKuDatabase.kt     # @Database, singleton
│   └── AppContainer.kt        # Dependency Injection manual
│
└── ui/
    ├── theme/                 # Warna, tipografi, WaktuKuTheme
    ├── viewmodel/             # [Mahasiswa 2]
    │   └── TaskViewModel.kt   # HomeUiState + StateFlow + aksi pengguna
    ├── components/            # [Mahasiswa 1]
    │   ├── TaskCard.kt        # Kartu satu tugas (stateless)
    │   └── AddTaskDialog.kt   # Dialog tambah tugas
    └── screens/               # [Mahasiswa 1]
        └── HomeScreen.kt      # Scaffold + LazyColumn + filter
```

## Pembagian tugas

| Anggota | Lapisan | Folder yang disentuh |
|---|---|---|
| Mahasiswa 1 | UI/UX (Jetpack Compose) | `ui/screens`, `ui/components`, `ui/theme` |
| Mahasiswa 2 | State & logika | `ui/viewmodel` |
| Mahasiswa 3 | Local storage | `model`, `data` |
| Mahasiswa 4 | Navigasi & integrasi sistem | `ui/navigation`, `notification`, `data/preferences` |

Batas folder ini dipilih supaya empat orang bisa bekerja bersamaan tanpa
menyunting file yang sama - konflik Git jadi minimal.

## Panduan untuk anggota kelompok

Baca bagian ini sekali sebelum mulai. Isinya hal-hal yang kalau tidak
disepakati di awal akan jadi sumber ribut di tengah jalan.

### Sekali saja, saat pertama kali

```bash
git clone https://github.com/HafizhRaditya/projek_matakuliah_pemogramanmobile_kel6.git
```

Lalu buka Android Studio, pilih **File -> Open**, arahkan ke folder hasil clone
(pilih folder induknya, bukan folder `app` di dalamnya). Tunggu Gradle sync
selesai sampai bar bawah berhenti bergerak. Sync pertama memakan waktu lama
karena harus mengunduh Room, Compose, dan Navigation - lakukan di rumah, jangan
saat sedang kerja kelompok.

Jangan pernah meng-commit `local.properties`, folder `build/`, atau `.idea/`.
Ketiganya sudah diabaikan `.gitignore` dan memang harus begitu, karena isinya
menyesuaikan laptop masing-masing.

### Setiap kali mulai mengerjakan sesuatu

```bash
git checkout main
git pull
git checkout -b feat/f3-timer
```

Nama branch mengikuti kode fitur di [PRD.md](PRD.md): `feat/f1-home`,
`feat/f3-timer`, `feat/f4-notifikasi`, `feat/f5-detail`, `feat/f6-statistik`,
`feat/f7-pengaturan`.

**Selalu `git pull` di `main` dulu sebelum membuat branch.** Kalau langsung
bercabang dari `main` yang tertinggal, nanti PR-mu penuh konflik.

### Setiap kali selesai

```bash
git add -A
git commit -m "Tambah layar timer Pomodoro"
git push -u origin feat/f3-timer
```

Terminal akan menampilkan tautan untuk membuka Pull Request. Buka tautannya,
isi judul, lalu tandai satu anggota lain sebagai **Reviewer**. Jangan merge PR
sendiri tanpa ada yang melihat - itu sama saja dengan push langsung ke `main`,
cuma lebih banyak langkahnya.

### Aturan yang menjaga kita tidak saling menimpa

**Hormati batas folder.** Tiap orang punya wilayahnya sendiri (lihat tabel
Pembagian tugas di atas). Kalau kamu perlu mengubah berkas milik orang lain,
bilang dulu di grup - jangan diam-diam.

**`main` hanya boleh berisi kode yang bisa di-build.** Sebelum push, jalankan:

```bash
./gradlew :app:assembleDebug
```

Kalau merah, jangan di-push. `main` yang rusak menghambat tiga orang sekaligus.

**Satu PR untuk satu fitur.** PR yang berisi lima hal sekaligus tidak akan
ditinjau siapa pun dengan sungguh-sungguh.

### Kalau tersesat

| Gejala | Yang harus dilakukan |
|---|---|
| `git push` ditolak, "permission denied" | Kamu belum jadi collaborator, atau undangannya belum diterima. Cek email |
| `git pull` bilang ada konflik | Jangan panik dan jangan hapus apa pun. Screenshot pesannya, tanyakan di grup |
| Gradle sync gagal minta JDK | Biarkan Gradle mengunduh sendiri, jangan diubah manual |
| Muncul peringatan `android.disallowKotlinSourceSets` | Normal dan disengaja, lihat bagian Catatan build di bawah |
| Preview Compose kosong | Tekan **Build & Refresh** di panel preview |

## Teknologi

| Komponen | Versi |
|---|---|
| Kotlin | 2.2.10 |
| AGP / Gradle | 9.3.2 / 9.5.0 |
| Jetpack Compose | BOM 2026.02.01 (Material 3) |
| Room | 2.8.4 (via KSP 2.2.10-2.0.2) |
| Lifecycle / ViewModel | 2.11.0 |
| minSdk / targetSdk | 24 / 37 |

## Menjalankan

```bash
./gradlew :app:assembleDebug
```

Atau buka folder ini di Android Studio, lalu tekan **Run**.

## Rencana berikutnya

- [ ] Fitur Pomodoro: entity `PomodoroSession`, `PomodoroViewModel` (countdown berbasis Flow), `TimerScreen`
- [ ] Navigasi antar layar dengan `NavHost`
- [ ] Notifikasi saat sesi Pomodoro selesai
- [ ] Unit test `TaskViewModel` memakai `FakeTaskRepository`

## Catatan build (penting saat clone pertama kali)

Proyek ini memakai AGP 9 yang sudah membawa Kotlin sendiri (*built-in Kotlin*).
Ada dua penyesuaian yang sudah dilakukan dan sebaiknya jangan dihapus:

1. **`android.disallowKotlinSourceSets=false` di `gradle.properties`**
   KSP (pembangkit kode Room) masih mendaftarkan folder hasil generate lewat
   `kotlin.sourceSets`, cara yang kini dilarang AGP 9. Tanpa baris ini, build
   berhenti dengan pesan *"Using kotlin.sourceSets DSL to add Kotlin sources is
   not allowed with built-in Kotlin"*. Baris ini boleh dihapus setelah KSP
   merilis versi yang mendukung built-in Kotlin.

2. **`material-icons-core` ditulis eksplisit di `build.gradle.kts`**
   Compose BOM versi baru tidak lagi menarik pustaka ikon lewat `material3`.
   Tanpa dependensi ini muncul error *"Unresolved reference: Icons"*.

Status terakhir yang sudah diverifikasi:

```
./gradlew :app:assembleDebug :app:testDebugUnitTest
BUILD SUCCESSFUL
```
