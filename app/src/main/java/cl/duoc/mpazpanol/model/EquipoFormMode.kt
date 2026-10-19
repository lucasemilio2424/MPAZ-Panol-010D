package cl.duoc.mpazpanol.model

data class EquipoFormModel(
    val nombre: String = "",
    val categoria: String = "",
    val descripcion: String = "",
    val ubicacion: String = "",
    val cantidad: String = "",
    val estado: String = "DISPONIBLE",

    val nombreError: String? = null,
    val categoriaError: String? = null,
    val descripcionError: String? = null,
    val ubicacionError: String? = null,
    val cantidadError: String? = null,

    val mensaje: String? = null,
    val guardando: Boolean = false
)
