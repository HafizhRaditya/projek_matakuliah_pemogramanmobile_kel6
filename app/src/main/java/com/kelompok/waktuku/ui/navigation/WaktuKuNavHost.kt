package com.kelompok.waktuku.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kelompok.waktuku.ui.screens.HomeScreen
import com.kelompok.waktuku.ui.screens.PlaceholderScreen
import com.kelompok.waktuku.ui.screens.TaskDetailScreen
import com.kelompok.waktuku.ui.screens.TimerScreen

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 4 (Navigasi & Integrasi Sistem)
// ============================================================================
// NavHost adalah "peta jalan" aplikasi: ia mendaftarkan setiap rute ke layar
// yang harus digambar. Hanya SATU layar aktif pada satu waktu, dan NavHost
// yang mengganti isinya saat rute berubah.
//
// Sejak memakai type-safe navigation, pendaftarannya berbentuk
//     composable<NamaRute> { ... }
// dan perpindahan layar ditulis sebagai
//     navController.navigate(TaskDetailRoute(taskId = 5))
// Tidak ada lagi teks rute yang dirangkai manual.
//
// Perhatikan bahwa NavHost tidak memuat TopAppBar maupun bottom bar. Kerangka
// itu berada di WaktuKuApp.kt, satu tingkat di atas, supaya bilah bawah tidak
// ikut digambar ulang setiap kali layar berganti.
// ============================================================================

@Composable
fun WaktuKuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        // Layar pertama yang dibuka saat aplikasi dijalankan.
        startDestination = HomeRoute,
        modifier = modifier,
    ) {

        // ------------------------------------------------------------------
        // BERANDA
        // ------------------------------------------------------------------
        composable<HomeRoute> {
            // Di sinilah kejadian dari Beranda diterjemahkan menjadi
            // perpindahan layar. HomeScreen sendiri tidak tahu apa-apa soal
            // NavController - ia hanya melaporkan "kartu ditekan", dan berkas
            // inilah yang memutuskan artinya "buka Detail Tugas".
            //
            // Pemisahan ini yang membuat HomeScreen tetap bisa di-@Preview
            // tanpa navigasi sama sekali.
            HomeScreen(
                onTaskClick = { taskId ->
                    navController.navigate(TaskDetailRoute(taskId = taskId))
                },
                onStartFocus = { taskId ->
                    navController.navigate(TimerRoute(taskId = taskId))
                },
                onOpenSettings = {
                    navController.navigate(SettingsRoute)
                },
            )
        }

        // ------------------------------------------------------------------
        // FOKUS / TIMER POMODORO - argumen taskId OPSIONAL
        // ------------------------------------------------------------------
        composable<TimerRoute> { backStackEntry ->
            // toRoute() mengubah argumen kembali menjadi objek TimerRoute.
            // Bila layar dibuka dari tab tanpa taskId, nilai bawaannya
            // (NO_TASK_ID) yang dipakai.
            val route = backStackEntry.toRoute<TimerRoute>()
            TimerScreen(taskId = route.taskId)
        }

        // ------------------------------------------------------------------
        // STATISTIK
        // ------------------------------------------------------------------
        composable<StatsRoute> {
            // CATATAN UNTUK MAHASISWA 3 + 1 (fitur F6):
            PlaceholderScreen(
                title = "Statistik",
                penanggungJawab = "Mahasiswa 3 + 1",
                keterangan = "Total sesi dan menit fokus hari ini, diagram " +
                    "batang 7 hari terakhir, serta jumlah tugas selesai minggu ini.",
            )
        }

        // ------------------------------------------------------------------
        // DETAIL TUGAS - argumen taskId WAJIB
        // ------------------------------------------------------------------
        composable<TaskDetailRoute> {
            // Tidak ada taskId yang diteruskan di sini. TaskDetailViewModel
            // membacanya sendiri dari SavedStateHandle, yang diisi otomatis
            // oleh Navigation dengan argumen TaskDetailRoute.
            TaskDetailScreen(
                // Layar ini dicapai dari Beranda, bukan dari tab, jadi ia
                // butuh jalan kembali. Dipanggil juga setelah tugas disimpan
                // atau dihapus.
                onBack = { navController.popBackStack() },
            )
        }

        // ------------------------------------------------------------------
        // PENGATURAN
        // ------------------------------------------------------------------
        composable<SettingsRoute> {
            // CATATAN UNTUK MAHASISWA 4 + 1 (fitur F7):
            PlaceholderScreen(
                title = "Pengaturan",
                penanggungJawab = "Mahasiswa 4 + 1",
                keterangan = "Durasi fokus dan istirahat, mode gelap, serta " +
                    "sakelar notifikasi. Nilainya disimpan memakai DataStore.",
                onBack = { navController.popBackStack() },
            )
        }
    }
}
