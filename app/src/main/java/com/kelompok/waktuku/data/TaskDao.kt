package com.kelompok.waktuku.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.kelompok.waktuku.model.Task
import kotlinx.coroutines.flow.Flow

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// DAO = Data Access Object. Berisi DAFTAR PERINTAH SQL yang boleh dijalankan
// aplikasi. Kita hanya menulis tanda tangan fungsinya; KSP yang menuliskan
// implementasi SQLite-nya saat build.
//
// Dua aturan penting yang perlu dijelaskan ke dosen:
// 1. Fungsi yang MEMBACA banyak data mengembalikan Flow<...> -> bukan sekadar
//    "ambil sekali", tapi "kirim ulang setiap kali tabel berubah". Inilah yang
//    membuat UI otomatis ter-update tanpa perlu refresh manual.
// 2. Fungsi yang MENGUBAH data ditandai `suspend` -> memaksa pemanggilnya
//    berjalan di coroutine (background thread), sehingga UI tidak pernah macet.
// ============================================================================

@Dao
interface TaskDao {

    /**
     * Mengambil semua tugas dengan urutan yang paling masuk akal bagi pengguna:
     * 1. `is_done ASC`      -> tugas yang belum selesai selalu di atas.
     * 2. `due_at IS NULL`   -> SQLite menilai FALSE(0) < TRUE(1), jadi tugas
     *                          yang PUNYA tenggat naik lebih dulu.
     * 3. `due_at ASC`       -> tenggat paling dekat di urutan teratas.
     * 4. `created_at DESC`  -> untuk tugas tanpa tenggat, yang terbaru di atas.
     *
     * Catatan: kolom `priority` sengaja TIDAK dipakai untuk ORDER BY karena
     * tersimpan sebagai teks ("HIGH", "LOW", "MEDIUM"), sehingga pengurutannya
     * akan mengikuti abjad, bukan tingkat kepentingan.
     */
    @Query(
        """
        SELECT * FROM tasks
        ORDER BY is_done ASC, due_at IS NULL, due_at ASC, created_at DESC
        """
    )
    fun observeAll(): Flow<List<Task>>

    /** Memantau satu tugas, dipakai layar detail/edit nanti. */
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun observeById(id: Long): Flow<Task?>

    /**
     * Upsert = UPDATE kalau id-nya sudah ada, INSERT kalau belum.
     * Satu fungsi ini menggantikan pasangan insert() + update().
     * Nilai balik Long adalah id baris yang baru dibuat.
     */
    @Upsert
    suspend fun upsert(task: Task): Long

    @Delete
    suspend fun delete(task: Task)

    /**
     * Mengubah status centang tanpa perlu memuat objek Task lengkap dulu.
     * Lebih hemat dibanding membaca-lalu-menulis kembali.
     */
    @Query("UPDATE tasks SET is_done = :isDone WHERE id = :id")
    suspend fun updateDoneStatus(id: Long, isDone: Boolean)

    /** Membersihkan semua tugas yang sudah selesai sekaligus. */
    @Query("DELETE FROM tasks WHERE is_done = 1")
    suspend fun deleteCompleted()
}
