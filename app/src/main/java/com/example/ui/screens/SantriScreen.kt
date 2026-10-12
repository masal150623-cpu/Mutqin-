package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.KelompokEntity
import com.example.data.model.SantriEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MutqinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SantriScreen(
    viewModel: MutqinViewModel,
    onSelectSantri: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredSantri by viewModel.filteredSantri.collectAsStateWithLifecycle()
    val allKelompok by viewModel.allKelompok.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val programFilter by viewModel.selectedProgramFilter.collectAsStateWithLifecycle()

    val kelompokMap = remember(allKelompok) { allKelompok.associateBy { it.id } }

    var isAddDialogOpen by remember { mutableStateOf(false) }
    var santriToEdit by remember { mutableStateOf<SantriEntity?>(null) }
    var santriToMove by remember { mutableStateOf<SantriEntity?>(null) }
    var santriToArchive by remember { mutableStateOf<SantriEntity?>(null) }

    Scaffold(
        modifier = modifier.testTag("santri_screen"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { isAddDialogOpen = true },
                containerColor = EmeraldGreenPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Santri Baru", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("add_santri_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Cari nama, NIS, atau panggilan...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("santri_search_field")
            )

            // Status Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val statuses = listOf("Semua", "Aktif", "Lulus", "Keluar", "Pindah")
                items(statuses) { status ->
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = { viewModel.setStatusFilter(status) },
                        label = { Text(status) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Program Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val programs = listOf("Semua", "Tahfidz Khusus", "TPA")
                items(programs) { program ->
                    FilterChip(
                        selected = programFilter == program,
                        onClick = { viewModel.setProgramFilter(program) },
                        label = { Text(program) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Text(
                text = "Daftar Santri (${filteredSantri.size} santri)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            if (filteredSantri.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = OutlineLight,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak ditemukan data santri yang cocok.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSantri, key = { it.id }) { santri ->
                        val halaqah = kelompokMap[santri.kelompokId]

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectSantri(santri.id) }
                                .testTag("santri_card_${santri.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (santri.jenisKelamin == "Laki-laki") EmeraldContainer else GoldContainer
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = santri.namaLengkap.take(1).uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = if (santri.jenisKelamin == "Laki-laki") EmeraldGreenDark else Color(0xFF6B4700)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = santri.namaLengkap,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "NIS: ${santri.nis} • TP ${santri.tahunAjaranMasuk}",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    StatusBadge(status = santri.status)
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Groups,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = EmeraldGreenPrimary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = halaqah?.namaKelompok ?: "Belum ada kelompok",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = EmeraldGreenDark
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Program: ${santri.program} | Target: ${santri.targetJuzHafalan} Juz",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    // Quick actions popup
                                    Row {
                                        IconButton(
                                            onClick = { santriToMove = santri },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SwapHoriz,
                                                contentDescription = "Pindah Kelompok",
                                                tint = SageGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { santriToArchive = santri },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Archive,
                                                contentDescription = "Arsipkan",
                                                tint = WarningOrange,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { santriToEdit = santri },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = EmeraldGreenPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
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

    // Add / Edit Dialog
    if (isAddDialogOpen || santriToEdit != null) {
        SantriFormDialog(
            santri = santriToEdit,
            kelompokList = allKelompok,
            onDismiss = {
                isAddDialogOpen = false
                santriToEdit = null
            },
            onSave = { entity, isNew ->
                viewModel.saveSantri(entity, isNew) {
                    isAddDialogOpen = false
                    santriToEdit = null
                }
            }
        )
    }

    // Pindah Kelompok Dialog (with auto logging to RiwayatPerpindahanKelompok)
    santriToMove?.let { santri ->
        PindahKelompokDialog(
            santri = santri,
            kelompokList = allKelompok,
            onDismiss = { santriToMove = null },
            onConfirm = { kelompokBaruId, alasan, dicatatOleh ->
                viewModel.pindahKelompok(
                    santriId = santri.id,
                    kelompokLamaId = santri.kelompokId,
                    kelompokBaruId = kelompokBaruId,
                    alasan = alasan,
                    dicatatOleh = dicatatOleh
                ) {
                    santriToMove = null
                }
            }
        )
    }

    // Arsipkan Santri Dialog (Lulus / Keluar / Pindah)
    santriToArchive?.let { santri ->
        ArsipSantriDialog(
            santri = santri,
            onDismiss = { santriToArchive = null },
            onConfirm = { status, alasan ->
                viewModel.arsipkanSantri(santri.id, status, alasan) {
                    santriToArchive = null
                }
            }
        )
    }
}

@Composable
fun SantriFormDialog(
    santri: SantriEntity?,
    kelompokList: List<KelompokEntity>,
    onDismiss: () -> Unit,
    onSave: (SantriEntity, Boolean) -> Unit
) {
    val isNew = santri == null
    var nis by remember { mutableStateOf(santri?.nis ?: "UA${System.currentTimeMillis() % 100000}") }
    var namaLengkap by remember { mutableStateOf(santri?.namaLengkap ?: "") }
    var namaPanggilan by remember { mutableStateOf(santri?.namaPanggilan ?: "") }
    var jenisKelamin by remember { mutableStateOf(santri?.jenisKelamin ?: "Laki-laki") }
    var program by remember { mutableStateOf(santri?.program ?: "Tahfidz Khusus") }
    var kelompokId by remember { mutableStateOf(santri?.kelompokId ?: kelompokList.firstOrNull()?.id) }
    var tahunAjaranMasuk by remember { mutableStateOf(santri?.tahunAjaranMasuk ?: "2025/2026") }
    var namaWali by remember { mutableStateOf(santri?.namaWali ?: "") }
    var kontakWali by remember { mutableStateOf(santri?.kontakWali ?: "") }
    var alamat by remember { mutableStateOf(santri?.alamat ?: "Lubuklinggau") }
    var targetJuz by remember { mutableStateOf((santri?.targetJuzHafalan ?: 30).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Tambah Santri Baru" else "Ubah Data Santri", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = nis,
                        onValueChange = { nis = it },
                        label = { Text("Nomor Induk Santri (NIS)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("form_nis")
                    )
                }
                item {
                    OutlinedTextField(
                        value = namaLengkap,
                        onValueChange = { namaLengkap = it },
                        label = { Text("Nama Lengkap Santri *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("form_nama_lengkap")
                    )
                }
                item {
                    OutlinedTextField(
                        value = namaPanggilan,
                        onValueChange = { namaPanggilan = it },
                        label = { Text("Nama Panggilan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Jenis Kelamin:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = jenisKelamin == "Laki-laki",
                            onClick = { jenisKelamin = "Laki-laki" },
                            label = { Text("Laki-laki (Ikhwan)") }
                        )
                        FilterChip(
                            selected = jenisKelamin == "Perempuan",
                            onClick = { jenisKelamin = "Perempuan" },
                            label = { Text("Perempuan (Akhwat)") }
                        )
                    }
                }
                item {
                    Text("Program Pembelajaran:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = program == "Tahfidz Khusus",
                            onClick = { program = "Tahfidz Khusus" },
                            label = { Text("Tahfidz Khusus") }
                        )
                        FilterChip(
                            selected = program == "TPA",
                            onClick = { program = "TPA" },
                            label = { Text("TPA") }
                        )
                    }
                }
                item {
                    Text("Halaqah / Kelompok Belajar:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        kelompokList.forEach { k ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { kelompokId = k.id }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = kelompokId == k.id,
                                    onClick = { kelompokId = k.id }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(k.namaKelompok, fontSize = 13.sp)
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = tahunAjaranMasuk,
                        onValueChange = { tahunAjaranMasuk = it },
                        label = { Text("Tahun Ajaran Masuk") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = namaWali,
                        onValueChange = { namaWali = it },
                        label = { Text("Nama Orang Tua / Wali") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = kontakWali,
                        onValueChange = { kontakWali = it },
                        label = { Text("Nomor Kontak / WhatsApp") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = alamat,
                        onValueChange = { alamat = it },
                        label = { Text("Alamat") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = targetJuz,
                        onValueChange = { targetJuz = it },
                        label = { Text("Target Juz Hafalan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (namaLengkap.isNotBlank()) {
                        val entity = SantriEntity(
                            id = santri?.id ?: 0,
                            nis = nis,
                            namaLengkap = namaLengkap,
                            namaPanggilan = namaPanggilan,
                            jenisKelamin = jenisKelamin,
                            program = program,
                            kelompokId = kelompokId,
                            tahunAjaranMasuk = tahunAjaranMasuk,
                            status = santri?.status ?: "Aktif",
                            tanggalMasuk = santri?.tanggalMasuk ?: "2025-07-15",
                            namaWali = namaWali,
                            kontakWali = kontakWali,
                            alamat = alamat,
                            targetJuzHafalan = targetJuz.toIntOrNull() ?: 30
                        )
                        onSave(entity, isNew)
                    }
                },
                modifier = Modifier.testTag("save_santri_button")
            ) {
                Text(if (isNew) "Simpan Santri" else "Perbarui")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun PindahKelompokDialog(
    santri: SantriEntity,
    kelompokList: List<KelompokEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String) -> Unit
) {
    var selectedKelompokId by remember { mutableStateOf(kelompokList.firstOrNull { it.id != santri.kelompokId }?.id ?: 0L) }
    var alasan by remember { mutableStateOf("Kenaikan tingkat / akselerasi hafalan") }
    var dicatatOleh by remember { mutableStateOf("Kepala Program") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pindah Kelompok / Halaqah", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Santri: ${santri.namaLengkap}",
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldGreenDark
                )
                Text("Pilih Halaqah Tujuan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                kelompokList.forEach { k ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedKelompokId = k.id }
                    ) {
                        RadioButton(
                            selected = selectedKelompokId == k.id,
                            onClick = { selectedKelompokId = k.id }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(k.namaKelompok, fontSize = 13.sp)
                    }
                }
                OutlinedTextField(
                    value = alasan,
                    onValueChange = { alasan = it },
                    label = { Text("Alasan Perpindahan") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dicatatOleh,
                    onValueChange = { dicatatOleh = it },
                    label = { Text("Dicatat Oleh") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedKelompokId, alasan, dicatatOleh) },
                modifier = Modifier.testTag("confirm_pindah_kelompok_button")
            ) {
                Text("Proses Pindah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun ArsipSantriDialog(
    santri: SantriEntity,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("Lulus") }
    var alasan by remember { mutableStateOf("Menyelesaikan target hafalan dan tashih bersanad") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pengarsipan Santri", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Pengarsipan santri ${santri.namaLengkap} tidak akan menghapus riwayat hafalan dan nilai yang telah dicatat.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text("Pilih Status Baru:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedStatus == "Lulus",
                        onClick = {
                            selectedStatus = "Lulus"
                            alasan = "Lulus Tashih Al-Qur'an / Khatam"
                        },
                        label = { Text("Lulus (Alumni)") }
                    )
                    FilterChip(
                        selected = selectedStatus == "Pindah",
                        onClick = {
                            selectedStatus = "Pindah"
                            alasan = "Pindah domisili orang tua"
                        },
                        label = { Text("Pindah") }
                    )
                    FilterChip(
                        selected = selectedStatus == "Keluar",
                        onClick = {
                            selectedStatus = "Keluar"
                            alasan = "Mengundurkan diri"
                        },
                        label = { Text("Keluar") }
                    )
                }
                OutlinedTextField(
                    value = alasan,
                    onValueChange = { alasan = it },
                    label = { Text("Alasan Pengarsipan") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedStatus, alasan) },
                colors = ButtonDefaults.buttonColors(containerColor = WarningOrange)
            ) {
                Text("Simpan Arsip")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
