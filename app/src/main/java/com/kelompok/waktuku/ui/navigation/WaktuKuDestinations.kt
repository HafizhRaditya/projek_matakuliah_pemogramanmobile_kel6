package com.kelompok.waktuku.ui.navigation

import androidx.annotation.DrawableRes
import com.kelompok.waktuku.R
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 4 (Navigasi & Integrasi Sistem)
// ============================================================================
// Daftar seluruh alamat layar di WaktuKu, memakai TYPE-SAFE NAVIGATION.
//
// Sebelumnya rute ditulis sebagai teks, misalnya "task/{taskId}". Masalah rute
// teks: compiler tidak memeriksanya. Salah ketik "taks/5", lupa mengirim
// argumen, atau mengirim teks ke argumen bertipe angka, semuanya lolos build
// lalu crash saat aplikasi dijalankan.
//
// Dengan type-safe navigation, setiap rute adalah KELAS Kotlin:
//   - rute tanpa argumen  -> `data object`, contoh HomeRoute
//   - rute dengan argumen -> `data class`,  contoh TaskDetailRoute(taskId)
// Anotasi @Serializable membuat Navigation bisa mengubah objek ini menjadi
// alamat dan sebaliknya. Kesalahan tipe kini ketahuan saat compile, bukan
// saat aplikasi sudah di tangan pengguna.
// ============================================================================

/** Nilai penanda "tidak ada tugas yang dipilih". */
const val NO_TASK_ID = -1L

/** Beranda: daftar tugas. */
@Serializable
data object HomeRoute

/**
 * Layar Fokus (timer Pomodoro).
 *
 * taskId punya nilai bawaan, artinya argumen ini OPSIONAL. Layar Fokus bisa
 * dibuka dari tab Fokus tanpa memilih tugas (TimerRoute()), atau dari tombol
 * mulai di kartu tugas (TimerRoute(taskId = 5)).
 */
@Serializable
data class TimerRoute(val taskId: Long = NO_TASK_ID)

/**
 * Detail Tugas. taskId WAJIB, karena layar ini tidak berarti apa-apa tanpa
 * tugas yang dibuka. Nama propertinya "taskId" juga menjadi kunci yang dibaca
 * TaskDetailViewModel dari SavedStateHandle.
 */
@Serializable
data class TaskDetailRoute(val taskId: Long)

/** Statistik. */
@Serializable
data object StatsRoute

/** Pengaturan. */
@Serializable
data object SettingsRoute

/**
 * Tiga tujuan utama yang muncul sebagai tab di bottom navigation.
 *
 * Dibuat enum, bukan daftar biasa, supaya menambah tab baru cukup dilakukan di
 * sini - berkas bottom bar tidak perlu disentuh sama sekali.
 *
 * Catatan: Pengaturan sengaja TIDAK dijadikan tab. Panduan Material Design
 * menganjurkan bottom navigation hanya diisi 3-5 tujuan yang sering dipakai,
 * sedangkan Pengaturan jarang dibuka. Ia dicapai lewat ikon gerigi di TopAppBar.
 *
 * @param route objek rute yang dituju saat tab ditekan.
 * @param routeClass kelas rute, dipakai untuk mengecek tab mana yang aktif.
 *        Yang dicocokkan kelasnya, bukan objeknya: tab Fokus harus tetap
 *        tersorot baik saat dibuka dengan TimerRoute() maupun TimerRoute(5).
 */
enum class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    val label: String,
    // Ikon disimpan sebagai berkas XML di res/drawable, bukan diambil dari
    // pustaka material-icons-core. Pustaka itu hanya berisi 49 ikon dan tidak
    // punya ikon timer maupun diagram batang. Menambah material-icons-extended
    // demi dua ikon terlalu mahal karena ukurannya besar sekali.
    // @param:DrawableRes membuat lint menolak angka yang bukan id drawable.
    @param:DrawableRes val iconRes: Int,
) {
    BERANDA(
        route = HomeRoute,
        routeClass = HomeRoute::class,
        label = "Beranda",
        iconRes = R.drawable.ic_home,
    ),
    FOKUS(
        // Dibuka dari tab berarti belum memilih tugas.
        route = TimerRoute(),
        routeClass = TimerRoute::class,
        label = "Fokus",
        // Ikon jam henti menandakan TEMPAT (layar timer). Ini sengaja dibedakan
        // dari ikon segitiga "mulai" di kartu tugas, yang menandakan AKSI.
        iconRes = R.drawable.ic_timer,
    ),
    STATISTIK(
        route = StatsRoute,
        routeClass = StatsRoute::class,
        label = "Statistik",
        iconRes = R.drawable.ic_bar_chart,
    ),
}
