package com.kelompok.waktuku.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.kelompok.waktuku.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// Perintah SQL untuk tabel `pomodoro_sessions`.
//
// Query di sini sengaja mengembalikan angka hasil hitungan SQL (COUNT, SUM),
// bukan mengambil seluruh baris lalu dijumlahkan di Kotlin. SQLite jauh lebih
// cepat menghitung agregat daripada kita menyalin ratusan baris ke memori
// hanya untuk menjumlahkannya.
// ============================================================================

@Dao
interface PomodoroDao {

    /** Menyimpan satu sesi. Mengembalikan id baris yang baru dibuat. */
    @Insert
    suspend fun insert(session: PomodoroSession): Long

    /** Riwayat sesi sebuah tugas, terbaru di atas. Dipakai layar Detail Tugas. */
    @Query(
        """
        SELECT * FROM pomodoro_sessions
        WHERE task_id = :taskId
        ORDER BY started_at DESC
        """
    )
    fun observeSessionsForTask(taskId: Long): Flow<List<PomodoroSession>>

    /**
     * Jumlah sesi fokus yang selesai penuh dalam rentang waktu tertentu.
     *
     * Rentang ditentukan pemanggil sebagai epoch millis, sehingga satu query
     * ini bisa dipakai untuk "hari ini", "minggu ini", maupun rentang bebas.
     * `>=` dan `<` dipilih supaya batas akhir tidak terhitung dua kali saat
     * dipakai berurutan per hari.
     */
    @Query(
        """
        SELECT COUNT(*) FROM pomodoro_sessions
        WHERE is_completed = 1 AND started_at >= :mulai AND started_at < :sampai
        """
    )
    fun observeCompletedCount(mulai: Long, sampai: Long): Flow<Int>

    /**
     * Total menit fokus dalam rentang waktu.
     *
     * COALESCE dipakai karena SUM mengembalikan NULL bila tidak ada satu pun
     * baris yang cocok. Tanpa COALESCE, Room akan gagal memasukkan NULL ke
     * tipe Int yang tidak boleh null.
     */
    @Query(
        """
        SELECT COALESCE(SUM(duration_minutes), 0) FROM pomodoro_sessions
        WHERE is_completed = 1 AND started_at >= :mulai AND started_at < :sampai
        """
    )
    fun observeTotalMinutes(mulai: Long, sampai: Long): Flow<Int>

    /** Menambah satu pada hitungan sesi selesai milik sebuah tugas. */
    @Query("UPDATE tasks SET completed_pomodoros = completed_pomodoros + 1 WHERE id = :taskId")
    suspend fun incrementTaskProgress(taskId: Long)

    /**
     * Mencatat sesi yang selesai penuh SEKALIGUS menaikkan progres tugasnya.
     *
     * @Transaction memastikan kedua perintah di bawah berhasil bersama atau
     * gagal bersama. Tanpa itu, aplikasi yang mati tepat di antara keduanya
     * akan meninggalkan data timpang: sesi tercatat tapi progres tugas tidak
     * bertambah, atau sebaliknya.
     */
    @Transaction
    suspend fun insertCompletedSession(session: PomodoroSession): Long {
        val id = insert(session.copy(isCompleted = true))
        incrementTaskProgress(session.taskId)
        return id
    }
}
