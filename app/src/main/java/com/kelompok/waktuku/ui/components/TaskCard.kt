package com.kelompok.waktuku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelompok.waktuku.model.Task
import com.kelompok.waktuku.model.TaskPriority
import com.kelompok.waktuku.ui.theme.WaktuKuTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ============================================================================
// PENANGGUNG JAWAB: Mahasiswa 1 (UI/UX dengan Jetpack Compose)
// ============================================================================
// TaskCard diletakkan di ui/components (bukan di dalam HomeScreen.kt) karena
// komponen ini akan dipakai ulang di layar lain - misalnya layar Pomodoro yang
// menampilkan tugas sedang dikerjakan. Ini juga memperkecil peluang konflik
// Git: Mahasiswa 1 bisa mengubah tampilan kartu tanpa menyentuh HomeScreen.kt.
// ============================================================================

/**
 * Kartu untuk satu tugas.
 *
 * Perhatikan: fungsi ini STATELESS - ia tidak menyimpan apa pun dan tidak tahu
 * ada ViewModel. Ia hanya menerima data (`task`) dan melaporkan kejadian ke
 * atas lewat lambda. Pola ini bernama STATE HOISTING, dan itulah sebabnya
 * @Preview di bawah bisa jalan tanpa database.
 *
 * Kartu ini punya TIGA area sentuh yang berbeda, dan pembagiannya disengaja:
 *   - kotak centang  -> menandai tugas selesai
 *   - badan kartu    -> membuka layar Detail Tugas
 *   - tombol fokus   -> langsung memulai sesi Pomodoro untuk tugas ini
 *
 * @param task tugas yang digambar.
 * @param onToggleDone dipanggil saat kotak centang ditekan.
 * @param onDelete dipanggil saat ikon tong sampah ditekan.
 * @param onClick dipanggil saat badan kartu ditekan.
 * @param onStartFocus dipanggil saat tombol mulai fokus ditekan.
 */
@Composable
fun TaskCard(
    task: Task,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onStartFocus: () -> Unit,
    // Modifier selalu jadi parameter opsional TERAKHIR dengan nilai bawaan
    // Modifier - ini konvensi resmi Compose agar komponen bisa diatur induknya.
    modifier: Modifier = Modifier,
) {
    Card(
        // onClick dipasang di Card, bukan Modifier.clickable, supaya efek riak
        // (ripple) dan perilaku aksesibilitasnya mengikuti bawaan Material 3.
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = task.isDone,
                // Nilai boolean dari Checkbox tidak dipakai; ViewModel yang
                // menentukan status berikutnya, supaya UI tidak punya versi
                // kebenarannya sendiri.
                onCheckedChange = { onToggleDone() },
            )

            // weight(1f) membuat kolom teks memakan seluruh sisa ruang,
            // sehingga tombol-tombol tetap menempel di kanan.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    // Judul tugas yang selesai dicoret.
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (task.notes.isNotBlank()) {
                    Text(
                        text = task.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Text(
                    text = buildMetaLabel(task),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Tombol fokus disembunyikan pada tugas yang sudah selesai.
            // Menawarkan "mulai fokus" untuk pekerjaan yang sudah rampung itu
            // membingungkan, dan sesi yang tercatat di sana tidak ada gunanya.
            if (!task.isDone) {
                FilledTonalIconButton(
                    onClick = onStartFocus,
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Mulai sesi fokus untuk ${task.title}",
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    // contentDescription WAJIB diisi demi aksesibilitas
                    // (pembaca layar untuk pengguna tunanetra).
                    contentDescription = "Hapus tugas ${task.title}",
                )
            }
        }
    }
}

/**
 * Menyusun baris keterangan kecil, contoh: "Tinggi - 12 Mar 2026 - 2/4 sesi".
 *
 * Fungsi bantu ini sengaja `private` dan bukan @Composable karena tugasnya
 * murni mengolah teks, tidak menggambar apa pun.
 */
private fun buildMetaLabel(task: Task): String {
    val parts = mutableListOf(task.priority.label)

    task.dueAt?.let { millis ->
        // SimpleDateFormat dipakai (bukan java.time) karena minSdk proyek ini
        // 24, sedangkan java.time baru tersedia mulai API 26.
        val formatter = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))
        parts += formatter.format(Date(millis))
    }

    if (task.estimatedPomodoros > 0) {
        // Ditulis sebagai pecahan "2/4 sesi", bukan sekadar "4 sesi", supaya
        // pengguna langsung melihat berapa banyak fokus yang SUDAH ia curahkan
        // untuk tugas ini - itulah inti pertanyaan yang dijawab WaktuKu.
        parts += "${task.completedPomodoros}/${task.estimatedPomodoros} sesi"
    }

    return parts.joinToString(" · ")
}

// ---------------------------------------------------------------------------
// PREVIEW: menggambar komponen langsung di Android Studio tanpa menjalankan
// aplikasi di emulator. Datanya dibuat manual di sini, bukan dari Room.
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Sedang dikerjakan")
@Composable
private fun TaskCardPreview() {
    WaktuKuTheme {
        TaskCard(
            task = Task(
                id = 1,
                title = "Kerjakan laporan Pemrograman Mobile",
                notes = "Bab arsitektur MVVM dan pembagian tugas kelompok",
                dueAt = 1772236800000L,
                priority = TaskPriority.HIGH,
                estimatedPomodoros = 4,
                completedPomodoros = 2,
            ),
            onToggleDone = {},
            onDelete = {},
            onClick = {},
            onStartFocus = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "Sudah selesai")
@Composable
private fun TaskCardDonePreview() {
    WaktuKuTheme {
        TaskCard(
            // Tugas selesai: judul tercoret dan tombol fokus tidak muncul.
            task = Task(
                id = 2,
                title = "Baca dokumentasi Room",
                isDone = true,
                priority = TaskPriority.LOW,
                estimatedPomodoros = 3,
                completedPomodoros = 3,
            ),
            onToggleDone = {},
            onDelete = {},
            onClick = {},
            onStartFocus = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
