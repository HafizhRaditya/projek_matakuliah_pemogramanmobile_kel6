package com.kelompok.waktuku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.kelompok.waktuku.model.TaskPriority
import com.kelompok.waktuku.ui.theme.WaktuKuTheme

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 1 (UI/UX dengan Jetpack Compose)
// ============================================================================
// Dialog untuk menambah tugas baru.
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

    // Tombol Simpan dinonaktifkan selama judul masih kosong - mencegah tugas
    // tanpa nama masuk ke database sejak dari lapisan UI.
    val isValid = title.isNotBlank()

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text("Tugas baru") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul tugas") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        // Huruf pertama tiap kalimat otomatis kapital.
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("Prioritas")

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
                onClick = { onConfirm(title, priority) },
                enabled = isValid,
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
