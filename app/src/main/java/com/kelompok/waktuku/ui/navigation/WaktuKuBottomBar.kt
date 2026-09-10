package com.kelompok.waktuku.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 4 (Navigasi & Integrasi Sistem)
// ============================================================================
// Bilah navigasi bawah berisi tiga tab: Beranda, Fokus, Statistik.
//
// Komponen ini STATELESS - ia tidak memegang NavController dan tidak tahu cara
// berpindah layar. Ia hanya menerima "tujuan mana yang sedang aktif" lalu
// melaporkan "tab ini ditekan" ke atas. Sama persis dengan pola yang dipakai
// TaskCard: state hoisting.
// ============================================================================

/**
 * @param currentDestination tujuan yang sedang ditampilkan, dari NavController.
 * @param onNavigate dilaporkan saat sebuah tab ditekan.
 */
@Composable
fun WaktuKuBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        TopLevelDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination.isOn(destination),
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        // null karena label teksnya sudah tepat di bawah ikon,
                        // jadi pembaca layar tidak perlu membacanya dua kali.
                        contentDescription = null,
                    )
                },
                label = { Text(destination.label) },
                // Label selalu ditampilkan, tidak hanya saat tab aktif.
                // Ikon tanpa teks memaksa pengguna menebak artinya.
                alwaysShowLabel = true,
            )
        }
    }
}

/**
 * Memeriksa apakah tab ini sedang aktif.
 *
 * Memakai `hierarchy`, bukan sekadar membandingkan `route` secara langsung.
 * Alasannya: `hierarchy` menelusuri tujuan saat ini beserta seluruh induknya
 * di dalam grafik navigasi. Jadi kalau nanti tab Fokus dikembangkan menjadi
 * beberapa layar bersarang, tabnya tetap tersorot dengan benar - tidak perlu
 * mengubah kode ini lagi.
 */
private fun NavDestination?.isOn(destination: TopLevelDestination): Boolean =
    this?.hierarchy?.any { it.route == destination.route } == true
