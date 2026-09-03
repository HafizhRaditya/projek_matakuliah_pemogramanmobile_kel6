package com.kelompok.waktuku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelompok.waktuku.model.TaskPriority
import com.kelompok.waktuku.ui.theme.WaktuKuTheme

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 1 (UI/UX dengan Jetpack Compose)
// ============================================================================
// Dialog untuk menambah tugas baru.
//
// Penanganan input di sini mengikuti pola resmi Material Design 3 yang dibahas
// pada kuliah Pertemuan 3 (slide 26 "TextField Input Handling" dan slide 30
// "Analisis Studi Kasus"):
//   value + onValueChange  -> state input dan callback saat teks berubah
//   isError + supportingText -> warna berubah merah, disertai pesan penjelas
//   leadingIcon            -> ikon penanda di sisi kiri field
//
// Catatan penting soal pembagian state:
// Teks yang sedang DIKETIK pengguna disimpan di sini dengan `remember`, BUKAN
// di ViewModel. Alasannya, teks setengah jadi belum berarti apa-apa bagi
// aplikasi - ia baru menjadi "data" setelah tombol Simpan ditekan. Kalau
// setiap ketukan huruf dikirim ke ViewModel, kita membuat ViewModel bekerja
// puluhan kali tanpa manfaat.
// ============================================================================

/**
 * @param onDismiss dipanggil saat dialog ditutup tanpa menyimpan.
 * @param onConfirm dipanggil dengan judul dan prioritas yang dipilih.
 */
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, priority: TaskPriority) -> Unit,
    modifier: Modifier = Modifier,
) {
    // rememberSaveable tidak dipakai di sini karena dialog memang ditutup saat
    // layar diputar. Kalau nanti isian ini ingin bertahan, ganti `remember`
    // menjadi `rememberSaveable`.
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }

    // Penanda apakah pengguna sudah pernah menyentuh field judul.
    // Tanpa penanda ini, pesan merah "tidak boleh kosong" akan langsung muncul
    // begitu dialog dibuka - padahal pengguna belum melakukan kesalahan apa pun.
    var sudahDisentuh by remember { mutableStateOf(false) }

    val judulKosong = title.isBlank()

    // SATU SUMBER KEBENARAN: nilai ini dipakai bersama oleh `isError` dan
    // `supportingText`, sehingga warna field dan pesan di bawahnya mustahil
    // saling bertentangan (slide 30, bagian "Error Propagation").
    val tampilkanError = sudahDisentuh && judulKosong

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text("Tugas baru") },
        text = {
            // spacedBy dipilih daripada padding per elemen: jaraknya ditulis
            // satu kali di induk dan otomatis berlaku ke semua anak
            // (slide 30, "Mengapa Arrangement.spacedBy()").
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        // Validasi berjalan langsung saat pengguna mengetik,
                        // bukan hanya saat tombol ditekan.
                        sudahDisentuh = true
                    },
                    label = { Text("Judul tugas") },
                    placeholder = { Text("Contoh: Kerjakan laporan PemMob") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            // null karena ikon ini hiasan; label field sudah
                            // menjelaskan maknanya bagi pembaca layar.
                            contentDescription = null,
                        )
                    },
                    isError = tampilkanError,
                    supportingText = {
                        // Selalu ditampilkan - berisi petunjuk saat normal dan
                        // pesan kesalahan saat kosong. Kalau hanya dirender
                        // ketika error, tinggi dialog akan melompat-lompat.
                        Text(
                            text = if (tampilkanError) {
                                "Judul tugas tidak boleh kosong"
                            } else {
                                "Wajib diisi"
                            }
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        // Huruf pertama tiap kalimat otomatis kapital.
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                // Merujuk skala tipografi dari tema, bukan menulis fontSize
                // secara langsung (slide 22: "Jangan gunakan fontSize = 18.sp
                // secara hardcoded").
                Text(
                    text = "Prioritas",
                    style = MaterialTheme.typography.labelLarge,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { option ->
                        FilterChip(
                            selected = option == priority,
                            onClick = { priority = option },
                            label = { Text(option.label) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validasi diulang saat tombol ditekan, mengikuti pola
                    // slide 30. Tombol sengaja TIDAK dinonaktifkan supaya
                    // pengguna yang menekannya tanpa mengisi apa pun tetap
                    // mendapat penjelasan, bukan sekadar tombol mati tanpa
                    // alasan yang terlihat.
                    sudahDisentuh = true
                    if (!judulKosong) {
                        onConfirm(title, priority)
                    }
                },
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
    )
}

@Preview
@Composable
private fun AddTaskDialogPreview() {
    WaktuKuTheme {
        AddTaskDialog(
            onDismiss = {},
            onConfirm = { _, _ -> },
        )
    }
}
