package com.kelompok.waktuku.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 1 (UI/UX dengan Jetpack Compose)
// ============================================================================
// Skala tipografi WaktuKu.
//
// Material Design 3 punya 5 skala x 3 ukuran = 15 token (kuliah Pertemuan 3,
// slide 22-23). Berkas ini menuliskan token yang DIPAKAI aplikasi, beserta
// yang akan dipakai layar Detail dan Statistik:
//
//   Token           Ukuran/baris   Dipakai untuk
//   displayMedium   45 / 52 sp     angka hitung mundur di layar Fokus
//   headlineMedium  28 / 36 sp     judul besar halaman
//   headlineSmall   24 / 32 sp     judul form (layar Detail)
//   titleLarge      22 / 28 sp     judul TopAppBar, judul tugas di layar Fokus
//   titleMedium     16 / 24 sp     judul tugas di kartu
//   bodyLarge       16 / 24 sp     teks bacaan utama, pesan tampilan kosong
//   bodyMedium      14 / 20 sp     keterangan pendukung
//   bodySmall       12 / 16 sp     catatan tugas di kartu
//   labelLarge      14 / 20 sp     teks tombol dan chip (bawaan Button M3)
//   labelMedium     12 / 16 sp     label kecil
//   labelSmall      11 / 16 sp     lencana prioritas, tanggal, "2/4 sesi"
//
// Token yang tidak ditulis di sini (displayLarge, displaySmall, headlineLarge,
// titleSmall) otomatis memakai nilai bawaan Material 3.
//
// Layar TIDAK BOLEH menulis fontSize sendiri. Selalu pakai
// `style = MaterialTheme.typography.xxx`, supaya mengubah ukuran huruf cukup
// dilakukan di berkas ini (slide 22: "Jangan gunakan fontSize = 18.sp secara
// hardcoded").
//
// Arti tiap properti:
//   fontFamily     jenis huruf
//   fontWeight     ketebalan (Light, Normal, Medium, SemiBold)
//   fontSize       ukuran huruf, satuan sp agar ikut setelan ukuran font HP
//   lineHeight     jarak antarbaris
//   letterSpacing  jarak antarhuruf; teks kecil sedikit direnggangkan agar
//                  tetap mudah dibaca
// ============================================================================

// Satu jenis huruf untuk seluruh aplikasi. SansSerif di Android adalah Roboto.
// Ditaruh di satu variabel supaya kalau suatu saat font diganti, cukup satu
// baris ini yang diubah.
private val WaktuKuFont = FontFamily.SansSerif

val Typography = Typography(

    // --- Display: teks paling besar ---------------------------------------
    // Hanya dipakai untuk angka timer "24:59".
    displayMedium = TextStyle(
        fontFamily = WaktuKuFont,
        // Light: angka sebesar ini terlihat berat dan sesak bila tebal.
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        // "tnum" = tabular numbers: setiap angka selebar sama. Tanpa ini, teks
        // timer bisa bergeser sedikit setiap detik karena angka 1 lebih sempit
        // daripada angka 0. Roboto sudah begitu secara bawaan, tetapi baris ini
        // menjaminnya tetap berlaku bila font diganti.
        fontFeatureSettings = "tnum",
    ),

    // --- Headline: judul halaman ------------------------------------------
    // SemiBold agar judul jelas lebih "berat" daripada isi di bawahnya.
    headlineMedium = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),

    // --- Title: judul komponen --------------------------------------------
    // titleLarge dipakai TopAppBar secara bawaan, jadi semua judul bilah atas
    // ("WaktuKu", "Fokus") ikut berubah dari sini.
    titleLarge = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),

    // --- Body: teks bacaan ------------------------------------------------
    bodyLarge = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),

    // --- Label: tombol, chip, keterangan kecil ----------------------------
    // Medium agar teks sekecil ini tetap terbaca jelas.
    labelLarge = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = WaktuKuFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
