package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.KelompokEntity
import com.example.data.model.PembimbingEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MutqinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelompokScreen(
    viewModel: MutqinViewModel,
    modifier: Modifier = Modifier
) {
    val allKelompok by viewModel.allKelompok.collectAsStateWithLifecycle()
    val allPembimbing by viewModel.allPembimbing.collectAsStateWithLifecycle()
    val allSantri by viewModel.allSantri.collectAsStateWithLifecycle()
    val allRiwayatGanti by viewModel.allRiwayatGantiPembimbing.collectAsStateWithLifecycle()

    val pembimbingMap = remember(allPembimbing) { allPembimbing.associateBy { it.id } }
    val kelompokMap = remember(allKelompok) { allKelompok.associateBy { it.id } }

    var selectedTab by remember { mutableIntStateOf(0) }
    var kelompokToChangePembimbing by remember { mutableStateOf<KelompokEntity?>(null) }

    Scaffold(
        modifier = modifier.testTag("kelompok_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Kelompok Pembelajaran (Halaqah)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Daftar Halaqah (${allKelompok.size})", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Riwayat Pembimbing (${allRiwayatGanti.size})", fontWeight = FontWeight.SemiBold) }
                )
            }

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allKelompok) { kelompok ->
                        val ustadz = pembimbingMap[kelompok.pembimbingId]
                        val totalSantri = allSantri.count { it.kelompokId == kelompok.id && it.status == "Aktif" }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth().testTag("kelompok_card_${kelompok.id}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = kelompok.namaKelompok,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = EmeraldGreenDark
                                        )
                                        Text(
                                            text = "Program: ${kelompok.program} • TP ${kelompok.tahunAjaran}",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Surface(
                                        color = EmeraldContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "$totalSantri Santri",
                                            color = EmeraldGreenDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = IslamicGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Pembimbing: ${ustadz?.nama ?: "Belum ditentukan"}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                        if (ustadz?.gelar?.isNotBlank() == true) {
                                            Text(
                                                text = "${ustadz.gelar} (${ustadz.peran})",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { kelompokToChangePembimbing = kelompok },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rotasi Pembimbing", fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "📍 Ruangan: ${kelompok.ruangan}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "⏰ Sesi: ${kelompok.jadwalSesi}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "🎯 Target: ${kelompok.targetHafalan}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SageGreen
                                )
                            }
                        }
                    }
                }
            } else {
                // Riwayat Pergantian Pembimbing Log
                if (allRiwayatGanti.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada riwayat pergantian pembimbing.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allRiwayatGanti) { r ->
                            val k = kelompokMap[r.kelompokId]
                            val pLama = pembimbingMap[r.pembimbingLamaId]
                            val pBaru = pembimbingMap[r.pembimbingBaruId]

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
                                        Text(k?.namaKelompok ?: "Kelompok #${r.kelompokId}", fontWeight = FontWeight.Bold)
                                        Text(r.tanggalGanti, fontSize = 12.sp, color = TextSecondary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Dari: ${pLama?.nama ?: "-"}", fontSize = 12.sp, color = TextSecondary)
                                    Text("Menjadi: ${pBaru?.nama ?: "-"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldGreenDark)
                                    Text("Alasan: ${r.alasan}", fontSize = 12.sp)
                                    Text("Dicatat Oleh: ${r.dicatatOleh}", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Rotasi / Ganti Pembimbing Dialog
    kelompokToChangePembimbing?.let { kelompok ->
        GantiPembimbingDialog(
            kelompok = kelompok,
            pembimbingList = allPembimbing,
            onDismiss = { kelompokToChangePembimbing = null },
            onConfirm = { pembimbingBaruId, alasan, dicatatOleh ->
                viewModel.gantiPembimbing(
                    kelompokId = kelompok.id,
                    pembimbingLamaId = kelompok.pembimbingId,
                    pembimbingBaruId = pembimbingBaruId,
                    alasan = alasan,
                    dicatatOleh = dicatatOleh
                ) {
                    kelompokToChangePembimbing = null
                }
            }
        )
    }
}

@Composable
fun GantiPembimbingDialog(
    kelompok: KelompokEntity,
    pembimbingList: List<PembimbingEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String) -> Unit
) {
    var selectedPembimbingId by remember {
        mutableStateOf(pembimbingList.firstOrNull { it.id != kelompok.pembimbingId }?.id ?: 0L)
    }
    var alasan by remember { mutableStateOf("Rotasi penugasan semesteran halaqah") }
    var dicatatOleh by remember { mutableStateOf("Kepala Program") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rotasi Pembimbing Halaqah", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Halaqah: ${kelompok.namaKelompok}", fontWeight = FontWeight.SemiBold, color = EmeraldGreenDark)
                Text("Pilih Ustadz / Ustadzah Baru:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                pembimbingList.forEach { p ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedPembimbingId == p.id,
                            onClick = { selectedPembimbingId = p.id }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${p.nama} (${p.peran})", fontSize = 13.sp)
                    }
                }
                OutlinedTextField(
                    value = alasan,
                    onValueChange = { alasan = it },
                    label = { Text("Alasan Pergantian") },
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
                onClick = { onConfirm(selectedPembimbingId, alasan, dicatatOleh) }
            ) {
                Text("Simpan Rotasi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
