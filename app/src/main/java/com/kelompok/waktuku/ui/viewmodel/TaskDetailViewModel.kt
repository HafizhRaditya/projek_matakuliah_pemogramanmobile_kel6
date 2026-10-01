package com.kelompok.waktuku.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kelompok.waktuku.WaktuKuApplication
import com.kelompok.waktuku.data.PomodoroRepository
import com.kelompok.waktuku.data.TaskRepository
import com.kelompok.waktuku.model.PomodoroSession
import com.kelompok.waktuku.model.Task
import com.kelompok.waktuku.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 2 (State Management & Logika)
// ============================================================================
// Otak layar Detail Tugas (fitur F5): memuat satu tugas beserta riwayat sesi
// Pomodoro-nya, menampung isian formulir yang sedang diedit, lalu menyimpan
// atau menghapus tugas itu.
//
// Dua hal yang paling mungkin ditanyakan dosen dari berkas ini:
//
// 1. Dari mana ViewModel tahu tugas MANA yang dibuka?
//    Dari SavedStateHandle. Navigation Compose otomatis memasukkan argumen
//    rute (taskId) ke sana, jadi ViewModel tidak perlu menerima id lewat
//    layar. Nilainya juga ikut dipulihkan bila Android sempat mematikan
//    proses aplikasi di latar belakang.
//
// 2. Kenapa isian formulir disimpan di ViewModel, bukan remember di layar?
//    Karena layar ini punya aturan bisnis: judul wajib diisi, target sesi
//    dibatasi 1 sampai 12, dan tombol Simpan hanya aktif bila ada perubahan.
//    Aturan seperti itu tempatnya di ViewModel supaya bisa diuji tanpa UI
//    (lihat TaskDetailViewModelTest).
// ============================================================================

/**
 * Isian formulir yang sedang diedit pengguna.
 *
 * Sengaja dipisah dari [Task]: Task adalah data yang SUDAH tersimpan di
 * database, sedangkan form adalah data yang BELUM disimpan. Membandingkan
 * keduanya itulah cara kita tahu apakah pengguna sudah mengubah sesuatu.
 */
data class TaskDetailForm(
    val title: String,
    val notes: String,
    val priority: TaskPriority,
    val dueAt: Long?,
    val estimatedPomodoros: Int,
) {
    /** Judul berisi spasi saja dianggap kosong. */
    val judulValid: Boolean get() = title.isNotBlank()

    companion object {
        const val TARGET_MIN = 1
        const val TARGET_MAX = 12

        /** Membuat isian awal formulir dari tugas yang tersimpan. */
        fun dari(task: Task) = TaskDetailForm(
            title = task.title,
            notes = task.notes,
            priority = task.priority,
            dueAt = task.dueAt,
            estimatedPomodoros = task.estimatedPomodoros,
        )
    }
}

/**
 * Kondisi layar Detail Tugas. Polanya sama dengan HomeUiState:
 * Loading, Success, atau Error - tidak pernah dua sekaligus.
 */
sealed interface TaskDetailUiState {

    /** Data belum sempat dibaca dari database. */
    data object Loading : TaskDetailUiState

    data class Success(
        /** Tugas sebagaimana tersimpan di database. */
        val task: Task,
        /** Isian formulir saat ini, mungkin sudah diubah pengguna. */
        val form: TaskDetailForm,
        /** Riwayat sesi Pomodoro tugas ini, terbaru di atas. */
        val sessions: List<PomodoroSession>,
    ) : TaskDetailUiState {
        /** true bila isian berbeda dari yang tersimpan. Menentukan tombol Simpan. */
        val adaPerubahan: Boolean get() = form != TaskDetailForm.dari(task)
    }

    /** Tugas tidak ditemukan, atau database gagal dibaca. */
    data class Error(val message: String) : TaskDetailUiState
}

class TaskDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val pomodoroRepository: PomodoroRepository,
) : ViewModel() {

    /**
     * Id tugas yang sedang dibuka. Kuncinya "taskId", sama persis dengan nama
     * properti di rute Detail Tugas. Kalau namanya berbeda, nilainya null dan
     * checkNotNull langsung menghentikan aplikasi dengan pesan yang jelas.
     */
    private val taskId: Long = checkNotNull(savedStateHandle[ARG_TASK_ID]) {
        "Rute Detail Tugas harus membawa argumen $ARG_TASK_ID"
    }

    /**
     * Isian formulir. null artinya pengguna belum mengubah apa pun, sehingga
     * formulir cukup menampilkan isi tugas dari database.
     */
    private val _form = MutableStateFlow<TaskDetailForm?>(null)

    /**
     * Penanda bahwa penghapusan sedang berlangsung. Begitu baris terhapus,
     * observeTask memancarkan null. Tanpa penanda ini, layar sempat
     * menampilkan "Tugas tidak ditemukan" sesaat sebelum kembali ke Beranda.
     */
    private var sedangMenghapus = false

    private val _selesai = MutableStateFlow(false)

    /**
     * true setelah tugas berhasil disimpan atau dihapus. Layar mengamatinya
     * lalu kembali ke Beranda.
     *
     * Kenapa layar tidak langsung kembali saat tombol ditekan? Karena begitu
     * layar ditutup, ViewModel ikut dihancurkan dan viewModelScope dibatalkan.
     * Penyimpanan yang belum rampung bisa ikut terbatalkan. Menunggu sinyal
     * ini memastikan data sudah benar-benar tertulis.
     */
    val selesai: StateFlow<Boolean> = _selesai.asStateFlow()

    /**
     * Satu-satunya state yang dibaca layar.
     *
     * combine menggabungkan tiga aliran: tugas dari Room, riwayat sesinya, dan
     * isian formulir. Setiap kali salah satunya berubah, TaskDetailUiState
     * baru dipancarkan.
     */
    val uiState: StateFlow<TaskDetailUiState> =
        combine<Task?, List<PomodoroSession>, TaskDetailForm?, TaskDetailUiState>(
            taskRepository.observeTask(taskId),
            pomodoroRepository.observeSessionsForTask(taskId),
            _form,
        ) { task, sessions, form ->
            when {
                task == null && sedangMenghapus -> TaskDetailUiState.Loading
                task == null -> TaskDetailUiState.Error("Tugas tidak ditemukan. Mungkin sudah dihapus.")
                else -> TaskDetailUiState.Success(
                    task = task,
                    form = form ?: TaskDetailForm.dari(task),
                    sessions = sessions,
                )
            }
        }
            // .catch menangkap kegagalan dari database, lalu mengubahnya
            // menjadi pesan di layar, bukan aplikasi yang tertutup paksa.
            .catch { emit(TaskDetailUiState.Error("Gagal memuat tugas.")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TaskDetailUiState.Loading,
            )

    // ---------------------------------------------------------------------
    // MENGUBAH ISIAN
    // Semua fungsi ubah... memakai satu pola: ambil isian saat ini, buat
    // salinan dengan satu properti diganti (copy), lalu simpan ke _form.
    // Data class yang immutable membuat Compose yakin kapan harus menggambar
    // ulang.
    // ---------------------------------------------------------------------

    fun ubahJudul(judul: String) = ubahForm { it.copy(title = judul) }

    fun ubahCatatan(catatan: String) = ubahForm { it.copy(notes = catatan) }

    fun ubahPrioritas(prioritas: TaskPriority) = ubahForm { it.copy(priority = prioritas) }

    /** @param tenggat epoch millis, atau null untuk menghapus tenggat. */
    fun ubahTenggat(tenggat: Long?) = ubahForm { it.copy(dueAt = tenggat) }

    fun tambahTarget() = ubahForm {
        it.copy(estimatedPomodoros = (it.estimatedPomodoros + 1).coerceAtMost(TaskDetailForm.TARGET_MAX))
    }

    fun kurangiTarget() = ubahForm {
        it.copy(estimatedPomodoros = (it.estimatedPomodoros - 1).coerceAtLeast(TaskDetailForm.TARGET_MIN))
    }

    private fun ubahForm(perubahan: (TaskDetailForm) -> TaskDetailForm) {
        _form.value = perubahan(isianTerbaru() ?: return)
    }

    /**
     * Isian formulir paling baru, atau null bila tugas belum selesai dimuat.
     *
     * Dibaca dari _form lebih dulu, BUKAN dari uiState. Alasannya: uiState
     * baru diperbarui setelah combine berjalan lagi, sesaat kemudian. Bila
     * dua perubahan terjadi beruntun (misalnya pengguna mengetik cepat),
     * membaca uiState membuat perubahan pertama tertimpa oleh isian lama.
     * Uji ubahLaluSimpan di TaskDetailViewModelTest menangkap masalah ini.
     */
    private fun isianTerbaru(): TaskDetailForm? =
        _form.value ?: (uiState.value as? TaskDetailUiState.Success)?.form

    // ---------------------------------------------------------------------
    // SIMPAN DAN HAPUS
    // ---------------------------------------------------------------------

    fun simpan() {
        val state = uiState.value as? TaskDetailUiState.Success ?: return
        val form = isianTerbaru() ?: return
        // Validasi diulang di sini walau tombolnya sudah dinonaktifkan di
        // layar: aturan bisnis tidak boleh bergantung pada tampilan.
        if (!form.judulValid) return

        viewModelScope.launch {
            // copy() mempertahankan kolom yang tidak ada di formulir, misalnya
            // isDone, completedPomodoros, dan createdAt.
            taskRepository.saveTask(
                state.task.copy(
                    title = form.title.trim(),
                    notes = form.notes.trim(),
                    priority = form.priority,
                    dueAt = form.dueAt,
                    estimatedPomodoros = form.estimatedPomodoros,
                ),
            )
            _selesai.value = true
        }
    }

    /**
     * Menghapus tugas. Riwayat sesinya ikut terhapus otomatis oleh database
     * (ON DELETE CASCADE pada tabel pomodoro_sessions).
     */
    fun hapus() {
        val state = uiState.value as? TaskDetailUiState.Success ?: return
        sedangMenghapus = true
        viewModelScope.launch {
            taskRepository.deleteTask(state.task)
            _selesai.value = true
        }
    }

    companion object {
        /** Nama argumen rute. Harus sama dengan properti di TaskDetailRoute. */
        const val ARG_TASK_ID = "taskId"

        /**
         * Factory: resep membuat TaskDetailViewModel.
         *
         * createSavedStateHandle() mengambil SavedStateHandle milik layar yang
         * sedang dibuka, lengkap dengan argumen rutenya.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WaktuKuApplication
                TaskDetailViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    taskRepository = application.container.taskRepository,
                    pomodoroRepository = application.container.pomodoroRepository,
                )
            }
        }
    }
}
