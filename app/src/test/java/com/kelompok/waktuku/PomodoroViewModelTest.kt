package com.kelompok.waktuku

import com.kelompok.waktuku.data.PomodoroRepository
import com.kelompok.waktuku.data.TaskRepository
import com.kelompok.waktuku.model.PomodoroSession
import com.kelompok.waktuku.model.Task
import com.kelompok.waktuku.model.TaskPriority
import com.kelompok.waktuku.ui.viewmodel.PomodoroPhase
import com.kelompok.waktuku.ui.viewmodel.PomodoroViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 2 (State Management & Logika)
// ============================================================================
// Uji unit untuk PomodoroViewModel.
//
// Ini uji JVM biasa - TIDAK butuh HP maupun emulator, dan selesai dalam
// hitungan milidetik. Itu memungkinkan karena dua keputusan desain:
//
//   1. PomodoroViewModel menerima sumber waktu dari luar (nowMillis), sehingga
//      di sini kita bisa memajukan jam 25 menit hanya dengan satu baris kode
//      alih-alih menunggu 25 menit sungguhan.
//
//   2. TaskRepository dan PomodoroRepository berupa interface, sehingga bisa
//      digantikan versi palsu tanpa database sama sekali.
//
// Cara menjalankan:
//     ./gradlew :app:testDebugUnitTest
// ============================================================================

@OptIn(ExperimentalCoroutinesApi::class)
class PomodoroViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    /** Jam palsu. Nilainya hanya berubah kalau uji yang memajukannya. */
    private var jamPalsu = 1_700_000_000_000L

    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var pomodoroRepository: FakePomodoroRepository

    @Before
    fun siapkan() {
        Dispatchers.setMain(dispatcher)
        taskRepository = FakeTaskRepository().apply {
            tasks[1L] = Task(id = 1L, title = "Belajar UTS", priority = TaskPriority.HIGH)
        }
        pomodoroRepository = FakePomodoroRepository()
    }

    @After
    fun bersihkan() {
        Dispatchers.resetMain()
    }

    private fun buatViewModel() = PomodoroViewModel(
        taskRepository = taskRepository,
        pomodoroRepository = pomodoroRepository,
        nowMillis = { jamPalsu },
    )

    /**
     * Pembungkus untuk setiap uji.
     *
     * Bagian `finally` WAJIB ada. Ticker di dalam ViewModel adalah perulangan
     * tak berujung yang menjadwalkan delay berikutnya terus-menerus. Saat uji
     * ditutup, runTest menjalankan advanceUntilIdle() - dan penjadwal tidak
     * akan pernah "idle" selama ticker masih hidup, sehingga ujinya
     * menggantung selamanya. Menjeda timer akan membatalkan ticker itu.
     */
    private fun ujiTimer(body: TestScope.(PomodoroViewModel) -> Unit) = runTest(dispatcher) {
        val vm = buatViewModel()
        try {
            body(vm)
        } finally {
            vm.jeda()
        }
    }

    private fun majukanJam(menit: Int) {
        jamPalsu += menit * 60_000L
    }

    // ------------------------------------------------------------------
    // Inilah uji yang paling penting di seluruh berkas ini.
    // ------------------------------------------------------------------

    @Test
    fun `sisa waktu dihitung dari jam sistem, bukan dari hitungan tick`() = ujiTimer { vm ->
        vm.pilihTugas(1L)
        runCurrent()
        vm.mulai()

        assertEquals(25 * 60, vm.uiState.value.remainingSeconds)

        // Jam maju 10 menit tanpa satu pun tick dijalankan.
        majukanJam(10)
        vm.perbaruiDariJam()

        assertEquals(15 * 60, vm.uiState.value.remainingSeconds)
    }

    @Test
    fun `timer tetap akurat setelah aplikasi lama ditinggalkan`() = ujiTimer { vm ->
        vm.pilihTugas(1L)
        runCurrent()
        vm.mulai()

        // Menirukan keadaan nyata: pengguna mematikan layar, Android
        // menidurkan proses aplikasi, lalu 24 menit kemudian dibuka lagi.
        // Kalau timer memakai hitungan tick, angkanya akan tertinggal jauh.
        majukanJam(24)
        vm.perbaruiDariJam()

        assertEquals(60, vm.uiState.value.remainingSeconds)
        assertTrue(vm.uiState.value.isRunning)
    }

    @Test
    fun `lama jeda tidak ikut memakan waktu sesi`() = ujiTimer { vm ->
        vm.pilihTugas(1L)
        runCurrent()
        vm.mulai()

        majukanJam(5)
        vm.perbaruiDariJam()
        assertEquals(20 * 60, vm.uiState.value.remainingSeconds)

        vm.jeda()
        assertFalse(vm.uiState.value.isRunning)
        assertTrue(vm.uiState.value.isPaused)

        // Dijeda selama 30 menit - jauh lebih lama dari sisa sesinya.
        majukanJam(30)
        vm.lanjut()

        // Sisa waktunya harus tetap 20 menit, bukan habis.
        assertEquals(20 * 60, vm.uiState.value.remainingSeconds)
        assertTrue(vm.uiState.value.isRunning)
    }

    // ------------------------------------------------------------------
    // Pencatatan sesi
    // ------------------------------------------------------------------

    @Test
    fun `sesi fokus yang selesai penuh dicatat dan menambah progres`() = ujiTimer { vm ->
        vm.pilihTugas(1L)
        runCurrent()
        vm.mulai()

        majukanJam(25)
        vm.perbaruiDariJam()
        runCurrent()

        assertEquals(1, pomodoroRepository.selesai.size)
        assertEquals(1L, pomodoroRepository.selesai.first().taskId)
        assertEquals(25, pomodoroRepository.selesai.first().durationMinutes)
        assertEquals(0, pomodoroRepository.dibatalkan.size)

        // Setelah fokus pertama, otomatis masuk istirahat pendek.
        assertEquals(PomodoroPhase.SHORT_BREAK, vm.uiState.value.phase)
        assertEquals(1, vm.uiState.value.completedFocusInCycle)
    }

    @Test
    fun `sesi yang dihentikan di tengah dicatat tapi tidak menambah progres`() = ujiTimer { vm ->
        vm.pilihTugas(1L)
        runCurrent()
        vm.mulai()

        majukanJam(7)
        vm.perbaruiDariJam()
        vm.hentikan()
        runCurrent()

        assertEquals(0, pomodoroRepository.selesai.size)
        assertEquals(1, pomodoroRepository.dibatalkan.size)
        assertEquals(7, pomodoroRepository.dibatalkan.first().durationMinutes)

        assertEquals(PomodoroPhase.IDLE, vm.uiState.value.phase)
        assertEquals(0, vm.uiState.value.completedFocusInCycle)
    }

    // ------------------------------------------------------------------
    // Siklus
    // ------------------------------------------------------------------

    @Test
    fun `empat sesi fokus diikuti istirahat panjang`() = ujiTimer { vm ->
        vm.pilihTugas(1L)
        runCurrent()
        vm.mulai()

        // Tiga siklus pertama: fokus 25 menit lalu istirahat pendek 5 menit.
        repeat(3) {
            majukanJam(25); vm.perbaruiDariJam(); runCurrent()
            assertEquals(PomodoroPhase.SHORT_BREAK, vm.uiState.value.phase)
            majukanJam(5); vm.perbaruiDariJam(); runCurrent()
            assertEquals(PomodoroPhase.FOCUS, vm.uiState.value.phase)
        }

        // Sesi fokus keempat selesai -> harus masuk istirahat PANJANG.
        majukanJam(25); vm.perbaruiDariJam(); runCurrent()

        assertEquals(PomodoroPhase.LONG_BREAK, vm.uiState.value.phase)
        assertEquals(4, vm.uiState.value.completedFocusInCycle)
        assertEquals(4, pomodoroRepository.selesai.size)
    }

    @Test
    fun `timer tidak bisa dimulai tanpa memilih tugas`() = ujiTimer { vm ->

        vm.mulai()

        assertEquals(PomodoroPhase.IDLE, vm.uiState.value.phase)
        assertFalse(vm.uiState.value.isRunning)
        assertTrue(vm.uiState.value.belumAdaTugas)
    }

    @Test
    fun `judul tugas diambil dari repository saat layar dibuka`() = ujiTimer { vm ->

        vm.pilihTugas(1L)
        runCurrent()

        assertEquals("Belajar UTS", vm.uiState.value.taskTitle)
        assertEquals(1L, vm.uiState.value.taskId)
    }
}

