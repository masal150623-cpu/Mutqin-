package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RiwayatPerpindahanKelompokEntity
import com.example.data.model.SantriEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SantriDao {
    @Query("SELECT * FROM santri ORDER BY status ASC, namaLengkap ASC")
    fun getAllSantri(): Flow<List<SantriEntity>>

    @Query("SELECT * FROM santri")
    suspend fun getAllSantriList(): List<SantriEntity>

    @Query("SELECT * FROM santri WHERE id = :id")
    fun getSantriById(id: Long): Flow<SantriEntity?>

    @Query("SELECT * FROM santri WHERE id = :id")
    suspend fun getSantriByIdOnce(id: Long): SantriEntity?

    @Query("SELECT * FROM santri WHERE status = 'Aktif' ORDER BY namaLengkap ASC")
    fun getSantriAktif(): Flow<List<SantriEntity>>

    @Query("SELECT * FROM santri WHERE kelompokId = :kelompokId AND status = 'Aktif'")
    fun getSantriByKelompok(kelompokId: Long): Flow<List<SantriEntity>>

    @Query("SELECT * FROM santri WHERE kelompokId = :kelompokId")
    suspend fun getSantriListByKelompok(kelompokId: Long): List<SantriEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSantri(santri: SantriEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSantriList(santriList: List<SantriEntity>)

    @Update
    suspend fun updateSantri(santri: SantriEntity)

    @Delete
    suspend fun deleteSantri(santri: SantriEntity)

    @Query("UPDATE santri SET status = :status, tanggalKeluar = :tanggalKeluar, alasanKeluar = :alasan WHERE id = :santriId")
    suspend fun arsipkanSantri(santriId: Long, status: String, tanggalKeluar: String, alasan: String)

    @Query("UPDATE santri SET kelompokId = :kelompokBaruId WHERE id = :santriId")
    suspend fun updateKelompokSantri(santriId: Long, kelompokBaruId: Long)

    // Riwayat Perpindahan Kelompok
    @Query("SELECT * FROM riwayat_perpindahan_kelompok WHERE santriId = :santriId ORDER BY tanggalPindah DESC")
    fun getRiwayatPindahBySantri(santriId: Long): Flow<List<RiwayatPerpindahanKelompokEntity>>

    @Query("SELECT * FROM riwayat_perpindahan_kelompok ORDER BY tanggalPindah DESC")
    fun getAllRiwayatPindah(): Flow<List<RiwayatPerpindahanKelompokEntity>>

    @Query("SELECT * FROM riwayat_perpindahan_kelompok")
    suspend fun getAllRiwayatPindahList(): List<RiwayatPerpindahanKelompokEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayatPindah(riwayat: RiwayatPerpindahanKelompokEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayatPindahList(list: List<RiwayatPerpindahanKelompokEntity>)
}
