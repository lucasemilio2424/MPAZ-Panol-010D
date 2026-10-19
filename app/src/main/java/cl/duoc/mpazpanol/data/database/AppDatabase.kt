package cl.duoc.mpazpanol.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.mpazpanol.data.local.dao.EquipoDao
import cl.duoc.mpazpanol.data.local.dao.SolicitudDao
import cl.duoc.mpazpanol.data.local.dao.UsuarioDao
import cl.duoc.mpazpanol.data.local.entity.EquipoEntity
import cl.duoc.mpazpanol.data.local.entity.SolicitudEntity
import cl.duoc.mpazpanol.data.local.entity.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class, EquipoEntity::class, SolicitudEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun equipoDao(): EquipoDao
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "mpaz_panol.db"
            ).build().also { INSTANCE = it }
        }
    }
}