// ============================================================================
// VERSI PALSU DARI REPOSITORY
// ============================================================================
// Keduanya bisa dibuat karena TaskRepository dan PomodoroRepository berupa
// interface. Inilah alasan konkret kenapa sejak awal keduanya tidak ditulis
// sebagai class langsung.
// ============================================================================

private class FakeTaskRepository : TaskRepository {
    val tasks = mutableMapOf<Long, Task>()

    override fun observeTasks(): Flow<List<Task>> = flowOf(tasks.values.toList())
    override fun observeTask(id: Long): Flow<Task?> = flowOf(tasks[id])
    override suspend fun saveTask(task: Task): Long = task.id
    override suspend fun addTask(
        title: String,
        notes: String,
        dueAt: Long?,
        priority: TaskPriority,
        estimatedPomodoros: Int,
    ): Long = 0L

    override suspend fun setDone(id: Long, isDone: Boolean) = Unit
    override suspend fun deleteTask(task: Task) = Unit
    override suspend fun clearCompleted() = Unit
}

private class FakePomodoroRepository : PomodoroRepository {
    data class Catatan(val taskId: Long, val startedAt: Long, val durationMinutes: Int)

    val selesai = mutableListOf<Catatan>()
    val dibatalkan = mutableListOf<Catatan>()

    override fun observeSessionsForTask(taskId: Long): Flow<List<PomodoroSession>> = flowOf(emptyList())
    override fun observeCompletedCount(mulai: Long, sampai: Long): Flow<Int> = flowOf(selesai.size)
    override fun observeTotalMinutes(mulai: Long, sampai: Long): Flow<Int> =
        flowOf(selesai.sumOf { it.durationMinutes })

    override suspend fun recordCompletedSession(
        taskId: Long,
        startedAt: Long,
        durationMinutes: Int,
    ): Long {
        selesai += Catatan(taskId, startedAt, durationMinutes)
        return selesai.size.toLong()
    }

    override suspend fun recordAbandonedSession(
        taskId: Long,
        startedAt: Long,
        durationMinutes: Int,
    ): Long {
        dibatalkan += Catatan(taskId, startedAt, durationMinutes)
        return dibatalkan.size.toLong()
    }
}
