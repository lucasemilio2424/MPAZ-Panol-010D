package cl.duoc.mpazpanol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val ubicacion: String,
    val stockTotal: Int,
    val stockDisponible: Int,
    val estado: String = "DISPONIBLE"
)
