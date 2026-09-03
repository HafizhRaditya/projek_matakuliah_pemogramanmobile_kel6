plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // KSP wajib diaktifkan agar anotasi Room (@Entity, @Dao, @Database)
    // diproses menjadi kode nyata saat build.
    alias(libs.plugins.ksp)
}

android {
    // namespace = identitas package untuk class R dan Manifest
    namespace = "com.kelompok.waktuku"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        // applicationId = identitas unik aplikasi di perangkat & Play Store
        applicationId = "com.kelompok.waktuku"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Compose (BOM mengunci semua versi artefak Compose agar sinkron) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)

    // --- Lifecycle & ViewModel ---
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // --- Navigasi antar layar ---
    implementation(libs.androidx.navigation.compose)

    // --- Room (database lokal, aplikasi tetap jalan tanpa internet) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // Perhatikan: compiler memakai ksp(), BUKAN implementation().
    // Kalau baris ini lupa ditulis, Room error "cannot find implementation for WaktuKuDatabase".
    ksp(libs.androidx.room.compiler)

    // --- Testing ---
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.room.testing)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
