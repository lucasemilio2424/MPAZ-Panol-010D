package cl.duoc.mpazpanol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.mpazpanol.data.local.entity.SolicitudEntity

@Dao
interface SolicitudDao {
    @Insert suspend fun insertar(solicitud: SolicitudEntity)
    @Update suspend fun actualizar(solicitud: SolicitudEntity)
    @Query("SELECT * FROM solicitudes ORDER BY creadaEn DESC")
    suspend fun listarTodas(): List<SolicitudEntity>
    @Query("SELECT * FROM solicitudes WHERE usuarioId = :usuarioId ORDER BY creadaEn DESC")
    suspend fun listarPorUsuario(usuarioId: Long): List<SolicitudEntity>
    @Query("SELECT * FROM solicitudes WHERE id = :id LIMIT 1")
    suspend fun buscar(id: Long): SolicitudEntity?
}
