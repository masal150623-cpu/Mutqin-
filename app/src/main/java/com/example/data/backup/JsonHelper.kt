package com.example.data.backup

import com.example.data.model.*
import org.json.JSONArray
import org.json.JSONObject

object JsonHelper {

    // --- Santri ---
    fun santriToJson(s: SantriEntity): JSONObject {
        return JSONObject().apply {
            put("id", s.id)
            put("nis", s.nis)
            put("namaLengkap", s.namaLengkap)
            put("namaPanggilan", s.namaPanggilan)
            put("jenisKelamin", s.jenisKelamin)
            put("tempatLahir", s.tempatLahir)
            put("tanggalLahir", s.tanggalLahir)
            put("program", s.program)
            put("kelompokId", s.kelompokId ?: JSONObject.NULL)
            put("tahunAjaranMasuk", s.tahunAjaranMasuk)
            put("status", s.status)
            put("tanggalMasuk", s.tanggalMasuk)
            put("tanggalKeluar", s.tanggalKeluar ?: JSONObject.NULL)
            put("alasanKeluar", s.alasanKeluar ?: JSONObject.NULL)
            put("namaWali", s.namaWali)
            put("kontakWali", s.kontakWali)
            put("alamat", s.alamat)
            put("catatanKhusus", s.catatanKhusus)
            put("targetJuzHafalan", s.targetJuzHafalan)
        }
    }

    fun jsonToSantri(obj: JSONObject): SantriEntity {
        return SantriEntity(
            id = obj.optLong("id", 0),
            nis = obj.optString("nis"),
            namaLengkap = obj.optString("namaLengkap"),
            namaPanggilan = obj.optString("namaPanggilan"),
            jenisKelamin = obj.optString("jenisKelamin", "Laki-laki"),
            tempatLahir = obj.optString("tempatLahir"),
            tanggalLahir = obj.optString("tanggalLahir"),
            program = obj.optString("program", "Tahfidz Khusus"),
            kelompokId = if (obj.isNull("kelompokId")) null else obj.optLong("kelompokId"),
            tahunAjaranMasuk = obj.optString("tahunAjaranMasuk", "2025/2026"),
            status = obj.optString("status", "Aktif"),
            tanggalMasuk = obj.optString("tanggalMasuk"),
            tanggalKeluar = if (obj.isNull("tanggalKeluar")) null else obj.optString("tanggalKeluar"),
            alasanKeluar = if (obj.isNull("alasanKeluar")) null else obj.optString("alasanKeluar"),
            namaWali = obj.optString("namaWali"),
            kontakWali = obj.optString("kontakWali"),
            alamat = obj.optString("alamat"),
            catatanKhusus = obj.optString("catatanKhusus"),
            targetJuzHafalan = obj.optInt("targetJuzHafalan", 30)
        )
    }

    // --- Pembimbing ---
    fun pembimbingToJson(p: PembimbingEntity): JSONObject {
        return JSONObject().apply {
            put("id", p.id)
            put("nip", p.nip)
            put("nama", p.nama)
            put("gelar", p.gelar)
            put("jenisKelamin", p.jenisKelamin)
            put("peran", p.peran)
            put("kontak", p.kontak)
            put("spesialisasi", p.spesialisasi)
            put("statusAktif", p.statusAktif)
        }
    }

    fun jsonToPembimbing(obj: JSONObject): PembimbingEntity {
        return PembimbingEntity(
            id = obj.optLong("id", 0),
            nip = obj.optString("nip"),
            nama = obj.optString("nama"),
            gelar = obj.optString("gelar"),
            jenisKelamin = obj.optString("jenisKelamin", "Laki-laki"),
            peran = obj.optString("peran", "Ustaz Pembimbing"),
            kontak = obj.optString("kontak"),
            spesialisasi = obj.optString("spesialisasi"),
            statusAktif = obj.optBoolean("statusAktif", true)
        )
    }

    // --- Kelompok ---
    fun kelompokToJson(k: KelompokEntity): JSONObject {
        return JSONObject().apply {
            put("id", k.id)
            put("namaKelompok", k.namaKelompok)
            put("program", k.program)
            put("pembimbingId", k.pembimbingId ?: JSONObject.NULL)
            put("tahunAjaran", k.tahunAjaran)
            put("ruangan", k.ruangan)
            put("jadwalSesi", k.jadwalSesi)
            put("targetHafalan", k.targetHafalan)
            put("statusAktif", k.statusAktif)
        }
    }

