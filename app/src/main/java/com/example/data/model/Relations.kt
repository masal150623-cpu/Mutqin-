package com.example.data.model

data class SantriWithKelompok(
    val santri: SantriEntity,
    val namaKelompok: String?,
    val namaPembimbing: String?
)

data class KelompokWithDetails(
    val kelompok: KelompokEntity,
    val namaPembimbing: String?,
    val totalSantri: Int = 0
)

data class DashboardSummary(
    val totalSantriAktif: Int = 0,
    val totalSantriAlumni: Int = 0,
    val totalSantriKeluar: Int = 0,
    val totalProgramTahfidz: Int = 0,
    val totalProgramTpa: Int = 0,
    val totalKelompok: Int = 0,
    val totalPembimbing: Int = 0,
    val setoranHariIni: Int = 0,
    val targetTercapaiPersen: Int = 0
)

data class DatabaseCounts(
    val santriCount: Int = 0,
    val pembimbingCount: Int = 0,
    val kelompokCount: Int = 0,
    val tahunAjaranCount: Int = 0,
    val setoranHafalanCount: Int = 0,
    val murajaahCount: Int = 0,
    val iqroCount: Int = 0,
    val tahsinCount: Int = 0,
    val penilaianCount: Int = 0,
    val targetCount: Int = 0,
    val evaluasiBulananCount: Int = 0,
    val kehadiranCount: Int = 0,
    val catatanHarianCount: Int = 0,
    val riwayatPindahCount: Int = 0,
    val riwayatGantiPembimbingCount: Int = 0,
    val evaluasiProgramCount: Int = 0,
    val pengaturanCount: Int = 0,
    val totalRecords: Int = 0
)
