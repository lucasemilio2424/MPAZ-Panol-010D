package cl.duoc.mpazpanol.data.remote

data class EquipoRemoto(
    val id: Long, val nombre: String, val categoria: String,
    val descripcion: String, val ubicacion: String,
    val stockTotal: Int, val stockDisponible: Int, val estado: String
)
data class EquipoNuevo(
    val nombre: String, val categoria: String, val descripcion: String,
    val ubicacion: String, val stockTotal: Int, val estado: String = "DISPONIBLE"
)
data class SolicitudRemota(
    val id: Long, val usuarioId: Long, val equipoId: Long,
    val cantidad: Int, val sala: String, val inicio: Long,
    val devolucionEstimada: Long, val estado: String,
    val motivoRechazo: String? = null
)
data class SolicitudNueva(
    val usuarioId: Long, val equipoId: Long,
    val cantidad: Int, val sala: String, val inicio: Long,
    val devolucionEstimada: Long
)
data class MotivoRechazo(val motivo: String)
