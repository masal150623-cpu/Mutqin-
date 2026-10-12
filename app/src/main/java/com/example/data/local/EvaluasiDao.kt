package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CatatanEvaluasiProgramEntity
import com.example.data.model.EvaluasiBulananEntity
import com.example.data.model.KehadiranKegiatanEntity
import com.example.data.model.PenilaianSantriEntity
import com.example.data.model.TargetPembelajaranEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluasiDao {
    // 12. Penilaian Santri (Tashih Juz / Ujian)
    @Query("SELECT * FROM penilaian_santri ORDER BY tanggal DESC")
    fun getAllPenilaian(): Flow<List<PenilaianSantriEntity>>

    @Query("SELECT * FROM penilaian_santri")
    suspend fun getAllPenilaianList(): List<PenilaianSantriEntity>

    @Query("SELECT * FROM penilaian_santri WHERE santriId = :santriId ORDER BY tanggal DESC")
    fun getPenilaianBySantri(santriId: Long): Flow<List<PenilaianSantriEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPenilaian(penilaian: PenilaianSantriEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPenilaianList(list: List<PenilaianSantriEntity>)

    @Delete
    suspend fun deletePenilaian(penilaian: PenilaianSantriEntity)

    // 13. Target Pembelajaran
    @Query("SELECT * FROM target_pembelajaran ORDER BY batasWaktu ASC")
    fun getAllTarget(): Flow<List<TargetPembelajaranEntity>>

    @Query("SELECT * FROM target_pembelajaran")
    suspend fun getAllTargetList(): List<TargetPembelajaranEntity>

    @Query("SELECT * FROM target_pembelajaran WHERE santriId = :santriId")
    fun getTargetBySantri(santriId: Long): Flow<List<TargetPembelajaranEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: TargetPembelajaranEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTargetList(list: List<TargetPembelajaranEntity>)

    @Update
    suspend fun updateTarget(target: TargetPembelajaranEntity)

    @Delete
    suspend fun deleteTarget(target: TargetPembelajaranEntity)

    // 14. Evaluasi Bulanan
    @Query("SELECT * FROM evaluasi_bulanan ORDER BY bulanTahun DESC")
    fun getAllEvaluasiBulanan(): Flow<List<EvaluasiBulananEntity>>

    @Query("SELECT * FROM evaluasi_bulanan")
    suspend fun getAllEvaluasiBulananList(): List<EvaluasiBulananEntity>

    @Query("SELECT * FROM evaluasi_bulanan WHERE santriId = :santriId ORDER BY bulanTahun DESC")
    fun getEvaluasiBulananBySantri(santriId: Long): Flow<List<EvaluasiBulananEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluasiBulanan(evaluasi: EvaluasiBulananEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluasiBulananList(list: List<EvaluasiBulananEntity>)

    // 15. Kehadiran dan Kegiatan
    @Query("SELECT * FROM kehadiran_kegiatan ORDER BY tanggal DESC")
    fun getAllKehadiran(): Flow<List<KehadiranKegiatanEntity>>

    @Query("SELECT * FROM kehadiran_kegiatan")
    suspend fun getAllKehadiranList(): List<KehadiranKegiatanEntity>

    @Query("SELECT * FROM kehadiran_kegiatan WHERE santriId = :santriId ORDER BY tanggal DESC")
    fun getKehadiranBySantri(santriId: Long): Flow<List<KehadiranKegiatanEntity>>

    @Query("SELECT * FROM kehadiran_kegiatan WHERE tanggal = :tanggal")
    fun getKehadiranByTanggal(tanggal: String): Flow<List<KehadiranKegiatanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKehadiran(kehadiran: KehadiranKegiatanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKehadiranList(list: List<KehadiranKegiatanEntity>)

    // 16. Catatan Evaluasi Program
    @Query("SELECT * FROM evaluasi_program ORDER BY tanggal DESC")
    fun getAllEvaluasiProgram(): Flow<List<CatatanEvaluasiProgramEntity>>

    @Query("SELECT * FROM evaluasi_program")
    suspend fun getAllEvaluasiProgramList(): List<CatatanEvaluasiProgramEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluasiProgram(evaluasi: CatatanEvaluasiProgramEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluasiProgramList(list: List<CatatanEvaluasiProgramEntity>)
}
