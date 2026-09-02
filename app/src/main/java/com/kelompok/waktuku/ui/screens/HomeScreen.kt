package com.kelompok.waktuku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok.waktuku.model.Task
import com.kelompok.waktuku.model.TaskPriority
import com.kelompok.waktuku.ui.components.AddTaskDialog
import com.kelompok.waktuku.ui.components.TaskCard
import com.kelompok.waktuku.ui.theme.WaktuKuTheme
import com.kelompok.waktuku.ui.viewmodel.HomeUiState
import com.kelompok.waktuku.ui.viewmodel.TaskFilter
import com.kelompok.waktuku.ui.viewmodel.TaskViewModel

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 1 (UI/UX dengan Jetpack Compose)
// ============================================================================
// Layar utama WaktuKu: daftar tugas + penyaring + tombol tambah.
//
// Layar ini sengaja dipecah menjadi DUA fungsi, dan pemisahan inilah yang
// paling penting untuk dijelaskan saat presentasi:
//
//   1. HomeScreen(viewModel)    -> versi STATEFUL. Hanya bertugas mengambil
//      state dari ViewModel lalu meneruskannya ke bawah.
//   2. HomeScreen(uiState, ...) -> versi STATELESS. Tidak tahu-menahu soal
//      ViewModel maupun database, sehingga bisa di-@Preview dan diuji dengan
//      data buatan.
//
// Pola ini dipakai di semua contoh resmi android/compose-samples.
// ============================================================================

/**
 * Versi stateful - inilah yang dipanggil MainActivity.
 *
 * `viewModel(factory = ...)` membuat (atau mengambil kembali) TaskViewModel
 * yang terikat pada layar ini. Saat layar diputar, ViewModel yang SAMA
 * dikembalikan, jadi daftar tugas tidak perlu dimuat ulang.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel(factory = TaskViewModel.Factory),
) {
    // collectAsStateWithLifecycle: mengubah StateFlow menjadi State milik
    // Compose. Versi "WithLifecycle" (bukan collectAsState biasa) otomatis
    // berhenti mendengarkan saat aplikasi masuk latar belakang - hemat baterai.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onFilterChange = viewModel::setFilter,
        onToggleDone = viewModel::toggleTaskDone,
        onDeleteTask = viewModel::deleteTask,
        onAddTask = { title, priority -> viewModel.addTask(title = title, priority = priority) },
        modifier = modifier,
    )
}

/**
 * Versi stateless. Semua yang ia butuhkan datang lewat parameter.
 *
 * @param uiState potret kondisi layar saat ini (dari HomeUiState).
 * @param onFilterChange dilaporkan saat pengguna menekan chip penyaring.
 * @param onToggleDone dilaporkan saat kotak centang sebuah tugas ditekan.
 * @param onDeleteTask dilaporkan saat tugas dihapus.
 * @param onAddTask dilaporkan saat tugas baru dikirim dari dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onFilterChange: (TaskFilter) -> Unit,
    onToggleDone: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onAddTask: (String, TaskPriority) -> Unit,
    modifier: Modifier = Modifier,
) {
    // State milik UI semata (dialog sedang terbuka atau tidak) BOLEH disimpan
    // di sini dengan remember, karena ViewModel tidak perlu tahu soal ini.
    // Aturannya: state yang menyangkut DATA -> ViewModel; state yang murni
    // TAMPILAN -> cukup di Composable.
    var showAddDialog by remember { mutableStateOf(false) }

    // Scaffold menyediakan kerangka baku Material Design: top bar, floating
    // action button, snackbar, dan area konten.
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("WaktuKu") },
                actions = {
                    // Ringkasan progres, contoh: "1/3 selesai".
                    Text(
                        text = "${uiState.doneCount}/${uiState.totalCount} selesai",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Tugas baru") },
            )
        },
    ) { innerPadding ->
        // innerPadding WAJIB diteruskan, kalau tidak konten akan tertutup
        // top bar dan navigation bar.
        Column(modifier = Modifier.padding(innerPadding)) {

            TaskFilterRow(
                selected = uiState.filter,
                onFilterChange = onFilterChange,
            )

            when {
                // Kondisi 1: database belum sempat menjawab.
                uiState.isLoading -> LoadingState()

                // Kondisi 2: sudah dimuat, tapi tidak ada tugas untuk ditampilkan.
                uiState.isEmpty -> EmptyState(filter = uiState.filter)

                // Kondisi 3: ada isinya - gambar daftarnya.
                else -> TaskList(
                    tasks = uiState.tasks,
                    onToggleDone = onToggleDone,
                    onDeleteTask = onDeleteTask,
                )
            }
        }
    }

    // Dialog digambar di luar Scaffold agar melayang di atas seluruh layar.
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, priority ->
                onAddTask(title, priority)
                showAddDialog = false
            },
        )
    }
}

/** Deretan chip penyaring: Semua / Belum selesai / Selesai. */
@Composable
private fun TaskFilterRow(
    selected: TaskFilter,
    onFilterChange: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // TaskFilter.entries menghasilkan semua nilai enum, sehingga menambah
        // filter baru cukup dilakukan di enum-nya - layar ini tidak diubah.
        TaskFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onFilterChange(filter) },
                label = { Text(filter.label) },
            )
        }
    }
}

