package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CatatanPerkembanganHarianEntity
import com.example.data.model.MurajaahEntity
import com.example.data.model.PerbaikanBacaanEntity
import com.example.data.model.PerkembanganIqroEntity
import com.example.data.model.SetoranHafalanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PerkembanganDao {
    // 10. Setoran Hafalan (Ziyadah)
    @Query("SELECT * FROM setoran_hafalan ORDER BY tanggal DESC")
    fun getAllSetoran(): Flow<List<SetoranHafalanEntity>>

    @Query("SELECT * FROM setoran_hafalan")
    suspend fun getAllSetoranList(): List<SetoranHafalanEntity>

    @Query("SELECT * FROM setoran_hafalan WHERE santriId = :santriId ORDER BY tanggal DESC")
    fun getSetoranBySantri(santriId: Long): Flow<List<SetoranHafalanEntity>>

    @Query("SELECT * FROM setoran_hafalan WHERE tanggal = :tanggal")
    fun getSetoranByTanggal(tanggal: String): Flow<List<SetoranHafalanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetoran(setoran: SetoranHafalanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetoranList(list: List<SetoranHafalanEntity>)

    @Delete
    suspend fun deleteSetoran(setoran: SetoranHafalanEntity)

    // 11. Murajaah
    @Query("SELECT * FROM murajaah ORDER BY tanggal DESC")
    fun getAllMurajaah(): Flow<List<MurajaahEntity>>

    @Query("SELECT * FROM murajaah")
    suspend fun getAllMurajaahList(): List<MurajaahEntity>

    @Query("SELECT * FROM murajaah WHERE santriId = :santriId ORDER BY tanggal DESC")
    fun getMurajaahBySantri(santriId: Long): Flow<List<MurajaahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMurajaah(murajaah: MurajaahEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMurajaahList(list: List<MurajaahEntity>)

    @Delete
    suspend fun deleteMurajaah(murajaah: MurajaahEntity)

    // 8. Perkembangan Iqro
    @Query("SELECT * FROM perkembangan_iqro ORDER BY tanggal DESC")
    fun getAllIqro(): Flow<List<PerkembanganIqroEntity>>

    @Query("SELECT * FROM perkembangan_iqro")
    suspend fun getAllIqroList(): List<PerkembanganIqroEntity>

    @Query("SELECT * FROM perkembangan_iqro WHERE santriId = :santriId ORDER BY tanggal DESC")
    fun getIqroBySantri(santriId: Long): Flow<List<PerkembanganIqroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIqro(iqro: PerkembanganIqroEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIqroList(list: List<PerkembanganIqroEntity>)

    @Delete
    suspend fun deleteIqro(iqro: PerkembanganIqroEntity)

    // 9. Perbaikan Bacaan (Tahsin)
    @Query("SELECT * FROM perbaikan_bacaan ORDER BY tanggal DESC")
    fun getAllPerbaikanBacaan(): Flow<List<PerbaikanBacaanEntity>>

    @Query("SELECT * FROM perbaikan_bacaan")
    suspend fun getAllPerbaikanBacaanList(): List<PerbaikanBacaanEntity>

    @Query("SELECT * FROM perbaikan_bacaan WHERE santriId = :santriId ORDER BY tanggal DESC")
    fun getPerbaikanBacaanBySantri(santriId: Long): Flow<List<PerbaikanBacaanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerbaikanBacaan(bacaan: PerbaikanBacaanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerbaikanBacaanList(list: List<PerbaikanBacaanEntity>)

    @Delete
    suspend fun deletePerbaikanBacaan(bacaan: PerbaikanBacaanEntity)

    // 7. Catatan Perkembangan Harian
    @Query("SELECT * FROM catatan_perkembangan_harian ORDER BY tanggal DESC")
    fun getAllCatatanHarian(): Flow<List<CatatanPerkembanganHarianEntity>>

    @Query("SELECT * FROM catatan_perkembangan_harian")
    suspend fun getAllCatatanHarianList(): List<CatatanPerkembanganHarianEntity>

    @Query("SELECT * FROM catatan_perkembangan_harian WHERE santriId = :santriId ORDER BY tanggal DESC")
    fun getCatatanHarianBySantri(santriId: Long): Flow<List<CatatanPerkembanganHarianEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatatanHarian(catatan: CatatanPerkembanganHarianEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatatanHarianList(list: List<CatatanPerkembanganHarianEntity>)
}
