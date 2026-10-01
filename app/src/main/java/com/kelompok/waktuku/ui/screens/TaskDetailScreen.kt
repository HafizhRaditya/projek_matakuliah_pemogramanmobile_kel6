package com.kelompok.waktuku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok.waktuku.model.PomodoroSession
import com.kelompok.waktuku.model.Task
import com.kelompok.waktuku.model.TaskPriority
import com.kelompok.waktuku.ui.theme.WaktuKuTheme
import com.kelompok.waktuku.ui.viewmodel.TaskDetailForm
import com.kelompok.waktuku.ui.viewmodel.TaskDetailUiState
import com.kelompok.waktuku.ui.viewmodel.TaskDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 1 (UI/UX), memakai logika dari Mahasiswa 2
// ============================================================================
// Layar Detail Tugas (fitur F5): mengubah judul, catatan, prioritas, tenggat,
// dan target sesi, serta melihat riwayat sesi Pomodoro sebuah tugas.
//
// Polanya sama dengan HomeScreen dan TimerScreen:
//   1. TaskDetailScreen(onBack)       -> STATEFUL, mengambil state dari
//      TaskDetailViewModel.
//   2. TaskDetailScreen(uiState, ...) -> STATELESS, hanya menggambar, jadi
//      bisa di-@Preview tanpa database.
// ============================================================================

/**
 * Versi stateful.
 *
 * Perhatikan bahwa fungsi ini TIDAK menerima taskId. ViewModel membacanya
 * sendiri dari argumen rute lewat SavedStateHandle.
 *
 * @param onBack dipanggil untuk kembali ke layar sebelumnya.
 */
@Composable
fun TaskDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = viewModel(factory = TaskDetailViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selesai by viewModel.selesai.collectAsStateWithLifecycle()

    // Kembali ke Beranda SETELAH penyimpanan atau penghapusan rampung,
    // bukan saat tombol ditekan. Alasannya ada di TaskDetailViewModel.selesai.
    LaunchedEffect(selesai) {
        if (selesai) onBack()
    }

    TaskDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onTitleChange = viewModel::ubahJudul,
        onNotesChange = viewModel::ubahCatatan,
        onPriorityChange = viewModel::ubahPrioritas,
        onDueDateChange = viewModel::ubahTenggat,
        onTambahTarget = viewModel::tambahTarget,
        onKurangiTarget = viewModel::kurangiTarget,
        onSave = viewModel::simpan,
        onDelete = viewModel::hapus,
        modifier = modifier,
    )
}

/** Versi stateless. Semua yang dibutuhkan datang lewat parameter. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    uiState: TaskDetailUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onTambahTarget: () -> Unit,
    onKurangiTarget: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // State milik tampilan semata: apakah dialog konfirmasi hapus sedang
    // terbuka. rememberSaveable supaya dialog tidak hilang saat HP diputar.
    var tanyaHapus by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detail Tugas") },
                // Layar ini dicapai dari Beranda, bukan dari tab, jadi butuh
                // tombol kembali. Bilah navigasi bawah disembunyikan di sini.
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                        )
                    }
                },
                actions = {
                    // Tombol hapus hanya muncul bila tugasnya berhasil dimuat.
                    if (uiState is TaskDetailUiState.Success) {
                        IconButton(onClick = { tanyaHapus = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus tugas",
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

        // Tiga kondisi layar, persis seperti pola Loading/Success/Error di
        // HomeScreen. `when` pada sealed interface memaksa ketiganya ditulis:
        // lupa satu, kode tidak akan ter-compile.
        when (uiState) {
            TaskDetailUiState.Loading -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }

            is TaskDetailUiState.Error -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                TextButton(onClick = onBack) { Text("Kembali ke Beranda") }
            }

            is TaskDetailUiState.Success -> DetailForm(
                state = uiState,
                onTitleChange = onTitleChange,
                onNotesChange = onNotesChange,
                onPriorityChange = onPriorityChange,
                onDueDateChange = onDueDateChange,
                onTambahTarget = onTambahTarget,
                onKurangiTarget = onKurangiTarget,
                onSave = onSave,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    // Menghapus tidak bisa diurungkan, jadi wajib dikonfirmasi dulu.
    if (tanyaHapus) {
        AlertDialog(
            onDismissRequest = { tanyaHapus = false },
            title = { Text("Hapus tugas ini?") },
            text = { Text("Tugas beserta seluruh riwayat sesinya akan dihapus permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        tanyaHapus = false
                        onDelete()
                    },
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { tanyaHapus = false }) { Text("Batal") }
            },
        )
    }
}

/** Isi formulir saat tugas berhasil dimuat. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailForm(
    state: TaskDetailUiState.Success,
    onTitleChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onTambahTarget: () -> Unit,
    onKurangiTarget: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val form = state.form
    var pilihTanggal by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            // Formulir bisa lebih tinggi dari layar, terutama saat papan
            // ketik muncul, jadi dibuat bisa digulir.
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        // --- Judul ---
        OutlinedTextField(
            value = form.title,
            onValueChange = onTitleChange,
            label = { Text("Judul tugas") },
            isError = !form.judulValid,
            supportingText = {
                if (!form.judulValid) Text("Judul tugas tidak boleh kosong")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // --- Catatan ---
        OutlinedTextField(
            value = form.notes,
            onValueChange = onNotesChange,
            label = { Text("Catatan") },
            placeholder = { Text("Opsional") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )

        // --- Prioritas ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Prioritas", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskPriority.entries.forEach { prioritas ->
                    val terpilih = prioritas == form.priority
                    FilterChip(
                        selected = terpilih,
                        onClick = { onPriorityChange(prioritas) },
                        label = { Text(prioritas.label) },
                        // Centang sebagai penanda kedua selain warna.
                        leadingIcon = if (terpilih) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }
        }

        // --- Tenggat ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tenggat", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { pilihTanggal = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(form.dueAt?.let(::formatTanggal) ?: "Pilih tanggal")
                }
                if (form.dueAt != null) {
                    TextButton(onClick = { onDueDateChange(null) }) { Text("Hapus tenggat") }
                }
            }
        }

        // --- Target sesi ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Target sesi Pomodoro", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Tombol dinonaktifkan di batasnya, sehingga pengguna langsung
                // melihat bahwa angkanya tidak bisa turun atau naik lagi.
                FilledTonalIconButton(
                    onClick = onKurangiTarget,
                    enabled = form.estimatedPomodoros > TaskDetailForm.TARGET_MIN,
                ) { Text("−") }
                Text(
                    text = "${form.estimatedPomodoros} sesi",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                FilledTonalIconButton(
                    onClick = onTambahTarget,
                    enabled = form.estimatedPomodoros < TaskDetailForm.TARGET_MAX,
                ) { Text("+") }
            }

            // Progres dihitung dari sesi yang benar-benar selesai.
            val progres = (state.task.completedPomodoros.toFloat() / form.estimatedPomodoros)
                .coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progres },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "${state.task.completedPomodoros} dari ${form.estimatedPomodoros} sesi selesai",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // --- Riwayat sesi ---
        RiwayatSesi(sessions = state.sessions)

        // --- Simpan ---
        // Tombol hanya aktif bila judul sah DAN ada yang diubah. Menekan
        // Simpan tanpa perubahan apa pun tidak ada gunanya.
        Button(
            onClick = onSave,
            enabled = form.judulValid && state.adaPerubahan,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Simpan perubahan")
        }
    }

    // Pemilih tanggal bawaan Material 3.
    if (pilihTanggal) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = form.dueAt)
        DatePickerDialog(
            onDismissRequest = { pilihTanggal = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDueDateChange(datePickerState.selectedDateMillis)
                        pilihTanggal = false
                    },
                ) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { pilihTanggal = false }) { Text("Batal") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Daftar sesi Pomodoro milik tugas ini.
 *
 * Memakai Column biasa, bukan LazyColumn, karena sudah berada di dalam
 * Column yang bisa digulir. LazyColumn di dalam wadah yang juga bisa digulir
 * pada arah yang sama akan membuat aplikasi crash. Jumlahnya dibatasi 10
 * supaya tetap ringan.
 */
