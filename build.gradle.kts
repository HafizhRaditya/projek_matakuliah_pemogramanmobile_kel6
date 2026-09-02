// Top-level build file. Semua plugin dideklarasikan di sini dengan `apply false`,
// lalu benar-benar diaktifkan di app/build.gradle.kts.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
