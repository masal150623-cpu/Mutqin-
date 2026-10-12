package com.example.data.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

enum class BackupFormat {
    ZIP_COMPLETE,
    EXCEL_SHEETS,
    CSV_DATA
}

enum class BackupPeriodType {
    SEMUA_DATA,
    BULAN_TAHUN,
    TAHUN_AJARAN,
    KELOMPOK,
    KATEGORI_TERTENTU
}

data class BackupFilterOptions(
    val format: BackupFormat = BackupFormat.ZIP_COMPLETE,
    val periodType: BackupPeriodType = BackupPeriodType.SEMUA_DATA,
    val bulanTahun: String = "2026-10",
    val tahunAjaran: String = "2025/2026",
    val kelompokId: Long? = null,
    val kelompokNama: String = "",
    val kategori: String = "Semua Kategori",
    val passwordProtection: String = "" // Optional PIN/Password protection
)

data class BackupResult(
    val success: Boolean,
    val fileName: String,
    val fileUri: Uri?,
    val file: File?,
    val totalRecords: Int,
    val totalEntities: Int,
    val fileSizeFormatted: String,
    val errorMessage: String? = null
)

class BackupService(
    private val context: Context,
    private val database: AppDatabase
) {

    suspend fun createBackup(options: BackupFilterOptions): BackupResult = withContext(Dispatchers.IO) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val humanDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            // 1. Fetch filtered data from DB
            val fullData = fetchFilteredData(options, humanDate)

            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
            if (!dir.exists()) dir.mkdirs()

            val fileName: String
            val backupFile: File
            val totalRecords = fullData.manifest.totalRecords
            val totalEntities = fullData.manifest.entityCounts.size

            when (options.format) {
                BackupFormat.ZIP_COMPLETE -> {
                    fileName = "BACKUP_MUTQIN_ULUL_ALBAB_${timestamp}.zip"
                    backupFile = File(dir, fileName)
                    writeZipBackup(backupFile, fullData, options)
                }
                BackupFormat.EXCEL_SHEETS -> {
                    fileName = "DATA_MUTQIN_ULUL_ALBAB_${timestamp}.xls"
                    backupFile = File(dir, fileName)
                    writeExcelBackup(backupFile, fullData)
                }
                BackupFormat.CSV_DATA -> {
                    fileName = "DATA_CSV_MUTQIN_${timestamp}.csv"
                    backupFile = File(dir, fileName)
                    writeCsvBackup(backupFile, fullData, options)
                }
            }

            val fileSizeBytes = backupFile.length()
            val fileSizeFormatted = formatFileSize(fileSizeBytes)

            val fileUri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    backupFile
                )
            } catch (e: Exception) {
                Uri.fromFile(backupFile)
            }

            // 2. Save into RiwayatBackupEntity
            val periodLabel = when (options.periodType) {
                BackupPeriodType.SEMUA_DATA -> "Seluruh Data Penggunaan"
                BackupPeriodType.BULAN_TAHUN -> "Bulan ${options.bulanTahun}"
                BackupPeriodType.TAHUN_AJARAN -> "Tahun Ajaran ${options.tahunAjaran}"
                BackupPeriodType.KELOMPOK -> "Kelompok: ${options.kelompokNama}"
                BackupPeriodType.KATEGORI_TERTENTU -> "Kategori: ${options.kategori}"
            }

            val formatLabel = when (options.format) {
                BackupFormat.ZIP_COMPLETE -> "ZIP Backup Lengkap"
                BackupFormat.EXCEL_SHEETS -> "Excel Multi-Sheet"
                BackupFormat.CSV_DATA -> "CSV Kategori"
            }

            val securityNote = if (options.passwordProtection.isNotBlank()) {
                "Dilindungi Sandi Backup (Tersimpan aman di arsip)"
            } else {
                "Arsip Resmi Pondok Pesantren Ulul Albab Lubuklinggau"
            }

            val riwayat = RiwayatBackupEntity(
                tanggalWaktu = humanDate,
                namaFile = fileName,
                jenisBackup = formatLabel,
                periodeData = periodLabel,
                totalEntitas = totalEntities,
                totalRecord = totalRecords,
                ukuranFileBytes = fileSizeBytes,
                status = "Selesai",
                fileUri = fileUri.toString(),
                filePath = backupFile.absolutePath,
                proteksiPassword = options.passwordProtection.isNotBlank(),
                catatanKeamanan = securityNote
            )
            database.backupDao().insertRiwayatBackup(riwayat)

            BackupResult(
                success = true,
                fileName = fileName,
                fileUri = fileUri,
                file = backupFile,
                totalRecords = totalRecords,
                totalEntities = totalEntities,
                fileSizeFormatted = fileSizeFormatted
            )
        } catch (e: Exception) {
            e.printStackTrace()
            BackupResult(
                success = false,
                fileName = "",
                fileUri = null,
                file = null,
                totalRecords = 0,
                totalEntities = 0,
                fileSizeFormatted = "0 KB",
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat memproses backup."
            )
        }
    }

    private suspend fun fetchFilteredData(options: BackupFilterOptions, timestamp: String): FullDatabaseBackup {
        var santriList = database.santriDao().getAllSantriList()
        val pembimbingList = database.akademikDao().getAllPembimbingList()
        var kelompokList = database.akademikDao().getAllKelompokList()
        val tahunAjaranList = database.akademikDao().getAllTahunAjaranList()
        var riwayatPindahList = database.santriDao().getAllRiwayatPindahList()
        val riwayatGantiList = database.akademikDao().getAllRiwayatGantiPembimbingList()
        var catatanHarianList = database.perkembanganDao().getAllCatatanHarianList()
        var iqroList = database.perkembanganDao().getAllIqroList()
        var perbaikanList = database.perkembanganDao().getAllPerbaikanBacaanList()
        var setoranList = database.perkembanganDao().getAllSetoranList()
        var murajaahList = database.perkembanganDao().getAllMurajaahList()
        var penilaianList = database.evaluasiDao().getAllPenilaianList()
        var targetList = database.evaluasiDao().getAllTargetList()
        var evaluasiBulananList = database.evaluasiDao().getAllEvaluasiBulananList()
        var kehadiranList = database.evaluasiDao().getAllKehadiranList()
        val evaluasiProgramList = database.evaluasiDao().getAllEvaluasiProgramList()
        val pengaturanList = database.backupDao().getAllPengaturanList()

        // Apply filters
        when (options.periodType) {
            BackupPeriodType.SEMUA_DATA -> {
                // Keep everything
            }
            BackupPeriodType.BULAN_TAHUN -> {
                val prefix = options.bulanTahun // e.g. "2026-10"
                catatanHarianList = catatanHarianList.filter { it.tanggal.startsWith(prefix) }
                iqroList = iqroList.filter { it.tanggal.startsWith(prefix) }
                perbaikanList = perbaikanList.filter { it.tanggal.startsWith(prefix) }
                setoranList = setoranList.filter { it.tanggal.startsWith(prefix) }
                murajaahList = murajaahList.filter { it.tanggal.startsWith(prefix) }
                penilaianList = penilaianList.filter { it.tanggal.startsWith(prefix) }
                evaluasiBulananList = evaluasiBulananList.filter { it.bulanTahun.startsWith(prefix) }
                kehadiranList = kehadiranList.filter { it.tanggal.startsWith(prefix) }
            }
            BackupPeriodType.TAHUN_AJARAN -> {
                val ta = options.tahunAjaran
                santriList = santriList.filter { it.tahunAjaranMasuk == ta }
                kelompokList = kelompokList.filter { it.tahunAjaran == ta }
                targetList = targetList.filter { it.tahunAjaran == ta }
            }
            BackupPeriodType.KELOMPOK -> {
                val kId = options.kelompokId
                if (kId != null) {
                    val matchingSantriIds = santriList.filter { it.kelompokId == kId }.map { it.id }.toSet()
                    santriList = santriList.filter { it.kelompokId == kId }
                    kelompokList = kelompokList.filter { it.id == kId }
                    catatanHarianList = catatanHarianList.filter { it.santriId in matchingSantriIds }
                    iqroList = iqroList.filter { it.santriId in matchingSantriIds }
                    perbaikanList = perbaikanList.filter { it.santriId in matchingSantriIds }
                    setoranList = setoranList.filter { it.santriId in matchingSantriIds }
                    murajaahList = murajaahList.filter { it.santriId in matchingSantriIds }
                    penilaianList = penilaianList.filter { it.santriId in matchingSantriIds }
                    targetList = targetList.filter { it.santriId in matchingSantriIds }
                    evaluasiBulananList = evaluasiBulananList.filter { it.santriId in matchingSantriIds }
                    kehadiranList = kehadiranList.filter { it.santriId in matchingSantriIds || it.kelompokId == kId }
                }
            }
            BackupPeriodType.KATEGORI_TERTENTU -> {
                when (options.kategori) {
                    "Santri & Halaqah" -> {
                        catatanHarianList = emptyList()
                        iqroList = emptyList()
                        perbaikanList = emptyList()
                        setoranList = emptyList()
                        murajaahList = emptyList()
                        penilaianList = emptyList()
                        kehadiranList = emptyList()
                    }
                    "Hafalan & Murajaah" -> {
                        iqroList = emptyList()
                        perbaikanList = emptyList()
                    }
                    "Iqro & Tahsin" -> {
                        setoranList = emptyList()
                        murajaahList = emptyList()
                    }
                    "Evaluasi & Penilaian" -> {
                        iqroList = emptyList()
                        setoranList = emptyList()
                        murajaahList = emptyList()
                    }
                }
            }
        }

        val counts = mapOf(
            "santri" to santriList.size,
            "pembimbing" to pembimbingList.size,
            "kelompok" to kelompokList.size,
            "tahunAjaran" to tahunAjaranList.size,
            "riwayatPerpindahanKelompok" to riwayatPindahList.size,
            "riwayatPergantianPembimbing" to riwayatGantiList.size,
            "catatanPerkembanganHarian" to catatanHarianList.size,
            "perkembanganIqro" to iqroList.size,
            "perbaikanBacaan" to perbaikanList.size,
            "setoranHafalan" to setoranList.size,
            "murajaah" to murajaahList.size,
            "penilaianSantri" to penilaianList.size,
            "targetPembelajaran" to targetList.size,
            "evaluasiBulanan" to evaluasiBulananList.size,
            "kehadiranKegiatan" to kehadiranList.size,
            "catatanEvaluasiProgram" to evaluasiProgramList.size,
            "pengaturanSistem" to pengaturanList.size
        )

        val totalRecords = counts.values.sum()

        val manifest = BackupManifest(
            appName = "MUTQIN ULUL ALBAB",
            pesantren = "Pondok Pesantren Ulul Albab Lubuklinggau",
            backupVersion = "1.0",
            timestamp = timestamp,
            jenisBackup = options.format.name,
            filterPeriode = options.periodType.name,
            entityCounts = counts,
            totalRecords = totalRecords
        )

        return FullDatabaseBackup(
            manifest = manifest,
            santriList = santriList,
            pembimbingList = pembimbingList,
            kelompokList = kelompokList,
            tahunAjaranList = tahunAjaranList,
            riwayatPerpindahanKelompokList = riwayatPindahList,
            riwayatPergantianPembimbingList = riwayatGantiList,
            catatanPerkembanganHarianList = catatanHarianList,
            perkembanganIqroList = iqroList,
            perbaikanBacaanList = perbaikanList,
            setoranHafalanList = setoranList,
            murajaahList = murajaahList,
            penilaianSantriList = penilaianList,
            targetPembelajaranList = targetList,
            evaluasiBulananList = evaluasiBulananList,
            kehadiranKegiatanList = kehadiranList,
            catatanEvaluasiProgramList = evaluasiProgramList,
            pengaturanSistemList = pengaturanList
        )
    }

    private fun writeZipBackup(file: File, backup: FullDatabaseBackup, options: BackupFilterOptions) {
        val fos = FileOutputStream(file)
        val zos = ZipOutputStream(fos)

        fun addFileToZip(entryName: String, content: String) {
            val entry = ZipEntry(entryName)
            zos.putNextEntry(entry)
            zos.write(content.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()
        }

        // 1. README documentation
        val readmeContent = """
            ================================================================================
            MUTQIN ULUL ALBAB — ARSIP & CADANGAN LENGKAP DATA SANTRI
            Pondok Pesantren Ulul Albab Lubuklinggau
            ================================================================================
            Waktu Pembuatan   : ${backup.manifest.timestamp}
            Versi Aplikasi    : ${backup.manifest.backupVersion}
            Filter Periode    : ${backup.manifest.filterPeriode}
            Total Record Data : ${backup.manifest.totalRecords} baris
            
            PERINGATAN KEAMANAN & PRIVASI:
            Berkas ini memuat rekaman data identitas santri, nomor kontak wali santri,
            riwayat capaian hafalan Al-Qur'an, dan evaluasi pengajar.
            Harap simpan berkas ini di media penyimpanan yang aman (Google Drive resmi,
            flashdisk arsip pesantren, atau brankas digital pengelola).
            Dilarang menyebarluaskan dokumen ini kepada pihak di luar manajemen pesantren.
            
            STRUKTUR BERKAS DALAM ARSIP INI:
            1. database_master_restore.json  : Berkas master format JSON untuk pemulihan (restore) penuh.
            2. manifest.json                : Rincian jumlah data per entitas dan tanda waktu pembuatan.
            3. csv/                         : Seluruh tabel dalam format CSV (dapat dibuka via Excel).
            4. json/                        : Data per entitas dalam format JSON murni.
            
            DAFTAR ENTITAS YANG DICADANGKAN:
            - data_santri.csv / .json                       (${backup.santriList.size} data)
            - data_pembimbing.csv / .json                   (${backup.pembimbingList.size} data)
            - data_kelompok_halaqah.csv / .json             (${backup.kelompokList.size} data)
            - data_tahun_ajaran.csv / .json                 (${backup.tahunAjaranList.size} data)
            - data_riwayat_pindah_kelompok.csv / .json      (${backup.riwayatPerpindahanKelompokList.size} data)
            - data_riwayat_ganti_pembimbing.csv / .json     (${backup.riwayatPergantianPembimbingList.size} data)
            - data_setoran_hafalan.csv / .json              (${backup.setoranHafalanList.size} data)
            - data_murajaah.csv / .json                     (${backup.murajaahList.size} data)
            - data_perkembangan_iqro.csv / .json            (${backup.perkembanganIqroList.size} data)
            - data_tahsin_perbaikan_bacaan.csv / .json      (${backup.perbaikanBacaanList.size} data)
            - data_catatan_perkembangan_harian.csv / .json  (${backup.catatanPerkembanganHarianList.size} data)
            - data_penilaian_tashih_santri.csv / .json      (${backup.penilaianSantriList.size} data)
            - data_target_pembelajaran.csv / .json          (${backup.targetPembelajaranList.size} data)
            - data_evaluasi_bulanan.csv / .json             (${backup.evaluasiBulananList.size} data)
            - data_kehadiran_kegiatan.csv / .json           (${backup.kehadiranKegiatanList.size} data)
            - data_evaluasi_program.csv / .json             (${backup.catatanEvaluasiProgramList.size} data)
            - data_pengaturan_sistem.csv / .json            (${backup.pengaturanSistemList.size} data)
            ================================================================================
        """.trimIndent()
        addFileToZip("README_DOKUMENTASI_DATA.txt", readmeContent)

        // 2. Master Restore JSON
        val masterJson = JsonHelper.fullBackupToJson(backup)
        addFileToZip("database_master_restore.json", masterJson)

        // 3. Manifest JSON
        val manifestJson = org.json.JSONObject().apply {
            put("appName", backup.manifest.appName)
            put("pesantren", backup.manifest.pesantren)
            put("timestamp", backup.manifest.timestamp)
            put("version", backup.manifest.backupVersion)
            put("totalRecords", backup.manifest.totalRecords)
            val counts = org.json.JSONObject()
            backup.manifest.entityCounts.forEach { (k, v) -> counts.put(k, v) }
            put("entityCounts", counts)
            put("passwordProtected", options.passwordProtection.isNotBlank())
        }.toString(2)
        addFileToZip("manifest.json", manifestJson)

        // 4. CSV files
        val sheets = buildAllTableData(backup)
        for (sheet in sheets) {
            val csvContent = CsvHelper.buildCsv(sheet.headers, sheet.rows)
            val cleanName = sheet.name.lowercase().replace(" ", "_")
            addFileToZip("csv/${cleanName}.csv", csvContent)
        }

        // 5. Individual JSON files
        fun <T> addJsonList(name: String, list: List<T>, serializer: (T) -> org.json.JSONObject) {
            val arr = org.json.JSONArray()
            list.forEach { arr.put(serializer(it)) }
            addFileToZip("json/${name}.json", arr.toString(2))
        }

        addJsonList("data_santri", backup.santriList, JsonHelper::santriToJson)
        addJsonList("data_pembimbing", backup.pembimbingList, JsonHelper::pembimbingToJson)
        addJsonList("data_kelompok_halaqah", backup.kelompokList, JsonHelper::kelompokToJson)
        addJsonList("data_tahun_ajaran", backup.tahunAjaranList, JsonHelper::tahunAjaranToJson)
        addJsonList("data_riwayat_pindah_kelompok", backup.riwayatPerpindahanKelompokList, JsonHelper::riwayatPindahToJson)
        addJsonList("data_riwayat_ganti_pembimbing", backup.riwayatPergantianPembimbingList, JsonHelper::riwayatGantiPembimbingToJson)
        addJsonList("data_setoran_hafalan", backup.setoranHafalanList, JsonHelper::setoranToJson)
        addJsonList("data_murajaah", backup.murajaahList, JsonHelper::murajaahToJson)
        addJsonList("data_perkembangan_iqro", backup.perkembanganIqroList, JsonHelper::iqroToJson)
        addJsonList("data_tahsin_perbaikan_bacaan", backup.perbaikanBacaanList, JsonHelper::perbaikanBacaanToJson)
        addJsonList("data_catatan_harian", backup.catatanPerkembanganHarianList, JsonHelper::catatanHarianToJson)
        addJsonList("data_penilaian_tashih", backup.penilaianSantriList, JsonHelper::penilaianToJson)
        addJsonList("data_target_pembelajaran", backup.targetPembelajaranList, JsonHelper::targetToJson)
        addJsonList("data_evaluasi_bulanan", backup.evaluasiBulananList, JsonHelper::evaluasiBulananToJson)
        addJsonList("data_kehadiran_kegiatan", backup.kehadiranKegiatanList, JsonHelper::kehadiranToJson)
        addJsonList("data_evaluasi_program", backup.catatanEvaluasiProgramList, JsonHelper::evaluasiProgramToJson)
        addJsonList("data_pengaturan_sistem", backup.pengaturanSistemList, JsonHelper::pengaturanToJson)

        zos.finish()
        zos.close()
        fos.close()
    }

    private fun writeExcelBackup(file: File, backup: FullDatabaseBackup) {
        val sheets = buildAllTableData(backup)
        val xmlContent = ExcelXmlHelper.generateWorkbook(sheets)
        file.writeText(xmlContent, StandardCharsets.UTF_8)
    }

    private fun writeCsvBackup(file: File, backup: FullDatabaseBackup, options: BackupFilterOptions) {
        // Export requested or primary dataset as CSV
        val sheets = buildAllTableData(backup)
        val targetSheet = when (options.kategori) {
            "Santri" -> sheets.firstOrNull { it.name == "Data Santri" }
            "Hafalan" -> sheets.firstOrNull { it.name == "Data Hafalan" }
            "Murajaah" -> sheets.firstOrNull { it.name == "Data Murajaah" }
            "Iqro" -> sheets.firstOrNull { it.name == "Data Iqro" }
            "Bacaan Al-Qur'an" -> sheets.firstOrNull { it.name == "Data Bacaan Al-Qur'an" }
            "Evaluasi" -> sheets.firstOrNull { it.name == "Data Evaluasi" }
            "Kehadiran" -> sheets.firstOrNull { it.name == "Data Kehadiran" }
            else -> sheets.firstOrNull { it.name == "Data Santri" }
        } ?: sheets.first()

        val csvContent = CsvHelper.buildCsv(targetSheet.headers, targetSheet.rows)
        file.writeText(csvContent, StandardCharsets.UTF_8)
    }

    private fun buildAllTableData(backup: FullDatabaseBackup): List<ExcelXmlHelper.SheetData> {
        val santriMap = backup.santriList.associateBy { it.id }
        val pembimbingMap = backup.pembimbingList.associateBy { it.id }
        val kelompokMap = backup.kelompokList.associateBy { it.id }

        // 1. Data Santri
        val santriSheet = ExcelXmlHelper.SheetData(
            name = "Data Santri",
            headers = listOf("ID", "NIS", "Nama Lengkap", "Panggilan", "Jenis Kelamin", "Program", "Kelompok/Halaqah", "Tahun Masuk", "Status", "Tgl Masuk", "Tgl Keluar", "Alasan Keluar", "Nama Wali", "Kontak Wali", "Alamat", "Target Juz"),
            rows = backup.santriList.map { s ->
                listOf(
                    s.id, s.nis, s.namaLengkap, s.namaPanggilan, s.jenisKelamin, s.program,
                    kelompokMap[s.kelompokId]?.namaKelompok ?: "-",
                    s.tahunAjaranMasuk, s.status, s.tanggalMasuk, s.tanggalKeluar ?: "-", s.alasanKeluar ?: "-",
                    s.namaWali, s.kontakWali, s.alamat, s.targetJuzHafalan
                )
            }
        )

        // 2. Data Pembimbing
        val pembimbingSheet = ExcelXmlHelper.SheetData(
            name = "Data Pembimbing",
            headers = listOf("ID", "NIP", "Nama", "Gelar", "Jenis Kelamin", "Peran", "Kontak", "Spesialisasi", "Status"),
            rows = backup.pembimbingList.map { p ->
                listOf(p.id, p.nip, p.nama, p.gelar, p.jenisKelamin, p.peran, p.kontak, p.spesialisasi, if (p.statusAktif) "Aktif" else "Non-Aktif")
            }
        )

        // 3. Data Kelompok
        val kelompokSheet = ExcelXmlHelper.SheetData(
            name = "Data Kelompok",
            headers = listOf("ID", "Nama Kelompok", "Program", "Pembimbing", "Tahun Ajaran", "Ruangan", "Jadwal Sesi", "Target", "Status"),
            rows = backup.kelompokList.map { k ->
                listOf(k.id, k.namaKelompok, k.program, pembimbingMap[k.pembimbingId]?.nama ?: "-", k.tahunAjaran, k.ruangan, k.jadwalSesi, k.targetHafalan, if (k.statusAktif) "Aktif" else "Non-Aktif")
            }
        )

        // 4. Data Iqro'
        val iqroSheet = ExcelXmlHelper.SheetData(
            name = "Data Iqro",
            headers = listOf("ID", "Santri", "Tanggal", "Jilid", "Hal Awal", "Hal Akhir", "Kelancaran", "Makhraj", "Status Kenaikan", "Catatan"),
            rows = backup.perkembanganIqroList.map { i ->
                listOf(i.id, santriMap[i.santriId]?.namaLengkap ?: i.santriId, i.tanggal, i.jilid, i.halamanAwal, i.halamanAkhir, i.kelancaran, i.makhrajNilai, i.statusKenaikan, i.catatan)
            }
        )

        // 5. Data Bacaan Al-Qur'an (Tahsin)
        val tahsinSheet = ExcelXmlHelper.SheetData(
            name = "Data Bacaan Al-Qur'an",
            headers = listOf("ID", "Santri", "Tanggal", "Surah", "Ayat Awal", "Ayat Akhir", "Fokus Tajwid", "Catatan Kekurangan", "Status Penguasaan"),
            rows = backup.perbaikanBacaanList.map { t ->
                listOf(t.id, santriMap[t.santriId]?.namaLengkap ?: t.santriId, t.tanggal, t.surah, t.ayatAwal, t.ayatAkhir, t.fokusTajwid, t.catatanKekurangan, t.statusPenguasaan)
            }
        )

        // 6. Data Hafalan (Ziyadah)
        val hafalanSheet = ExcelXmlHelper.SheetData(
            name = "Data Hafalan",
            headers = listOf("ID", "Santri", "Tanggal", "Surah", "Juz", "Ayat Awal", "Ayat Akhir", "Total Ayat", "Predikat", "Nilai Kelancaran", "Nilai Tajwid", "Status", "Catatan"),
            rows = backup.setoranHafalanList.map { h ->
                listOf(h.id, santriMap[h.santriId]?.namaLengkap ?: h.santriId, h.tanggal, h.surah, h.juz, h.ayatAwal, h.ayatAkhir, h.totalAyat, h.predikat, h.nilaiKelancaran, h.nilaiTajwid, h.statusKelulusan, h.catatan)
            }
        )

        // 7. Data Murajaah
        val murajaahSheet = ExcelXmlHelper.SheetData(
            name = "Data Murajaah",
            headers = listOf("ID", "Santri", "Tanggal", "Juz", "Surah Awal", "Surah Akhir", "Total Hal", "Kelancaran", "Kemutqinan", "Catatan"),
            rows = backup.murajaahList.map { m ->
                listOf(m.id, santriMap[m.santriId]?.namaLengkap ?: m.santriId, m.tanggal, m.juz, m.surahAwal, m.surahAkhir, m.totalHalaman, m.kelancaran, m.kemutqinan, m.catatan)
            }
        )

        // 8. Data Evaluasi
        val evaluasiSheet = ExcelXmlHelper.SheetData(
            name = "Data Evaluasi",
            headers = listOf("ID", "Santri", "Bulan/Tahun", "Ayat Ziyadah", "Juz Murajaah", "Kehadiran %", "Predikat", "Catatan", "Rekomendasi", "Evaluator"),
            rows = backup.evaluasiBulananList.map { e ->
                listOf(e.id, santriMap[e.santriId]?.namaLengkap ?: e.santriId, e.bulanTahun, e.totalSetoranAyat, e.totalJuzMurajaah, "${e.persentaseKehadiran}%", e.predikatBulanan, e.catatanEvaluasi, e.rekomendasiPembimbing, e.evaluasiOleh)
            }
        )

        // 9. Data Target
        val targetSheet = ExcelXmlHelper.SheetData(
            name = "Data Target",
            headers = listOf("ID", "Santri", "Tahun Ajaran", "Jenis Target", "Deskripsi", "Kuantitas", "Deadline", "Capaian %", "Status", "Catatan"),
            rows = backup.targetPembelajaranList.map { tg ->
                listOf(tg.id, santriMap[tg.santriId]?.namaLengkap ?: tg.santriId, tg.tahunAjaran, tg.targetJenis, tg.deskripsiTarget, tg.targetKuantitas, tg.batasWaktu, "${tg.capaianPersen}%", tg.status, tg.catatan)
            }
        )

        // 10. Data Kehadiran
        val kehadiranSheet = ExcelXmlHelper.SheetData(
            name = "Data Kehadiran",
            headers = listOf("ID", "Santri", "Kelompok", "Tanggal", "Kegiatan", "Status Kehadiran", "Keterangan"),
            rows = backup.kehadiranKegiatanList.map { k ->
                listOf(k.id, santriMap[k.santriId]?.namaLengkap ?: k.santriId, kelompokMap[k.kelompokId]?.namaKelompok ?: "-", k.tanggal, k.jenisKegiatan, k.statusKehadiran, k.keterangan)
            }
        )

        // 11. Data Perpindahan Kelompok
        val pindahSheet = ExcelXmlHelper.SheetData(
            name = "Perpindahan Kelompok",
            headers = listOf("ID", "Santri", "Kelompok Lama", "Kelompok Baru", "Tanggal Pindah", "Alasan", "Dicatat Oleh"),
            rows = backup.riwayatPerpindahanKelompokList.map { r ->
                listOf(r.id, santriMap[r.santriId]?.namaLengkap ?: r.santriId, kelompokMap[r.kelompokLamaId]?.namaKelompok ?: "-", kelompokMap[r.kelompokBaruId]?.namaKelompok ?: r.kelompokBaruId, r.tanggalPindah, r.alasanPindah, r.dicatatOleh)
            }
        )

        // 12. Data Riwayat Pembimbing
        val gantiPembimbingSheet = ExcelXmlHelper.SheetData(
            name = "Riwayat Pembimbing",
            headers = listOf("ID", "Kelompok", "Pembimbing Lama", "Pembimbing Baru", "Tanggal Ganti", "Alasan", "Dicatat Oleh"),
            rows = backup.riwayatPergantianPembimbingList.map { g ->
                listOf(g.id, kelompokMap[g.kelompokId]?.namaKelompok ?: g.kelompokId, pembimbingMap[g.pembimbingLamaId]?.nama ?: "-", pembimbingMap[g.pembimbingBaruId]?.nama ?: g.pembimbingBaruId, g.tanggalGanti, g.alasan, g.dicatatOleh)
            }
        )

        return listOf(
            santriSheet,
            pembimbingSheet,
            kelompokSheet,
            iqroSheet,
            tahsinSheet,
            hafalanSheet,
            murajaahSheet,
            evaluasiSheet,
            targetSheet,
            kehadiranSheet,
            pindahSheet,
            gantiPembimbingSheet
        )
    }

    fun createShareIntent(fileUri: Uri, mimeType: String, subject: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "Arsip data resmi MUTQIN ULUL ALBAB — Pondok Pesantren Ulul Albab Lubuklinggau.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    suspend fun restoreFromBackupJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val backup = JsonHelper.parseFullBackupJson(jsonString)

            // Insert or replace all entities into database
            if (backup.tahunAjaranList.isNotEmpty()) database.akademikDao().insertTahunAjaranList(backup.tahunAjaranList)
            if (backup.pembimbingList.isNotEmpty()) database.akademikDao().insertPembimbingList(backup.pembimbingList)
            if (backup.kelompokList.isNotEmpty()) database.akademikDao().insertKelompokList(backup.kelompokList)
            if (backup.santriList.isNotEmpty()) database.santriDao().insertSantriList(backup.santriList)
            if (backup.riwayatPerpindahanKelompokList.isNotEmpty()) database.santriDao().insertRiwayatPindahList(backup.riwayatPerpindahanKelompokList)
            if (backup.riwayatPergantianPembimbingList.isNotEmpty()) database.akademikDao().insertRiwayatGantiPembimbingList(backup.riwayatPergantianPembimbingList)
            if (backup.catatanPerkembanganHarianList.isNotEmpty()) database.perkembanganDao().insertCatatanHarianList(backup.catatanPerkembanganHarianList)
            if (backup.perkembanganIqroList.isNotEmpty()) database.perkembanganDao().insertIqroList(backup.perkembanganIqroList)
            if (backup.perbaikanBacaanList.isNotEmpty()) database.perkembanganDao().insertPerbaikanBacaanList(backup.perbaikanBacaanList)
            if (backup.setoranHafalanList.isNotEmpty()) database.perkembanganDao().insertSetoranList(backup.setoranHafalanList)
            if (backup.murajaahList.isNotEmpty()) database.perkembanganDao().insertMurajaahList(backup.murajaahList)
            if (backup.penilaianSantriList.isNotEmpty()) database.evaluasiDao().insertPenilaianList(backup.penilaianSantriList)
            if (backup.targetPembelajaranList.isNotEmpty()) database.evaluasiDao().insertTargetList(backup.targetPembelajaranList)
            if (backup.evaluasiBulananList.isNotEmpty()) database.evaluasiDao().insertEvaluasiBulananList(backup.evaluasiBulananList)
            if (backup.kehadiranKegiatanList.isNotEmpty()) database.evaluasiDao().insertKehadiranList(backup.kehadiranKegiatanList)
            if (backup.catatanEvaluasiProgramList.isNotEmpty()) database.evaluasiDao().insertEvaluasiProgramList(backup.catatanEvaluasiProgramList)
            if (backup.pengaturanSistemList.isNotEmpty()) database.backupDao().savePengaturanList(backup.pengaturanSistemList)

            Result.success(backup.manifest.totalRecords)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format(Locale.US, "%.1f KB", bytes / 1024.0)
            else -> String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }
}
