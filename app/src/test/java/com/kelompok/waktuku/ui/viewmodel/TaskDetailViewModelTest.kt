package com.kelompok.waktuku.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.kelompok.waktuku.data.PomodoroRepository
import com.kelompok.waktuku.data.TaskRepository
import com.kelompok.waktuku.model.PomodoroSession
import com.kelompok.waktuku.model.Task
import com.kelompok.waktuku.model.TaskPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
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
// Uji unit untuk TaskDetailViewModel. Uji JVM biasa: tidak butuh HP maupun
// emulator. Database diganti repository palsu berisi data di memori, dan
// argumen rute diganti SavedStateHandle buatan.
//
// Cara menjalankan:
//     ./gradlew :app:testDebugUnitTest
// ============================================================================

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val laporan = Task(
        id = 1L,
        title = "Kerjakan laporan",
        priority = TaskPriority.MEDIUM,
        estimatedPomodoros = 4,
        completedPomodoros = 2,
    )

    private lateinit var tasks: FakeTaskRepository

    @Before
    fun siapkan() {
        Dispatchers.setMain(dispatcher)
        tasks = FakeTaskRepository(listOf(laporan))
    }

    @After
    fun bersihkan() {
        Dispatchers.resetMain()
    }

    /** Membuat ViewModel seolah layar Detail dibuka dengan rute "taskId = [id]". */
    private fun TestScope.bukaDetail(id: Long): TaskDetailViewModel {
        val viewModel = TaskDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf(TaskDetailViewModel.ARG_TASK_ID to id)),
            taskRepository = tasks,
            pomodoroRepository = FakePomodoroRepository(),
        )
        // uiState memakai WhileSubscribed, jadi harus ada yang mengamati,
        // persis seperti layar yang sedang tampil.
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()
        return viewModel
    }

    private fun TaskDetailViewModel.sukses() = uiState.value as TaskDetailUiState.Success

    @Test
    fun dibuka_formulirBerisiDataTugas() = runTest(dispatcher) {
        val viewModel = bukaDetail(laporan.id)

        val state = viewModel.sukses()
        assertEquals("Kerjakan laporan", state.form.title)
        assertEquals(4, state.form.estimatedPomodoros)
        // Belum ada yang diubah, jadi tombol Simpan harus mati.
        assertFalse(state.adaPerubahan)
    }

    @Test
    fun idTidakAda_tampilkanError() = runTest(dispatcher) {
        val viewModel = bukaDetail(99L)

        assertTrue(viewModel.uiState.value is TaskDetailUiState.Error)
    }

    @Test
    fun judulKosong_tidakDisimpan() = runTest(dispatcher) {
        val viewModel = bukaDetail(laporan.id)

        viewModel.ubahJudul("   ")
        advanceUntilIdle()
        viewModel.simpan()
        advanceUntilIdle()

        assertFalse(viewModel.sukses().form.judulValid)
        assertTrue(tasks.disimpan.isEmpty())
        assertFalse(viewModel.selesai.value)
    }

    @Test
    fun ubahLaluSimpan_perubahanTertulisDanProgresTetap() = runTest(dispatcher) {
        val viewModel = bukaDetail(laporan.id)

        viewModel.ubahJudul("Laporan bab 3")
        viewModel.ubahPrioritas(TaskPriority.HIGH)
        viewModel.tambahTarget()
        advanceUntilIdle()
        assertTrue(viewModel.sukses().adaPerubahan)

        viewModel.simpan()
        advanceUntilIdle()

        val tersimpan = tasks.disimpan.single()
        assertEquals("Laporan bab 3", tersimpan.title)
        assertEquals(TaskPriority.HIGH, tersimpan.priority)
        assertEquals(5, tersimpan.estimatedPomodoros)
        // Kolom yang tidak ada di formulir tidak boleh ikut berubah.
        assertEquals(2, tersimpan.completedPomodoros)
        assertTrue(viewModel.selesai.value)
    }

    @Test
    fun targetSesi_tidakBisaKurangDariSatu() = runTest(dispatcher) {
        tasks = FakeTaskRepository(listOf(laporan.copy(estimatedPomodoros = 1)))
        val viewModel = bukaDetail(laporan.id)

        viewModel.kurangiTarget()
        advanceUntilIdle()

        assertEquals(TaskDetailForm.TARGET_MIN, viewModel.sukses().form.estimatedPomodoros)
    }

    @Test
    fun hapus_tugasTerhapusTanpaPesanError() = runTest(dispatcher) {
        val viewModel = bukaDetail(laporan.id)

        viewModel.hapus()
        advanceUntilIdle()

        assertEquals(listOf(laporan), tasks.dihapus)
        assertTrue(viewModel.selesai.value)
        // Tugas yang sengaja dihapus tidak boleh memunculkan
        // "Tugas tidak ditemukan" sebelum layar ditutup.
        assertEquals(TaskDetailUiState.Loading, viewModel.uiState.value)
    }

    /** Pengganti database tugas: cukup daftar di memori. */
    private class FakeTaskRepository(awal: List<Task>) : TaskRepository {

        private val semua = MutableStateFlow(awal)

        val disimpan = mutableListOf<Task>()
        val dihapus = mutableListOf<Task>()

        override fun observeTasks(): Flow<List<Task>> = semua

        override fun observeTask(id: Long): Flow<Task?> =
            semua.map { daftar -> daftar.find { it.id == id } }

        override suspend fun saveTask(task: Task): Long {
            disimpan += task
            semua.update { daftar -> daftar.map { if (it.id == task.id) task else it } }
            return task.id
        }

        override suspend fun addTask(
            title: String,
            notes: String,
            dueAt: Long?,
            priority: TaskPriority,
            estimatedPomodoros: Int,
        ): Long = error("Tidak dipakai di uji ini")

        override suspend fun setDone(id: Long, isDone: Boolean) = Unit

        override suspend fun deleteTask(task: Task) {
            dihapus += task
            semua.update { daftar -> daftar.filterNot { it.id == task.id } }
        }

        override suspend fun clearCompleted() = Unit
    }

    /** Pengganti database sesi. Riwayat sesinya kosong. */
    private class FakePomodoroRepository : PomodoroRepository {
        override fun observeSessionsForTask(taskId: Long): Flow<List<PomodoroSession>> = flowOf(emptyList())
        override fun observeCompletedCount(mulai: Long, sampai: Long): Flow<Int> = flowOf(0)
        override fun observeTotalMinutes(mulai: Long, sampai: Long): Flow<Int> = flowOf(0)
        override suspend fun recordCompletedSession(taskId: Long, startedAt: Long, durationMinutes: Int): Long = 0L
        override suspend fun recordAbandonedSession(taskId: Long, startedAt: Long, durationMinutes: Int): Long = 0L
    }
}
