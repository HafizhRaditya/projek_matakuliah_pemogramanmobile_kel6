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

Batas folder ini dipilih supaya tiga orang bisa bekerja bersamaan tanpa
menyunting file yang sama - konflik Git jadi minimal.

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
