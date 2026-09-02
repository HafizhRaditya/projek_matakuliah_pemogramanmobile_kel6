package com.kelompok.waktuku.data

import androidx.room.TypeConverter
import com.kelompok.waktuku.model.TaskPriority

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// SQLite hanya mengenal tipe primitif: INTEGER, REAL, TEXT, BLOB, NULL.
// Enum TaskPriority tidak termasuk, jadi Room butuh "penerjemah" dua arah.
// Tanpa file ini, build akan gagal dengan pesan:
//   "Cannot figure out how to save this field into database."
// ============================================================================

class TaskConverters {

    /** Kotlin -> database: TaskPriority.HIGH disimpan sebagai teks "HIGH". */
    @TypeConverter
    fun fromPriority(priority: TaskPriority): String = priority.name

    /**
     * Database -> Kotlin: teks "HIGH" dibaca kembali menjadi TaskPriority.HIGH.
     * Dibungkus runCatching agar data lama/rusak tidak membuat aplikasi crash,
     * melainkan jatuh ke nilai aman MEDIUM.
     */
    @TypeConverter
    fun toPriority(value: String): TaskPriority =
        runCatching { TaskPriority.valueOf(value) }.getOrDefault(TaskPriority.MEDIUM)
}
