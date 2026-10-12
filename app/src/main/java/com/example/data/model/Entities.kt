package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 1. Data Seluruh Santri (Aktif, Lulus, Keluar/Pindah)
 * Mendukung penerimaan santri baru, pengarsipan santri keluar.
 */
@Entity(tableName = "santri")
data class SantriEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nis: String, // Nomor Induk Santri
    val namaLengkap: String,
    val namaPanggilan: String = "",
    val jenisKelamin: String = "Laki-laki", // Laki-laki / Perempuan
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val program: String = "Tahfidz Khusus", // TPA / Tahfidz Khusus
    val kelompokId: Long? = null,
    val tahunAjaranMasuk: String = "2025/2026",
    val status: String = "Aktif", // Aktif / Lulus / Keluar / Pindah
    val tanggalMasuk: String = "",
    val tanggalKeluar: String? = null,
    val alasanKeluar: String? = null,
    val namaWali: String = "",
    val kontakWali: String = "",
    val alamat: String = "Lubuklinggau",
    val catatanKhusus: String = "",
    val targetJuzHafalan: Int = 30
)

/**
 * 2. Data Ustaz dan Ustazah (Pembimbing Halaqah/Kelas)
 */
@Entity(tableName = "pembimbing")
data class PembimbingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nip: String,
    val nama: String,
    val gelar: String = "Al-Hafidz",
    val jenisKelamin: String = "Laki-laki",
    val peran: String = "Ustaz Pembimbing", // Kepala Program / Ustaz Pembimbing / Ustazah Pembimbing
    val kontak: String = "",
    val spesialisasi: String = "Tahfidz & Tajwid", // Tahfidz / Tahsin / Iqro
    val statusAktif: Boolean = true
)

/**
 * 3. Data Kelompok Pembelajaran (Halaqah / Kelas)
 */
@Entity(tableName = "kelompok")
data class KelompokEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val namaKelompok: String, // misal: Zaid bin Tsabit, Utsman bin Affan
    val program: String = "Tahfidz Khusus", // TPA / Tahfidz Khusus
    val pembimbingId: Long? = null,
    val tahunAjaran: String = "2025/2026",
    val ruangan: String = "Halaqah Utama Masjid",
    val jadwalSesi: String = "Ba'da Subuh & Ba'da Ashar",
    val targetHafalan: String = "Juz 30 & Juz 29",
    val statusAktif: Boolean = true
)

/**
 * 4. Data Kelas dan Tahun Ajaran
 */
@Entity(tableName = "tahun_ajaran")
data class TahunAjaranEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val kode: String, // misal: 2025-2026-GANJIL
    val tahun: String = "2025/2026",
    val semester: String = "Ganjil", // Ganjil / Genap
    val tanggalMulai: String = "2025-07-15",
    val tanggalSelesai: String = "2025-12-20",
    val isAktif: Boolean = true,
    val catatan: String = "Semester Berjalan TP 2025/2026"
)

/**
 * 5. Riwayat Perpindahan Kelompok Santri
 */
@Entity(
    tableName = "riwayat_perpindahan_kelompok",
    indices = [Index("santriId")]
)
data class RiwayatPerpindahanKelompokEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val kelompokLamaId: Long?,
    val kelompokBaruId: Long,
    val tanggalPindah: String,
    val alasanPindah: String = "Kenaikan tingkat / pemerataan halaqah",
    val dicatatOleh: String = "Kepala Program"
)

/**
 * 6. Riwayat Pergantian Pembimbing Kelompok
 */
@Entity(
    tableName = "riwayat_pergantian_pembimbing",
    indices = [Index("kelompokId")]
)
data class RiwayatPergantianPembimbingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val kelompokId: Long,
    val pembimbingLamaId: Long?,
    val pembimbingBaruId: Long,
    val tanggalGanti: String,
    val alasan: String = "Rotasi semesteran",
    val dicatatOleh: String = "Kepala Program"
)

/**
 * 7. Catatan Perkembangan Harian Santri
 */
