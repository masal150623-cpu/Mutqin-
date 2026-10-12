package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.backup.BackupFilterOptions
import com.example.data.backup.BackupFormat
import com.example.data.backup.BackupPeriodType
import com.example.data.backup.BackupResult
import com.example.data.backup.BackupService
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.MutqinRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class BackupUiState {
    object Idle : BackupUiState()
    data class Loading(val message: String = "Sedang memproses cadangan data...") : BackupUiState()
    data class Success(val result: BackupResult) : BackupUiState()
    data class Error(val error: String) : BackupUiState()
    data class RestoreSuccess(val totalRecords: Int) : BackupUiState()
}

class MutqinViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = MutqinRepository(database)
    val backupService = BackupService(application, database)

    // Flow states
    val allSantri = repository.allSantri.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPembimbing = repository.allPembimbing.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allKelompok = repository.allKelompok.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTahunAjaran = repository.allTahunAjaran.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSetoran = repository.allSetoran.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMurajaah = repository.allMurajaah.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allIqro = repository.allIqro.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPerbaikanBacaan = repository.allPerbaikanBacaan.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allCatatanHarian = repository.allCatatanHarian.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPenilaian = repository.allPenilaian.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTarget = repository.allTarget.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allEvaluasiBulanan = repository.allEvaluasiBulanan.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allKehadiran = repository.allKehadiran.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allEvaluasiProgram = repository.allEvaluasiProgram.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRiwayatPindah = repository.allRiwayatPindah.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allRiwayatGantiPembimbing = repository.allRiwayatGantiPembimbing.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allRiwayatBackup = repository.allRiwayatBackup.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter states for Santri screen
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("Semua")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    private val _selectedProgramFilter = MutableStateFlow("Semua")
    val selectedProgramFilter: StateFlow<String> = _selectedProgramFilter.asStateFlow()

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setStatusFilter(status: String) { _selectedStatusFilter.value = status }
    fun setProgramFilter(program: String) { _selectedProgramFilter.value = program }

    val filteredSantri = combine(
        allSantri,
        searchQuery,
        selectedStatusFilter,
        selectedProgramFilter
    ) { list, query, status, program ->
        list.filter { santri ->
            val matchQuery = query.isBlank() ||
                    santri.namaLengkap.contains(query, ignoreCase = true) ||
                    santri.nis.contains(query, ignoreCase = true) ||
                    santri.namaPanggilan.contains(query, ignoreCase = true)

            val matchStatus = status == "Semua" || santri.status.equals(status, ignoreCase = true)
            val matchProgram = program == "Semua" || santri.program.equals(program, ignoreCase = true)

            matchQuery && matchStatus && matchProgram
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard calculations
    val dashboardSummary = combine(
        allSantri,
        allKelompok,
        allPembimbing,
        allSetoran,
        allTarget
    ) { santri, kelompok, pembimbing, setoran, target ->
        val aktif = santri.count { it.status == "Aktif" }
        val alumni = santri.count { it.status == "Lulus" }
        val keluar = santri.count { it.status in listOf("Keluar", "Pindah") }
        val tahfidz = santri.count { it.program == "Tahfidz Khusus" && it.status == "Aktif" }
        val tpa = santri.count { it.program == "TPA" && it.status == "Aktif" }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val setoranHariIni = setoran.count { it.tanggal == today }

        val avgTarget = if (target.isNotEmpty()) {
            target.map { it.capaianPersen }.average().toInt()
        } else 0

        DashboardSummary(
            totalSantriAktif = aktif,
            totalSantriAlumni = alumni,
            totalSantriKeluar = keluar,
            totalProgramTahfidz = tahfidz,
            totalProgramTpa = tpa,
            totalKelompok = kelompok.size,
            totalPembimbing = pembimbing.size,
            setoranHariIni = setoranHariIni,
            targetTercapaiPersen = avgTarget
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    // Selected Santri detail
    private val _selectedSantriId = MutableStateFlow<Long?>(null)
    val selectedSantriId: StateFlow<Long?> = _selectedSantriId.asStateFlow()

    fun selectSantri(santriId: Long?) {
        _selectedSantriId.value = santriId
    }

    val selectedSantri = _selectedSantriId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getSantriById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Backup UI State & Options
    private val _backupUiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val backupUiState: StateFlow<BackupUiState> = _backupUiState.asStateFlow()

    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    fun checkAdminPin(inputPin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val correctPin = repository.getAdminPin()
            val valid = inputPin == correctPin
            _isAdminAuthenticated.value = valid
            onResult(valid)
        }
    }

    fun lockAdminAccess() {
        _isAdminAuthenticated.value = false
    }

    fun resetBackupState() {
        _backupUiState.value = BackupUiState.Idle
    }

    fun executeBackup(options: BackupFilterOptions) {
        viewModelScope.launch {
            _backupUiState.value = BackupUiState.Loading("Menyiapkan berkas cadangan ${options.format.name}...")
            val result = backupService.createBackup(options)
            if (result.success) {
                _backupUiState.value = BackupUiState.Success(result)
            } else {
                _backupUiState.value = BackupUiState.Error(result.errorMessage ?: "Gagal membuat backup.")
            }
        }
    }

    fun restoreBackup(jsonString: String) {
        viewModelScope.launch {
            _backupUiState.value = BackupUiState.Loading("Memulihkan data ke sistem...")
            val result = backupService.restoreFromBackupJson(jsonString)
            if (result.isSuccess) {
                _backupUiState.value = BackupUiState.RestoreSuccess(result.getOrDefault(0))
            } else {
                _backupUiState.value = BackupUiState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Gagal memulihkan data dari berkas backup."
                )
            }
        }
    }

    fun deleteBackupHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteRiwayatBackup(id)
        }
    }

    // Santri Operations
    fun saveSantri(
        santri: SantriEntity,
        isNew: Boolean,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            if (isNew) {
                repository.insertSantri(santri)
            } else {
                repository.updateSantri(santri)
            }
            onComplete()
        }
    }

    fun arsipkanSantri(santriId: Long, status: String, alasan: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.arsipkanSantri(santriId, status, alasan)
            onComplete()
        }
    }

    fun pindahKelompok(
        santriId: Long,
        kelompokLamaId: Long?,
        kelompokBaruId: Long,
        alasan: String,
        dicatatOleh: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.pindahKelompokSantri(santriId, kelompokLamaId, kelompokBaruId, alasan, dicatatOleh)
            onComplete()
        }
    }

    // Kelompok Operations
    fun saveKelompok(kelompok: KelompokEntity, isNew: Boolean, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (isNew) {
                repository.insertKelompok(kelompok)
            } else {
                repository.updateKelompok(kelompok)
            }
            onComplete()
        }
    }

    fun gantiPembimbing(
        kelompokId: Long,
        pembimbingLamaId: Long?,
        pembimbingBaruId: Long,
        alasan: String,
        dicatatOleh: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.gantiPembimbingKelompok(kelompokId, pembimbingLamaId, pembimbingBaruId, alasan, dicatatOleh)
            onComplete()
        }
    }

    // Setoran & Perkembangan Operations
    fun saveSetoran(setoran: SetoranHafalanEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertSetoran(setoran)
            onComplete()
        }
    }

    fun saveMurajaah(murajaah: MurajaahEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertMurajaah(murajaah)
            onComplete()
        }
    }

    fun saveIqro(iqro: PerkembanganIqroEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertIqro(iqro)
            onComplete()
        }
    }

    fun savePerbaikanBacaan(bacaan: PerbaikanBacaanEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertPerbaikanBacaan(bacaan)
            onComplete()
        }
    }

    fun saveKehadiran(kehadiran: KehadiranKegiatanEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertKehadiran(kehadiran)
            onComplete()
        }
    }

    fun saveTarget(target: TargetPembelajaranEntity, isNew: Boolean, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (isNew) {
                repository.insertTarget(target)
            } else {
                repository.updateTarget(target)
            }
            onComplete()
        }
    }

    fun savePenilaian(penilaian: PenilaianSantriEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertPenilaian(penilaian)
            onComplete()
        }
    }

    fun saveEvaluasiBulanan(evaluasi: EvaluasiBulananEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertEvaluasiBulanan(evaluasi)
            onComplete()
        }
    }

    fun saveEvaluasiProgram(evaluasi: CatatanEvaluasiProgramEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertEvaluasiProgram(evaluasi)
            onComplete()
        }
    }
}
