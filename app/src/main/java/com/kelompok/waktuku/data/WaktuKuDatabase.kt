package com.kelompok.waktuku.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kelompok.waktuku.model.PomodoroSession
import com.kelompok.waktuku.model.Task

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// Kelas ini adalah "pintu utama" database. Room akan membuat kelas turunannya
// (WaktuKuDatabase_Impl) secara otomatis saat build lewat KSP.
// ============================================================================

/**
 * @param entities  daftar tabel di dalam database ini.
 * @param version   dinaikkan setiap kali struktur tabel berubah, dan setiap
 *                  kenaikan WAJIB disertai satu Migration.
 * @param exportSchema  true agar Room menuliskan struktur database sebagai
 *                  JSON ke folder schemas/. Berkas itu dipakai MigrationTest
 *                  dan wajib ikut di-commit.
 */
@Database(
    entities = [Task::class, PomodoroSession::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(TaskConverters::class)
abstract class WaktuKuDatabase : RoomDatabase() {

    // Room mengisi sendiri isi fungsi-fungsi ini.
    abstract fun taskDao(): TaskDao

    abstract fun pomodoroDao(): PomodoroDao

    companion object {

        /**
         * Migrasi dari versi 1 ke versi 2.
         *
         * Dua perubahan yang dibawa versi 2:
         *   1. Tabel `tasks` mendapat kolom baru `completed_pomodoros`
         *   2. Tabel baru `pomodoro_sessions` beserta indeksnya
         *
         * KENAPA INI PENTING: sebelumnya database memakai
         * fallbackToDestructiveMigration, yang artinya setiap kenaikan versi
         * akan MENGHAPUS SELURUH data pengguna. Dengan Migration ini, tugas
         * yang sudah pengguna buat tetap utuh saat aplikasi diperbarui.
         *
         * Perhatikan penulisan SQL-nya: DDL di bawah harus sama persis dengan
         * yang dihasilkan Room untuk versi 2 - termasuk tanda petik balik,
         * urutan kolom, dan klausa ON UPDATE. Kalau meleset satu karakter pun,
         * MigrationTest akan gagal dengan pesan bahwa skema hasil migrasi
         * berbeda dari skema yang diharapkan. Cara paling aman menuliskannya
         * adalah menyalin dari berkas app/schemas/...2.json bagian "createSql",
         * lalu mengganti ${'$'}{TABLE_NAME} dengan nama tabel sesungguhnya.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Kolom baru wajib punya DEFAULT karena baris-baris lama sudah
                // terlanjur ada dan harus diberi nilai. Tanpa DEFAULT, SQLite
                // menolak menambah kolom NOT NULL pada tabel yang berisi data.
                db.execSQL(
                    "ALTER TABLE `tasks` ADD COLUMN `completed_pomodoros` INTEGER NOT NULL DEFAULT 0"
                )

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `pomodoro_sessions` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`task_id` INTEGER NOT NULL, " +
                        "`started_at` INTEGER NOT NULL, " +
                        "`duration_minutes` INTEGER NOT NULL, " +
                        "`is_completed` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`task_id`) REFERENCES `tasks`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_pomodoro_sessions_task_id` " +
                        "ON `pomodoro_sessions` (`task_id`)"
                )
            }
        }

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
                    // fallbackToDestructiveMigration SUDAH DIHAPUS dan diganti
                    // Migration sungguhan. Sejak baris ini ada, data pengguna
                    // tidak lagi hilang saat versi database naik.
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
