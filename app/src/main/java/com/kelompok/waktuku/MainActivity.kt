package com.kelompok.waktuku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kelompok.waktuku.ui.screens.HomeScreen
import com.kelompok.waktuku.ui.theme.WaktuKuTheme

// ============================================================================
// SATU-SATUNYA Activity di aplikasi ini (pola single-activity).
// Seluruh layar setelah ini adalah Composable, bukan Activity/Fragment baru.
// ============================================================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menggambar konten sampai ke belakang status bar & navigation bar.
        enableEdgeToEdge()
        setContent {
            WaktuKuTheme {
                // Nanti saat WaktuKu punya lebih dari satu layar (Home,
                // Pomodoro, Statistik), baris ini diganti dengan NavHost.
                HomeScreen()
            }
        }
    }
}
