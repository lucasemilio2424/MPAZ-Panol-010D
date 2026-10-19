package cl.duoc.mpazpanol.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "solicitudes",
    foreignKeys = [
        ForeignKey(entity = UsuarioEntity::class, parentColumns = ["id"], childColumns = ["usuarioId"]),
        ForeignKey(entity = EquipoEntity::class, parentColumns = ["id"], childColumns = ["equipoId"])
    ],
    indices = [Index("usuarioId"), Index("equipoId")]
)
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val equipoId: Long,
    val cantidad: Int,
    val sala: String,
    val inicio: Long,
    val devolucionEstimada: Long,
    val estado: String = "PENDIENTE",
    val motivoRechazo: String = "",
    val creadaEn: Long = System.currentTimeMillis(),
    val aprobadaEn: Long? = null,
    val entregadaEn: Long? = null,
    val devueltaEn: Long? = null
)