/**
 * Daftar tugas yang dapat digulir.
 *
 * LazyColumn hanya menyusun item yang benar-benar terlihat di layar (mirip
 * RecyclerView), jadi tetap ringan walau tugasnya ratusan.
 */
@Composable
private fun TaskList(
    tasks: List<Task>,
    onToggleDone: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        // bottom = 96.dp memberi ruang agar kartu terakhir tidak tertutup FAB.
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = tasks,
            // key WAJIB diisi. Tanpanya, saat sebuah tugas dihapus Compose
            // bisa salah mencocokkan item dan tampilannya terlihat "loncat".
            key = { task -> task.id },
        ) { task ->
            TaskCard(
                task = task,
                onToggleDone = { onToggleDone(task) },
                onDelete = { onDeleteTask(task) },
            )
        }
    }
}

/** Tampilan saat data pertama kali dimuat. */
@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Tampilan saat daftar kosong.
 *
 * Pesannya menyesuaikan filter yang sedang aktif - detail kecil seperti ini
 * yang membedakan aplikasi terasa matang atau tidak.
 */
@Composable
private fun EmptyState(filter: TaskFilter, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            // null karena ikon ini murni hiasan; teks di bawahnya sudah
            // menjelaskan maknanya bagi pembaca layar.
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = when (filter) {
                TaskFilter.ALL -> "Belum ada tugas.\nTekan \"Tugas baru\" untuk memulai."
                TaskFilter.ACTIVE -> "Semua tugas sudah selesai. Kerja bagus!"
                TaskFilter.DONE -> "Belum ada tugas yang diselesaikan."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// ---------------------------------------------------------------------------
// PREVIEW
// Karena versi stateless menerima HomeUiState buatan, seluruh tampilan bisa
// diperiksa di Android Studio tanpa emulator dan tanpa isi database.
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Ada isinya")
@Composable
private fun HomeScreenPreview() {
    WaktuKuTheme {
        HomeScreen(
            uiState = HomeUiState(
                tasks = listOf(
                    Task(
                        id = 1,
                        title = "Rancang UI WaktuKu",
                        priority = TaskPriority.HIGH,
                        estimatedPomodoros = 4,
                    ),
                    Task(
                        id = 2,
                        title = "Buat TaskDao",
                        notes = "Query observeAll() + Flow",
                        priority = TaskPriority.MEDIUM,
                    ),
                    Task(
                        id = 3,
                        title = "Kumpulkan proposal",
                        isDone = true,
                        priority = TaskPriority.LOW,
                    ),
                ),
                totalCount = 3,
                doneCount = 1,
                isLoading = false,
            ),
            onFilterChange = {},
            onToggleDone = {},
            onDeleteTask = {},
            onAddTask = { _, _ -> },
        )
    }
}

@Preview(showBackground = true, name = "Kosong")
@Composable
private fun HomeScreenEmptyPreview() {
    WaktuKuTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = false),
            onFilterChange = {},
            onToggleDone = {},
            onDeleteTask = {},
            onAddTask = { _, _ -> },
        )
    }
}
