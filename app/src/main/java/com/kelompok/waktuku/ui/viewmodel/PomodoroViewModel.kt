package com.kelompok.waktuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kelompok.waktuku.WaktuKuApplication
import com.kelompok.waktuku.data.PomodoroRepository
import com.kelompok.waktuku.data.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 2 (State Management & Logika)
// ============================================================================
// Mesin timer Pomodoro. Ini bagian paling rawan bug di seluruh aplikasi, dan
// paling sering ditanyakan dosen - jadi baca komentarnya sampai habis.
//
// ATURAN PALING PENTING: JANGAN MENGHITUNG TICK.
//
// Cara yang SALAH, dan paling sering dipakai pemula:
//     var sisaDetik = 25 * 60
//     while (sisaDetik > 0) { delay(1000); sisaDetik-- }
//
// Kenapa salah? Karena Android boleh menidurkan proses aplikasi kapan saja -
// saat layar dimatikan, saat pengguna pindah aplikasi, atau saat baterai
// menipis. Selama tertidur, `delay` berhenti berjalan. Begitu aplikasi bangun,
// hitungannya sudah tertinggal jauh dari waktu sesungguhnya. Pengguna menutup
// aplikasi 10 menit, kembali, dan timer masih menunjukkan angka 10 menit lalu.
//
// Cara yang BENAR, dan yang dipakai di sini: simpan KAPAN sesi seharusnya
// berakhir (targetEndMillis), lalu sisa waktu SELALU dihitung ulang sebagai
//     targetEndMillis - waktuSekarang
// Jam sistem tidak ikut tertidur, jadi hitungannya tetap benar berapa lama pun
// aplikasi ditinggalkan.
//
// Perulangan `delay` di bawah tetap ada, tapi tugasnya hanya MENGGAMBAR ULANG
// layar, bukan menghitung waktu. Itu perbedaan yang menentukan.
// ============================================================================

/** Tahapan siklus Pomodoro. */
enum class PomodoroPhase(val label: String, val defaultMinutes: Int) {
    /** Belum ada sesi berjalan. */
    IDLE("Siap", 0),
    FOCUS("Fokus", 25),
    SHORT_BREAK("Istirahat pendek", 5),
    LONG_BREAK("Istirahat panjang", 15),
}

/**
 * Potret lengkap kondisi layar Fokus.
 *
 * Sama seperti HomeUiState: satu data class, bukan banyak StateFlow terpisah,
 * supaya mustahil muncul keadaan janggal seperti `isRunning = true` padahal
 * sisa waktunya nol.
 */
data class TimerUiState(
    val phase: PomodoroPhase = PomodoroPhase.IDLE,
    val isRunning: Boolean = false,
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    /** Berapa sesi fokus yang sudah selesai dalam siklus berjalan, 0 sampai 4. */
    val completedFocusInCycle: Int = 0,
    val taskId: Long = NO_TASK,
    val taskTitle: String = "",
) {
    /** Sisa waktu sebagai teks "MM:SS". */
    val timeLabel: String
        get() = "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60)

    /**
     * Bagian waktu yang SUDAH berlalu, bernilai 0f sampai 1f.
     * Dipakai lingkaran progres di layar.
     */
    val progress: Float
        get() = if (totalSeconds == 0) 0f
        else ((totalSeconds - remainingSeconds).toFloat() / totalSeconds).coerceIn(0f, 1f)

    /** true bila sesi sudah dimulai lalu dijeda. */
    val isPaused: Boolean get() = phase != PomodoroPhase.IDLE && !isRunning

    /** true bila layar dibuka tanpa memilih tugas lebih dulu. */
    val belumAdaTugas: Boolean get() = taskId == NO_TASK

    companion object {
        const val NO_TASK = -1L
    }
}

