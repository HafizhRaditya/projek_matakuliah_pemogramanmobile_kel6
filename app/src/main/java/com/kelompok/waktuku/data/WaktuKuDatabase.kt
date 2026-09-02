package com.kelompok.waktuku.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kelompok.waktuku.model.Task

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// Kelas ini adalah "pintu utama" database. Room akan membuat kelas turunannya
// (WaktuKuDatabase_Impl) secara otomatis saat build lewat KSP.
// ============================================================================

/**
 * @param entities  daftar tabel di dalam database ini.
 * @param version   dinaikkan setiap kali struktur tabel berubah. Kalau lupa
 *                  menaikkannya, aplikasi crash saat dibuka pengguna lama.
 * @param exportSchema  di-set false agar tidak perlu mengatur folder skema.
 *                  Untuk aplikasi yang sudah rilis ke publik, sebaiknya true.
 */
@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(TaskConverters::class)
abstract class WaktuKuDatabase : RoomDatabase() {

    // Room mengisi sendiri isi fungsi ini.
    abstract fun taskDao(): TaskDao

    companion object {
        // @Volatile: memastikan perubahan nilai INSTANCE langsung terlihat oleh
        // semua thread, bukan tersimpan di cache thread masing-masing.
        @Volatile
        private var INSTANCE: WaktuKuDatabase? = null

        /**
         * Pola Singleton. Membuka koneksi database itu mahal, jadi seluruh
         * aplikasi harus memakai SATU instance yang sama.
         *
         * `synchronized` mencegah dua thread membuat database bersamaan.
         */
        fun getDatabase(context: Context): WaktuKuDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    // applicationContext dipakai agar database tidak ikut
                    // memegang Activity -> mencegah memory leak.
                    context.applicationContext,
                    WaktuKuDatabase::class.java,
                    "waktuku_database",
                )
                    // Saat versi database naik tanpa Migration yang ditulis,
                    // baris ini menghapus data lama daripada crash.
                    // Aman selama masa pengembangan; HAPUS sebelum rilis
                    // dan ganti dengan addMigrations(...) agar data pengguna
                    // tidak hilang.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
