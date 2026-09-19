package com.kelompok.waktuku.ui.navigation

import androidx.annotation.DrawableRes
import com.kelompok.waktuku.R

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 4 (Navigasi & Integrasi Sistem)
// ============================================================================
// Daftar seluruh alamat layar di WaktuKu.
//
// Kenapa rute dikumpulkan di satu berkas dan bukan ditulis sebagai teks biasa
// di tempat masing-masing? Karena rute adalah teks, dan teks itu tidak dicek
// oleh compiler. Kalau di satu tempat ditulis "home" lalu di tempat lain
// "Home", aplikasi tetap ter-compile tapi crash saat dijalankan. Dengan
// dikumpulkan di sini, salah ketik langsung ketahuan sebagai error merah.
// ============================================================================

object WaktuKuRoutes {

    // --- Rute tanpa argumen ---
    const val HOME = "home"
    const val STATS = "stats"
    const val SETTINGS = "settings"

    // --- Nama argumen ---
    const val ARG_TASK_ID = "taskId"

    /** Nilai penanda "tidak ada tugas yang dipilih". */
    const val NO_TASK_ID = -1L

    // --- Rute dengan argumen ---
    // Pola penulisan Navigation Compose:
    //   {argumen}   -> argumen WAJIB, contoh "task/5"
    //   ?nama={...} -> argumen OPSIONAL, contoh "timer" atau "timer?taskId=5"
    //
    // Timer memakai argumen opsional karena layar itu bisa dibuka lewat dua
    // jalan: dari tab Fokus (belum memilih tugas) atau dari kartu tugas di
    // Beranda (sudah membawa tugas tertentu).
    const val TIMER_ROUTE = "timer?$ARG_TASK_ID={$ARG_TASK_ID}"
    const val TASK_DETAIL_ROUTE = "task/{$ARG_TASK_ID}"

    /**
     * Membangun alamat layar Timer.
     *
     * Fungsi pembangun seperti ini mencegah kesalahan merangkai teks secara
     * manual di banyak tempat. Cukup panggil `WaktuKuRoutes.timer(task.id)`.
     */
    fun timer(taskId: Long = NO_TASK_ID): String = "timer?$ARG_TASK_ID=$taskId"

    /** Membangun alamat layar Detail Tugas. */
    fun taskDetail(taskId: Long): String = "task/$taskId"
}

/**
 * Tiga tujuan utama yang muncul sebagai tab di bottom navigation.
 *
 * Dibuat enum, bukan daftar biasa, supaya menambah tab baru cukup dilakukan di
 * sini - berkas bottom bar tidak perlu disentuh sama sekali.
 *
 * Catatan: Pengaturan sengaja TIDAK dijadikan tab. Panduan Material Design
 * menganjurkan bottom navigation hanya diisi 3-5 tujuan yang sering dipakai,
 * sedangkan Pengaturan jarang dibuka. Ia dicapai lewat ikon gerigi di TopAppBar.
 */
enum class TopLevelDestination(
    val route: String,
    val label: String,
    // Ikon disimpan sebagai berkas XML di res/drawable, bukan diambil dari
    // pustaka material-icons-core. Pustaka itu hanya berisi 49 ikon dan tidak
    // punya ikon timer maupun diagram batang. Menambah material-icons-extended
    // demi dua ikon terlalu mahal karena ukurannya besar sekali.
    // @param:DrawableRes membuat lint menolak angka yang bukan id drawable.
    @param:DrawableRes val iconRes: Int,
) {
    BERANDA(
        route = WaktuKuRoutes.HOME,
        label = "Beranda",
        iconRes = R.drawable.ic_home,
    ),
    FOKUS(
        // Memakai pola rute lengkap (dengan argumen opsional) supaya
        // pencocokan tab aktif tetap benar walau timer dibuka membawa taskId.
        route = WaktuKuRoutes.TIMER_ROUTE,
        label = "Fokus",
        // Ikon jam henti menandakan TEMPAT (layar timer). Ini sengaja dibedakan
        // dari ikon segitiga "mulai" di kartu tugas, yang menandakan AKSI.
        iconRes = R.drawable.ic_timer,
    ),
    STATISTIK(
        route = WaktuKuRoutes.STATS,
        label = "Statistik",
        iconRes = R.drawable.ic_bar_chart,
    ),
}
