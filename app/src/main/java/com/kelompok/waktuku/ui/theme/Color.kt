package com.kelompok.waktuku.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 1 (UI/UX dengan Jetpack Compose)
// ============================================================================
// Palet warna WaktuKu, diturunkan dari SATU warna dasar (seed): sage #4E7D6B.
// Sage dipilih karena di DESIGN.md hasil Stitch warna ini dipakai untuk
// "fokus" - inti aplikasi ini - dan kesannya tenang, cocok untuk belajar.
//
// Kenapa tidak memilih warna satu per satu? Material 3 memakai 35 peran warna
// untuk mode terang dan 35 lagi untuk mode gelap. Memilih manual hampir pasti
// menghasilkan pasangan yang kontrasnya kurang, sehingga teks sulit dibaca.
//
// Algoritma warna M3 (ruang warna HCT) membuat enam PALET dari seed:
//   primary        rona seed, cukup berwarna   -> tombol utama, cincin timer
//   secondary      rona seed, kalem            -> chip terpilih, tombol tonal
//   tertiary       rona seed + 60 derajat      -> aksen pembeda, lencana "Sedang"
//   error          merah tetap                 -> lencana "Tinggi", pesan galat
//   neutral        hampir abu-abu              -> latar, permukaan, teks
//   neutralVariant abu-abu sedikit berwarna    -> garis tepi, teks sekunder
// Setiap palet punya "nada" (tone) dari 0 = hitam sampai 100 = putih. Setiap
// peran mengambil satu nada, tertulis di komentar tiap baris. Contoh: di mode
// terang primary = nada 40 dan onPrimary = nada 100. Selisih nada yang besar
// itulah yang menjamin teks di atas warna tetap terbaca.
// Mode gelap memakai palet yang sama, hanya nadanya dibalik.
//
// Mengganti seed: buka Material Theme Builder
// (https://material-foundation.github.io/material-theme-builder/), masukkan
// seed baru, pilih Export -> Jetpack Compose, lalu salin nilainya ke sini.
// Nama variabel sengaja mengikuti pola ekspor alat itu (primaryLight, dst.).
// ============================================================================

// --- Mode terang ---
val primaryLight = Color(0xFF186B53)                 // primary nada 40
val onPrimaryLight = Color(0xFFFFFFFF)               // primary nada 100
val primaryContainerLight = Color(0xFFA5F2D4)        // primary nada 90
val onPrimaryContainerLight = Color(0xFF002116)      // primary nada 10
val secondaryLight = Color(0xFF4C6359)               // secondary nada 40
val onSecondaryLight = Color(0xFFFFFFFF)             // secondary nada 100
val secondaryContainerLight = Color(0xFFCEE8DB)      // secondary nada 90
val onSecondaryContainerLight = Color(0xFF082018)    // secondary nada 10
val tertiaryLight = Color(0xFF3F6375)                // tertiary nada 40
val onTertiaryLight = Color(0xFFFFFFFF)              // tertiary nada 100
val tertiaryContainerLight = Color(0xFFC3E8FD)       // tertiary nada 90
val onTertiaryContainerLight = Color(0xFF001E2B)     // tertiary nada 10
val errorLight = Color(0xFFBA1B1B)                   // error nada 40
val onErrorLight = Color(0xFFFFFFFF)                 // error nada 100
val errorContainerLight = Color(0xFFFFDAD4)          // error nada 90
val onErrorContainerLight = Color(0xFF410001)        // error nada 10
val backgroundLight = Color(0xFFF5FBF6)              // neutral nada 98
val onBackgroundLight = Color(0xFF171D1A)            // neutral nada 10
val surfaceLight = Color(0xFFF5FBF6)                 // neutral nada 98
val onSurfaceLight = Color(0xFF171D1A)               // neutral nada 10
val surfaceVariantLight = Color(0xFFDBE5DE)          // neutralVariant nada 90
val onSurfaceVariantLight = Color(0xFF3F4944)        // neutralVariant nada 30
val outlineLight = Color(0xFF707974)                 // neutralVariant nada 50
val outlineVariantLight = Color(0xFFBFC9C3)          // neutralVariant nada 80
val scrimLight = Color(0xFF000000)                   // neutral nada 0
val inverseSurfaceLight = Color(0xFF2C322F)          // neutral nada 20
val inverseOnSurfaceLight = Color(0xFFECF2ED)        // neutral nada 95
val inversePrimaryLight = Color(0xFF89D5B8)          // primary nada 80
val surfaceDimLight = Color(0xFFD6DCD7)              // neutral nada 87
val surfaceBrightLight = Color(0xFFF5FBF6)           // neutral nada 98
val surfaceContainerLowestLight = Color(0xFFFFFFFF)  // neutral nada 100
val surfaceContainerLowLight = Color(0xFFEFF5F0)     // neutral nada 96
val surfaceContainerLight = Color(0xFFE9EFEA)        // neutral nada 94
val surfaceContainerHighLight = Color(0xFFE4EAE5)    // neutral nada 92
val surfaceContainerHighestLight = Color(0xFFDEE4DF) // neutral nada 90