@Composable
private fun RiwayatSesi(sessions: List<PomodoroSession>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Riwayat sesi", style = MaterialTheme.typography.labelLarge)

        if (sessions.isEmpty()) {
            Text(
                text = "Belum ada sesi fokus untuk tugas ini.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@Column
        }

        sessions.take(MAKS_RIWAYAT).forEachIndexed { index, sesi ->
            if (index > 0) HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = formatWaktu(sesi.startedAt),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    // Sesi yang dihentikan tetap ditampilkan, tetapi tidak
                    // dihitung sebagai progres (PRD bagian 7).
                    text = if (sesi.isCompleted) {
                        "${sesi.durationMinutes} menit · selesai"
                    } else {
                        "${sesi.durationMinutes} menit · dihentikan"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (sesi.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }

        if (sessions.size > MAKS_RIWAYAT) {
            Text(
                text = "dan ${sessions.size - MAKS_RIWAYAT} sesi sebelumnya",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private const val MAKS_RIWAYAT = 10

// SimpleDateFormat dipakai (bukan java.time) karena minSdk proyek ini 24,
// sedangkan java.time baru tersedia mulai API 26. Sama seperti di TaskCard.
private fun formatTanggal(millis: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID")).format(Date(millis))

private fun formatWaktu(millis: Long): String =
    SimpleDateFormat("d MMM, HH:mm", Locale.forLanguageTag("id-ID")).format(Date(millis))

// ---------------------------------------------------------------------------
// PREVIEW
// ---------------------------------------------------------------------------

private val contohTugas = Task(
    id = 1,
    title = "Kerjakan laporan Pemrograman Mobile",
    notes = "Bab arsitektur MVVM",
    dueAt = 1772236800000L,
    priority = TaskPriority.HIGH,
    estimatedPomodoros = 4,
    completedPomodoros = 2,
)

@Preview(showBackground = true, name = "Berhasil dimuat")
@Composable
private fun TaskDetailScreenPreview() {
    WaktuKuTheme {
        TaskDetailScreen(
            uiState = TaskDetailUiState.Success(
                task = contohTugas,
                form = TaskDetailForm.dari(contohTugas),
                sessions = listOf(
                    PomodoroSession(id = 2, taskId = 1, startedAt = 1772100000000L, durationMinutes = 25, isCompleted = true),
                    PomodoroSession(id = 1, taskId = 1, startedAt = 1772000000000L, durationMinutes = 12, isCompleted = false),
                ),
            ),
            onBack = {},
            onTitleChange = {},
            onNotesChange = {},
            onPriorityChange = {},
            onDueDateChange = {},
            onTambahTarget = {},
            onKurangiTarget = {},
            onSave = {},
            onDelete = {},
        )
    }
}

@Preview(showBackground = true, name = "Tidak ditemukan")
@Composable
private fun TaskDetailScreenErrorPreview() {
    WaktuKuTheme {
        TaskDetailScreen(
            uiState = TaskDetailUiState.Error("Tugas tidak ditemukan. Mungkin sudah dihapus."),
            onBack = {},
            onTitleChange = {},
            onNotesChange = {},
            onPriorityChange = {},
            onDueDateChange = {},
            onTambahTarget = {},
            onKurangiTarget = {},
            onSave = {},
            onDelete = {},
        )
    }
}
