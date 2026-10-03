package com.kelompok.waktuku.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelompok.waktuku.ui.navigation.TopLevelDestination
import com.kelompok.waktuku.ui.navigation.WaktuKuBottomBar
import com.kelompok.waktuku.ui.navigation.WaktuKuNavHost

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

    // Bilah bawah hanya muncul di tujuan utama (Beranda dan Fokus). Di layar
    // Detail Tugas ia disembunyikan, karena itu layar "masuk lebih dalam"
    // yang jalan keluarnya lewat tombol kembali, bukan pindah tab.
    // hasRoute mencocokkan KELAS rute, jadi tab Fokus tetap dikenali baik saat
    // dibuka dengan TimerRoute() maupun TimerRoute(taskId = 5).
    val tampilkanBottomBar = TopLevelDestination.entries.any { destination ->
        currentDestination?.hierarchy?.any { it.hasRoute(destination.routeClass) } == true
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
        // innerPadding berisi tinggi bilah bawah DAN tinggi status bar di atas.
        // Scaffold ini tidak punya topBar, jadi ia sendiri yang menyisihkan
        // ruang untuk status bar.
        WaktuKuNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                // padding() hanya menggeser isi. consumeWindowInsets() yang
                // memberi tahu layar di dalamnya bahwa ruang itu SUDAH diurus.
                // Tanpa baris ini, TopAppBar di tiap layar menambahkan tinggi
                // status bar sekali lagi dan muncul pita kosong di atas judul.
                .consumeWindowInsets(innerPadding),
        )
    }
}

/**
 * Berpindah tab dengan perilaku yang benar.
 *
 * Dua pengaturan di bawah ini yang membedakan bottom navigation yang terasa
 * wajar dengan yang menjengkelkan:
 *
 * 1. `popUpTo(startDestination)`
 *    Membersihkan tumpukan layar sampai Beranda sebelum pindah tab. Tanpa
 *    ini, berpindah tab bolak-balik sepuluh kali akan menumpuk sepuluh layar,
 *    dan pengguna harus menekan tombol kembali sepuluh kali untuk keluar.
 *
 * 2. `launchSingleTop = true`
 *    Menekan tab yang sedang aktif tidak membuat salinan layar baru.
 *
 * Kenapa TIDAK memakai `saveState` dan `restoreState` seperti contoh resmi?
 * Keduanya menyimpan tumpukan layar yang dibuang, lalu memulihkannya saat
 * tujuan yang sama dibuka lagi. Di WaktuKu, layar Fokus juga bisa dibuka dari
 * tombol putar di kartu tugas, sehingga ia menumpuk di atas Beranda. Dengan
 * `saveState`, tumpukan [Fokus] itu tersimpan atas nama Beranda, dan menekan
 * tab Beranda langsung memulihkannya: pengguna terlempar balik ke layar Fokus
 * dan seolah tidak bisa kembali ke Beranda.
 *
 * Tanpa keduanya pun tidak ada yang hilang: Beranda tidak pernah dibuang
 * karena ia tujuan awal, dan timer tersimpan di ViewModel milik Activity
 * (lihat WaktuKuNavHost), bukan di layar Fokus.
 */
private fun NavHostController.navigateToTab(destination: TopLevelDestination) {

    // destination.route adalah objek rute, misalnya HomeRoute atau
    // TimerRoute(). Dengan type-safe navigation tidak perlu lagi membedakan
    // "pola alamat" dan "alamat tujuan" seperti saat rute masih berupa teks.
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id)
        launchSingleTop = true
    }
}