@Entity(
    tableName = "catatan_perkembangan_harian",
    indices = [Index("santriId")]
)
data class CatatanPerkembanganHarianEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val tanggal: String,
    val sesi: String = "Pagi (Subuh)", // Pagi / Sore / Malam
    val adabKedisiplinan: String = "Sangat Baik", // Sangat Baik / Baik / Cukup / Perlu Bimbingan
    val fokusBelajar: String = "Ziyadah & Murajaah Mandiri",
    val catatanUmum: String = "",
    val pembimbingId: Long? = null
)

/**
 * 8. Data Perkembangan Iqro' (Jilid 1 s.d. 6)
 */
@Entity(
    tableName = "perkembangan_iqro",
    indices = [Index("santriId")]
)
data class PerkembanganIqroEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val tanggal: String,
    val jilid: Int = 1, // 1 s.d 6
    val halamanAwal: Int = 1,
    val halamanAkhir: Int = 1,
    val kelancaran: String = "Lancar (A)", // Lancar (A) / Cukup (B) / Mengulang (C)
    val makhrajNilai: String = "Baik",
    val statusKenaikan: String = "Lanjut Halaman", // Lanjut Halaman / Naik Jilid / Mengulang
    val catatan: String = "",
    val pembimbingId: Long? = null
)

/**
 * 9. Data Perbaikan Bacaan Al-Qur'an (Tahsin / Fashahah)
 */
@Entity(
    tableName = "perbaikan_bacaan",
    indices = [Index("santriId")]
)
data class PerbaikanBacaanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val tanggal: String,
    val surah: String,
    val ayatAwal: Int,
    val ayatAkhir: Int,
    val fokusTajwid: String = "Makharijul Huruf", // Makharijul Huruf / Mad Ashli & Far'i / Ghunnah / Ahkam Nun & Mim / Waqaf & Ibtida
    val catatanKekurangan: String = "",
    val statusPenguasaan: String = "Sudah Baik", // Perlu Latihan / Sudah Baik / Mutqin
    val pembimbingId: Long? = null
)

/**
 * 10. Data Setoran Hafalan Baru (Ziyadah)
 */
@Entity(
    tableName = "setoran_hafalan",
    indices = [Index("santriId")]
)
data class SetoranHafalanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val tanggal: String,
    val surah: String,
    val juz: Int = 30,
    val ayatAwal: Int = 1,
    val ayatAkhir: Int = 10,
    val totalAyat: Int = 10,
    val predikat: String = "Mutqin (A)", // Mutqin (A) / Jayyid Jiddan (B+) / Jayyid (B) / Maqbul (C)
    val nilaiKelancaran: Int = 90, // skala 1-100
    val nilaiTajwid: Int = 90, // skala 1-100
    val statusKelulusan: String = "Lulus", // Lulus / Ulang
    val catatan: String = "",
    val pembimbingId: Long? = null
)

/**
 * 11. Data Murajaah (Pengulangan Hafalan)
 */
@Entity(
    tableName = "murajaah",
    indices = [Index("santriId")]
)
data class MurajaahEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val tanggal: String,
    val juz: Int = 30,
    val surahAwal: String,
    val surahAkhir: String,
    val totalHalaman: Double = 1.0,
    val kelancaran: String = "Sangat Lancar",
    val kemutqinan: String = "Mutqin", // Mutqin / Jayyid / Perlu Pengulangan
    val catatan: String = "",
    val pembimbingId: Long? = null
)

/**
 * 12. Riwayat Penilaian Santri (Ujian Tashih Juz / Semester)
 */
@Entity(
    tableName = "penilaian_santri",
    indices = [Index("santriId")]
)
data class PenilaianSantriEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val tanggal: String,
    val jenisUjian: String = "Tashih Juz", // Tashih Juz / Ujian Semester / Ujian Kenaikan Jilid
    val materiUjian: String = "Juz 30 (Al-Naba - An-Nas)",
    val nilaiKelancaran: Int = 95,
    val nilaiTajwid: Int = 90,
    val nilaiFashahah: Int = 92,
    val nilaiAdab: Int = 95,
    val totalNilaiRataRata: Double = 93.0,
    val predikatAkhir: String = "Mumtaz (Istimewa)", // Mumtaz / Jayyid Jiddan / Jayyid / Rasib
    val namaPenguji: String = "Ustaz Ahmad Al-Hafidz",
    val rekomendasi: String = "Lulus Tashih Juz 30, berhak melanjutkan Ziyadah Juz 29",
    val sertifikatDiterbitkan: Boolean = true
)

