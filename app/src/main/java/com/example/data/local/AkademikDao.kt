package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.KelompokEntity
import com.example.data.model.PembimbingEntity
import com.example.data.model.RiwayatPergantianPembimbingEntity
import com.example.data.model.TahunAjaranEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AkademikDao {
    // Pembimbing (Ustaz / Ustazah)
    @Query("SELECT * FROM pembimbing ORDER BY statusAktif DESC, nama ASC")
    fun getAllPembimbing(): Flow<List<PembimbingEntity>>

    @Query("SELECT * FROM pembimbing")
    suspend fun getAllPembimbingList(): List<PembimbingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPembimbing(pembimbing: PembimbingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPembimbingList(list: List<PembimbingEntity>)

    @Update
    suspend fun updatePembimbing(pembimbing: PembimbingEntity)

    @Delete
    suspend fun deletePembimbing(pembimbing: PembimbingEntity)

    // Kelompok / Halaqah
    @Query("SELECT * FROM kelompok ORDER BY statusAktif DESC, namaKelompok ASC")
    fun getAllKelompok(): Flow<List<KelompokEntity>>

    @Query("SELECT * FROM kelompok")
    suspend fun getAllKelompokList(): List<KelompokEntity>

    @Query("SELECT * FROM kelompok WHERE id = :id")
    fun getKelompokById(id: Long): Flow<KelompokEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKelompok(kelompok: KelompokEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKelompokList(list: List<KelompokEntity>)

    @Update
    suspend fun updateKelompok(kelompok: KelompokEntity)

    @Query("UPDATE kelompok SET pembimbingId = :pembimbingBaruId WHERE id = :kelompokId")
    suspend fun updatePembimbingKelompok(kelompokId: Long, pembimbingBaruId: Long)

    @Delete
    suspend fun deleteKelompok(kelompok: KelompokEntity)

    // Tahun Ajaran
    @Query("SELECT * FROM tahun_ajaran ORDER BY isAktif DESC, tahun DESC")
    fun getAllTahunAjaran(): Flow<List<TahunAjaranEntity>>

    @Query("SELECT * FROM tahun_ajaran")
    suspend fun getAllTahunAjaranList(): List<TahunAjaranEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTahunAjaran(tahunAjaran: TahunAjaranEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTahunAjaranList(list: List<TahunAjaranEntity>)

    // Riwayat Pergantian Pembimbing
    @Query("SELECT * FROM riwayat_pergantian_pembimbing ORDER BY tanggalGanti DESC")
    fun getAllRiwayatGantiPembimbing(): Flow<List<RiwayatPergantianPembimbingEntity>>

    @Query("SELECT * FROM riwayat_pergantian_pembimbing")
    suspend fun getAllRiwayatGantiPembimbingList(): List<RiwayatPergantianPembimbingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayatGantiPembimbing(riwayat: RiwayatPergantianPembimbingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayatGantiPembimbingList(list: List<RiwayatPergantianPembimbingEntity>)
}
