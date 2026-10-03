package com.kelompok.waktuku.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kelompok.waktuku.ui.screens.HomeScreen
import com.kelompok.waktuku.ui.screens.TaskDetailScreen
import com.kelompok.waktuku.ui.screens.TimerScreen
import com.kelompok.waktuku.ui.viewmodel.PomodoroViewModel

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
// Peta ini memuat tiga layar: Beranda, Fokus, dan Detail Tugas. Layar
// Statistik dan Pengaturan ditunda sampai setelah UTS (lihat PRD bagian 9),
// jadi rutenya belum didaftarkan di sini.
//
// Perhatikan bahwa NavHost tidak memuat TopAppBar maupun bottom bar. Kerangka
// itu berada di WaktuKuApp.kt, satu tingkat di atas, supaya bilah bawah tidak
// ikut digambar ulang setiap kali layar berganti.
// ============================================================================

/**
 * Mode demo untuk video dan presentasi.
 *
 * true  -> fokus 5 detik, istirahat pendek 1 detik, istirahat panjang 3 detik
 * false -> durasi asli 25 / 5 / 15 menit
 *
 * Ubah menjadi true, build ulang, lalu rekam video. Kembalikan ke false
 * sesudahnya. Durasinya sendiri ditentukan PomodoroViewModel (DURASI_DEMO).
 */
private const val MODE_DEMO = false

@Composable
fun WaktuKuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // ----------------------------------------------------------------------
    // SATU TIMER UNTUK SELURUH APLIKASI
    // ----------------------------------------------------------------------
    // viewModel() di sini dipanggil DI LUAR blok composable<...>, jadi
    // ViewModel-nya milik Activity, bukan milik layar Fokus. Akibatnya:
    //   - timer tetap berjalan walau pengguna pindah ke Beranda atau menekan
    //     tombol kembali di layar Fokus
    //   - hanya ada satu timer; membuka layar Fokus berkali-kali tidak
    //     membuat timer baru
    // Polanya sama dengan ProductViewModel di praktikum Pertemuan 5, yang
    // dibuat sekali di Activity lalu dikirim ke beberapa layar.
    //
    // Sebelumnya ViewModel ini dibuat di dalam TimerScreen, sehingga terikat
    // pada entri layar Fokus di back stack. Menekan tombol kembali menghapus
    // entri itu beserta timernya.
    val pomodoroViewModel: PomodoroViewModel = viewModel(
        factory = if (MODE_DEMO) PomodoroViewModel.DemoFactory else PomodoroViewModel.Factory,
    )

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
            TimerScreen(taskId = route.taskId, viewModel = pomodoroViewModel)
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
    }
}