/**
 * 13. Target Pembelajaran Santri
 */
@Entity(
    tableName = "target_pembelajaran",
    indices = [Index("santriId")]
)
data class TargetPembelajaranEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val tahunAjaran: String = "2025/2026",
    val targetJenis: String = "Hafalan Baru", // Hafalan Baru / Murajaah / Iqro / Tahsin
    val deskripsiTarget: String = "Tuntas Hafalan Juz 30 Mutqin",
    val targetKuantitas: String = "1 Juz (Juz 30)",
    val batasWaktu: String = "2025-12-31",
    val capaianPersen: Int = 85,
    val status: String = "Dalam Proses", // Tercapai / Dalam Proses / Belum
    val catatan: String = "Tinggal surah An-Naba dan An-Nazi'at"
)

/**
 * 14. Hasil Evaluasi Bulanan Santri
 */
@Entity(
    tableName = "evaluasi_bulanan",
    indices = [Index("santriId")]
)
data class EvaluasiBulananEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val bulanTahun: String = "2025-10", // YYYY-MM
    val totalSetoranAyat: Int = 120,
    val totalJuzMurajaah: Double = 2.5,
    val tingkatKelancaranIqro: String = "-",
    val persentaseKehadiran: Int = 96,
    val predikatBulanan: String = "Sangat Baik (A)",
    val catatanEvaluasi: String = "Perkembangan makhraj huruf sangat pesat, tajwid konsisten.",
    val rekomendasiPembimbing: String = "Pertahankan ritme murajaah harian.",
    val evaluasiOleh: String = "Ustaz Ahmad Al-Hafidz"
)

/**
 * 15. Data Kehadiran dan Kegiatan Santri
 */
@Entity(
    tableName = "kehadiran_kegiatan",
    indices = [Index("santriId")]
)
data class KehadiranKegiatanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val santriId: Long,
    val kelompokId: Long? = null,
    val tanggal: String,
    val jenisKegiatan: String = "Halaqah Rutin", // Halaqah Rutin / Tahsin Akbar / Mabit Qur'ani / Ujian Tashih
    val statusKehadiran: String = "Hadir", // Hadir / Sakit / Izin / Alpa
    val keterangan: String = ""
)

/**
 * 16. Catatan Evaluasi Program Pesantren
 */
@Entity(tableName = "evaluasi_program")
data class CatatanEvaluasiProgramEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tahunAjaran: String = "2025/2026",
    val semester: String = "Ganjil",
    val tanggal: String,
    val judul: String,
    val ringkasanCapaian: String,
    val kendalaTantangan: String,
    val rekomendasiTindakLanjut: String,
    val evaluator: String = "Kepala Program Tahfidz"
)

/**
 * 17. Pengaturan Sistem dan Metadata Pemulihan
 */
@Entity(tableName = "pengaturan_sistem")
data class PengaturanSistemEntity(
    @PrimaryKey val kunci: String,
    val nilai: String,
    val deskripsi: String = "",
    val tanggalPerubahan: String = ""
)

/**
 * 18. Log Riwayat Backup Data
 */
@Entity(tableName = "riwayat_backup")
data class RiwayatBackupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tanggalWaktu: String,
    val namaFile: String,
    val jenisBackup: String, // ZIP Lengkap / Excel Multi-Sheet / CSV Kategori
    val periodeData: String, // Seluruh Data / Bulan YYYY-MM / Tahun Ajaran / Kelompok
    val totalEntitas: Int = 18,
    val totalRecord: Int = 0,
    val ukuranFileBytes: Long = 0L,
    val status: String = "Selesai", // Selesai / Gagal / Dalam Proses
    val fileUri: String = "",
    val filePath: String = "",
    val proteksiPassword: Boolean = false,
    val catatanKeamanan: String = "Arsip internal Ponpes Ulul Albab Lubuklinggau"
)
