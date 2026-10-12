package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MutqinRepository(private val database: AppDatabase) {

    // DAOs
    private val santriDao = database.santriDao()
    private val akademikDao = database.akademikDao()
    private val perkembanganDao = database.perkembanganDao()
    private val evaluasiDao = database.evaluasiDao()
    private val backupDao = database.backupDao()

    // --- Santri ---
    val allSantri: Flow<List<SantriEntity>> = santriDao.getAllSantri()
    val santriAktif: Flow<List<SantriEntity>> = santriDao.getSantriAktif()
    fun getSantriById(id: Long): Flow<SantriEntity?> = santriDao.getSantriById(id)
    fun getSantriByKelompok(kelompokId: Long): Flow<List<SantriEntity>> = santriDao.getSantriByKelompok(kelompokId)

    suspend fun insertSantri(santri: SantriEntity): Long = santriDao.insertSantri(santri)
    suspend fun updateSantri(santri: SantriEntity) = santriDao.updateSantri(santri)
    suspend fun arsipkanSantri(santriId: Long, status: String, alasan: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        santriDao.arsipkanSantri(santriId, status, today, alasan)
    }

    suspend fun pindahKelompokSantri(santriId: Long, kelompokLamaId: Long?, kelompokBaruId: Long, alasan: String, dicatatOleh: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        // 1. Catat riwayat log
        val riwayat = RiwayatPerpindahanKelompokEntity(
            santriId = santriId,
            kelompokLamaId = kelompokLamaId,
            kelompokBaruId = kelompokBaruId,
            tanggalPindah = today,
            alasanPindah = alasan,
            dicatatOleh = dicatatOleh
        )
        santriDao.insertRiwayatPindah(riwayat)
        // 2. Update santri
        santriDao.updateKelompokSantri(santriId, kelompokBaruId)
    }

    val allRiwayatPindah: Flow<List<RiwayatPerpindahanKelompokEntity>> = santriDao.getAllRiwayatPindah()
    fun getRiwayatPindahBySantri(santriId: Long): Flow<List<RiwayatPerpindahanKelompokEntity>> =
        santriDao.getRiwayatPindahBySantri(santriId)

    // --- Akademik & Kelompok ---
    val allPembimbing: Flow<List<PembimbingEntity>> = akademikDao.getAllPembimbing()
    suspend fun insertPembimbing(pembimbing: PembimbingEntity): Long = akademikDao.insertPembimbing(pembimbing)
    suspend fun updatePembimbing(pembimbing: PembimbingEntity) = akademikDao.updatePembimbing(pembimbing)

    val allKelompok: Flow<List<KelompokEntity>> = akademikDao.getAllKelompok()
    fun getKelompokById(id: Long): Flow<KelompokEntity?> = akademikDao.getKelompokById(id)
    suspend fun insertKelompok(kelompok: KelompokEntity): Long = akademikDao.insertKelompok(kelompok)
    suspend fun updateKelompok(kelompok: KelompokEntity) = akademikDao.updateKelompok(kelompok)

    suspend fun gantiPembimbingKelompok(kelompokId: Long, pembimbingLamaId: Long?, pembimbingBaruId: Long, alasan: String, dicatatOleh: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val riwayat = RiwayatPergantianPembimbingEntity(
            kelompokId = kelompokId,
            pembimbingLamaId = pembimbingLamaId,
            pembimbingBaruId = pembimbingBaruId,
            tanggalGanti = today,
            alasan = alasan,
            dicatatOleh = dicatatOleh
        )
        akademikDao.insertRiwayatGantiPembimbing(riwayat)
        akademikDao.updatePembimbingKelompok(kelompokId, pembimbingBaruId)
    }

    val allRiwayatGantiPembimbing: Flow<List<RiwayatPergantianPembimbingEntity>> = akademikDao.getAllRiwayatGantiPembimbing()
    val allTahunAjaran: Flow<List<TahunAjaranEntity>> = akademikDao.getAllTahunAjaran()

    // --- Perkembangan Harian & Setoran ---
    val allSetoran: Flow<List<SetoranHafalanEntity>> = perkembanganDao.getAllSetoran()
    fun getSetoranBySantri(santriId: Long): Flow<List<SetoranHafalanEntity>> = perkembanganDao.getSetoranBySantri(santriId)
    suspend fun insertSetoran(setoran: SetoranHafalanEntity): Long = perkembanganDao.insertSetoran(setoran)
    suspend fun deleteSetoran(setoran: SetoranHafalanEntity) = perkembanganDao.deleteSetoran(setoran)

    val allMurajaah: Flow<List<MurajaahEntity>> = perkembanganDao.getAllMurajaah()
    fun getMurajaahBySantri(santriId: Long): Flow<List<MurajaahEntity>> = perkembanganDao.getMurajaahBySantri(santriId)
    suspend fun insertMurajaah(murajaah: MurajaahEntity): Long = perkembanganDao.insertMurajaah(murajaah)

    val allIqro: Flow<List<PerkembanganIqroEntity>> = perkembanganDao.getAllIqro()
    fun getIqroBySantri(santriId: Long): Flow<List<PerkembanganIqroEntity>> = perkembanganDao.getIqroBySantri(santriId)
    suspend fun insertIqro(iqro: PerkembanganIqroEntity): Long = perkembanganDao.insertIqro(iqro)

    val allPerbaikanBacaan: Flow<List<PerbaikanBacaanEntity>> = perkembanganDao.getAllPerbaikanBacaan()
    fun getPerbaikanBacaanBySantri(santriId: Long): Flow<List<PerbaikanBacaanEntity>> = perkembanganDao.getPerbaikanBacaanBySantri(santriId)
    suspend fun insertPerbaikanBacaan(bacaan: PerbaikanBacaanEntity): Long = perkembanganDao.insertPerbaikanBacaan(bacaan)

    val allCatatanHarian: Flow<List<CatatanPerkembanganHarianEntity>> = perkembanganDao.getAllCatatanHarian()
    fun getCatatanHarianBySantri(santriId: Long): Flow<List<CatatanPerkembanganHarianEntity>> = perkembanganDao.getCatatanHarianBySantri(santriId)
    suspend fun insertCatatanHarian(catatan: CatatanPerkembanganHarianEntity): Long = perkembanganDao.insertCatatanHarian(catatan)

    // --- Evaluasi & Target ---
    val allPenilaian: Flow<List<PenilaianSantriEntity>> = evaluasiDao.getAllPenilaian()
    fun getPenilaianBySantri(santriId: Long): Flow<List<PenilaianSantriEntity>> = evaluasiDao.getPenilaianBySantri(santriId)
    suspend fun insertPenilaian(penilaian: PenilaianSantriEntity): Long = evaluasiDao.insertPenilaian(penilaian)

    val allTarget: Flow<List<TargetPembelajaranEntity>> = evaluasiDao.getAllTarget()
    fun getTargetBySantri(santriId: Long): Flow<List<TargetPembelajaranEntity>> = evaluasiDao.getTargetBySantri(santriId)
    suspend fun insertTarget(target: TargetPembelajaranEntity): Long = evaluasiDao.insertTarget(target)
    suspend fun updateTarget(target: TargetPembelajaranEntity) = evaluasiDao.updateTarget(target)

    val allEvaluasiBulanan: Flow<List<EvaluasiBulananEntity>> = evaluasiDao.getAllEvaluasiBulanan()
    fun getEvaluasiBulananBySantri(santriId: Long): Flow<List<EvaluasiBulananEntity>> = evaluasiDao.getEvaluasiBulananBySantri(santriId)
    suspend fun insertEvaluasiBulanan(evaluasi: EvaluasiBulananEntity): Long = evaluasiDao.insertEvaluasiBulanan(evaluasi)

    val allKehadiran: Flow<List<KehadiranKegiatanEntity>> = evaluasiDao.getAllKehadiran()
    fun getKehadiranBySantri(santriId: Long): Flow<List<KehadiranKegiatanEntity>> = evaluasiDao.getKehadiranBySantri(santriId)
    suspend fun insertKehadiran(kehadiran: KehadiranKegiatanEntity): Long = evaluasiDao.insertKehadiran(kehadiran)

    val allEvaluasiProgram: Flow<List<CatatanEvaluasiProgramEntity>> = evaluasiDao.getAllEvaluasiProgram()
    suspend fun insertEvaluasiProgram(evaluasi: CatatanEvaluasiProgramEntity): Long = evaluasiDao.insertEvaluasiProgram(evaluasi)

    // --- Backup & Pengaturan ---
    val allPengaturan: Flow<List<PengaturanSistemEntity>> = backupDao.getAllPengaturan()
    val allRiwayatBackup: Flow<List<RiwayatBackupEntity>> = backupDao.getAllRiwayatBackup()
    suspend fun getAdminPin(): String {
        return backupDao.getPengaturan("PIN_ADMINISTRATOR")?.nilai ?: "1928"
    }
    suspend fun updateAdminPin(newPin: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        backupDao.savePengaturan(
            PengaturanSistemEntity(
                kunci = "PIN_ADMINISTRATOR",
                nilai = newPin,
                deskripsi = "PIN akses menu Backup dan Pemulihan Data",
                tanggalPerubahan = today
            )
        )
    }
    suspend fun deleteRiwayatBackup(id: Long) = backupDao.deleteRiwayatBackupById(id)
}
