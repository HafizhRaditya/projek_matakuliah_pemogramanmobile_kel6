package com.kelompok.waktuku.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelompok.waktuku.ui.navigation.TopLevelDestination
import com.kelompok.waktuku.ui.navigation.WaktuKuBottomBar
import com.kelompok.waktuku.ui.navigation.WaktuKuNavHost
import com.kelompok.waktuku.ui.navigation.WaktuKuRoutes

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 4 (Navigasi & Integrasi Sistem)
// ============================================================================
// Kerangka utama aplikasi. Berkas ini setara dengan JetnewsApp.kt pada contoh
// resmi android/compose-samples.
//
// Tugasnya cuma dua:
//   1. Menyediakan bilah navigasi bawah yang tetap terlihat saat layar berganti
//   2. Menempatkan NavHost sebagai isi yang berubah-ubah
// ============================================================================

@Composable
fun WaktuKuApp(modifier: Modifier = Modifier) {

    // rememberNavController menyimpan NavController beserta riwayat layarnya.
    // Karena ia memakai penyimpanan yang tahan perubahan konfigurasi, memutar
    // layar TIDAK akan melemparkan pengguna kembali ke Beranda.
    val navController = rememberNavController()

    // Mengamati layar yang sedang aktif. Nilainya berubah setiap kali pengguna
    // berpindah, sehingga tab yang tersorot ikut menyesuaikan dengan sendirinya.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // Bilah bawah hanya muncul di tiga tujuan utama. Di layar Detail Tugas dan
    // Pengaturan ia disembunyikan, karena keduanya adalah layar "masuk lebih
    // dalam" yang jalan keluarnya lewat tombol kembali, bukan pindah tab.
    val tampilkanBottomBar = TopLevelDestination.entries.any { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (tampilkanBottomBar) {
                WaktuKuBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = navController::navigateToTab,
                )
            }
        },
    ) { innerPadding ->
        // innerPadding berisi tinggi bilah bawah. Diteruskan ke NavHost supaya
        // isi layar tidak tertutup bilah navigasi.
        WaktuKuNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

/**
 * Berpindah tab dengan perilaku yang benar.
 *
 * Tiga pengaturan di bawah ini yang membedakan bottom navigation yang terasa
 * wajar dengan yang menjengkelkan:
 *
 * 1. `popUpTo(startDestination) { saveState = true }`
 *    Membersihkan tumpukan layar sampai Beranda, sambil MENYIMPAN kondisi tab
 *    yang ditinggalkan. Tanpa ini, berpindah tab bolak-balik sepuluh kali akan
 *    menumpuk sepuluh layar, dan pengguna harus menekan tombol kembali sepuluh
 *    kali untuk keluar dari aplikasi.
 *
 * 2. `launchSingleTop = true`
 *    Menekan tab yang sedang aktif tidak membuat salinan layar baru.
 *
 * 3. `restoreState = true`
 *    Mengembalikan kondisi tab saat dibuka lagi - misalnya posisi gulir daftar
 *    tugas dan filter yang sedang dipilih.
 */
private fun NavHostController.navigateToTab(destination: TopLevelDestination) {

    // Tab Fokus perlu perlakuan khusus. Nilai `route` miliknya adalah POLA
    // alamat ("timer?taskId={taskId}") yang dipakai untuk mencocokkan tab
    // aktif, bukan alamat yang bisa dituju. Alamat sesungguhnya dibangun lewat
    // WaktuKuRoutes.timer() tanpa argumen, artinya "belum memilih tugas".
    val route = when (destination) {
        TopLevelDestination.FOKUS -> WaktuKuRoutes.timer()
        else -> destination.route
    }

    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
