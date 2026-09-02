package com.kelompok.waktuku.data

import android.content.Context

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 3, dipakai oleh Mahasiswa 2
// ============================================================================
// AppContainer = Dependency Injection secara MANUAL.
// Polanya diambil persis dari JetNews di repositori android/compose-samples.
//
// Tugasnya cuma satu: menjadi tempat perakitan objek. Ia yang tahu bahwa
// "untuk membuat TaskRepository, dibutuhkan TaskDao dari WaktuKuDatabase".
// Dengan begitu, TaskViewModel cukup menerima TaskRepository yang sudah jadi
// dan tidak perlu tahu cara membuatnya.
//
// Kenapa tidak pakai Hilt? Untuk aplikasi seukuran ini, DI manual lebih mudah
// dijelaskan karena seluruh alurnya terlihat kasat mata - tidak ada kode yang
// "muncul dari anotasi". compose-samples pun memilih pendekatan ini.
// ============================================================================

/** Kontrak berisi semua dependensi tingkat aplikasi. */
interface AppContainer {
    val taskRepository: TaskRepository
}

/**
 * Implementasi container untuk aplikasi sungguhan.
 *
 * `by lazy` berarti database & repository baru dibuat saat PERTAMA KALI
 * dipakai, bukan saat aplikasi dinyalakan. Efeknya: waktu buka aplikasi
 * tetap ringan.
 */
class AppDataContainer(private val context: Context) : AppContainer {

    override val taskRepository: TaskRepository by lazy {
        OfflineTaskRepository(WaktuKuDatabase.getDatabase(context).taskDao())
    }
}
