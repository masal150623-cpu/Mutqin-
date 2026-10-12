package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.MutqinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluasiScreen(
    viewModel: MutqinViewModel,
    modifier: Modifier = Modifier
) {
    val allSantri by viewModel.allSantri.collectAsStateWithLifecycle()
    val allTarget by viewModel.allTarget.collectAsStateWithLifecycle()
    val allPenilaian by viewModel.allPenilaian.collectAsStateWithLifecycle()
    val allEvaluasiBulanan by viewModel.allEvaluasiBulanan.collectAsStateWithLifecycle()
    val allEvaluasiProgram by viewModel.allEvaluasiProgram.collectAsStateWithLifecycle()

    val santriMap = remember(allSantri) { allSantri.associateBy { it.id } }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Target Belajar", "Tashih Juz", "Evaluasi Bulanan", "Program Pesantren")

    Scaffold(
        modifier = modifier.testTag("evaluasi_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Evaluasi & Target Pembelajaran",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (selectedTab) {
                    0 -> { // Target Pembelajaran
                        if (allTarget.isEmpty()) {
                            item { EmptyStateView("Belum ada target pembelajaran yang tercatat.") }
                        } else {
                            items(allTarget) { target ->
                                val santri = santriMap[target.santriId]
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = santri?.namaLengkap ?: "Santri #${target.santriId}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = EmeraldGreenDark
                                            )
                                            StatusBadge(status = target.status)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${target.targetJenis}: ${target.deskripsiTarget}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Target: ${target.targetKuantitas} • Batas: ${target.batasWaktu}",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Capaian:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            Text("${target.capaianPersen}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldGreenPrimary)
                                        }
                                        LinearProgressIndicator(
                                            progress = { target.capaianPersen / 100f },
                                            modifier = Modifier.fillMaxWidth().height(8.dp),
                                            color = EmeraldGreenPrimary,
                                            trackColor = EmeraldContainer,
                                        )
                                        if (target.catatan.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("Catatan: ${target.catatan}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> { // Tashih Juz & Ujian
                        if (allPenilaian.isEmpty()) {
                            item { EmptyStateView("Belum ada data ujian tashih juz.") }
                        } else {
                            items(allPenilaian) { p ->
                                val santri = santriMap[p.santriId]
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(santri?.namaLengkap ?: "-", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                Text(p.materiUjian, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EmeraldGreenDark)
                                            }
                                            StatusBadge(status = p.predikatAkhir)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Kelancaran: ${p.nilaiKelancaran} • Tajwid: ${p.nilaiTajwid} • Fashahah: ${p.nilaiFashahah} • Adab: ${p.nilaiAdab}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Rata-rata: ${p.totalNilaiRataRata} • Tgl: ${p.tanggal}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IslamicGold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Penguji: ${p.namaPenguji}", fontSize = 12.sp, color = TextSecondary)
                                        Text("Rekomendasi: ${p.rekomendasi}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Evaluasi Bulanan
                        if (allEvaluasiBulanan.isEmpty()) {
                            item { EmptyStateView("Belum ada evaluasi bulanan.") }
                        } else {
                            items(allEvaluasiBulanan) { e ->
                                val santri = santriMap[e.santriId]
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(santri?.namaLengkap ?: "-", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                Text("Bulan: ${e.bulanTahun}", fontSize = 12.sp, color = TextSecondary)
                                            }
                                            StatusBadge(status = e.predikatBulanan)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Total Ziyadah: ${e.totalSetoranAyat} ayat • Murajaah: ${e.totalJuzMurajaah} juz • Kehadiran: ${e.persentaseKehadiran}%",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Catatan: ${e.catatanEvaluasi}", fontSize = 12.sp)
                                        Text("Rekomendasi: ${e.rekomendasiPembimbing}", fontSize = 12.sp, color = EmeraldGreenDark)
                                        Text("Oleh: ${e.evaluasiOleh}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                    3 -> { // Catatan Evaluasi Program Pesantren
                        if (allEvaluasiProgram.isEmpty()) {
                            item { EmptyStateView("Belum ada catatan evaluasi program.") }
                        } else {
                            items(allEvaluasiProgram) { prog ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(prog.judul, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EmeraldGreenDark)
                                        Text("TP ${prog.tahunAjaran} Semester ${prog.semester} • Tgl: ${prog.tanggal}", fontSize = 12.sp, color = TextSecondary)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Ringkasan Capaian:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        Text(prog.ringkasanCapaian, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Kendala & Tantangan:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = ErrorRed)
                                        Text(prog.kendalaTantangan, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Rekomendasi Tindak Lanjut:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = SuccessGreen)
                                        Text(prog.rekomendasiTindakLanjut, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Evaluator: ${prog.evaluator}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
