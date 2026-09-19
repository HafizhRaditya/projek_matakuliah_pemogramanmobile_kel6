package com.kelompok.waktuku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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

                // Baris keterangan: lencana prioritas, lalu tenggat bila ada.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    PriorityBadge(task.priority)

                    val dueText = buildDueDateLabel(task)
                    if (dueText.isNotEmpty()) {
                        Text(
                            text = dueText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Progres sesi Pomodoro, hanya untuk tugas yang punya target.
                if (task.estimatedPomodoros > 0) {
                    // coerceIn(0f, 1f) menjaga bar tidak kebablasan saat sesi
                    // yang selesai melebihi target (misalnya 5 dari 4 sesi).
                    val progress = (task.completedPomodoros.toFloat() / task.estimatedPomodoros)
                        .coerceIn(0f, 1f)

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LinearProgressIndicator(
                            // Lambda, bukan nilai langsung - bentuk yang dipakai
                            // Material 3 versi baru. Ujung bar sudah membulat
                            // dengan sendirinya, jadi tidak perlu clip().
                            progress = { progress },
                            modifier = Modifier
                                .width(80.dp)
                                .height(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            // trackColor sengaja tidak diisi. Warna jalur bawaan
                            // M3 lebih redup; kalau diisi primaryContainer, di
                            // mode gelap jalur kosong tampak seperti bar penuh.
                        )
                        // Tetap ditulis sebagai pecahan "2/4 sesi" di samping bar,
                        // supaya pengguna tahu angka pastinya: berapa banyak fokus
                        // yang SUDAH ia curahkan untuk tugas ini - itulah inti
                        // pertanyaan yang dijawab WaktuKu.
                        Text(
                            text = "${task.completedPomodoros}/${task.estimatedPomodoros} sesi",
                            // Ukuran huruf mengikuti skala tipografi tema, tidak
                            // ditulis sendiri (slide 22: jangan fontSize hardcoded).
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
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
 * Menyusun teks tenggat, contoh: "12 Mar 2026". Kosong bila tugas tanpa tenggat.
 *
 * Fungsi bantu ini sengaja `private` dan bukan @Composable karena tugasnya
 * murni mengolah teks, tidak menggambar apa pun.
 */
private fun buildDueDateLabel(task: Task): String {
    val millis = task.dueAt ?: return ""
    // SimpleDateFormat dipakai (bukan java.time) karena minSdk proyek ini
    // 24, sedangkan java.time baru tersedia mulai API 26.
    val formatter = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))
    return formatter.format(Date(millis))
}

/**
 * Lencana kecil berisi label prioritas.
 *
 * Warnanya diambil dari PASANGAN peran tema: latar ...Container dengan teks
 * on...Container. Material 3 merancang pasangan ini agar kontrasnya selalu
 * cukup, baik di mode terang maupun gelap.
 *
 * Teks "Tinggi / Sedang / Rendah" tetap ditulis walau sudah ada warna, karena
 * informasi yang hanya dibedakan lewat warna tidak terbaca oleh pengguna buta
 * warna.
 */
@Composable
private fun PriorityBadge(priority: TaskPriority) {
    val (backgroundColor, textColor) = when (priority) {
        // Merah (error) khusus prioritas tinggi: warna yang paling menarik
        // perhatian dipakai untuk hal yang paling mendesak.
        TaskPriority.HIGH -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        TaskPriority.LOW -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Box(
        modifier = Modifier
            // Sudut diambil dari skala bentuk tema (extraSmall = 4dp), bukan
            // RoundedCornerShape yang ditulis sendiri.
            .background(color = backgroundColor, shape = MaterialTheme.shapes.extraSmall)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = priority.label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
        )
    }
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
