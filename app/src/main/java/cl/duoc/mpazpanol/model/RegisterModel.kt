package cl.duoc.mpazpanol.model

data class RegisterModel(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val confirmacion: String = "",
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmacionError: String? = null,
    val errorMessage: String? = null,
    val cargando: Boolean = false
)
