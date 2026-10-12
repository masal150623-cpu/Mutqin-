package com.example.data.model

data class BackupManifest(
    val appName: String = "MUTQIN ULUL ALBAB",
    val pesantren: String = "Pondok Pesantren Ulul Albab Lubuklinggau",
    val backupVersion: String = "1.0",
    val timestamp: String,
    val jenisBackup: String,
    val filterPeriode: String,
    val entityCounts: Map<String, Int>,
    val totalRecords: Int,
    val hashSha256: String = "",
    val securityNotice: String = "Peringatan: Berkas ini memuat data pribadi santri dan pengajar Pondok Pesantren Ulul Albab Lubuklinggau. Simpan di tempat yang aman dan jangan disebarluaskan."
)

data class FullDatabaseBackup(
    val manifest: BackupManifest,
    val santriList: List<SantriEntity> = emptyList(),
    val pembimbingList: List<PembimbingEntity> = emptyList(),
    val kelompokList: List<KelompokEntity> = emptyList(),
    val tahunAjaranList: List<TahunAjaranEntity> = emptyList(),
    val riwayatPerpindahanKelompokList: List<RiwayatPerpindahanKelompokEntity> = emptyList(),
    val riwayatPergantianPembimbingList: List<RiwayatPergantianPembimbingEntity> = emptyList(),
    val catatanPerkembanganHarianList: List<CatatanPerkembanganHarianEntity> = emptyList(),
    val perkembanganIqroList: List<PerkembanganIqroEntity> = emptyList(),
    val perbaikanBacaanList: List<PerbaikanBacaanEntity> = emptyList(),
    val setoranHafalanList: List<SetoranHafalanEntity> = emptyList(),
    val murajaahList: List<MurajaahEntity> = emptyList(),
    val penilaianSantriList: List<PenilaianSantriEntity> = emptyList(),
    val targetPembelajaranList: List<TargetPembelajaranEntity> = emptyList(),
    val evaluasiBulananList: List<EvaluasiBulananEntity> = emptyList(),
    val kehadiranKegiatanList: List<KehadiranKegiatanEntity> = emptyList(),
    val catatanEvaluasiProgramList: List<CatatanEvaluasiProgramEntity> = emptyList(),
    val pengaturanSistemList: List<PengaturanSistemEntity> = emptyList()
)
