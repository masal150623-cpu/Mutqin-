package com.example.data.local

import com.example.data.model.CatatanEvaluasiProgramEntity
import com.example.data.model.CatatanPerkembanganHarianEntity
import com.example.data.model.EvaluasiBulananEntity
import com.example.data.model.KehadiranKegiatanEntity
import com.example.data.model.KelompokEntity
import com.example.data.model.MurajaahEntity
import com.example.data.model.PembimbingEntity
import com.example.data.model.PengaturanSistemEntity
import com.example.data.model.PenilaianSantriEntity
import com.example.data.model.PerbaikanBacaanEntity
import com.example.data.model.PerkembanganIqroEntity
import com.example.data.model.RiwayatPergantianPembimbingEntity
import com.example.data.model.RiwayatPerpindahanKelompokEntity
import com.example.data.model.SantriEntity
import com.example.data.model.SetoranHafalanEntity
import com.example.data.model.TahunAjaranEntity
import com.example.data.model.TargetPembelajaranEntity

object DatabaseSeeder {

    suspend fun seedInitialData(database: AppDatabase) {
        val akademikDao = database.akademikDao()
        val santriDao = database.santriDao()
        val perkembanganDao = database.perkembanganDao()
        val evaluasiDao = database.evaluasiDao()
        val backupDao = database.backupDao()

        // 1. Tahun Ajaran
        val tahunAjaranList = listOf(
            TahunAjaranEntity(
                id = 1,
                kode = "2025-2026-GANJIL",
                tahun = "2025/2026",
                semester = "Ganjil",
                tanggalMulai = "2025-07-15",
                tanggalSelesai = "2025-12-20",
                isAktif = true,
                catatan = "Semester Ganjil TP 2025/2026"
            ),
            TahunAjaranEntity(
                id = 2,
                kode = "2025-2026-GENAP",
                tahun = "2025/2026",
                semester = "Genap",
                tanggalMulai = "2026-01-05",
                tanggalSelesai = "2026-06-25",
                isAktif = false,
                catatan = "Persiapan Semester Genap"
            )
        )
        akademikDao.insertTahunAjaranList(tahunAjaranList)

        // 2. Pembimbing (Ustaz & Ustazah)
        val pembimbingList = listOf(
            PembimbingEntity(
                id = 1,
                nip = "UA-001",
                nama = "Ustaz H. Muhammad Fatih",
                gelar = "Al-Hafidz, Lc.",
                jenisKelamin = "Laki-laki",
                peran = "Kepala Program",
                kontak = "0812-7890-1122",
                spesialisasi = "Sanad Al-Qur'an & Qiraat",
                statusAktif = true
            ),
            PembimbingEntity(
                id = 2,
                nip = "UA-002",
                nama = "Ustaz Ahmad Fauzan",
                gelar = "Al-Muqri, S.Pd.I",
                jenisKelamin = "Laki-laki",
                peran = "Ustaz Pembimbing",
                kontak = "0813-6789-3344",
                spesialisasi = "Tahfidz & Tajwid Tuhfatul Athfal",
                statusAktif = true
            ),
            PembimbingEntity(
                id = 3,
                nip = "UA-003",
                nama = "Ustazah Nur Hasanah",
                gelar = "S.Ag., Al-Hafidzah",
                jenisKelamin = "Perempuan",
                peran = "Ustazah Pembimbing",
                kontak = "0821-8899-5566",
                spesialisasi = "Tahfidz Putri & Fashahah",
                statusAktif = true
            ),
            PembimbingEntity(
                id = 4,
                nip = "UA-004",
                nama = "Ustazah Siti Maryam",
                gelar = "S.Pd.I",
                jenisKelamin = "Perempuan",
                peran = "Ustazah Pembimbing",
                kontak = "0852-7711-2233",
                spesialisasi = "Iqro & Tahsin Dasar Anak",
                statusAktif = true
            )
        )
        akademikDao.insertPembimbingList(pembimbingList)

        // 3. Kelompok Pembelajaran / Halaqah
        val kelompokList = listOf(
            KelompokEntity(
                id = 1,
                namaKelompok = "Halaqah Zaid bin Tsabit",
                program = "Tahfidz Khusus",
                pembimbingId = 2,
                tahunAjaran = "2025/2026",
                ruangan = "Serambi Masjid Ulul Albab (Utara)",
                jadwalSesi = "Ba'da Subuh & Ba'da Ashar",
                targetHafalan = "Juz 30, Juz 29, Juz 28",
                statusAktif = true
            ),
            KelompokEntity(
                id = 2,
                namaKelompok = "Halaqah Utsman bin Affan",
                program = "Tahfidz Khusus",
                pembimbingId = 1,
                tahunAjaran = "2025/2026",
                ruangan = "Ruang Tahfidz Eksekutif",
                jadwalSesi = "Ba'da Subuh & Ba'da Maghrib",
                targetHafalan = "Juz 1 s.d. Juz 5 Mutqin",
                statusAktif = true
            ),
            KelompokEntity(
                id = 3,
                namaKelompok = "Halaqah Aisyah binti Abi Bakar",
                program = "Tahfidz Khusus",
                pembimbingId = 3,
                tahunAjaran = "2025/2026",
                ruangan = "Gedung Asrama Putri Lt. 2",
                jadwalSesi = "Ba'da Subuh & Ba'da Ashar",
                targetHafalan = "Juz 30 & Juz 1",
                statusAktif = true
            ),
            KelompokEntity(
                id = 4,
                namaKelompok = "Kelas Iqro & Tahsin TPA Maryam",
                program = "TPA",
                pembimbingId = 4,
                tahunAjaran = "2025/2026",
                ruangan = "Aula TPA Ulul Albab",
                jadwalSesi = "Sore (16.00 - 17.30 WIB)",
                targetHafalan = "Iqro Jilid 1-6 & Juz 'Amma Pendek",
                statusAktif = true
            )
        )
        akademikDao.insertKelompokList(kelompokList)

        // 4. Data Santri (Aktif, Alumni, Keluar)
        val santriList = listOf(
            SantriEntity(
                id = 1,
                nis = "UA2025001",
                namaLengkap = "Muhammad Al-Fatih Pratama",
                namaPanggilan = "Fatih",
                jenisKelamin = "Laki-laki",
                tempatLahir = "Lubuklinggau",
                tanggalLahir = "2013-05-12",
                program = "Tahfidz Khusus",
                kelompokId = 1,
                tahunAjaranMasuk = "2025/2026",
                status = "Aktif",
                tanggalMasuk = "2025-07-15",
                namaWali = "H. Hendra Pratama",
                kontakWali = "0812-7100-2233",
                alamat = "Jl. Yos Sudarso No. 45, Lubuklinggau Timur",
                catatanKhusus = "Semangat ziyadah tinggi, tajwid rapi",
                targetJuzHafalan = 30
            ),
            SantriEntity(
                id = 2,
                nis = "UA2025002",
                namaLengkap = "Abdullah Azzam Lubis",
                namaPanggilan = "Azzam",
                jenisKelamin = "Laki-laki",
                tempatLahir = "Lubuklinggau",
                tanggalLahir = "2012-08-20",
                program = "Tahfidz Khusus",
                kelompokId = 2,
                tahunAjaranMasuk = "2025/2026",
                status = "Aktif",
                tanggalMasuk = "2025-07-15",
                namaWali = "Drs. M. Lubis",
                kontakWali = "0813-7311-4455",
                alamat = "Kecamatan Lubuklinggau Barat I",
                catatanKhusus = "Hafalan mutqin Juz 30 dan Juz 29",
                targetJuzHafalan = 30
            ),
            SantriEntity(
                id = 3,
                nis = "UA2025003",
                namaLengkap = "Fathimah Az-Zahra",
                namaPanggilan = "Zahra",
                jenisKelamin = "Perempuan",
                tempatLahir = "Musirawas",
                tanggalLahir = "2014-01-10",
                program = "Tahfidz Khusus",
                kelompokId = 3,
                tahunAjaranMasuk = "2025/2026",
                status = "Aktif",
                tanggalMasuk = "2025-07-15",
                namaWali = "Ahmad Syukri, S.Pd",
                kontakWali = "0821-7788-9900",
                alamat = "Taba Jemekeh, Lubuklinggau Timur II",
                catatanKhusus = "Suara merdu, makhraj murni",
                targetJuzHafalan = 30
            ),
            SantriEntity(
                id = 4,
                nis = "UA2025004",
                namaLengkap = "Rayhan Al-Ghifari",
                namaPanggilan = "Rayhan",
                jenisKelamin = "Laki-laki",
                tempatLahir = "Lubuklinggau",
                tanggalLahir = "2015-09-03",
                program = "TPA",
                kelompokId = 4,
                tahunAjaranMasuk = "2025/2026",
                status = "Aktif",
                tanggalMasuk = "2025-07-20",
                namaWali = "Bambang Irawan",
                kontakWali = "0853-6622-1100",
                alamat = "Kel. Cereme Taba, Lubuklinggau Timur II",
                catatanKhusus = "Sedang di Iqro Jilid 4",
                targetJuzHafalan = 3
            ),
            SantriEntity(
                id = 5,
                nis = "UA2025005",
                namaLengkap = "Khadijah Nayla Putri",
                namaPanggilan = "Nayla",
                jenisKelamin = "Perempuan",
                tempatLahir = "Lubuklinggau",
                tanggalLahir = "2015-11-18",
                program = "TPA",
                kelompokId = 4,
                tahunAjaranMasuk = "2025/2026",
                status = "Aktif",
                tanggalMasuk = "2025-07-20",
                namaWali = "Zulkifli",
                kontakWali = "0822-8119-9002",
                alamat = "Jl. Garuda, Lubuklinggau Barat II",
                catatanKhusus = "Iqro Jilid 5 halaman 12",
                targetJuzHafalan = 3
            ),
            SantriEntity(
                id = 6,
                nis = "UA2024090",
                namaLengkap = "Ibrahim Al-Mansyur",
                namaPanggilan = "Ibrahim",
                jenisKelamin = "Laki-laki",
                tempatLahir = "Palembang",
                tanggalLahir = "2011-03-14",
                program = "Tahfidz Khusus",
                kelompokId = 2,
                tahunAjaranMasuk = "2024/2025",
                status = "Lulus",
                tanggalMasuk = "2024-07-10",
                tanggalKeluar = "2025-06-25",
                alasanKeluar = "Telah menyelesaikan Tashih 5 Juz Mutqin dan melanjutkan ke MA",
                namaWali = "Mansyur Hidayat",
                kontakWali = "0812-7300-8811",
                alamat = "Lubuklinggau Selatan",
                catatanKhusus = "Alumni berprestasi Tashih Mumtaz",
                targetJuzHafalan = 5
            ),
            SantriEntity(
                id = 7,
                nis = "UA2024095",
                namaLengkap = "Danish Farhan Nugraha",
                namaPanggilan = "Farhan",
                jenisKelamin = "Laki-laki",
                tempatLahir = "Lubuklinggau",
                tanggalLahir = "2013-12-05",
                program = "Tahfidz Khusus",
                kelompokId = 1,
                tahunAjaranMasuk = "2024/2025",
                status = "Pindah",
                tanggalMasuk = "2024-07-10",
                tanggalKeluar = "2025-08-30",
                alasanKeluar = "Pindah domisili orang tua ke Kota Bengkulu",
                namaWali = "Nugraha Kusuma",
                kontakWali = "0813-6800-4422",
                alamat = "Pindah ke Bengkulu",
                catatanKhusus = "Catatan tersimpan lengkap untuk arsip mutasi",
                targetJuzHafalan = 10
            )
        )
        santriDao.insertSantriList(santriList)

        // 5. Riwayat Perpindahan Kelompok
        val riwayatPindah = listOf(
            RiwayatPerpindahanKelompokEntity(
                id = 1,
                santriId = 2,
                kelompokLamaId = 1,
                kelompokBaruId = 2,
                tanggalPindah = "2025-08-01",
                alasanPindah = "Akselerasi tahfidz ke kelompok intensif Utsman bin Affan",
                dicatatOleh = "Ustaz H. Muhammad Fatih"
            )
        )
        santriDao.insertRiwayatPindahList(riwayatPindah)

        // 6. Riwayat Pergantian Pembimbing
        val riwayatGantiPembimbing = listOf(
            RiwayatPergantianPembimbingEntity(
                id = 1,
                kelompokId = 1,
                pembimbingLamaId = 1,
                pembimbingBaruId = 2,
                tanggalGanti = "2025-07-25",
                alasan = "Pemerataan tugas halaqah tahfidz",
                dicatatOleh = "Kepala Program"
            )
        )
        akademikDao.insertRiwayatGantiPembimbingList(riwayatGantiPembimbing)

        // 7. Catatan Perkembangan Harian
        val catatanHarian = listOf(
            CatatanPerkembanganHarianEntity(
                id = 1,
                santriId = 1,
                tanggal = "2026-10-10",
                sesi = "Pagi (Subuh)",
                adabKedisiplinan = "Sangat Baik",
                fokusBelajar = "Ziyadah Surah An-Naba 1-20",
                catatanUmum = "Disiplin hadir tepat waktu di barisan depan halaqah",
                pembimbingId = 2
            ),
            CatatanPerkembanganHarianEntity(
                id = 2,
                santriId = 2,
                tanggal = "2026-10-10",
                sesi = "Malam (Isya)",
                adabKedisiplinan = "Sangat Baik",
                fokusBelajar = "Murajaah Juz 29 tuntas 5 halaman",
                catatanUmum = "Fokus tinggi dan mandiri dalam pengulangan",
                pembimbingId = 1
            ),
            CatatanPerkembanganHarianEntity(
                id = 3,
                santriId = 3,
                tanggal = "2026-10-11",
                sesi = "Sore (Ashar)",
                adabKedisiplinan = "Baik",
                fokusBelajar = "Tahsin Al-Infitar & Al-Muthaffifin",
                catatanUmum = "Suara jelas, intonasi fasih",
                pembimbingId = 3
            )
        )
        perkembanganDao.insertCatatanHarianList(catatanHarian)

        // 8. Perkembangan Iqro
        val iqroList = listOf(
            PerkembanganIqroEntity(
                id = 1,
                santriId = 4,
                tanggal = "2026-10-08",
                jilid = 4,
                halamanAwal = 10,
                halamanAkhir = 12,
                kelancaran = "Lancar (A)",
                makhrajNilai = "Baik",
                statusKenaikan = "Lanjut Halaman",
                catatan = "Mengenal bunyi sukun dan tasydid dengan baik",
                pembimbingId = 4
            ),
            PerkembanganIqroEntity(
                id = 2,
                santriId = 5,
                tanggal = "2026-10-09",
                jilid = 5,
                halamanAwal = 12,
                halamanAkhir = 15,
                kelancaran = "Lancar (A)",
                makhrajNilai = "Sangat Baik",
                statusKenaikan = "Lanjut Halaman",
                catatan = "Panjang pendek 2 harakat (Mad Thabi'i) sudah konsisten",
                pembimbingId = 4
            )
        )
        perkembanganDao.insertIqroList(iqroList)

        // 9. Perbaikan Bacaan / Tahsin
        val perbaikanBacaanList = listOf(
            PerbaikanBacaanEntity(
                id = 1,
                santriId = 1,
                tanggal = "2026-10-05",
                surah = "An-Naba",
                ayatAwal = 1,
                ayatAkhir = 16,
                fokusTajwid = "Ghunnah & Idgham Bighunnah",
                catatanKekurangan = "Tahan ghunnah selama 2 ketukan pada huruf mim bertasydid",
                statusPenguasaan = "Sudah Baik",
                pembimbingId = 2
            ),
            PerbaikanBacaanEntity(
                id = 2,
                santriId = 3,
                tanggal = "2026-10-07",
                surah = "At-Takwir",
                ayatAwal = 1,
                ayatAkhir = 14,
                fokusTajwid = "Makharijul Huruf (Dhad & Zha)",
                catatanKekurangan = "Perjelas sisi lidah saat mengucapkan huruf Dhad",
                statusPenguasaan = "Mutqin",
                pembimbingId = 3
            )
        )
        perkembanganDao.insertPerbaikanBacaanList(perbaikanBacaanList)

        // 10. Setoran Hafalan Baru (Ziyadah)
        val setoranList = listOf(
            SetoranHafalanEntity(
                id = 1,
                santriId = 1,
                tanggal = "2026-10-09",
                surah = "An-Naba",
                juz = 30,
                ayatAwal = 1,
                ayatAkhir = 20,
                totalAyat = 20,
                predikat = "Mutqin (A)",
                nilaiKelancaran = 95,
                nilaiTajwid = 92,
                statusKelulusan = "Lulus",
                catatan = "Hafalan lancar tanpa ragu, intonasi tartil",
                pembimbingId = 2
            ),
            SetoranHafalanEntity(
                id = 2,
                santriId = 1,
                tanggal = "2026-10-11",
                surah = "An-Naba",
                juz = 30,
                ayatAwal = 21,
                ayatAkhir = 40,
                totalAyat = 20,
                predikat = "Mutqin (A)",
                nilaiKelancaran = 94,
                nilaiTajwid = 90,
                statusKelulusan = "Lulus",
                catatan = "Tuntas Surah An-Naba, lanjut An-Nazi'at",
                pembimbingId = 2
            ),
            SetoranHafalanEntity(
                id = 3,
                santriId = 2,
                tanggal = "2026-10-10",
                surah = "Al-Mulk",
                juz = 29,
                ayatAwal = 1,
                ayatAkhir = 30,
                totalAyat = 30,
                predikat = "Mutqin (A)",
                nilaiKelancaran = 98,
                nilaiTajwid = 96,
                statusKelulusan = "Lulus",
                catatan = "Satu surah utuh satu majelis tanpa kesalahan",
                pembimbingId = 1
            ),
            SetoranHafalanEntity(
                id = 4,
                santriId = 3,
                tanggal = "2026-10-11",
                surah = "Al-Infitar",
                juz = 30,
                ayatAwal = 1,
                ayatAkhir = 19,
                totalAyat = 19,
                predikat = "Jayyid Jiddan (B+)",
                nilaiKelancaran = 88,
                nilaiTajwid = 88,
                statusKelulusan = "Lulus",
                catatan = "Ada sedikit pengulangan di ayat 10",
                pembimbingId = 3
            )
        )
        perkembanganDao.insertSetoranList(setoranList)

        // 11. Murajaah
        val murajaahList = listOf(
            MurajaahEntity(
                id = 1,
                santriId = 1,
                tanggal = "2026-10-10",
                juz = 30,
                surahAwal = "An-Nas",
                surahAkhir = "Ad-Duha",
                totalHalaman = 4.0,
                kelancaran = "Sangat Lancar",
                kemutqinan = "Mutqin",
                catatan = "Pengulangan surah-surah pendek tuntas dengan baik",
                pembimbingId = 2
            ),
            MurajaahEntity(
                id = 2,
                santriId = 2,
                tanggal = "2026-10-11",
                juz = 30,
                surahAwal = "An-Naba",
                surahAkhir = "An-Nas",
                totalHalaman = 20.0,
                kelancaran = "Sangat Lancar",
                kemutqinan = "Mutqin",
                catatan = "Murajaah Akbar 1 Juz dalam satu duduk",
                pembimbingId = 1
            ),
            MurajaahEntity(
                id = 3,
                santriId = 3,
                tanggal = "2026-10-11",
                juz = 30,
                surahAwal = "Al-Fajr",
                surahAkhir = "An-Nas",
                totalHalaman = 8.0,
                kelancaran = "Lancar",
                kemutqinan = "Mutqin",
                catatan = "Tajwid terjaga dengan baik",
                pembimbingId = 3
            )
        )
        perkembanganDao.insertMurajaahList(murajaahList)

        // 12. Penilaian Santri (Ujian Tashih)
        val penilaianList = listOf(
            PenilaianSantriEntity(
                id = 1,
                santriId = 2,
                tanggal = "2026-09-28",
                jenisUjian = "Tashih Juz",
                materiUjian = "Juz 30 (Al-Naba s.d. An-Nas)",
                nilaiKelancaran = 98,
                nilaiTajwid = 95,
                nilaiFashahah = 96,
                nilaiAdab = 98,
                totalNilaiRataRata = 96.75,
                predikatAkhir = "Mumtaz (Istimewa)",
                namaPenguji = "Ustaz H. Muhammad Fatih, Al-Hafidz",
                rekomendasi = "Tuntas Tashih Juz 30 Bersanad, Berhak lanjut Tashih Juz 29",
                sertifikatDiterbitkan = true
            )
        )
        evaluasiDao.insertPenilaianList(penilaianList)

        // 13. Target Pembelajaran
        val targetList = listOf(
            TargetPembelajaranEntity(
                id = 1,
                santriId = 1,
                tahunAjaran = "2025/2026",
                targetJenis = "Hafalan Baru",
                deskripsiTarget = "Tuntas Hafalan Juz 30 Mutqin",
                targetKuantitas = "1 Juz (Juz 30)",
                batasWaktu = "2026-12-31",
                capaianPersen = 80,
                status = "Dalam Proses",
                catatan = "Tersisa surah Al-Muthaffifin dan Al-Inshiqaq"
            ),
            TargetPembelajaranEntity(
                id = 2,
                santriId = 2,
                tahunAjaran = "2025/2026",
                targetJenis = "Hafalan Baru",
                deskripsiTarget = "Tuntas Hafalan Juz 29 Mutqin",
                targetKuantitas = "1 Juz (Juz 29)",
                batasWaktu = "2026-12-15",
                capaianPersen = 90,
                status = "Dalam Proses",
                catatan = "Telah menyelesaikan Al-Mulk, Al-Qalam, Al-Haqqah"
            ),
            TargetPembelajaranEntity(
                id = 3,
                santriId = 4,
                tahunAjaran = "2025/2026",
                targetJenis = "Iqro",
                deskripsiTarget = "Khatam Iqro Jilid 6 & Naik ke Al-Qur'an",
                targetKuantitas = "Jilid 4 s.d. Jilid 6",
                batasWaktu = "2026-12-20",
                capaianPersen = 60,
                status = "Dalam Proses",
                catatan = "Progres stabil 2 halaman per pertemuan"
            )
        )
        evaluasiDao.insertTargetList(targetList)

        // 14. Evaluasi Bulanan
        val evaluasiList = listOf(
            EvaluasiBulananEntity(
                id = 1,
                santriId = 1,
                bulanTahun = "2026-09",
                totalSetoranAyat = 145,
                totalJuzMurajaah = 3.0,
                tingkatKelancaranIqro = "-",
                persentaseKehadiran = 96,
                predikatBulanan = "Mumtaz (A+)",
                catatanEvaluasi = "Pencapaian ziyadah melebihi target bulanan. Tingkatkan frekuensi murajaah di asrama.",
                rekomendasiPembimbing = "Dapat diikutkan dalam Tashih Juz 30 bulan depan.",
                evaluasiOleh = "Ustaz Ahmad Fauzan, S.Pd.I"
            ),
            EvaluasiBulananEntity(
                id = 2,
                santriId = 2,
                bulanTahun = "2026-09",
                totalSetoranAyat = 210,
                totalJuzMurajaah = 6.0,
                tingkatKelancaranIqro = "-",
                persentaseKehadiran = 100,
                predikatBulanan = "Mumtaz (A+)",
                catatanEvaluasi = "Kemutqinan luar biasa, tartil dan adab santun.",
                rekomendasiPembimbing = "Pertahankan dan mulai bimbing santri junior.",
                evaluasiOleh = "Ustaz H. Muhammad Fatih"
            )
        )
        evaluasiDao.insertEvaluasiBulananList(evaluasiList)

        // 15. Kehadiran dan Kegiatan
        val kehadiranList = listOf(
            KehadiranKegiatanEntity(
                id = 1,
                santriId = 1,
                kelompokId = 1,
                tanggal = "2026-10-10",
                jenisKegiatan = "Halaqah Rutin",
                statusKehadiran = "Hadir",
                keterangan = "Hadir tepat waktu"
            ),
            KehadiranKegiatanEntity(
                id = 2,
                santriId = 2,
                kelompokId = 2,
                tanggal = "2026-10-10",
                jenisKegiatan = "Halaqah Rutin",
                statusKehadiran = "Hadir",
                keterangan = "Hadir"
            ),
            KehadiranKegiatanEntity(
                id = 3,
                santriId = 3,
                kelompokId = 3,
                tanggal = "2026-10-10",
                jenisKegiatan = "Halaqah Rutin",
                statusKehadiran = "Hadir",
                keterangan = "Hadir"
            ),
            KehadiranKegiatanEntity(
                id = 4,
                santriId = 4,
                kelompokId = 4,
                tanggal = "2026-10-10",
                jenisKegiatan = "Halaqah Rutin",
                statusKehadiran = "Hadir",
                keterangan = "Hadir TPA"
            )
        )
        evaluasiDao.insertKehadiranList(kehadiranList)

        // 16. Catatan Evaluasi Program Pesantren
        val evaluasiProgramList = listOf(
            CatatanEvaluasiProgramEntity(
                id = 1,
                tahunAjaran = "2025/2026",
                semester = "Ganjil",
                tanggal = "2026-10-01",
                judul = "Evaluasi Triwulan Program Tahfidz Khusus & TPA",
                ringkasanCapaian = "Rata-rata capaian santri mencapai 88% dari target kuartal pertama. Tashih Juz 30 telah diikuti 5 santri dengan predikat Mumtaz.",
                kendalaTantangan = "Beberapa santri baru masih perlu penyesuaian ritme bangun sahur dan tahajud untuk persiapan sesi subuh.",
                rekomendasiTindakLanjut = "Penambahan bimbingan tahsin privat di hari Sabtu serta pengadaan apresiasi mingguan bintang tahfidz.",
                evaluator = "Ustaz H. Muhammad Fatih, Al-Hafidz (Kepala Program)"
            )
        )
        evaluasiDao.insertEvaluasiProgramList(evaluasiProgramList)

        // 17. Pengaturan Sistem
        val pengaturanList = listOf(
            PengaturanSistemEntity(
                kunci = "NAMA_PESANTREN",
                nilai = "Pondok Pesantren Ulul Albab Lubuklinggau",
                deskripsi = "Nama resmi lembaga pesantren",
                tanggalPerubahan = "2026-10-01"
            ),
            PengaturanSistemEntity(
                kunci = "NAMA_PROGRAM",
                nilai = "Program TPA dan Tahfidz Khusus Al-Qur'an",
                deskripsi = "Nama divisi / program tahfidz",
                tanggalPerubahan = "2026-10-01"
            ),
            PengaturanSistemEntity(
                kunci = "PIN_ADMINISTRATOR",
                nilai = "1928",
                deskripsi = "PIN akses menu Backup dan Pemulihan Data",
                tanggalPerubahan = "2026-10-01"
            ),
            PengaturanSistemEntity(
                kunci = "TAHUN_AJARAN_AKTIF",
                nilai = "2025/2026",
                deskripsi = "Tahun ajaran operasional saat ini",
                tanggalPerubahan = "2026-10-01"
            ),
            PengaturanSistemEntity(
                kunci = "LOKASI_PESANTREN",
                nilai = "Kota Lubuklinggau, Sumatera Selatan",
                deskripsi = "Alamat kota pondok pesantren",
                tanggalPerubahan = "2026-10-01"
            )
        )
        backupDao.savePengaturanList(pengaturanList)
    }
}
