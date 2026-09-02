package com.kelompok.waktuku.data

import com.kelompok.waktuku.model.Task
import com.kelompok.waktuku.model.TaskPriority
import kotlinx.coroutines.flow.Flow

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// Repository adalah SATU-SATUNYA pintu masuk data bagi lapisan di atasnya.
// ViewModel tidak boleh menyentuh TaskDao secara langsung.
//
// Kenapa repot-repot menambah satu lapisan lagi?
// 1. ViewModel tidak perlu tahu datanya datang dari Room, file, atau internet.
//    Kalau besok WaktuKu menambah sinkronisasi cloud, HANYA file ini yang
//    berubah - ViewModel dan UI tidak tersentuh sama sekali.
// 2. Repository berbentuk interface, sehingga saat menguji TaskViewModel kita
//    bisa memasukkan versi palsu (FakeTaskRepository) tanpa perlu database asli.
// ============================================================================

/**
 * Kontrak data tugas. Perhatikan: tidak ada satu pun tipe khas Room di sini
 * (tidak ada @Dao, tidak ada Cursor) - itu memang disengaja.
 */
interface TaskRepository {

    /** Aliran seluruh tugas yang otomatis memancar ulang saat data berubah. */
    fun observeTasks(): Flow<List<Task>>

    /** Aliran satu tugas berdasarkan id. */
    fun observeTask(id: Long): Flow<Task?>

    /** Menyimpan tugas baru maupun hasil edit. Mengembalikan id baris. */
    suspend fun saveTask(task: Task): Long

    /** Membuat tugas baru langsung dari input pengguna di layar utama. */
    suspend fun addTask(
        title: String,
        notes: String = "",
        dueAt: Long? = null,
        priority: TaskPriority = TaskPriority.MEDIUM,
        estimatedPomodoros: Int = 1,
    ): Long

    suspend fun setDone(id: Long, isDone: Boolean)

    suspend fun deleteTask(task: Task)

    suspend fun clearCompleted()
}

/**
 * Implementasi luring (offline-first): seluruh data hidup di database Room
 * pada perangkat, jadi WaktuKu tetap berfungsi penuh tanpa internet.
 *
 * Nama "Offline..." mengikuti gaya penamaan resmi Google (bandingkan dengan
 * OfflineItemsRepository pada codelab Android Basics).
 */
class OfflineTaskRepository(private val taskDao: TaskDao) : TaskRepository {

    // Repository di sini hanya meneruskan ke DAO. Itu WAJAR dan bukan kode
    // sia-sia: nilainya ada pada batas arsitektur yang ia jaga, bukan pada
    // banyaknya logika di dalamnya.
    override fun observeTasks(): Flow<List<Task>> = taskDao.observeAll()

    override fun observeTask(id: Long): Flow<Task?> = taskDao.observeById(id)

    override suspend fun saveTask(task: Task): Long = taskDao.upsert(task)

    override suspend fun addTask(
        title: String,
        notes: String,
        dueAt: Long?,
        priority: TaskPriority,
        estimatedPomodoros: Int,
    ): Long = taskDao.upsert(
        Task(
            // id = 0 -> beri tahu Room bahwa ini baris baru, silakan buat id sendiri.
            id = 0L,
            // trim() supaya judul berisi spasi saja tidak lolos ke database.
            title = title.trim(),
            notes = notes.trim(),
            dueAt = dueAt,
            priority = priority,
            estimatedPomodoros = estimatedPomodoros,
        )
    )

    override suspend fun setDone(id: Long, isDone: Boolean) = taskDao.updateDoneStatus(id, isDone)

    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

    override suspend fun clearCompleted() = taskDao.deleteCompleted()
}
