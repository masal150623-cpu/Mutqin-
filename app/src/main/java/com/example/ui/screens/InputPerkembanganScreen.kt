package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MutqinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputPerkembanganScreen(
    viewModel: MutqinViewModel,
    modifier: Modifier = Modifier
) {
    val allSantri by viewModel.allSantri.collectAsStateWithLifecycle()
    val aktifSantri = remember(allSantri) { allSantri.filter { it.status == "Aktif" } }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Setoran Hafalan", "Murajaah", "Iqro'", "Tahsin", "Kehadiran")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var successMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(successMsg) {
        successMsg?.let {
            snackbarHostState.showSnackbar(it)
            successMsg = null
        }
    }

    Scaffold(
        modifier = modifier.testTag("input_perkembangan_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Catat Perkembangan Santri",
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

            if (aktifSantri.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data santri aktif. Tambahkan santri terlebih dahulu.")
                }
                return@Scaffold
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> item {
                        FormSetoranHafalan(
                            santriList = aktifSantri,
                            onSave = { setoran ->
                                viewModel.saveSetoran(setoran) {
                                    successMsg = "Alhamdulillah, setoran hafalan berhasil disimpan!"
                                }
                            }
                        )
                    }
                    1 -> item {
                        FormMurajaah(
                            santriList = aktifSantri,
                            onSave = { murajaah ->
                                viewModel.saveMurajaah(murajaah) {
                                    successMsg = "Alhamdulillah, data murajaah berhasil disimpan!"
                                }
                            }
                        )
                    }
                    2 -> item {
                        FormIqro(
                            santriList = aktifSantri,
                            onSave = { iqro ->
                                viewModel.saveIqro(iqro) {
                                    successMsg = "Catatan Iqro' berhasil disimpan!"
                                }
                            }
                        )
                    }
                    3 -> item {
                        FormTahsin(
                            santriList = aktifSantri,
                            onSave = { tahsin ->
                                viewModel.savePerbaikanBacaan(tahsin) {
                                    successMsg = "Catatan tahsin berhasil disimpan!"
                                }
                            }
                        )
                    }
                    4 -> item {
                        FormKehadiran(
                            santriList = aktifSantri,
                            onSave = { kehadiran ->
                                viewModel.saveKehadiran(kehadiran) {
                                    successMsg = "Presensi kehadiran berhasil dicatat!"
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FormSetoranHafalan(
    santriList: List<SantriEntity>,
    onSave: (SetoranHafalanEntity) -> Unit
) {
    var selectedSantriId by remember { mutableStateOf(santriList.first().id) }
    var tanggal by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var surah by remember { mutableStateOf("An-Naba") }
    var juz by remember { mutableStateOf("30") }
    var ayatAwal by remember { mutableStateOf("1") }
    var ayatAkhir by remember { mutableStateOf("20") }
    var predikat by remember { mutableStateOf("Mutqin (A)") }
    var nilaiKelancaran by remember { mutableStateOf("95") }
    var nilaiTajwid by remember { mutableStateOf("92") }
    var catatan by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Input Setoran Hafalan Baru (Ziyadah)", fontWeight = FontWeight.Bold, color = EmeraldGreenDark)

            SantriDropdownSelector(
                santriList = santriList,
                selectedId = selectedSantriId,
                onSelect = { selectedSantriId = it }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = { tanggal = it },
                    label = { Text("Tanggal") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = juz,
                    onValueChange = { juz = it },
                    label = { Text("Juz") },
                    modifier = Modifier.weight(0.7f)
                )
            }

            OutlinedTextField(
                value = surah,
                onValueChange = { surah = it },
                label = { Text("Nama Surah") },
                modifier = Modifier.fillMaxWidth().testTag("input_surah_name")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ayatAwal,
                    onValueChange = { ayatAwal = it },
                    label = { Text("Ayat Awal") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = ayatAkhir,
                    onValueChange = { ayatAkhir = it },
                    label = { Text("Ayat Akhir") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text("Predikat Kelancaran / Kemutqinan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            val predikats = listOf("Mutqin (A)", "Jayyid Jiddan (B+)", "Jayyid (B)", "Maqbul (C)")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                predikats.forEach { p ->
                    FilterChip(
                        selected = predikat == p,
                        onClick = { predikat = p },
                        label = { Text(p, fontSize = 11.sp) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nilaiKelancaran,
                    onValueChange = { nilaiKelancaran = it },
                    label = { Text("Kelancaran (1-100)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = nilaiTajwid,
                    onValueChange = { nilaiTajwid = it },
                    label = { Text("Tajwid (1-100)") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan Pembimbing") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val awal = ayatAwal.toIntOrNull() ?: 1
                    val akhir = ayatAkhir.toIntOrNull() ?: 1
                    val entity = SetoranHafalanEntity(
                        santriId = selectedSantriId,
                        tanggal = tanggal,
                        surah = surah,
                        juz = juz.toIntOrNull() ?: 30,
                        ayatAwal = awal,
                        ayatAkhir = akhir,
                        totalAyat = (akhir - awal + 1).coerceAtLeast(1),
                        predikat = predikat,
                        nilaiKelancaran = nilaiKelancaran.toIntOrNull() ?: 90,
                        nilaiTajwid = nilaiTajwid.toIntOrNull() ?: 90,
                        catatan = catatan
                    )
                    onSave(entity)
                },
                modifier = Modifier.fillMaxWidth().testTag("save_setoran_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Setoran Hafalan")
            }
        }
    }
}

@Composable
fun FormMurajaah(
    santriList: List<SantriEntity>,
    onSave: (MurajaahEntity) -> Unit
) {
    var selectedSantriId by remember { mutableStateOf(santriList.first().id) }
    var tanggal by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var juz by remember { mutableStateOf("30") }
    var surahAwal by remember { mutableStateOf("An-Naba") }
    var surahAkhir by remember { mutableStateOf("An-Nas") }
    var halaman by remember { mutableStateOf("10.0") }
    var kemutqinan by remember { mutableStateOf("Mutqin") }
    var kelancaran by remember { mutableStateOf("Sangat Lancar") }
    var catatan by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Input Pengulangan Hafalan (Murajaah)", fontWeight = FontWeight.Bold, color = EmeraldGreenDark)

            SantriDropdownSelector(santriList, selectedSantriId) { selectedSantriId = it }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = { tanggal = it },
                    label = { Text("Tanggal") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = juz,
                    onValueChange = { juz = it },
                    label = { Text("Juz") },
                    modifier = Modifier.weight(0.7f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = surahAwal,
                    onValueChange = { surahAwal = it },
                    label = { Text("Surah Awal") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = surahAkhir,
                    onValueChange = { surahAkhir = it },
                    label = { Text("Surah Akhir") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = halaman,
                onValueChange = { halaman = it },
                label = { Text("Total Halaman Murajaah") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Status Kemutqinan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Mutqin", "Jayyid", "Perlu Pengulangan").forEach { s ->
                    FilterChip(
                        selected = kemutqinan == s,
                        onClick = { kemutqinan = s },
                        label = { Text(s) }
                    )
                }
            }

            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val entity = MurajaahEntity(
                        santriId = selectedSantriId,
                        tanggal = tanggal,
                        juz = juz.toIntOrNull() ?: 30,
                        surahAwal = surahAwal,
                        surahAkhir = surahAkhir,
                        totalHalaman = halaman.toDoubleOrNull() ?: 1.0,
                        kelancaran = kelancaran,
                        kemutqinan = kemutqinan,
                        catatan = catatan
                    )
                    onSave(entity)
                },
                modifier = Modifier.fillMaxWidth().testTag("save_murajaah_button"),
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Murajaah", color = Color.Black)
            }
        }
    }
}

@Composable
fun FormIqro(
    santriList: List<SantriEntity>,
    onSave: (PerkembanganIqroEntity) -> Unit
) {
    var selectedSantriId by remember { mutableStateOf(santriList.first().id) }
    var tanggal by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var jilid by remember { mutableIntStateOf(4) }
    var halAwal by remember { mutableStateOf("10") }
    var halAkhir by remember { mutableStateOf("12") }
    var kelancaran by remember { mutableStateOf("Lancar (A)") }
    var statusKenaikan by remember { mutableStateOf("Lanjut Halaman") }
    var catatan by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Input Perkembangan Iqro' (TPA / Pra-Tahfidz)", fontWeight = FontWeight.Bold, color = EmeraldGreenDark)

            SantriDropdownSelector(santriList, selectedSantriId) { selectedSantriId = it }

            Text("Pilih Jilid Iqro':", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (1..6).forEach { num ->
                    FilterChip(
                        selected = jilid == num,
                        onClick = { jilid = num },
                        label = { Text("Jilid $num") }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = halAwal,
                    onValueChange = { halAwal = it },
                    label = { Text("Hal Awal") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = halAkhir,
                    onValueChange = { halAkhir = it },
                    label = { Text("Hal Akhir") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text("Status Kenaikan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Lanjut Halaman", "Naik Jilid", "Mengulang").forEach { s ->
                    FilterChip(
                        selected = statusKenaikan == s,
                        onClick = { statusKenaikan = s },
                        label = { Text(s) }
                    )
                }
            }

            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan Makhraj / Huruf") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val entity = PerkembanganIqroEntity(
                        santriId = selectedSantriId,
                        tanggal = tanggal,
                        jilid = jilid,
                        halamanAwal = halAwal.toIntOrNull() ?: 1,
                        halamanAkhir = halAkhir.toIntOrNull() ?: 1,
                        kelancaran = kelancaran,
                        statusKenaikan = statusKenaikan,
                        catatan = catatan
                    )
                    onSave(entity)
                },
                modifier = Modifier.fillMaxWidth().testTag("save_iqro_button")
            ) {
                Text("Simpan Perkembangan Iqro'")
            }
        }
    }
}

@Composable
fun FormTahsin(
    santriList: List<SantriEntity>,
    onSave: (PerbaikanBacaanEntity) -> Unit
) {
    var selectedSantriId by remember { mutableStateOf(santriList.first().id) }
    var tanggal by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var surah by remember { mutableStateOf("Al-Fatihah") }
    var ayatAwal by remember { mutableStateOf("1") }
    var ayatAkhir by remember { mutableStateOf("7") }
    var fokusTajwid by remember { mutableStateOf("Makharijul Huruf") }
    var statusPenguasaan by remember { mutableStateOf("Sudah Baik") }
    var catatan by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Perbaikan Bacaan Al-Qur'an (Tahsin / Fashahah)", fontWeight = FontWeight.Bold, color = EmeraldGreenDark)

            SantriDropdownSelector(santriList, selectedSantriId) { selectedSantriId = it }

            OutlinedTextField(
                value = surah,
                onValueChange = { surah = it },
                label = { Text("Surah") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ayatAwal,
                    onValueChange = { ayatAwal = it },
                    label = { Text("Ayat Awal") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = ayatAkhir,
                    onValueChange = { ayatAkhir = it },
                    label = { Text("Ayat Akhir") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = fokusTajwid,
                onValueChange = { fokusTajwid = it },
                label = { Text("Fokus Perbaikan (Makharijul Huruf, Mad, Ghunnah, dll)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan Kekurangan & Latihan Mandiri") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val entity = PerbaikanBacaanEntity(
                        santriId = selectedSantriId,
                        tanggal = tanggal,
                        surah = surah,
                        ayatAwal = ayatAwal.toIntOrNull() ?: 1,
                        ayatAkhir = ayatAkhir.toIntOrNull() ?: 1,
                        fokusTajwid = fokusTajwid,
                        catatanKekurangan = catatan,
                        statusPenguasaan = statusPenguasaan
                    )
                    onSave(entity)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Data Tahsin")
            }
        }
    }
}

@Composable
fun FormKehadiran(
    santriList: List<SantriEntity>,
    onSave: (KehadiranKegiatanEntity) -> Unit
) {
    var selectedSantriId by remember { mutableStateOf(santriList.first().id) }
    var tanggal by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var kegiatan by remember { mutableStateOf("Halaqah Rutin") }
    var statusKehadiran by remember { mutableStateOf("Hadir") }
    var keterangan by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Presensi Kehadiran & Kegiatan", fontWeight = FontWeight.Bold, color = EmeraldGreenDark)

            SantriDropdownSelector(santriList, selectedSantriId) { selectedSantriId = it }

            OutlinedTextField(
                value = tanggal,
                onValueChange = { tanggal = it },
                label = { Text("Tanggal") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Status Kehadiran:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Hadir", "Sakit", "Izin", "Alpa").forEach { s ->
                    FilterChip(
                        selected = statusKehadiran == s,
                        onClick = { statusKehadiran = s },
                        label = { Text(s) }
                    )
                }
            }

            OutlinedTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = { Text("Keterangan Tambahan") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val entity = KehadiranKegiatanEntity(
                        santriId = selectedSantriId,
                        tanggal = tanggal,
                        jenisKegiatan = kegiatan,
                        statusKehadiran = statusKehadiran,
                        keterangan = keterangan
                    )
                    onSave(entity)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Presensi")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SantriDropdownSelector(
    santriList: List<SantriEntity>,
    selectedId: Long,
    onSelect: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentSantri = santriList.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = currentSantri?.namaLengkap ?: "Pilih Santri",
            onValueChange = {},
            readOnly = true,
            label = { Text("Pilih Santri") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            santriList.forEach { s ->
                DropdownMenuItem(
                    text = { Text("${s.namaLengkap} (${s.nis})") },
                    onClick = {
                        onSelect(s.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
