package cl.duoc.mpazpanol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.mpazpanol.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Insert suspend fun insertar(usuario: UsuarioEntity): Long
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorCorreo(email: String): UsuarioEntity?
    @Query("SELECT * FROM usuarios") suspend fun listar(): List<UsuarioEntity>
}
