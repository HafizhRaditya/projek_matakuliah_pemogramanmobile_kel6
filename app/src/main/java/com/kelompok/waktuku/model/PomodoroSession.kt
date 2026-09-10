package com.kelompok.waktuku.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// Satu baris di tabel `pomodoro_sessions` mewakili satu sesi fokus.
//
// Sesuai prinsip produk nomor 2 di PRD, TIDAK ADA sesi mengambang: setiap sesi
// selalu melekat pada sebuah tugas. Aturan itu ditegakkan di tingkat database
// lewat foreign key, bukan sekadar disepakati lisan antar anggota kelompok.
// ============================================================================

/**
 * @param taskId tugas pemilik sesi ini.
 *
 * `onDelete = CASCADE` berarti: begitu sebuah tugas dihapus, seluruh sesi
 * miliknya ikut terhapus otomatis oleh SQLite. Tanpa ini, statistik akan
 * menghitung sesi milik tugas yang sudah tidak ada - angkanya jadi menipu.
 *
 * `indices` wajib diisi untuk kolom foreign key. Tanpa indeks, setiap kali
 * sebuah tugas dihapus SQLite harus memindai seluruh tabel sesi satu per satu.
 * Room juga akan memberi peringatan saat build kalau indeks ini lupa dibuat.
 */
@Entity(
    tableName = "pomodoro_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["task_id"])],
)
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "task_id")
    val taskId: Long,

    // Kapan sesi dimulai, epoch millis. Dipakai untuk rekap harian dan
    // diagram 7 hari terakhir pada layar Statistik.
    @ColumnInfo(name = "started_at")
    val startedAt: Long,

    // Lama sesi dalam menit. Disimpan per sesi, bukan diambil dari Pengaturan
    // saat menampilkan, karena pengguna bisa mengubah durasi di kemudian hari
    // dan riwayat lama harus tetap mencatat durasi yang benar-benar dijalani.
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int,

    // true hanya bila sesi berjalan sampai habis. Sesi yang dihentikan di
    // tengah jalan tetap dicatat dengan nilai false supaya bisa dianalisis,
    // tetapi TIDAK dihitung sebagai progres tugas (lihat PRD bagian 7).
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
)
