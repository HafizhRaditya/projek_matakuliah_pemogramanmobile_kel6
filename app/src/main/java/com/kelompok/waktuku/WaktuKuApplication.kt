package com.kelompok.waktuku

import android.app.Application
import com.kelompok.waktuku.data.AppContainer
import com.kelompok.waktuku.data.AppDataContainer

// ============================================================================
// TITIK MASUK APLIKASI (dipakai bersama oleh ketiga anggota tim)
// ============================================================================
// Kelas Application dibuat SATU KALI dan hidup selama aplikasi berjalan.
// Karena itu ia tempat paling tepat untuk menyimpan AppContainer.
//
// JANGAN LUPA: kelas ini harus didaftarkan di AndroidManifest.xml lewat
// atribut android:name=".WaktuKuApplication". Kalau lupa, aplikasi akan crash
// dengan ClassCastException saat TaskViewModel.Factory dijalankan.
// ============================================================================

class WaktuKuApplication : Application() {

    // `private set` -> boleh dibaca dari luar, tapi hanya kelas ini yang
    // berhak mengisinya.
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
