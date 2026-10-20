
package cl.duoc.mpazpanol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.mpazpanol.data.local.entity.EquipoEntity

@Dao
interface EquipoDao {

    @Query("SELECT * FROM equipos ORDER BY nombre ASC")
    suspend fun listar(): List<EquipoEntity>

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun buscar(id: Long): EquipoEntity?

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun contar(): Int

    @Insert
    suspend fun insertar(equipo: EquipoEntity): Long

    @Insert
    suspend fun insertarTodos(equipos: List<EquipoEntity>)

    @Update
    suspend fun actualizar(equipo: EquipoEntity)

    @Query(
        "SELECT COUNT(*) FROM equipos " +
                "WHERE nombre = :nombre COLLATE NOCASE AND id != :excluirId"
    )
    suspend fun contarPorNombre(
        nombre: String,
        excluirId: Long
    ): Int
}
