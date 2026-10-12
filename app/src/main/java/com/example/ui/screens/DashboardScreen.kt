package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.MutqinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MutqinViewModel,
    onNavigateToSantri: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToKelompok: () -> Unit,
    onNavigateToEvaluasi: () -> Unit,
    onNavigateToBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val allSantri by viewModel.allSantri.collectAsStateWithLifecycle()
    val allSetoran by viewModel.allSetoran.collectAsStateWithLifecycle()
    val allIqro by viewModel.allIqro.collectAsStateWithLifecycle()
    val allMurajaah by viewModel.allMurajaah.collectAsStateWithLifecycle()

    val santriMap = remember(allSantri) { allSantri.associateBy { it.id } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Pesantren Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EmeraldGreenPrimary)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "MUTQIN ULUL ALBAB",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pondok Pesantren Ulul Albab Lubuklinggau",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GoldContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Backup Action Quick Button in Top Bar
                        FilledTonalButton(
                            onClick = onNavigateToBackup,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = IslamicGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("quick_backup_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Backup Data", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Platform Monitoring & Evaluasi Santri Al-Qur'an (TPA & Tahfidz Khusus)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Monthly Backup Notice Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintSoft),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreenPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EmeraldGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cadangan Data Mandiri Pesantren",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = EmeraldGreenDark
                        )
                        Text(
                            text = "Unduh salinan berkas ZIP/Excel/CSV secara berkala setiap bulan untuk arsip aman.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    TextButton(
                        onClick = onNavigateToBackup,
                        modifier = Modifier.testTag("notice_backup_button")
                    ) {
                        Text("Buka", fontWeight = FontWeight.Bold, color = EmeraldGreenPrimary)
                    }
                }
            }
        }

        // Summary Statistics Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Ringkasan Pembelajaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        title = "Santri Aktif",
                        value = "${summary.totalSantriAktif}",
                        subtitle = "${summary.totalSantriAlumni} Lulus • ${summary.totalSantriKeluar} Keluar",
                        icon = Icons.Default.People,
                        iconTint = EmeraldGreenPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSantri
                    )
                    StatCard(
                        title = "Program Belajar",
                        value = "${summary.totalProgramTahfidz}",
                        subtitle = "Tahfidz Khusus • ${summary.totalProgramTpa} TPA",
                        icon = Icons.Default.MenuBook,
                        iconTint = IslamicGold,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSantri
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        title = "Halaqah & Ustadz",
                        value = "${summary.totalKelompok}",
                        subtitle = "${summary.totalPembimbing} Ustadz/Ustadzah",
                        icon = Icons.Default.Groups,
                        iconTint = SageGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToKelompok
                    )
                    StatCard(
                        title = "Target Capaian",
                        value = "${summary.targetTercapaiPersen}%",
                        subtitle = "Rata-rata Semester Ini",
                        icon = Icons.Default.TrendingUp,
                        iconTint = SuccessGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToEvaluasi
                    )
                }
            }
        }

        // Quick Navigation Tiles
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Akses Cepat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Create,
                        label = "Catat Setoran",
                        containerColor = EmeraldContainer,
                        contentColor = EmeraldGreenDark,
                        onClick = onNavigateToInput,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = Icons.Default.PersonAdd,
                        label = "Santri Baru",
                        containerColor = GoldContainer,
                        contentColor = Color(0xFF6B4700),
                        onClick = onNavigateToSantri,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = Icons.Default.Assessment,
                        label = "Evaluasi",
                        containerColor = Color(0xFFE8F0FE),
                        contentColor = Color(0xFF1967D2),
                        onClick = onNavigateToEvaluasi,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = Icons.Default.Download,
                        label = "Backup",
                        containerColor = Color(0xFFF1F3F4),
                        contentColor = TextPrimary,
                        onClick = onNavigateToBackup,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recent Setoran Hafalan (Ziyadah)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Setoran Hafalan Terbaru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onNavigateToInput) {
                    Text("Lihat Semua", color = EmeraldGreenPrimary, fontSize = 12.sp)
                }
            }
        }

        if (allSetoran.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada catatan setoran hafalan.", color = TextSecondary)
                    }
                }
            }
        } else {
            items(allSetoran.take(4)) { setoran ->
                val santri = santriMap[setoran.santriId]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("recent_setoran_${setoran.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "J${setoran.juz}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EmeraldGreenDark
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = santri?.namaLengkap ?: "Santri #${setoran.santriId}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Surah ${setoran.surah} : ${setoran.ayatAwal}-${setoran.ayatAkhir} (${setoran.totalAyat} ayat)",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            StatusBadge(status = setoran.predikat)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = setoran.tanggal,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Recent Iqro' updates
        if (allIqro.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Perkembangan Iqro' Terkini",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            items(allIqro.take(2)) { iqro ->
                val santri = santriMap[iqro.santriId]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = santri?.namaLengkap ?: "Santri #${iqro.santriId}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Iqro' Jilid ${iqro.jilid} Halaman ${iqro.halamanAwal}-${iqro.halamanAkhir}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            if (iqro.catatan.isNotBlank()) {
                                Text(
                                    text = iqro.catatan,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        StatusBadge(status = iqro.kelancaran)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier.testTag("quick_action_${label.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        }
    }
}