    fun jsonToKelompok(obj: JSONObject): KelompokEntity {
        return KelompokEntity(
            id = obj.optLong("id", 0),
            namaKelompok = obj.optString("namaKelompok"),
            program = obj.optString("program", "Tahfidz Khusus"),
            pembimbingId = if (obj.isNull("pembimbingId")) null else obj.optLong("pembimbingId"),
            tahunAjaran = obj.optString("tahunAjaran", "2025/2026"),
            ruangan = obj.optString("ruangan"),
            jadwalSesi = obj.optString("jadwalSesi"),
            targetHafalan = obj.optString("targetHafalan"),
            statusAktif = obj.optBoolean("statusAktif", true)
        )
    }

    // --- Tahun Ajaran ---
    fun tahunAjaranToJson(t: TahunAjaranEntity): JSONObject {
        return JSONObject().apply {
            put("id", t.id)
            put("kode", t.kode)
            put("tahun", t.tahun)
            put("semester", t.semester)
            put("tanggalMulai", t.tanggalMulai)
            put("tanggalSelesai", t.tanggalSelesai)
            put("isAktif", t.isAktif)
            put("catatan", t.catatan)
        }
    }

    fun jsonToTahunAjaran(obj: JSONObject): TahunAjaranEntity {
        return TahunAjaranEntity(
            id = obj.optLong("id", 0),
            kode = obj.optString("kode"),
            tahun = obj.optString("tahun"),
            semester = obj.optString("semester"),
            tanggalMulai = obj.optString("tanggalMulai"),
            tanggalSelesai = obj.optString("tanggalSelesai"),
            isAktif = obj.optBoolean("isAktif", true),
            catatan = obj.optString("catatan")
        )
    }

    // --- Riwayat Perpindahan Kelompok ---
    fun riwayatPindahToJson(r: RiwayatPerpindahanKelompokEntity): JSONObject {
        return JSONObject().apply {
            put("id", r.id)
            put("santriId", r.santriId)
            put("kelompokLamaId", r.kelompokLamaId ?: JSONObject.NULL)
            put("kelompokBaruId", r.kelompokBaruId)
            put("tanggalPindah", r.tanggalPindah)
            put("alasanPindah", r.alasanPindah)
            put("dicatatOleh", r.dicatatOleh)
        }
    }

