package com.kelompok.waktuku

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kelompok.waktuku.data.WaktuKuDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3 (Model & Local Storage)
// ============================================================================
// Menguji migrasi database versi 1 ke versi 2.
//
// KENAPA UJI INI ADA. Tabel risiko di PRD bagian 12 menempatkan "Migration
// Room salah" sebagai risiko dengan dampak terparah: data pengguna hilang,
// atau aplikasi crash saat dibuka. Bedanya dengan bug lain, kesalahan migrasi
// baru ketahuan setelah aplikasi terpasang di perangkat orang lain - saat itu
// datanya sudah terlanjur rusak dan tidak bisa dikembalikan.
//
// Ini uji INSTRUMENTASI, bukan unit test. Ia butuh perangkat atau emulator
// karena menjalankan SQLite Android yang sesungguhnya.
//
// Cara menjalankan:
//   ./gradlew :app:connectedDebugAndroidTest
// atau klik ikon segitiga hijau di sebelah nama kelas ini di Android Studio.
// ============================================================================

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val namaDbUji = "migration-test-waktuku"

    /**
     * MigrationTestHelper membaca berkas app/schemas/...json untuk membuat
     * database dengan struktur versi lama, lalu memverifikasi hasil migrasi.
     */
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        WaktuKuDatabase::class.java,
    )

    /**
     * Uji terpenting: tugas yang sudah dibuat pengguna di versi 1 harus tetap
     * utuh setelah aplikasi diperbarui ke versi 2.
     */
    @Test
    fun migrasi1Ke2_dataTugasLamaTetapUtuh() {
        // 1. Buat database versi 1 dan isi satu tugas, seolah-olah ini
        //    ponsel pengguna yang sudah memakai aplikasi versi lama.
        helper.createDatabase(namaDbUji, 1).apply {
            execSQL(
                """
                INSERT INTO tasks
                    (id, title, notes, due_at, priority, is_done, estimated_pomodoros, created_at)
                VALUES
                    (1, 'Belajar UTS Basis Data', 'Bab 1 sampai 5', 1772236800000, 'HIGH', 0, 4, 1772150400000)
                """.trimIndent()
            )
            close()
        }

        // 2. Jalankan migrasi. Parameter validateDroppedTables = true membuat
        //    Room membandingkan struktur hasil migrasi dengan skema versi 2
        //    yang diharapkan. Kalau SQL di Migration meleset sedikit saja,
        //    baris inilah yang gagal.
        val db = helper.runMigrationsAndValidate(
            namaDbUji,
            2,
            true,
            WaktuKuDatabase.MIGRATION_1_2,
        )

        // 3. Pastikan datanya masih ada dan kolom barunya terisi nilai bawaan.
        db.query(
            "SELECT title, notes, estimated_pomodoros, completed_pomodoros FROM tasks WHERE id = 1"
        ).use { cursor ->
            assertTrue("Tugas lama hilang setelah migrasi", cursor.moveToFirst())
            assertEquals("Belajar UTS Basis Data", cursor.getString(0))
            assertEquals("Bab 1 sampai 5", cursor.getString(1))
            assertEquals(4, cursor.getInt(2))
            // Kolom baru harus bernilai 0, bukan NULL, berkat DEFAULT 0.
            assertEquals(0, cursor.getInt(3))
        }
        db.close()
    }

    /**
     * Memastikan tabel sesi Pomodoro benar-benar terbentuk dan bisa diisi.
     */
    @Test
    fun migrasi1Ke2_tabelSesiPomodoroTerbentukDanBisaDiisi() {
        helper.createDatabase(namaDbUji, 1).apply {
            execSQL(
                """
                INSERT INTO tasks
                    (id, title, notes, due_at, priority, is_done, estimated_pomodoros, created_at)
                VALUES
                    (7, 'Rancang UI WaktuKu', '', NULL, 'MEDIUM', 0, 2, 1772150400000)
                """.trimIndent()
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(
            namaDbUji,
            2,
            true,
            WaktuKuDatabase.MIGRATION_1_2,
        )

        db.execSQL(
            """
            INSERT INTO pomodoro_sessions
                (task_id, started_at, duration_minutes, is_completed)
            VALUES
                (7, 1772150400000, 25, 1)
            """.trimIndent()
        )

        db.query(
            "SELECT task_id, duration_minutes, is_completed FROM pomodoro_sessions"
        ).use { cursor ->
            assertTrue("Sesi gagal disimpan", cursor.moveToFirst())
            assertEquals(7, cursor.getInt(0))
            assertEquals(25, cursor.getInt(1))
            assertEquals(1, cursor.getInt(2))
        }
        db.close()
    }
}