class PomodoroViewModel(
    private val taskRepository: TaskRepository,
    private val pomodoroRepository: PomodoroRepository,
    /**
     * Sumber waktu, disuntikkan dari luar.
     *
     * Kalau ViewModel memanggil System.currentTimeMillis() sendiri, ia mustahil
     * diuji - hasilnya berbeda setiap kali dijalankan dan kita harus menunggu
     * 25 menit sungguhan untuk menguji satu sesi. Dengan cara ini, uji bisa
     * memajukan jam sesuka hati.
     */
    private val nowMillis: () -> Long = System::currentTimeMillis,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    /** Kapan sesi berjalan seharusnya berakhir. Inti dari seluruh logika ini. */
    private var targetEndMillis: Long = 0L

    /** Kapan sesi fokus berjalan dimulai, untuk dicatat ke database. */
    private var sessionStartMillis: Long = 0L

    private var tickJob: Job? = null

    // ---------------------------------------------------------------------
    // AKSI PENGGUNA
    // ---------------------------------------------------------------------

    /** Mengambil judul tugas yang akan dikerjakan, dipanggil saat layar dibuka. */
    fun pilihTugas(taskId: Long) {
        if (taskId == TimerUiState.NO_TASK || taskId == _uiState.value.taskId) return

        _uiState.value = _uiState.value.copy(taskId = taskId)
        viewModelScope.launch {
            val task = taskRepository.observeTask(taskId).first()
            _uiState.value = _uiState.value.copy(taskTitle = task?.title.orEmpty())
        }
    }

    /** Memulai sesi fokus baru. */
    fun mulai() {
        if (_uiState.value.belumAdaTugas) return
        mulaiFase(PomodoroPhase.FOCUS)
    }

    /**
     * Menjeda sesi.
     *
     * Yang disimpan adalah SISA WAKTU, bukan waktu jeda. Saat dilanjutkan
     * nanti, targetEndMillis dihitung ulang dari jam saat itu ditambah sisa
     * tersebut - sehingga lamanya jeda tidak ikut memakan waktu sesi.
     */
    fun jeda() {
        val state = _uiState.value
        if (!state.isRunning) return

        hentikanTicker()
        _uiState.value = state.copy(
            isRunning = false,
            remainingSeconds = hitungSisaDetik(),
        )
    }

    /** Melanjutkan sesi yang sedang dijeda. */
    fun lanjut() {
        val state = _uiState.value
        if (state.phase == PomodoroPhase.IDLE || state.isRunning) return

        targetEndMillis = nowMillis() + state.remainingSeconds * 1000L
        _uiState.value = state.copy(isRunning = true)
        jalankanTicker()
    }

    /**
     * Menghentikan sesi sebelum waktunya habis.
     *
     * Sesi fokus yang dihentikan di tengah jalan tetap DICATAT ke database
     * dengan is_completed = 0, tetapi TIDAK menambah progres tugas. Sesuai
     * PRD bagian 7: statistik tidak boleh menipu penggunanya sendiri.
     */
    fun hentikan() {
        val state = _uiState.value
        if (state.phase == PomodoroPhase.IDLE) return

        hentikanTicker()

        if (state.phase == PomodoroPhase.FOCUS) {
            val menitBerjalan = ((nowMillis() - sessionStartMillis) / 60_000L).toInt()
            if (menitBerjalan > 0) {
                viewModelScope.launch {
                    pomodoroRepository.recordAbandonedSession(
                        taskId = state.taskId,
                        startedAt = sessionStartMillis,
                        durationMinutes = menitBerjalan,
                    )
                }
            }
        }

        _uiState.value = state.copy(
            phase = PomodoroPhase.IDLE,
            isRunning = false,
            remainingSeconds = 0,
            totalSeconds = 0,
        )
    }

    // ---------------------------------------------------------------------
    // MESIN WAKTU
    // ---------------------------------------------------------------------

    /**
     * Menghitung ulang sisa waktu dari jam sistem.
     *
     * Fungsi inilah yang membuat timer tetap akurat setelah aplikasi ditutup.
     * Bersifat internal supaya uji dapat memanggilnya langsung setelah
     * memajukan jam palsu, tanpa perlu menunggu perulangan ticker.
     */
    internal fun perbaruiDariJam() {
        val state = _uiState.value
        if (!state.isRunning) return

        val sisa = hitungSisaDetik()
        if (sisa <= 0) {
            selesaikanFase()
        } else {
            _uiState.value = state.copy(remainingSeconds = sisa)
        }
    }

    private fun hitungSisaDetik(): Int {
        val sisaMillis = targetEndMillis - nowMillis()
        // Dibulatkan ke atas agar detik terakhir tidak terlewat begitu saja.
        return if (sisaMillis <= 0) 0 else ((sisaMillis + 999) / 1000).toInt()
    }

    private fun mulaiFase(phase: PomodoroPhase) {
        val totalDetik = phase.defaultMinutes * 60
        val sekarang = nowMillis()

        targetEndMillis = sekarang + totalDetik * 1000L
        if (phase == PomodoroPhase.FOCUS) sessionStartMillis = sekarang

        _uiState.value = _uiState.value.copy(
            phase = phase,
            isRunning = true,
            remainingSeconds = totalDetik,
            totalSeconds = totalDetik,
        )
        jalankanTicker()
    }

    /**
     * Dipanggil saat waktu sebuah fase habis.
     *
     * Siklusnya: 4 sesi fokus, masing-masing diselingi istirahat pendek,
     * kecuali setelah sesi keempat yang diikuti istirahat panjang lalu
     * hitungan siklus kembali ke nol.
     */
    private fun selesaikanFase() {
        val state = _uiState.value
        hentikanTicker()

        when (state.phase) {
            PomodoroPhase.FOCUS -> {
                // Sesi yang selesai penuh dicatat DAN menambah progres tugas.
                viewModelScope.launch {
                    pomodoroRepository.recordCompletedSession(
                        taskId = state.taskId,
                        startedAt = sessionStartMillis,
                        durationMinutes = PomodoroPhase.FOCUS.defaultMinutes,
                    )
                }

                val sesiSelesai = state.completedFocusInCycle + 1
                _uiState.value = state.copy(completedFocusInCycle = sesiSelesai)

                if (sesiSelesai >= SESI_PER_SIKLUS) {
                    mulaiFase(PomodoroPhase.LONG_BREAK)
                } else {
                    mulaiFase(PomodoroPhase.SHORT_BREAK)
                }
            }

            PomodoroPhase.SHORT_BREAK -> mulaiFase(PomodoroPhase.FOCUS)

            PomodoroPhase.LONG_BREAK -> {
                // Siklus selesai, hitungan sesi dikembalikan ke nol.
                _uiState.value = state.copy(completedFocusInCycle = 0)
                mulaiFase(PomodoroPhase.FOCUS)
            }

            PomodoroPhase.IDLE -> Unit
        }
    }

    private fun jalankanTicker() {
        hentikanTicker()
        tickJob = viewModelScope.launch {
            while (isActive && _uiState.value.isRunning) {
                delay(TICK_INTERVAL_MILLIS)
                perbaruiDariJam()
            }
        }
    }

    private fun hentikanTicker() {
        tickJob?.cancel()
        tickJob = null
    }

    override fun onCleared() {
        super.onCleared()
        hentikanTicker()
    }

    companion object {
        /** Jumlah sesi fokus sebelum istirahat panjang. */
        const val SESI_PER_SIKLUS = 4

        /**
         * Seberapa sering layar digambar ulang. Bukan seberapa sering waktu
         * dihitung - waktu selalu diambil dari jam sistem.
         */
        private const val TICK_INTERVAL_MILLIS = 250L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WaktuKuApplication
                PomodoroViewModel(
                    taskRepository = application.container.taskRepository,
                    pomodoroRepository = application.container.pomodoroRepository,
                )
            }
        }
    }
}
