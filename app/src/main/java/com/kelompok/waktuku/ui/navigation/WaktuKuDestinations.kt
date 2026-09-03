package com.kelompok.waktuku.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

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
    val icon: ImageVector,
) {
    BERANDA(
        route = WaktuKuRoutes.HOME,
        label = "Beranda",
        icon = Icons.Default.Home,
    ),
    FOKUS(
        // Memakai pola rute lengkap (dengan argumen opsional) supaya
        // pencocokan tab aktif tetap benar walau timer dibuka membawa taskId.
        route = WaktuKuRoutes.TIMER_ROUTE,
        label = "Fokus",
        icon = Icons.Default.PlayArrow,
    ),
    STATISTIK(
        route = WaktuKuRoutes.STATS,
        label = "Statistik",
        // Ikon diagram batang hanya tersedia di material-icons-extended yang
        // ukurannya besar. DateRange dipilih karena statistik WaktuKu memang
        // berbasis rentang waktu (rekap 7 hari terakhir).
        icon = Icons.Default.DateRange,
    ),
}
