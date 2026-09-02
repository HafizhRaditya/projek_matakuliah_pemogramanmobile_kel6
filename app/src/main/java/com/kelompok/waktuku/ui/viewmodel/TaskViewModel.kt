package com.kelompok.waktuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kelompok.waktuku.WaktuKuApplication
import com.kelompok.waktuku.data.TaskRepository
import com.kelompok.waktuku.model.Task
import com.kelompok.waktuku.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 2 (State Management & Logika)
// ============================================================================
// ViewModel memegang STATE dan LOGIKA layar; ia bertahan hidup melewati
// perubahan konfigurasi (contoh: layar diputar), sehingga daftar tugas tidak
// hilang dan database tidak dibaca ulang tanpa alasan.
//
// Aturan main yang menjaga batas kerja tim:
// - ViewModel TIDAK BOLEH mengimpor apa pun dari androidx.compose.* .
//   Kalau sampai perlu, tandanya logika UI bocor ke sini.
// - Composable TIDAK BOLEH mengubah uiState langsung; ia hanya membaca
//   uiState dan memanggil fungsi-fungsi di kelas ini.
// ============================================================================

/** Pilihan penyaringan daftar tugas di layar utama. */
enum class TaskFilter(val label: String) {
    ALL("Semua"),
    ACTIVE("Belum selesai"),
    DONE("Selesai"),
}

/**
 * Potret lengkap kondisi layar utama pada satu titik waktu.
 *
 * Sengaja dibuat SATU data class, bukan banyak StateFlow terpisah. Ini pola
 * "UI state as a single source of truth" yang dianjurkan panduan arsitektur
 * Google: mustahil muncul kondisi janggal seperti isLoading = true padahal
 * daftar tugas sudah terisi, karena keduanya selalu berubah bersamaan.
 */
data class HomeUiState(
    // Daftar yang SUDAH disaring - inilah yang digambar LazyColumn.
    val tasks: List<Task> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    // Dua angka di bawah dihitung dari SELURUH tugas (sebelum disaring),
    // supaya ringkasan "3/10 selesai" tidak ikut berubah saat filter diganti.
    val totalCount: Int = 0,
    val doneCount: Int = 0,
    // true selama pembacaan pertama dari database belum selesai.
    val isLoading: Boolean = true,
) {
    /** Dipakai UI untuk memutuskan menampilkan daftar atau tampilan kosong. */
    val isEmpty: Boolean get() = tasks.isEmpty() && !isLoading
}

class TaskViewModel(
    // Dependensi diterima dari luar (lewat AppContainer), tidak dibuat sendiri
    // di dalam kelas ini. Itulah inti Dependency Injection.
    private val taskRepository: TaskRepository,
) : ViewModel() {

    // State internal yang hanya boleh diubah dari dalam ViewModel.
    private val _filter = MutableStateFlow(TaskFilter.ALL)

    /**
     * Satu-satunya state yang dibaca UI.
     *
     * combine(...)  -> menggabungkan dua aliran (data dari Room + pilihan
     *                  filter pengguna). Setiap kali SALAH SATU berubah,
     *                  blok di dalamnya dijalankan ulang dan HomeUiState baru
     *                  dipancarkan. Inilah alasan mencentang tugas langsung
     *                  memperbarui layar tanpa satu baris pun kode refresh.
     *
     * stateIn(...)  -> mengubah Flow biasa menjadi StateFlow yang selalu punya
     *                  nilai terkini dan dibagikan ke semua pengamat (jadi
     *                  database tidak dibaca berkali-kali).
     *
     * WhileSubscribed(5_000) -> aliran data dihentikan 5 detik setelah layar
     *                  tidak lagi terlihat. Jeda 5 detik itu penting: saat
     *                  pengguna hanya memutar layar, langganan tidak sempat
     *                  putus sehingga tidak ada pembacaan ulang yang sia-sia.
     */
    val uiState: StateFlow<HomeUiState> =
        combine(taskRepository.observeTasks(), _filter) { allTasks, filter ->
            HomeUiState(
                tasks = when (filter) {
                    TaskFilter.ALL -> allTasks
                    TaskFilter.ACTIVE -> allTasks.filterNot { it.isDone }
                    TaskFilter.DONE -> allTasks.filter { it.isDone }
                },
                filter = filter,
                totalCount = allTasks.size,
                doneCount = allTasks.count { it.isDone },
                isLoading = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            // Nilai awal sebelum database sempat menjawab.
            initialValue = HomeUiState(isLoading = true),
        )

    // ---------------------------------------------------------------------
    // AKSI PENGGUNA
    // Semua fungsi di bawah dipanggil dari lapisan UI.
    // Perhatikan polanya: tidak ada satu pun yang mengembalikan nilai. UI
    // cukup "melapor bahwa sesuatu terjadi", lalu menunggu uiState berubah.
    // ---------------------------------------------------------------------

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    /**
     * Menambah tugas baru.
     *
     * viewModelScope.launch menjalankan operasi tulis di background dan
     * otomatis dibatalkan bila ViewModel dihancurkan - jadi tidak ada
     * coroutine yang menggantung.
     */
    fun addTask(
        title: String,
        notes: String = "",
        dueAt: Long? = null,
        priority: TaskPriority = TaskPriority.MEDIUM,
    ) {
        // Validasi ditaruh di sini, BUKAN di Composable, agar aturan bisnis
        // tetap berlaku dari mana pun fungsi ini dipanggil.
        if (title.isBlank()) return

        viewModelScope.launch {
            taskRepository.addTask(title = title, notes = notes, dueAt = dueAt, priority = priority)
        }
    }

    /** Membalik status centang sebuah tugas. */
    fun toggleTaskDone(task: Task) {
        viewModelScope.launch {
            taskRepository.setDone(task.id, !task.isDone)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            taskRepository.clearCompleted()
        }
    }

    companion object {
        /**
         * Factory: resep bagi sistem Android untuk membuat TaskViewModel.
         *
         * Dibutuhkan karena constructor kita punya parameter (TaskRepository),
         * sedangkan secara bawaan Android hanya bisa membuat ViewModel yang
         * constructor-nya kosong.
         *
         * APPLICATION_KEY memberi kita objek Application, dari situ kita ambil
         * container, lalu repository-nya.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WaktuKuApplication
                TaskViewModel(application.container.taskRepository)
            }
        }
    }
}
