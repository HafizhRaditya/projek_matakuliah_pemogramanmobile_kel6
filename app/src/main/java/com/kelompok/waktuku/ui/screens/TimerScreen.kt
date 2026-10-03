package com.kelompok.waktuku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelompok.waktuku.ui.theme.WaktuKuTheme
import com.kelompok.waktuku.ui.viewmodel.PomodoroPhase
import com.kelompok.waktuku.ui.viewmodel.PomodoroViewModel
import com.kelompok.waktuku.ui.viewmodel.TimerUiState

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 1 (UI/UX), memakai logika dari Mahasiswa 2
// ============================================================================
// Layar Fokus: hitung mundur Pomodoro untuk satu tugas.
//
// Seperti HomeScreen, layar ini dipecah dua: versi stateful yang mengambil
// state dari PomodoroViewModel, dan versi stateless yang hanya menerima
// TimerUiState. Versi stateless itulah yang membuat @Preview di bawah bisa
// menggambar timer dalam berbagai keadaan tanpa menunggu 25 menit.
// ============================================================================

/**
 * Versi stateful.
 *
 * @param taskId tugas yang akan difokuskan. Bernilai NO_TASK bila layar
 *        dibuka lewat tab Fokus tanpa memilih tugas lebih dulu.
 * @param viewModel dibuat SEKALI di WaktuKuNavHost dan dikirim ke sini, bukan
 *        dibuat di layar ini. Dengan begitu timer tidak ikut hilang saat
 *        pengguna meninggalkan layar Fokus.
 */
@Composable
fun TimerScreen(
    taskId: Long,
    viewModel: PomodoroViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Memberi tahu ViewModel tugas mana yang dikerjakan. Dibungkus
    // LaunchedEffect agar hanya dijalankan saat kuncinya berubah, bukan
    // setiap kali layar digambar ulang.
    //
    // Kunci kedua, sesiAktif, ada karena tugas tidak bisa diganti selama sesi
    // berjalan. Begitu pengguna menekan Hentikan, sesiAktif berubah menjadi
    // false, efek ini berjalan lagi, dan tugas yang tadi dipilih pengguna
    // akhirnya dipasang.
    val sesiAktif = uiState.phase != PomodoroPhase.IDLE
    LaunchedEffect(taskId, sesiAktif) {
        viewModel.pilihTugas(taskId)
    }

    // true bila pengguna menekan tombol putar tugas lain di Beranda, padahal
    // sesi tugas sebelumnya masih berjalan.
    val tugasLainDiminta = sesiAktif &&
        taskId != TimerUiState.NO_TASK &&
        taskId != uiState.taskId

    TimerScreen(
        uiState = uiState,
        tugasLainDiminta = tugasLainDiminta,
        onMulai = viewModel::mulai,
        onJeda = viewModel::jeda,
        onLanjut = viewModel::lanjut,
        onHentikan = viewModel::hentikan,
        modifier = modifier,
    )
}