    fun jsonToRiwayatPindah(obj: JSONObject): RiwayatPerpindahanKelompokEntity {
        return RiwayatPerpindahanKelompokEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            kelompokLamaId = if (obj.isNull("kelompokLamaId")) null else obj.optLong("kelompokLamaId"),
            kelompokBaruId = obj.optLong("kelompokBaruId"),
            tanggalPindah = obj.optString("tanggalPindah"),
            alasanPindah = obj.optString("alasanPindah"),
            dicatatOleh = obj.optString("dicatatOleh")
        )
    }

    // --- Riwayat Pergantian Pembimbing ---
    fun riwayatGantiPembimbingToJson(r: RiwayatPergantianPembimbingEntity): JSONObject {
        return JSONObject().apply {
            put("id", r.id)
            put("kelompokId", r.kelompokId)
            put("pembimbingLamaId", r.pembimbingLamaId ?: JSONObject.NULL)
            put("pembimbingBaruId", r.pembimbingBaruId)
            put("tanggalGanti", r.tanggalGanti)
            put("alasan", r.alasan)
            put("dicatatOleh", r.dicatatOleh)
        }
    }

    fun jsonToRiwayatGantiPembimbing(obj: JSONObject): RiwayatPergantianPembimbingEntity {
        return RiwayatPergantianPembimbingEntity(
            id = obj.optLong("id", 0),
            kelompokId = obj.optLong("kelompokId"),
            pembimbingLamaId = if (obj.isNull("pembimbingLamaId")) null else obj.optLong("pembimbingLamaId"),
            pembimbingBaruId = obj.optLong("pembimbingBaruId"),
            tanggalGanti = obj.optString("tanggalGanti"),
            alasan = obj.optString("alasan"),
            dicatatOleh = obj.optString("dicatatOleh")
        )
    }

    // --- Catatan Perkembangan Harian ---
    fun catatanHarianToJson(c: CatatanPerkembanganHarianEntity): JSONObject {
        return JSONObject().apply {
            put("id", c.id)
            put("santriId", c.santriId)
            put("tanggal", c.tanggal)
            put("sesi", c.sesi)
            put("adabKedisiplinan", c.adabKedisiplinan)
            put("fokusBelajar", c.fokusBelajar)
            put("catatanUmum", c.catatanUmum)
            put("pembimbingId", c.pembimbingId ?: JSONObject.NULL)
        }
    }

    fun jsonToCatatanHarian(obj: JSONObject): CatatanPerkembanganHarianEntity {
        return CatatanPerkembanganHarianEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            tanggal = obj.optString("tanggal"),
            sesi = obj.optString("sesi"),
            adabKedisiplinan = obj.optString("adabKedisiplinan"),
            fokusBelajar = obj.optString("fokusBelajar"),
            catatanUmum = obj.optString("catatanUmum"),
            pembimbingId = if (obj.isNull("pembimbingId")) null else obj.optLong("pembimbingId")
        )
    }

    // --- Iqro ---
    fun iqroToJson(i: PerkembanganIqroEntity): JSONObject {
        return JSONObject().apply {
            put("id", i.id)
            put("santriId", i.santriId)
            put("tanggal", i.tanggal)
            put("jilid", i.jilid)
            put("halamanAwal", i.halamanAwal)
            put("halamanAkhir", i.halamanAkhir)
            put("kelancaran", i.kelancaran)
            put("makhrajNilai", i.makhrajNilai)
            put("statusKenaikan", i.statusKenaikan)
            put("catatan", i.catatan)
            put("pembimbingId", i.pembimbingId ?: JSONObject.NULL)
        }
    }

    fun jsonToIqro(obj: JSONObject): PerkembanganIqroEntity {
        return PerkembanganIqroEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            tanggal = obj.optString("tanggal"),
            jilid = obj.optInt("jilid", 1),
            halamanAwal = obj.optInt("halamanAwal", 1),
            halamanAkhir = obj.optInt("halamanAkhir", 1),
            kelancaran = obj.optString("kelancaran"),
            makhrajNilai = obj.optString("makhrajNilai"),
            statusKenaikan = obj.optString("statusKenaikan"),
            catatan = obj.optString("catatan"),
            pembimbingId = if (obj.isNull("pembimbingId")) null else obj.optLong("pembimbingId")
        )
    }

    // --- Perbaikan Bacaan Tahsin ---
    fun perbaikanBacaanToJson(p: PerbaikanBacaanEntity): JSONObject {
        return JSONObject().apply {
            put("id", p.id)
            put("santriId", p.santriId)
            put("tanggal", p.tanggal)
            put("surah", p.surah)
            put("ayatAwal", p.ayatAwal)
            put("ayatAkhir", p.ayatAkhir)
            put("fokusTajwid", p.fokusTajwid)
            put("catatanKekurangan", p.catatanKekurangan)
            put("statusPenguasaan", p.statusPenguasaan)
            put("pembimbingId", p.pembimbingId ?: JSONObject.NULL)
        }
    }

    fun jsonToPerbaikanBacaan(obj: JSONObject): PerbaikanBacaanEntity {
        return PerbaikanBacaanEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            tanggal = obj.optString("tanggal"),
            surah = obj.optString("surah"),
            ayatAwal = obj.optInt("ayatAwal"),
            ayatAkhir = obj.optInt("ayatAkhir"),
            fokusTajwid = obj.optString("fokusTajwid"),
            catatanKekurangan = obj.optString("catatanKekurangan"),
            statusPenguasaan = obj.optString("statusPenguasaan"),
            pembimbingId = if (obj.isNull("pembimbingId")) null else obj.optLong("pembimbingId")
        )
    }

    // --- Setoran Hafalan ---
    fun setoranToJson(s: SetoranHafalanEntity): JSONObject {
        return JSONObject().apply {
            put("id", s.id)
            put("santriId", s.santriId)
            put("tanggal", s.tanggal)
            put("surah", s.surah)
            put("juz", s.juz)
            put("ayatAwal", s.ayatAwal)
            put("ayatAkhir", s.ayatAkhir)
            put("totalAyat", s.totalAyat)
            put("predikat", s.predikat)
            put("nilaiKelancaran", s.nilaiKelancaran)
            put("nilaiTajwid", s.nilaiTajwid)
            put("statusKelulusan", s.statusKelulusan)
            put("catatan", s.catatan)
            put("pembimbingId", s.pembimbingId ?: JSONObject.NULL)
        }
    }

    fun jsonToSetoran(obj: JSONObject): SetoranHafalanEntity {
        return SetoranHafalanEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            tanggal = obj.optString("tanggal"),
            surah = obj.optString("surah"),
            juz = obj.optInt("juz", 30),
            ayatAwal = obj.optInt("ayatAwal", 1),
            ayatAkhir = obj.optInt("ayatAkhir", 1),
            totalAyat = obj.optInt("totalAyat", 1),
            predikat = obj.optString("predikat"),
            nilaiKelancaran = obj.optInt("nilaiKelancaran", 90),
            nilaiTajwid = obj.optInt("nilaiTajwid", 90),
            statusKelulusan = obj.optString("statusKelulusan", "Lulus"),
            catatan = obj.optString("catatan"),
            pembimbingId = if (obj.isNull("pembimbingId")) null else obj.optLong("pembimbingId")
        )
    }

    // --- Murajaah ---
    fun murajaahToJson(m: MurajaahEntity): JSONObject {
        return JSONObject().apply {
            put("id", m.id)
            put("santriId", m.santriId)
            put("tanggal", m.tanggal)
            put("juz", m.juz)
            put("surahAwal", m.surahAwal)
            put("surahAkhir", m.surahAkhir)
            put("totalHalaman", m.totalHalaman)
            put("kelancaran", m.kelancaran)
            put("kemutqinan", m.kemutqinan)
            put("catatan", m.catatan)
            put("pembimbingId", m.pembimbingId ?: JSONObject.NULL)
        }
    }

    fun jsonToMurajaah(obj: JSONObject): MurajaahEntity {
        return MurajaahEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            tanggal = obj.optString("tanggal"),
            juz = obj.optInt("juz", 30),
            surahAwal = obj.optString("surahAwal"),
            surahAkhir = obj.optString("surahAkhir"),
            totalHalaman = obj.optDouble("totalHalaman", 1.0),
            kelancaran = obj.optString("kelancaran"),
            kemutqinan = obj.optString("kemutqinan"),
            catatan = obj.optString("catatan"),
            pembimbingId = if (obj.isNull("pembimbingId")) null else obj.optLong("pembimbingId")
        )
    }

    // --- Penilaian Santri ---
    fun penilaianToJson(p: PenilaianSantriEntity): JSONObject {
        return JSONObject().apply {
            put("id", p.id)
            put("santriId", p.santriId)
            put("tanggal", p.tanggal)
            put("jenisUjian", p.jenisUjian)
            put("materiUjian", p.materiUjian)
            put("nilaiKelancaran", p.nilaiKelancaran)
            put("nilaiTajwid", p.nilaiTajwid)
            put("nilaiFashahah", p.nilaiFashahah)
            put("nilaiAdab", p.nilaiAdab)
            put("totalNilaiRataRata", p.totalNilaiRataRata)
            put("predikatAkhir", p.predikatAkhir)
            put("namaPenguji", p.namaPenguji)
            put("rekomendasi", p.rekomendasi)
            put("sertifikatDiterbitkan", p.sertifikatDiterbitkan)
        }
    }

    fun jsonToPenilaian(obj: JSONObject): PenilaianSantriEntity {
        return PenilaianSantriEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            tanggal = obj.optString("tanggal"),
            jenisUjian = obj.optString("jenisUjian"),
            materiUjian = obj.optString("materiUjian"),
            nilaiKelancaran = obj.optInt("nilaiKelancaran"),
            nilaiTajwid = obj.optInt("nilaiTajwid"),
            nilaiFashahah = obj.optInt("nilaiFashahah"),
            nilaiAdab = obj.optInt("nilaiAdab"),
            totalNilaiRataRata = obj.optDouble("totalNilaiRataRata"),
            predikatAkhir = obj.optString("predikatAkhir"),
            namaPenguji = obj.optString("namaPenguji"),
            rekomendasi = obj.optString("rekomendasi"),
            sertifikatDiterbitkan = obj.optBoolean("sertifikatDiterbitkan", false)
        )
    }

    // --- Target Pembelajaran ---
    fun targetToJson(t: TargetPembelajaranEntity): JSONObject {
        return JSONObject().apply {
            put("id", t.id)
            put("santriId", t.santriId)
            put("tahunAjaran", t.tahunAjaran)
            put("targetJenis", t.targetJenis)
            put("deskripsiTarget", t.deskripsiTarget)
            put("targetKuantitas", t.targetKuantitas)
            put("batasWaktu", t.batasWaktu)
            put("capaianPersen", t.capaianPersen)
            put("status", t.status)
            put("catatan", t.catatan)
        }
    }

    fun jsonToTarget(obj: JSONObject): TargetPembelajaranEntity {
        return TargetPembelajaranEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            tahunAjaran = obj.optString("tahunAjaran"),
            targetJenis = obj.optString("targetJenis"),
            deskripsiTarget = obj.optString("deskripsiTarget"),
            targetKuantitas = obj.optString("targetKuantitas"),
            batasWaktu = obj.optString("batasWaktu"),
            capaianPersen = obj.optInt("capaianPersen"),
            status = obj.optString("status"),
            catatan = obj.optString("catatan")
        )
    }

    // --- Evaluasi Bulanan ---
    fun evaluasiBulananToJson(e: EvaluasiBulananEntity): JSONObject {
        return JSONObject().apply {
            put("id", e.id)
            put("santriId", e.santriId)
            put("bulanTahun", e.bulanTahun)
            put("totalSetoranAyat", e.totalSetoranAyat)
            put("totalJuzMurajaah", e.totalJuzMurajaah)
            put("tingkatKelancaranIqro", e.tingkatKelancaranIqro)
            put("persentaseKehadiran", e.persentaseKehadiran)
            put("predikatBulanan", e.predikatBulanan)
            put("catatanEvaluasi", e.catatanEvaluasi)
            put("rekomendasiPembimbing", e.rekomendasiPembimbing)
            put("evaluasiOleh", e.evaluasiOleh)
        }
    }

    fun jsonToEvaluasiBulanan(obj: JSONObject): EvaluasiBulananEntity {
        return EvaluasiBulananEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            bulanTahun = obj.optString("bulanTahun"),
            totalSetoranAyat = obj.optInt("totalSetoranAyat"),
            totalJuzMurajaah = obj.optDouble("totalJuzMurajaah"),
            tingkatKelancaranIqro = obj.optString("tingkatKelancaranIqro"),
            persentaseKehadiran = obj.optInt("persentaseKehadiran"),
            predikatBulanan = obj.optString("predikatBulanan"),
            catatanEvaluasi = obj.optString("catatanEvaluasi"),
            rekomendasiPembimbing = obj.optString("rekomendasiPembimbing"),
            evaluasiOleh = obj.optString("evaluasiOleh")
        )
    }

    // --- Kehadiran Kegiatan ---
    fun kehadiranToJson(k: KehadiranKegiatanEntity): JSONObject {
        return JSONObject().apply {
            put("id", k.id)
            put("santriId", k.santriId)
            put("kelompokId", k.kelompokId ?: JSONObject.NULL)
            put("tanggal", k.tanggal)
            put("jenisKegiatan", k.jenisKegiatan)
            put("statusKehadiran", k.statusKehadiran)
            put("keterangan", k.keterangan)
        }
    }

    fun jsonToKehadiran(obj: JSONObject): KehadiranKegiatanEntity {
        return KehadiranKegiatanEntity(
            id = obj.optLong("id", 0),
            santriId = obj.optLong("santriId"),
            kelompokId = if (obj.isNull("kelompokId")) null else obj.optLong("kelompokId"),
            tanggal = obj.optString("tanggal"),
            jenisKegiatan = obj.optString("jenisKegiatan"),
            statusKehadiran = obj.optString("statusKehadiran"),
            keterangan = obj.optString("keterangan")
        )
    }

    // --- Evaluasi Program ---
    fun evaluasiProgramToJson(e: CatatanEvaluasiProgramEntity): JSONObject {
        return JSONObject().apply {
            put("id", e.id)
            put("tahunAjaran", e.tahunAjaran)
            put("semester", e.semester)
            put("tanggal", e.tanggal)
            put("judul", e.judul)
            put("ringkasanCapaian", e.ringkasanCapaian)
            put("kendalaTantangan", e.kendalaTantangan)
            put("rekomendasiTindakLanjut", e.rekomendasiTindakLanjut)
            put("evaluator", e.evaluator)
        }
    }

    fun jsonToEvaluasiProgram(obj: JSONObject): CatatanEvaluasiProgramEntity {
        return CatatanEvaluasiProgramEntity(
            id = obj.optLong("id", 0),
            tahunAjaran = obj.optString("tahunAjaran"),
            semester = obj.optString("semester"),
            tanggal = obj.optString("tanggal"),
            judul = obj.optString("judul"),
            ringkasanCapaian = obj.optString("ringkasanCapaian"),
            kendalaTantangan = obj.optString("kendalaTantangan"),
            rekomendasiTindakLanjut = obj.optString("rekomendasiTindakLanjut"),
            evaluator = obj.optString("evaluator")
        )
    }

    // --- Pengaturan Sistem ---
    fun pengaturanToJson(p: PengaturanSistemEntity): JSONObject {
        return JSONObject().apply {
            put("kunci", p.kunci)
            put("nilai", p.nilai)
            put("deskripsi", p.deskripsi)
            put("tanggalPerubahan", p.tanggalPerubahan)
        }
    }

    fun jsonToPengaturan(obj: JSONObject): PengaturanSistemEntity {
        return PengaturanSistemEntity(
            kunci = obj.optString("kunci"),
            nilai = obj.optString("nilai"),
            deskripsi = obj.optString("deskripsi"),
            tanggalPerubahan = obj.optString("tanggalPerubahan")
        )
    }

    // Full database serializer
    fun fullBackupToJson(backup: FullDatabaseBackup): String {
        val root = JSONObject()
        val manifestObj = JSONObject().apply {
            put("appName", backup.manifest.appName)
            put("pesantren", backup.manifest.pesantren)
            put("backupVersion", backup.manifest.backupVersion)
            put("timestamp", backup.manifest.timestamp)
            put("jenisBackup", backup.manifest.jenisBackup)
            put("filterPeriode", backup.manifest.filterPeriode)
            put("totalRecords", backup.manifest.totalRecords)
            put("securityNotice", backup.manifest.securityNotice)
            val counts = JSONObject()
            backup.manifest.entityCounts.forEach { (k, v) -> counts.put(k, v) }
            put("entityCounts", counts)
        }
        root.put("manifest", manifestObj)

        fun <T> listToJsonArray(list: List<T>, converter: (T) -> JSONObject): JSONArray {
            val arr = JSONArray()
            list.forEach { arr.put(converter(it)) }
            return arr
        }

        root.put("santri", listToJsonArray(backup.santriList, ::santriToJson))
        root.put("pembimbing", listToJsonArray(backup.pembimbingList, ::pembimbingToJson))
        root.put("kelompok", listToJsonArray(backup.kelompokList, ::kelompokToJson))
        root.put("tahunAjaran", listToJsonArray(backup.tahunAjaranList, ::tahunAjaranToJson))
        root.put("riwayatPerpindahanKelompok", listToJsonArray(backup.riwayatPerpindahanKelompokList, ::riwayatPindahToJson))
        root.put("riwayatPergantianPembimbing", listToJsonArray(backup.riwayatPergantianPembimbingList, ::riwayatGantiPembimbingToJson))
        root.put("catatanPerkembanganHarian", listToJsonArray(backup.catatanPerkembanganHarianList, ::catatanHarianToJson))
        root.put("perkembanganIqro", listToJsonArray(backup.perkembanganIqroList, ::iqroToJson))
        root.put("perbaikanBacaan", listToJsonArray(backup.perbaikanBacaanList, ::perbaikanBacaanToJson))
        root.put("setoranHafalan", listToJsonArray(backup.setoranHafalanList, ::setoranToJson))
        root.put("murajaah", listToJsonArray(backup.murajaahList, ::murajaahToJson))
        root.put("penilaianSantri", listToJsonArray(backup.penilaianSantriList, ::penilaianToJson))
        root.put("targetPembelajaran", listToJsonArray(backup.targetPembelajaranList, ::targetToJson))
        root.put("evaluasiBulanan", listToJsonArray(backup.evaluasiBulananList, ::evaluasiBulananToJson))
        root.put("kehadiranKegiatan", listToJsonArray(backup.kehadiranKegiatanList, ::kehadiranToJson))
        root.put("catatanEvaluasiProgram", listToJsonArray(backup.catatanEvaluasiProgramList, ::evaluasiProgramToJson))
        root.put("pengaturanSistem", listToJsonArray(backup.pengaturanSistemList, ::pengaturanToJson))

        return root.toString(2)
    }

    fun parseFullBackupJson(jsonString: String): FullDatabaseBackup {
        val root = JSONObject(jsonString)
        val manifestObj = root.optJSONObject("manifest") ?: JSONObject()
        val countsMap = mutableMapOf<String, Int>()
        val countsJson = manifestObj.optJSONObject("entityCounts")
        countsJson?.keys()?.forEach { k ->
            countsMap[k] = countsJson.optInt(k)
        }

        val manifest = BackupManifest(
            appName = manifestObj.optString("appName", "MUTQIN ULUL ALBAB"),
            pesantren = manifestObj.optString("pesantren", "Pondok Pesantren Ulul Albab Lubuklinggau"),
            backupVersion = manifestObj.optString("backupVersion", "1.0"),
            timestamp = manifestObj.optString("timestamp", ""),
            jenisBackup = manifestObj.optString("jenisBackup", "ZIP Lengkap"),
            filterPeriode = manifestObj.optString("filterPeriode", "Seluruh Data"),
            entityCounts = countsMap,
            totalRecords = manifestObj.optInt("totalRecords", 0)
        )

        fun <T> parseArray(key: String, parser: (JSONObject) -> T): List<T> {
            val arr = root.optJSONArray(key) ?: return emptyList()
            val list = mutableListOf<T>()
            for (i in 0 until arr.length()) {
                val item = arr.optJSONObject(i)
                if (item != null) list.add(parser(item))
            }
            return list
        }

        return FullDatabaseBackup(
            manifest = manifest,
            santriList = parseArray("santri", ::jsonToSantri),
            pembimbingList = parseArray("pembimbing", ::jsonToPembimbing),
            kelompokList = parseArray("kelompok", ::jsonToKelompok),
            tahunAjaranList = parseArray("tahunAjaran", ::jsonToTahunAjaran),
            riwayatPerpindahanKelompokList = parseArray("riwayatPerpindahanKelompok", ::jsonToRiwayatPindah),
            riwayatPergantianPembimbingList = parseArray("riwayatPergantianPembimbing", ::jsonToRiwayatGantiPembimbing),
            catatanPerkembanganHarianList = parseArray("catatanPerkembanganHarian", ::jsonToCatatanHarian),
            perkembanganIqroList = parseArray("perkembanganIqro", ::jsonToIqro),
            perbaikanBacaanList = parseArray("perbaikanBacaan", ::jsonToPerbaikanBacaan),
            setoranHafalanList = parseArray("setoranHafalan", ::jsonToSetoran),
            murajaahList = parseArray("murajaah", ::jsonToMurajaah),
            penilaianSantriList = parseArray("penilaianSantri", ::jsonToPenilaian),
            targetPembelajaranList = parseArray("targetPembelajaran", ::jsonToTarget),
            evaluasiBulananList = parseArray("evaluasiBulanan", ::jsonToEvaluasiBulanan),
            kehadiranKegiatanList = parseArray("kehadiranKegiatan", ::jsonToKehadiran),
            catatanEvaluasiProgramList = parseArray("catatanEvaluasiProgram", ::jsonToEvaluasiProgram),
            pengaturanSistemList = parseArray("pengaturanSistem", ::jsonToPengaturan)
        )
    }
}
