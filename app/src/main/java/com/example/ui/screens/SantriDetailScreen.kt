package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.MutqinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SantriDetailScreen(
    santriId: Long,
    viewModel: MutqinViewModel,
    onBack: () -> Unit,
    onNavigateToInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val allSantri by viewModel.allSantri.collectAsStateWithLifecycle()
    val allKelompok by viewModel.allKelompok.collectAsStateWithLifecycle()
    val allPembimbing by viewModel.allPembimbing.collectAsStateWithLifecycle()

    val santri = remember(allSantri, santriId) { allSantri.find { it.id == santriId } }
    val halaqah = remember(allKelompok, santri) { allKelompok.find { it.id == santri?.kelompokId } }
    val pembimbing = remember(allPembimbing, halaqah) { allPembimbing.find { it.id == halaqah?.pembimbingId } }

    val allSetoran by viewModel.allSetoran.collectAsStateWithLifecycle()
    val allMurajaah by viewModel.allMurajaah.collectAsStateWithLifecycle()
    val allIqro by viewModel.allIqro.collectAsStateWithLifecycle()
    val allTahsin by viewModel.allPerbaikanBacaan.collectAsStateWithLifecycle()
    val allKehadiran by viewModel.allKehadiran.collectAsStateWithLifecycle()
    val allPenilaian by viewModel.allPenilaian.collectAsStateWithLifecycle()
    val allRiwayatPindah by viewModel.allRiwayatPindah.collectAsStateWithLifecycle()

    val setoranSantri = remember(allSetoran, santriId) { allSetoran.filter { it.santriId == santriId } }
    val murajaahSantri = remember(allMurajaah, santriId) { allMurajaah.filter { it.santriId == santriId } }
    val iqroSantri = remember(allIqro, santriId) { allIqro.filter { it.santriId == santriId } }
    val tahsinSantri = remember(allTahsin, santriId) { allTahsin.filter { it.santriId == santriId } }
    val kehadiranSantri = remember(allKehadiran, santriId) { allKehadiran.filter { it.santriId == santriId } }
    val penilaianSantri = remember(allPenilaian, santriId) { allPenilaian.filter { it.santriId == santriId } }
    val riwayatPindahSantri = remember(allRiwayatPindah, santriId) { allRiwayatPindah.filter { it.santriId == santriId } }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ziyadah (${setoranSantri.size})", "Murajaah (${murajaahSantri.size})", "Iqro & Tahsin", "Presensi", "Tashih", "Mutasi")

    Scaffold(
        modifier = modifier.testTag("santri_detail_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Detail Santri", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = onNavigateToInput,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = EmeraldContainer,
                            contentColor = EmeraldGreenDark
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Catat Setoran", fontSize = 12.sp)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (santri == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Santri tidak ditemukan.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Profile Card Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(if (santri.jenisKelamin == "Laki-laki") EmeraldContainer else GoldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = santri.namaLengkap.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = if (santri.jenisKelamin == "Laki-laki") EmeraldGreenDark else Color(0xFF6B4700)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = santri.namaLengkap,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "NIS: ${santri.nis} • ${santri.program}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        StatusBadge(status = santri.status)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Halaqah:", fontSize = 11.sp, color = TextSecondary)
                            Text(halaqah?.namaKelompok ?: "-", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text("Pembimbing:", fontSize = 11.sp, color = TextSecondary)
                            Text(pembimbing?.nama ?: "-", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text("Target:", fontSize = 11.sp, color = TextSecondary)
                            Text("${santri.targetJuzHafalan} Juz", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (santri.kontakWali.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Wali: ${santri.namaWali} (${santri.kontakWali})",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Scrollable TabRow
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> { // Ziyadah
                        if (setoranSantri.isEmpty()) {
                            item { EmptyStateView("Belum ada riwayat setoran hafalan.") }
                        } else {
                            items(setoranSantri) { item ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Juz ${item.juz} • Surah ${item.surah}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = EmeraldGreenDark
                                            )
                                            StatusBadge(status = item.predikat)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Ayat ${item.ayatAwal} s.d. ${item.ayatAkhir} (${item.totalAyat} ayat) • Tgl: ${item.tanggal}",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "Nilai Kelancaran: ${item.nilaiKelancaran} | Tajwid: ${item.nilaiTajwid}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (item.catatan.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Catatan: ${item.catatan}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> { // Murajaah
                        if (murajaahSantri.isEmpty()) {
                            item { EmptyStateView("Belum ada riwayat murajaah.") }
                        } else {
                            items(murajaahSantri) { item ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Juz ${item.juz} • ${item.surahAwal} s.d. ${item.surahAkhir}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            StatusBadge(status = item.kemutqinan)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Total ${item.totalHalaman} halaman • Kelancaran: ${item.kelancaran} • Tgl: ${item.tanggal}",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        if (item.catatan.isNotBlank()) {
                                            Text("Catatan: ${item.catatan}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Iqro & Tahsin
                        item {
                            Text("Riwayat Iqro':", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        if (iqroSantri.isEmpty()) {
                            item { Text("Belum ada catatan Iqro'.", fontSize = 12.sp, color = TextSecondary) }
                        } else {
                            items(iqroSantri) { iqro ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Iqro Jilid ${iqro.jilid} Hal ${iqro.halamanAwal}-${iqro.halamanAkhir}", fontWeight = FontWeight.SemiBold)
                                            Text("Tgl: ${iqro.tanggal} • Status: ${iqro.statusKenaikan}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                        StatusBadge(status = iqro.kelancaran)
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Riwayat Perbaikan Bacaan (Tahsin):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        if (tahsinSantri.isEmpty()) {
                            item { Text("Belum ada catatan tahsin khusus.", fontSize = 12.sp, color = TextSecondary) }
                        } else {
                            items(tahsinSantri) { t ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Surah ${t.surah} : ${t.ayatAwal}-${t.ayatAkhir}", fontWeight = FontWeight.SemiBold)
                                            StatusBadge(status = t.statusPenguasaan)
                                        }
                                        Text("Fokus: ${t.fokusTajwid}", fontSize = 12.sp, color = EmeraldGreenDark)
                                        if (t.catatanKekurangan.isNotBlank()) {
                                            Text("Catatan: ${t.catatanKekurangan}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> { // Kehadiran
                        if (kehadiranSantri.isEmpty()) {
                            item { EmptyStateView("Belum ada data kehadiran.") }
                        } else {
                            items(kehadiranSantri) { k ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("${k.jenisKegiatan} • ${k.tanggal}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            if (k.keterangan.isNotBlank()) {
                                                Text(k.keterangan, fontSize = 11.sp, color = TextSecondary)
                                            }
                                        }
                                        StatusBadge(status = k.statusKehadiran)
                                    }
                                }
                            }
                        }
                    }
                    4 -> { // Tashih & Penilaian
                        if (penilaianSantri.isEmpty()) {
                            item { EmptyStateView("Belum ada rekaman ujian Tashih Juz.") }
                        } else {
                            items(penilaianSantri) { p ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(p.materiUjian, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EmeraldGreenDark)
                                            StatusBadge(status = p.predikatAkhir)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Nilai Rata-rata: ${p.totalNilaiRataRata} • Tgl: ${p.tanggal}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Penguji: ${p.namaPenguji}", fontSize = 12.sp, color = TextSecondary)
                                        Text("Rekomendasi: ${p.rekomendasi}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                    5 -> { // Mutasi / Pindah Kelompok
                        if (riwayatPindahSantri.isEmpty()) {
                            item { EmptyStateView("Santri belum pernah berpindah halaqah.") }
                        } else {
                            items(riwayatPindahSantri) { r ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Perpindahan Halaqah", fontWeight = FontWeight.Bold, color = SageGreen)
                                        Text("Tanggal: ${r.tanggalPindah}", fontSize = 12.sp, color = TextSecondary)
                                        Text("Alasan: ${r.alasanPindah}", fontSize = 12.sp)
                                        Text("Dicatat Oleh: ${r.dicatatOleh}", fontSize = 11.sp, color = TextSecondary)
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

@Composable
fun EmptyStateView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = TextSecondary, fontSize = 13.sp)
    }
}
