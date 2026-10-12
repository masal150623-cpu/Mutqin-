package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.example.data.model.RiwayatBackupEntity
import com.example.data.model.RiwayatPergantianPembimbingEntity
import com.example.data.model.RiwayatPerpindahanKelompokEntity
import com.example.data.model.SantriEntity
import com.example.data.model.SetoranHafalanEntity
import com.example.data.model.TahunAjaranEntity
import com.example.data.model.TargetPembelajaranEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SantriEntity::class,
        PembimbingEntity::class,
        KelompokEntity::class,
        TahunAjaranEntity::class,
        RiwayatPerpindahanKelompokEntity::class,
        RiwayatPergantianPembimbingEntity::class,
        CatatanPerkembanganHarianEntity::class,
        PerkembanganIqroEntity::class,
        PerbaikanBacaanEntity::class,
        SetoranHafalanEntity::class,
        MurajaahEntity::class,
        PenilaianSantriEntity::class,
        TargetPembelajaranEntity::class,
        EvaluasiBulananEntity::class,
        KehadiranKegiatanEntity::class,
        CatatanEvaluasiProgramEntity::class,
        PengaturanSistemEntity::class,
        RiwayatBackupEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun santriDao(): SantriDao
    abstract fun akademikDao(): AkademikDao
    abstract fun perkembanganDao(): PerkembanganDao
    abstract fun evaluasiDao(): EvaluasiDao
    abstract fun backupDao(): BackupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mutqin_ulul_albab.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate database asynchronously with authentic Ponpes Ulul Albab seed data
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    DatabaseSeeder.seedInitialData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