// --- Mode gelap ---
val primaryDark = Color(0xFF89D5B8)                  // primary nada 80
val onPrimaryDark = Color(0xFF003828)                // primary nada 20
val primaryContainerDark = Color(0xFF00513C)         // primary nada 30
val onPrimaryContainerDark = Color(0xFFA5F2D4)       // primary nada 90
val secondaryDark = Color(0xFFB3CCC0)                // secondary nada 80
val onSecondaryDark = Color(0xFF1E352C)              // secondary nada 20
val secondaryContainerDark = Color(0xFF344C42)       // secondary nada 30
val onSecondaryContainerDark = Color(0xFFCEE8DB)     // secondary nada 90
val tertiaryDark = Color(0xFFA7CCE1)                 // tertiary nada 80
val onTertiaryDark = Color(0xFF0B3445)               // tertiary nada 20
val tertiaryContainerDark = Color(0xFF264B5D)        // tertiary nada 30
val onTertiaryContainerDark = Color(0xFFC3E8FD)      // tertiary nada 90
val errorDark = Color(0xFFFFB4A9)                    // error nada 80
val onErrorDark = Color(0xFF680003)                  // error nada 20
val errorContainerDark = Color(0xFF930006)           // error nada 30
val onErrorContainerDark = Color(0xFFFFDAD4)         // error nada 90
val backgroundDark = Color(0xFF0F1512)               // neutral nada 6
val onBackgroundDark = Color(0xFFDEE4DF)             // neutral nada 90
val surfaceDark = Color(0xFF0F1512)                  // neutral nada 6
val onSurfaceDark = Color(0xFFDEE4DF)                // neutral nada 90
val surfaceVariantDark = Color(0xFF3F4944)           // neutralVariant nada 30
val onSurfaceVariantDark = Color(0xFFBFC9C3)         // neutralVariant nada 80
val outlineDark = Color(0xFF89938D)                  // neutralVariant nada 60
val outlineVariantDark = Color(0xFF3F4944)           // neutralVariant nada 30
val scrimDark = Color(0xFF000000)                    // neutral nada 0
val inverseSurfaceDark = Color(0xFFDEE4DF)           // neutral nada 90
val inverseOnSurfaceDark = Color(0xFF2C322F)         // neutral nada 20
val inversePrimaryDark = Color(0xFF186B53)           // primary nada 40
val surfaceDimDark = Color(0xFF0F1512)               // neutral nada 6
val surfaceBrightDark = Color(0xFF343B37)            // neutral nada 24
val surfaceContainerLowestDark = Color(0xFF0A0F0D)   // neutral nada 4
val surfaceContainerLowDark = Color(0xFF171D1A)      // neutral nada 10
val surfaceContainerDark = Color(0xFF1B211E)         // neutral nada 12
val surfaceContainerHighDark = Color(0xFF252B28)     // neutral nada 17
val surfaceContainerHighestDark = Color(0xFF303633)  // neutral nada 22
