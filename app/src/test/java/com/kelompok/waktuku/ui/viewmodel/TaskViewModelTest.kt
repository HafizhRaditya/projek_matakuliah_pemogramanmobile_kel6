package com.kelompok.waktuku.ui.viewmodel

import com.kelompok.waktuku.data.TaskRepository
import com.kelompok.waktuku.model.Task
import com.kelompok.waktuku.model.TaskPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 2 (State Management & Logika)
// ============================================================================
// Uji unit untuk alur "hapus + Urungkan" di TaskViewModel.
//
// Uji JVM biasa - tidak butuh HP maupun emulator. Database diganti
// FakeTaskRepository berisi daftar di memori; itu mungkin karena
// TaskRepository berbentuk interface.
//
// Cara menjalankan:
//     ./gradlew :app:testDebugUnitTest
// ============================================================================

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val laporan = Task(id = 1L, title = "Kerjakan laporan", priority = TaskPriority.HIGH)
    private val kuis = Task(id = 2L, title = "Belajar kuis", priority = TaskPriority.LOW)

    private lateinit var repository: FakeTaskRepository
    private lateinit var viewModel: TaskViewModel

    @Before
    fun siapkan() {
        Dispatchers.setMain(dispatcher)
        repository = FakeTaskRepository(listOf(laporan, kuis))
        viewModel = TaskViewModel(repository)
    }

    @After
    fun bersihkan() {
        Dispatchers.resetMain()
    }

    @Test
    fun tandaiHapus_tugasHilangDariLayar_tetapiBelumDihapusDariDatabase() = runTest(dispatcher) {
        // uiState memakai WhileSubscribed, jadi harus ada yang mengamati
        // supaya alirannya berjalan - persis seperti layar yang sedang tampil.
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.markForDeletion(laporan)
        advanceUntilIdle()

        assertEquals(listOf(kuis), viewModel.uiState.value.tasks)
        // Ringkasan "x/y selesai" juga tidak lagi menghitung tugas itu.
        assertEquals(1, viewModel.uiState.value.totalCount)
        assertTrue(repository.dihapus.isEmpty())
    }

    @Test
    fun urungkan_tugasMunculLagi_danTidakPernahDihapus() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.markForDeletion(laporan)
        advanceUntilIdle()
        viewModel.undoDeletion(laporan)
        advanceUntilIdle()

        assertEquals(listOf(laporan, kuis), viewModel.uiState.value.tasks)
        assertTrue(repository.dihapus.isEmpty())
    }

    @Test
    fun hapusDikonfirmasi_tugasBenarBenarDihapusDariDatabase() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.markForDeletion(laporan)
        viewModel.deleteTask(laporan)
        advanceUntilIdle()

        assertEquals(listOf(laporan), repository.dihapus)
        assertEquals(listOf(kuis), viewModel.uiState.value.tasks)
    }

    /**
     * Pengganti database: cukup daftar di memori.
     *
     * Dibuat private di dalam kelas uji supaya namanya tidak bentrok dengan
     * FakeTaskRepository milik uji lain di paket berbeda.
     */
    private class FakeTaskRepository(awal: List<Task>) : TaskRepository {

        private val semua = MutableStateFlow(awal)

        /** Catatan tugas yang benar-benar dihapus, untuk diperiksa uji. */
        val dihapus = mutableListOf<Task>()

        override fun observeTasks(): Flow<List<Task>> = semua

        override fun observeTask(id: Long): Flow<Task?> =
            semua.map { daftar -> daftar.find { it.id == id } }

        override suspend fun saveTask(task: Task): Long {
            semua.update { daftar -> daftar + task }
            return task.id
        }

        override suspend fun addTask(
            title: String,
            notes: String,
            dueAt: Long?,
            priority: TaskPriority,
            estimatedPomodoros: Int,
        ): Long {
            val idBaru = (semua.value.maxOfOrNull { it.id } ?: 0L) + 1
            return saveTask(Task(id = idBaru, title = title, priority = priority))
        }

        override suspend fun setDone(id: Long, isDone: Boolean) {
            semua.update { daftar -> daftar.map { if (it.id == id) it.copy(isDone = isDone) else it } }
        }

        override suspend fun deleteTask(task: Task) {
            dihapus += task
            semua.update { daftar -> daftar - task }
        }

        override suspend fun clearCompleted() {
            semua.update { daftar -> daftar.filterNot { it.isDone } }
        }
    }
}
