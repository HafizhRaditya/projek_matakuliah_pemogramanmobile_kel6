package com.kelompok.waktuku.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// Task adalah "model domain" sekaligus tabel database.
//
// Catatan arsitektur untuk presentasi:
// Di aplikasi berskala besar, model domain biasanya DIPISAH dari entity
// database (misalnya Task vs TaskEntity + fungsi toDomain()). Untuk aplikasi
// seukuran WaktuKu, penggabungan seperti ini dianjurkan oleh Google karena
// menghindari boilerplate yang belum dibutuhkan. Kalau nanti WaktuKu perlu
// sinkronisasi cloud, barulah pemisahan itu dilakukan.
// ============================================================================

/**
 * Satu baris tugas di dalam tabel `tasks`.
 *
 * Seluruh properti dibuat `val` (immutable). Untuk mengubah data, kita membuat
 * salinan baru dengan `task.copy(isDone = true)`. Ini penting bagi Compose:
 * objek baru = referensi baru = Compose tahu pasti bahwa UI harus digambar ulang.
 */
@Entity(tableName = "tasks")
data class Task(
    // autoGenerate = true -> SQLite yang memberi nomor id, kita cukup kirim 0
    // saat membuat task baru.
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Judul tugas, satu-satunya field yang wajib diisi pengguna.
    val title: String,

    // Catatan tambahan / deskripsi. Kosong artinya pengguna melewatinya.
    val notes: String = "",

    // Tenggat waktu disimpan sebagai epoch millis (Long), BUKAN String.
    // Alasannya: hanya angka yang bisa diurutkan & difilter dengan benar
    // langsung di query SQL (lihat ORDER BY di TaskDao).
    // Nullable karena tidak semua tugas punya tenggat.
    @ColumnInfo(name = "due_at")
    val dueAt: Long? = null,

    // Enum tidak dikenali SQLite, jadi dikonversi lewat TaskConverters.
    val priority: TaskPriority = TaskPriority.MEDIUM,

    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,

    // Target jumlah sesi Pomodoro untuk menyelesaikan tugas ini.
    // Dipakai fitur timer pada sprint berikutnya.
    @ColumnInfo(name = "estimated_pomodoros")
    val estimatedPomodoros: Int = 1,

    // Waktu pembuatan, dipakai sebagai urutan cadangan bila tidak ada tenggat.
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)

/**
 * Tingkat prioritas tugas.
 *
 * `label` sengaja disimpan di enum agar teks Bahasa Indonesia tidak ditulis
 * berulang-ulang di lapisan UI.
 */
enum class TaskPriority(val label: String) {
    LOW("Rendah"),
    MEDIUM("Sedang"),
    HIGH("Tinggi"),
}
