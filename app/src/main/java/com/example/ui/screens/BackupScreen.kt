package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.backup.BackupFilterOptions
import com.example.data.backup.BackupFormat
import com.example.data.backup.BackupPeriodType
import com.example.data.model.RiwayatBackupEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.BackupUiState
import com.example.ui.viewmodel.MutqinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    viewModel: MutqinViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsStateWithLifecycle()
    val backupState by viewModel.backupUiState.collectAsStateWithLifecycle()
    val riwayatBackupList by viewModel.allRiwayatBackup.collectAsStateWithLifecycle()
    val allKelompok by viewModel.allKelompok.collectAsStateWithLifecycle()

    var isPinDialogOpen by remember { mutableStateOf(!isAuthenticated) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonInput by remember { mutableStateOf("") }

    // Backup Configuration State
    var selectedFormat by remember { mutableStateOf(BackupFormat.ZIP_COMPLETE) }
    var selectedPeriodType by remember { mutableStateOf(BackupPeriodType.SEMUA_DATA) }
    var selectedBulanTahun by remember { mutableStateOf("2026-10") }
    var selectedTahunAjaran by remember { mutableStateOf("2025/2026") }
    var selectedKelompokId by remember { mutableStateOf(allKelompok.firstOrNull()?.id) }
    var selectedKategori by remember { mutableStateOf("Semua Kategori") }
    var passwordProtection by remember { mutableStateOf("") }
    var usePasswordProtection by remember { mutableStateOf(false) }

    // If not authenticated, require Admin PIN
    if (!isAuthenticated) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("admin_lock_screen"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF7A5400),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Akses Khusus Administrator",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Menu Backup dan Pemulihan Data hanya dapat diakses oleh Kepala Program atau Administrator Pondok Pesantren Ulul Albab.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { isPinDialogOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enter_admin_pin_button")
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Masukkan PIN Administrator")
                    }
                }
            }
        }

        PinEntryDialog(
            isOpen = isPinDialogOpen,
            onDismiss = { isPinDialogOpen = false },
            onSubmit = { enteredPin ->
                viewModel.checkAdminPin(enteredPin) { valid ->
                    if (valid) {
                        isPinDialogOpen = false
                    }
                }
            }
        )
        return
    }

    // Authenticated Screen
    Scaffold(
        modifier = modifier.testTag("backup_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Backup dan Pemulihan Data", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Arsip Mandiri Ponpes Ulul Albab", fontSize = 11.sp, color = TextSecondary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.lockAdminAccess() },
                        modifier = Modifier.testTag("lock_admin_button")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Kunci Akses")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Privacy & Security Warning Banner (Prompt Requirement D)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF7E0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Peringatan Keamanan & Privasi Data Santri",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF8A5300)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Berkas cadangan berisi data pribadi santri, kontak wali santri, dan rekaman evaluasi. Simpan berkas di lokasi yang aman dan jangan disebarkan kepada pihak yang tidak berwenang.",
                                fontSize = 11.sp,
                                color = Color(0xFF5F3B00),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 2. Active Backup Status / Progress State
            item {
                when (backupState) {
                    is BackupUiState.Loading -> {
                        val msg = (backupState as BackupUiState.Loading).message
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MintSoft),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = EmeraldGreenPrimary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(msg, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = EmeraldGreenDark)
                                Text("Mohon tunggu hingga proses selesai...", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                    is BackupUiState.Success -> {
                        val res = (backupState as BackupUiState.Success).result
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreenPrimary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Berkas Cadangan Berhasil Dibuat!", fontWeight = FontWeight.Bold, color = EmeraldGreenDark, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("📄 Nama: ${res.fileName}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("📦 Ukuran: ${res.fileSizeFormatted} • Total: ${res.totalRecords} record (${res.totalEntities} tabel)", fontSize = 12.sp)

                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            res.fileUri?.let { uri ->
                                                val intent = viewModel.backupService.createShareIntent(
                                                    fileUri = uri,
                                                    mimeType = if (res.fileName.endsWith(".zip")) "application/zip" else if (res.fileName.endsWith(".xls")) "application/vnd.ms-excel" else "text/csv",
                                                    subject = "Cadangan Data Mutqin Ulul Albab - ${res.fileName}"
                                                )
                                                context.startActivity(Intent.createChooser(intent, "Simpan / Bagikan Berkas Cadangan"))
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                                        modifier = Modifier.weight(1f).testTag("share_download_backup_button")
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Unduh & Simpan Berkas")
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.resetBackupState() },
                                        modifier = Modifier.weight(0.6f)
                                    ) {
                                        Text("Tutup")
                                    }
                                }
                            }
                        }
                    }
                    is BackupUiState.RestoreSuccess -> {
                        val total = (backupState as BackupUiState.RestoreSuccess).totalRecords
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("✅ Pemulihan Data Berhasil!", fontWeight = FontWeight.Bold, color = EmeraldGreenDark)
                                Text("Sebanyak $total data berhasil dipulihkan ke dalam aplikasi.", fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { viewModel.resetBackupState() }) {
                                    Text("Selesai")
                                }
                            }
                        }
                    }
                    is BackupUiState.Error -> {
                        val err = (backupState as BackupUiState.Error).error
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDE8E8)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("❌ Gagal Membuat Cadangan Data", fontWeight = FontWeight.Bold, color = ErrorRed)
                                Text(err, fontSize = 12.sp, color = Color(0xFF5F2120))
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { viewModel.resetBackupState() }) {
                                    Text("Coba Lagi")
                                }
                            }
                        }
                    }
                    BackupUiState.Idle -> { /* Nothing */ }
                }
            }

            // 3. SEKSI UTAMA: "BACKUP SELURUH DATA" (Prompt Requirement 1.A)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = EmeraldGreenDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Backup Seluruh Data",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreenDark
                                )
                                Text(
                                    text = "Unduh seluruh 18 tabel data aplikasi dalam satu proses",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Pilih Format Unduhan:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Format 1: ZIP Lengkap
                        FormatOptionCard(
                            title = "Format 1: ZIP Backup Lengkap",
                            description = "Satu file ZIP berisi seluruh file CSV dan JSON untuk santri, pembimbing, halaqah, hafalan, murajaah, iqro', tashih, dokumentasi README struktur data, dan manifest.",
                            isSelected = selectedFormat == BackupFormat.ZIP_COMPLETE,
                            badgeText = "Paling Direkomendasikan",
                            badgeColor = EmeraldGreenPrimary,
                            onClick = { selectedFormat = BackupFormat.ZIP_COMPLETE },
                            modifier = Modifier.testTag("select_format_zip")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Format 2: Excel Multi-Sheet
                        FormatOptionCard(
                            title = "Format 2: Excel (Multi-Sheet)",
                            description = "Berkas Excel dengan 12 lembar kerja terpisah (Santri, Pembimbing, Kelompok, Iqro, Hafalan, Murajaah, Evaluasi, Target, Kehadiran, Perpindahan, Riwayat Pembimbing).",
                            isSelected = selectedFormat == BackupFormat.EXCEL_SHEETS,
                            badgeText = "Mudah Diolah",
                            badgeColor = IslamicGold,
                            onClick = { selectedFormat = BackupFormat.EXCEL_SHEETS },
                            modifier = Modifier.testTag("select_format_excel")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Format 3: CSV
                        FormatOptionCard(
                            title = "Format 3: CSV",
                            description = "Data dalam format CSV murni yang ringan dan kompatibel dibuka pada spreadsheet apa pun.",
                            isSelected = selectedFormat == BackupFormat.CSV_DATA,
                            badgeText = "Ringan & Universal",
                            badgeColor = SageGreen,
                            onClick = { selectedFormat = BackupFormat.CSV_DATA },
                            modifier = Modifier.testTag("select_format_csv")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Opsi Kata Sandi Backup (Prompt Requirement D)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = usePasswordProtection,
                                onCheckedChange = { usePasswordProtection = it }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Proteksi Berkas Cadangan dengan Kata Sandi", fontSize = 12.sp)
                        }

                        if (usePasswordProtection) {
                            OutlinedTextField(
                                value = passwordProtection,
                                onValueChange = { passwordProtection = it },
                                label = { Text("Kata Sandi / PIN Proteksi") },
                                placeholder = { Text("Masukkan kata sandi pengaman arsip") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tombol Utama "Backup Seluruh Data"
                        Button(
                            onClick = {
                                val options = BackupFilterOptions(
                                    format = selectedFormat,
                                    periodType = BackupPeriodType.SEMUA_DATA,
                                    passwordProtection = if (usePasswordProtection) passwordProtection else ""
                                )
                                viewModel.executeBackup(options)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("backup_seluruh_data_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Backup Seluruh Data Sekarang",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // 4. SEKSI KEDUA: "BACKUP BERDASARKAN PERIODE & FILTER" (Prompt Requirement B)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Backup Berdasarkan Periode & Filter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Digunakan sebagai arsip tambahan bulanan atau per kelompok halaqah",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Pilih Kriteria Periode:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedPeriodType == BackupPeriodType.BULAN_TAHUN,
                                    onClick = { selectedPeriodType = BackupPeriodType.BULAN_TAHUN }
                                )
                                Text("Berdasarkan Bulan & Tahun (misal: 2026-10)", fontSize = 13.sp)
                            }
                            if (selectedPeriodType == BackupPeriodType.BULAN_TAHUN) {
                                OutlinedTextField(
                                    value = selectedBulanTahun,
                                    onValueChange = { selectedBulanTahun = it },
                                    label = { Text("Format: YYYY-MM") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 8.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedPeriodType == BackupPeriodType.TAHUN_AJARAN,
                                    onClick = { selectedPeriodType = BackupPeriodType.TAHUN_AJARAN }
                                )
                                Text("Berdasarkan Tahun Ajaran (misal: 2025/2026)", fontSize = 13.sp)
                            }
                            if (selectedPeriodType == BackupPeriodType.TAHUN_AJARAN) {
                                OutlinedTextField(
                                    value = selectedTahunAjaran,
                                    onValueChange = { selectedTahunAjaran = it },
                                    label = { Text("Tahun Ajaran") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 8.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedPeriodType == BackupPeriodType.KELOMPOK,
                                    onClick = { selectedPeriodType = BackupPeriodType.KELOMPOK }
                                )
                                Text("Berdasarkan Kelompok / Halaqah Tertentu", fontSize = 13.sp)
                            }
                            if (selectedPeriodType == BackupPeriodType.KELOMPOK) {
                                Column(modifier = Modifier.padding(start = 32.dp)) {
                                    allKelompok.forEach { k ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            RadioButton(
                                                selected = selectedKelompokId == k.id,
                                                onClick = { selectedKelompokId = k.id }
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(k.namaKelompok, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedPeriodType == BackupPeriodType.KATEGORI_TERTENTU,
                                    onClick = { selectedPeriodType = BackupPeriodType.KATEGORI_TERTENTU }
                                )
                                Text("Berdasarkan Kategori Data", fontSize = 13.sp)
                            }
                            if (selectedPeriodType == BackupPeriodType.KATEGORI_TERTENTU) {
                                Row(
                                    modifier = Modifier.padding(start = 32.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Santri & Halaqah", "Hafalan & Murajaah", "Iqro & Tahsin", "Evaluasi & Penilaian").forEach { kat ->
                                        FilterChip(
                                            selected = selectedKategori == kat,
                                            onClick = { selectedKategori = kat },
                                            label = { Text(kat, fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        FilledTonalButton(
                            onClick = {
                                val kelompokObj = allKelompok.find { it.id == selectedKelompokId }
                                val options = BackupFilterOptions(
                                    format = selectedFormat,
                                    periodType = selectedPeriodType,
                                    bulanTahun = selectedBulanTahun,
                                    tahunAjaran = selectedTahunAjaran,
                                    kelompokId = selectedKelompokId,
                                    kelompokNama = kelompokObj?.namaKelompok ?: "",
                                    kategori = selectedKategori,
                                    passwordProtection = if (usePasswordProtection) passwordProtection else ""
                                )
                                viewModel.executeBackup(options)
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = IslamicGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("backup_periode_button")
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cadangkan Data Berdasarkan Periode Ini", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 5. SEKSI RESTORE / PEMULIHAN DATA
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SettingsBackupRestore, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pemulihan Data (Restore)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pulihkan seluruh data aplikasi santri dari berkas arsip database_master_restore.json jika terjadi pergantian perangkat.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { showRestoreDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("open_restore_dialog_button")
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buka Pemulihan Data dari Berkas Master JSON")
                        }
                    }
                }
            }

            // 6. SEKSI RIWAYAT BACKUP (Prompt Requirement C)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Pembuatan Backup (${riwayatBackupList.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (riwayatBackupList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada riwayat backup yang dibuat.", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(riwayatBackupList, key = { it.id }) { item ->
                    BackupHistoryCard(
                        riwayat = item,
                        onShare = {
                            val uri = Uri.parse(item.fileUri)
                            val intent = viewModel.backupService.createShareIntent(
                                fileUri = uri,
                                mimeType = if (item.namaFile.endsWith(".zip")) "application/zip" else if (item.namaFile.endsWith(".xls")) "application/vnd.ms-excel" else "text/csv",
                                subject = item.namaFile
                            )
                            context.startActivity(Intent.createChooser(intent, "Bagikan Berkas ${item.namaFile}"))
                        },
                        onDelete = { viewModel.deleteBackupHistory(item.id) }
                    )
                }
            }
        }
    }

    // Restore Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Pemulihan Data Master", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Masukkan atau tempel (paste) isi berkas database_master_restore.json hasil cadangan sebelumnya:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = restoreJsonInput,
                        onValueChange = { restoreJsonInput = it },
                        placeholder = { Text("{\"manifest\": {...}, \"santri\": [...]}") },
                        modifier = Modifier.fillMaxWidth().height(160.dp).testTag("restore_json_input")
                    )
                    Text(
                        text = "⚠️ Perhatian: Proses ini akan memasukkan dan memperbarui data sesuai berkas cadangan.",
                        fontSize = 11.sp,
                        color = ErrorRed
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreJsonInput.isNotBlank()) {
                            showRestoreDialog = false
                            viewModel.restoreBackup(restoreJsonInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                    modifier = Modifier.testTag("submit_restore_button")
                ) {
                    Text("Jalankan Pemulihan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun FormatOptionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    badgeText: String,
    badgeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmeraldContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldGreenPrimary)) else null,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = onClick)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp,
                modifier = Modifier.padding(start = 36.dp, end = 4.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun BackupHistoryCard(
    riwayat: RiwayatBackupEntity,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("backup_history_card_${riwayat.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = riwayat.namaFile,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldGreenDark
                    )
                    Text(
                        text = "${riwayat.tanggalWaktu} • ${riwayat.periodeData}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                StatusBadge(status = riwayat.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Format: ${riwayat.jenisBackup}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Data: ${riwayat.totalRecord} baris (${riwayat.totalEntitas} entitas)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Row {
                    IconButton(onClick = onShare, modifier = Modifier.size(36.dp).testTag("share_history_btn_${riwayat.id}")) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan Berkas",
                            tint = EmeraldGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp).testTag("delete_history_btn_${riwayat.id}")) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus Riwayat",
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
