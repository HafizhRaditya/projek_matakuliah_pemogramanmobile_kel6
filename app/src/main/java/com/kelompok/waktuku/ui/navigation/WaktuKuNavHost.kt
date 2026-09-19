package com.kelompok.waktuku.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kelompok.waktuku.ui.screens.HomeScreen
import com.kelompok.waktuku.ui.screens.PlaceholderScreen
import com.kelompok.waktuku.ui.screens.TimerScreen

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 4 (Navigasi & Integrasi Sistem)
// ============================================================================
// NavHost adalah "peta jalan" aplikasi: ia mendaftarkan setiap alamat rute ke
// layar yang harus digambar. Hanya SATU layar aktif pada satu waktu, dan
// NavHost yang mengganti isinya saat rute berubah.
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
        startDestination = WaktuKuRoutes.HOME,
        modifier = modifier,
    ) {

        // ------------------------------------------------------------------
        // BERANDA - satu-satunya layar yang sudah jadi
        // ------------------------------------------------------------------
        composable(route = WaktuKuRoutes.HOME) {
            // Di sinilah kejadian dari Beranda diterjemahkan menjadi
            // perpindahan layar. HomeScreen sendiri tidak tahu apa-apa soal
            // NavController - ia hanya melaporkan "kartu ditekan", dan berkas
            // inilah yang memutuskan artinya "buka Detail Tugas".
            //
            // Pemisahan ini yang membuat HomeScreen tetap bisa di-@Preview
            // tanpa navigasi sama sekali.
            HomeScreen(
                onTaskClick = { taskId ->
                    navController.navigate(WaktuKuRoutes.taskDetail(taskId))
                },
                onStartFocus = { taskId ->
                    navController.navigate(WaktuKuRoutes.timer(taskId))
                },
                onOpenSettings = {
                    navController.navigate(WaktuKuRoutes.SETTINGS)
                },
            )
        }

        // ------------------------------------------------------------------
        // FOKUS / TIMER POMODORO - argumen taskId bersifat OPSIONAL
        // ------------------------------------------------------------------
        composable(
            route = WaktuKuRoutes.TIMER_ROUTE,
            arguments = listOf(
                navArgument(WaktuKuRoutes.ARG_TASK_ID) {
                    // Tipe argumen ditegaskan sebagai Long agar tidak perlu
                    // mengubah teks menjadi angka secara manual.
                    type = NavType.LongType
                    // defaultValue membuat argumen ini boleh tidak dikirim.
                    // Tanpa baris ini, membuka "timer" tanpa taskId akan crash.
                    defaultValue = WaktuKuRoutes.NO_TASK_ID
                },
            ),
        ) { backStackEntry ->
            // Begini cara membaca argumen yang dikirim lewat rute.
            val taskId = backStackEntry.arguments
                ?.getLong(WaktuKuRoutes.ARG_TASK_ID)
                ?: WaktuKuRoutes.NO_TASK_ID

            // Layar Fokus sungguhan (F3). TimerScreen menerima taskId apa
            // adanya - termasuk NO_TASK_ID saat dibuka dari tab Fokus tanpa
            // memilih tugas, dan ia sendiri yang menampilkan penjelasannya.
            TimerScreen(taskId = taskId)
        }

        // ------------------------------------------------------------------
        // STATISTIK
        // ------------------------------------------------------------------
        composable(route = WaktuKuRoutes.STATS) {
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
        composable(
            route = WaktuKuRoutes.TASK_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(WaktuKuRoutes.ARG_TASK_ID) { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments
                ?.getLong(WaktuKuRoutes.ARG_TASK_ID)
                ?: WaktuKuRoutes.NO_TASK_ID

            // CATATAN UNTUK MAHASISWA 1 + 3 (fitur F5):
            PlaceholderScreen(
                title = "Detail Tugas",
                penanggungJawab = "Mahasiswa 1 + 3",
                keterangan = "Ubah judul, catatan, prioritas, tenggat, dan " +
                    "target sesi untuk tugas dengan id $taskId.",
                // Layar ini dicapai dari Beranda, bukan dari tab, jadi ia
                // butuh tombol kembali.
                onBack = { navController.popBackStack() },
            )
        }

        // ------------------------------------------------------------------
        // PENGATURAN
        // ------------------------------------------------------------------
        composable(route = WaktuKuRoutes.SETTINGS) {
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
