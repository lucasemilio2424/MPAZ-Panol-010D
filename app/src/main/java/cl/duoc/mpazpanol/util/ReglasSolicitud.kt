package cl.duoc.mpazpanol.util

object ReglasSolicitud {
    fun errorCantidad(cantidad: Int?, disponible: Int, estado: String): String? = when {
        cantidad == null || cantidad < 1 -> "Indica al menos una unidad"
        estado != "DISPONIBLE" -> "El equipo no está disponible"
        cantidad > disponible -> "Solo hay $disponible unidades disponibles"
        else -> null
    }

    fun errorSala(sala: String): String? =
        if (sala.trim().length < 2) "Indica la sala o laboratorio" else null

    fun errorHorario(inicio: Long?, fin: Long?, ahora: Long): String? = when {
        inicio == null || fin == null -> "Selecciona fecha, hora de inicio y devolución"
        inicio < ahora + 3 * 60 * 60 * 1000L -> "Solicita con al menos 3 horas de anticipación"
        fin <= inicio -> "La devolución debe ser posterior al inicio"
        else -> null
    }
}