/** Versi stateless. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    uiState: TimerUiState,
    tugasLainDiminta: Boolean,
    onMulai: () -> Unit,
    onJeda: () -> Unit,
    onLanjut: () -> Unit,
    onHentikan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Fokus") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { innerPadding ->

        if (uiState.belumAdaTugas) {
            BelumMemilihTugas(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterVertically),
        ) {

            // --- Tugas yang sedang dikerjakan ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "SEDANG DIKERJAKAN",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = uiState.taskTitle,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                if (tugasLainDiminta) {
                    Text(
                        text = "Sesi tugas ini masih berjalan. Tekan Hentikan untuk " +
                            "beralih ke tugas yang kamu pilih.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // --- Lingkaran hitung mundur ---
            // Warna membedakan fase sekilas pandang: fokus memakai warna
            // utama (primary), istirahat memakai warna aksen (tertiary).
            val warnaFase = if (uiState.phase == PomodoroPhase.SHORT_BREAK ||
                uiState.phase == PomodoroPhase.LONG_BREAK
            ) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.primary
            }

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    // Lambda, bukan nilai langsung. Bentuk ini yang dipakai
                    // Material 3 versi baru agar progres bisa dianimasikan
                    // tanpa menggambar ulang seluruh komponen.
                    progress = { uiState.progress },
                    modifier = Modifier.size(260.dp),
                    color = warnaFase,
                    strokeWidth = 14.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = uiState.phase.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = warnaFase,
                    )
                    Text(
                        text = uiState.timeLabel,
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Text(
                        text = "sesi ${uiState.completedFocusInCycle + 1} dari ${PomodoroViewModel.SESI_PER_SIKLUS}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // --- Titik penanda siklus ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(PomodoroViewModel.SESI_PER_SIKLUS) { index ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (index < uiState.completedFocusInCycle) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = CircleShape,
                            )
                    )
                }
            }

            // Keterangan aturan siklus, supaya titik-titik di atas tidak
            // perlu ditebak artinya.
            Text(
                text = "Istirahat panjang setelah ${PomodoroViewModel.SESI_PER_SIKLUS} sesi fokus",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // --- Tombol ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when {
                    uiState.phase == PomodoroPhase.IDLE -> {
                        Button(
                            onClick = onMulai,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Text(
                                text = "Mulai fokus",
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }

                    uiState.isRunning -> {
                        Button(onClick = onJeda, modifier = Modifier.weight(1f)) {
                            Text("Jeda")
                        }
                        OutlinedButton(onClick = onHentikan, modifier = Modifier.weight(1f)) {
                            Text("Hentikan")
                        }
                    }

                    else -> {
                        Button(onClick = onLanjut, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Text(text = "Lanjut", modifier = Modifier.padding(start = 8.dp))
                        }
                        OutlinedButton(onClick = onHentikan, modifier = Modifier.weight(1f)) {
                            Text("Hentikan")
                        }
                    }
                }
            }
        }
    }
}

/** Tampilan saat tab Fokus dibuka tanpa memilih tugas lebih dulu. */
@Composable
private fun BelumMemilihTugas(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = "Belum ada tugas yang dipilih",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Buka Beranda, lalu tekan tombol putar pada tugas yang ingin " +
                "kamu kerjakan. Setiap sesi fokus di WaktuKu selalu melekat pada " +
                "satu tugas.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// ---------------------------------------------------------------------------
// PREVIEW
// Tiga keadaan timer bisa diperiksa tanpa menjalankan aplikasi dan tanpa
// menunggu 25 menit, karena versi stateless menerima TimerUiState buatan.
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Sedang berjalan")
@Composable
private fun TimerScreenBerjalanPreview() {
    WaktuKuTheme {
        TimerScreen(
            uiState = TimerUiState(
                phase = PomodoroPhase.FOCUS,
                isRunning = true,
                remainingSeconds = 14 * 60 + 32,
                totalSeconds = 25 * 60,
                completedFocusInCycle = 2,
                taskId = 1L,
                taskTitle = "Belajar UTS Basis Data",
            ),
            tugasLainDiminta = false,
            onMulai = {}, onJeda = {}, onLanjut = {}, onHentikan = {},
        )
    }
}

@Preview(showBackground = true, name = "Dijeda")
@Composable
private fun TimerScreenDijedaPreview() {
    WaktuKuTheme {
        TimerScreen(
            uiState = TimerUiState(
                phase = PomodoroPhase.FOCUS,
                isRunning = false,
                remainingSeconds = 9 * 60 + 5,
                totalSeconds = 25 * 60,
                completedFocusInCycle = 1,
                taskId = 1L,
                taskTitle = "Rancang UI WaktuKu",
            ),
            tugasLainDiminta = false,
            onMulai = {}, onJeda = {}, onLanjut = {}, onHentikan = {},
        )
    }
}

@Preview(showBackground = true, name = "Belum pilih tugas")
@Composable
private fun TimerScreenTanpaTugasPreview() {
    WaktuKuTheme {
        TimerScreen(
            uiState = TimerUiState(),
            tugasLainDiminta = false,
            onMulai = {}, onJeda = {}, onLanjut = {}, onHentikan = {},
        )
    }
}
