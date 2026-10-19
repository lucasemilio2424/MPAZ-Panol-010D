package cl.duoc.mpazpanol.model

data class SolicitudModel(
    val equipoId: Long = 0,
    val cantidad: String = "1",
    val sala: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val cantidadError: String? = null,
    val salaError: String? = null,
    val horarioError: String? = null,
    val mensaje: String? = null,
    val guardando: Boolean = false
)
