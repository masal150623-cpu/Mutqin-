package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MutqinViewModel

enum class MainDestination(val label: String, val icon: ImageVector) {
    DASHBOARD("Beranda", Icons.Default.Dashboard),
    SANTRI("Santri", Icons.Default.People),
    INPUT("Input", Icons.Default.PostAdd),
    HALAQAH("Halaqah", Icons.Default.Groups),
    EVALUASI("Evaluasi", Icons.Default.Assessment),
    BACKUP("Backup", Icons.Default.CloudDownload)
}

@Composable
fun MainScreen(
    viewModel: MutqinViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(MainDestination.DASHBOARD) }
    var viewingSantriId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        modifier = modifier.testTag("main_screen"),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                MainDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination && viewingSantriId == null
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            viewingSantriId = null
                            currentDestination = destination
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldGreenPrimary,
                            selectedTextColor = EmeraldGreenPrimary,
                            indicatorColor = EmeraldContainer
                        ),
                        modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (viewingSantriId != null) {
                SantriDetailScreen(
                    santriId = viewingSantriId!!,
                    viewModel = viewModel,
                    onBack = { viewingSantriId = null },
                    onNavigateToInput = {
                        viewingSantriId = null
                        currentDestination = MainDestination.INPUT
                    }
                )
            } else {
                when (currentDestination) {
                    MainDestination.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToSantri = { currentDestination = MainDestination.SANTRI },
                        onNavigateToInput = { currentDestination = MainDestination.INPUT },
                        onNavigateToKelompok = { currentDestination = MainDestination.HALAQAH },
                        onNavigateToEvaluasi = { currentDestination = MainDestination.EVALUASI },
                        onNavigateToBackup = { currentDestination = MainDestination.BACKUP }
                    )
                    MainDestination.SANTRI -> SantriScreen(
                        viewModel = viewModel,
                        onSelectSantri = { santriId -> viewingSantriId = santriId }
                    )
                    MainDestination.INPUT -> InputPerkembanganScreen(
                        viewModel = viewModel
                    )
                    MainDestination.HALAQAH -> KelompokScreen(
                        viewModel = viewModel
                    )
                    MainDestination.EVALUASI -> EvaluasiScreen(
                        viewModel = viewModel
                    )
                    MainDestination.BACKUP -> BackupScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
