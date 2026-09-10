package com.kelompok.waktuku.data

import com.kelompok.waktuku.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// Pintu masuk data sesi Pomodoro bagi PomodoroViewModel.
//
// Dibuat interface dengan alasan yang sama seperti TaskRepository: supaya
// Mahasiswa 2 bisa menguji PomodoroViewModel memakai FakePomodoroRepository,
// tanpa perlu database sungguhan dan tanpa perlu menunggu 25 menit.
// ============================================================================

interface PomodoroRepository {

    /** Riwayat sesi sebuah tugas, terbaru di atas. */
    fun observeSessionsForTask(taskId: Long): Flow<List<PomodoroSession>>

    /** Jumlah sesi selesai dalam rentang waktu (epoch millis). */
    fun observeCompletedCount(mulai: Long, sampai: Long): Flow<Int>

    /** Total menit fokus dalam rentang waktu (epoch millis). */
    fun observeTotalMinutes(mulai: Long, sampai: Long): Flow<Int>

    /**
     * Mencatat sesi yang berjalan sampai habis, sekaligus menaikkan progres
     * tugas terkait.
     */
    suspend fun recordCompletedSession(
        taskId: Long,
        startedAt: Long,
        durationMinutes: Int,
    ): Long

    /**
     * Mencatat sesi yang dihentikan pengguna di tengah jalan.
     *
     * Sesi ini TIDAK menambah progres tugas - lihat PRD bagian 7. Tetap
     * disimpan supaya nanti bisa dianalisis, misalnya untuk mengetahui pada
     * menit ke berapa pengguna paling sering menyerah.
     */
    suspend fun recordAbandonedSession(
        taskId: Long,
        startedAt: Long,
        durationMinutes: Int,
    ): Long
}

/**
 * Implementasi luring memakai Room.
 *
 * Perhatikan bahwa waktu mulai (`startedAt`) diterima sebagai parameter, bukan
 * diambil dari `System.currentTimeMillis()` di dalam sini. Itu disengaja:
 * repository yang memanggil jam sistem sendiri mustahil diuji, karena
 * hasilnya berbeda setiap kali dijalankan.
 */
class OfflinePomodoroRepository(
    private val pomodoroDao: PomodoroDao,
) : PomodoroRepository {

    override fun observeSessionsForTask(taskId: Long): Flow<List<PomodoroSession>> =
        pomodoroDao.observeSessionsForTask(taskId)

    override fun observeCompletedCount(mulai: Long, sampai: Long): Flow<Int> =
        pomodoroDao.observeCompletedCount(mulai, sampai)

    override fun observeTotalMinutes(mulai: Long, sampai: Long): Flow<Int> =
        pomodoroDao.observeTotalMinutes(mulai, sampai)

    override suspend fun recordCompletedSession(
        taskId: Long,
        startedAt: Long,
        durationMinutes: Int,
    ): Long = pomodoroDao.insertCompletedSession(
        PomodoroSession(
            taskId = taskId,
            startedAt = startedAt,
            durationMinutes = durationMinutes,
        )
    )

    override suspend fun recordAbandonedSession(
        taskId: Long,
        startedAt: Long,
        durationMinutes: Int,
    ): Long = pomodoroDao.insert(
        PomodoroSession(
            taskId = taskId,
            startedAt = startedAt,
            durationMinutes = durationMinutes,
            isCompleted = false,
        )
    )
}
