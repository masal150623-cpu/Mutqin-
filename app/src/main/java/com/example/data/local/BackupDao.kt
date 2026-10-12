package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PengaturanSistemEntity
import com.example.data.model.RiwayatBackupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupDao {
    // 17. Pengaturan Sistem
    @Query("SELECT * FROM pengaturan_sistem")
    fun getAllPengaturan(): Flow<List<PengaturanSistemEntity>>

    @Query("SELECT * FROM pengaturan_sistem")
    suspend fun getAllPengaturanList(): List<PengaturanSistemEntity>

    @Query("SELECT * FROM pengaturan_sistem WHERE kunci = :kunci LIMIT 1")
    suspend fun getPengaturan(kunci: String): PengaturanSistemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePengaturan(pengaturan: PengaturanSistemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePengaturanList(list: List<PengaturanSistemEntity>)

    // 18. Riwayat Backup
    @Query("SELECT * FROM riwayat_backup ORDER BY id DESC")
    fun getAllRiwayatBackup(): Flow<List<RiwayatBackupEntity>>

    @Query("SELECT * FROM riwayat_backup")
    suspend fun getAllRiwayatBackupList(): List<RiwayatBackupEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayatBackup(riwayat: RiwayatBackupEntity): Long

    @Update
    suspend fun updateRiwayatBackup(riwayat: RiwayatBackupEntity)

    @Delete
    suspend fun deleteRiwayatBackup(riwayat: RiwayatBackupEntity)

    @Query("DELETE FROM riwayat_backup WHERE id = :id")
    suspend fun deleteRiwayatBackupById(id: Long)
}
