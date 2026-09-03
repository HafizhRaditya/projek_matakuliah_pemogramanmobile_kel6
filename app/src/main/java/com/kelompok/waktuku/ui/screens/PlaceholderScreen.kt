package com.kelompok.waktuku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelompok.waktuku.ui.theme.WaktuKuTheme

// ============================================================================
// BERKAS SEMENTARA - dibuat oleh Mahasiswa 4 sebagai perancah navigasi.
// ============================================================================
// Layar Timer, Statistik, Pengaturan, dan Detail Tugas belum dikerjakan. Tanpa
// isi sementara, NavHost tidak bisa dibangun dan tidak ada yang bisa menguji
// perpindahan antar layar.
//
// CARA MENGGANTINYA nanti: di WaktuKuNavHost.kt, ganti pemanggilan
// PlaceholderScreen(...) dengan layar aslinya. Berkas ini dihapus setelah
// keempat layar selesai dibuat.
// ============================================================================

/**
 * @param title judul layar yang tampil di TopAppBar.
 * @param penanggungJawab anggota kelompok yang akan mengerjakan layar ini.
 * @param keterangan penjelasan singkat isi layar sesuai PRD.
 * @param onBack bila diisi, tombol panah kembali ditampilkan. Layar yang
 *        dicapai lewat tab bawah tidak perlu tombol ini.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    penanggungJawab: String,
    keterangan: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
            Text(
                text = "Layar ini belum dikerjakan",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = keterangan,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Penanggung jawab: $penanggungJawab",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderScreenPreview() {
    WaktuKuTheme {
        PlaceholderScreen(
            title = "Fokus",
            penanggungJawab = "Mahasiswa 2 + 3",
            keterangan = "Timer Pomodoro 25/5/15 yang terikat pada satu tugas.",
        )
    }
}
