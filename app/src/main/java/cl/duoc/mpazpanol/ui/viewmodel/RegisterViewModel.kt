package cl.duoc.mpazpanol.ui.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazpanol.data.database.AppDatabase
import cl.duoc.mpazpanol.data.local.entity.UsuarioEntity
import cl.duoc.mpazpanol.model.RegisterModel
import cl.duoc.mpazpanol.util.Passwords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel : ViewModel() {
    var uiState by mutableStateOf(RegisterModel())
        private set

    fun onNombreChanged(v: String) { uiState = uiState.copy(nombre = v, nombreError = null, errorMessage = null) }
    fun onEmailChanged(v: String) { uiState = uiState.copy(email = v, emailError = null, errorMessage = null) }
    fun onPasswordChanged(v: String) { uiState = uiState.copy(password = v, passwordError = null, errorMessage = null) }
    fun onConfirmacionChanged(v: String) { uiState = uiState.copy(confirmacion = v, confirmacionError = null, errorMessage = null) }

    fun onRegisterClick(context: Context, onSuccess: () -> Unit) {
        val estado = uiState
        val email = estado.email.trim().lowercase()
        val nombreError = if (estado.nombre.trim().length < 3) "Escribe tu nombre (mínimo 3 caracteres)" else null
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Ingresa un correo válido" else null
        val passwordError = if (estado.password.length < 8) "Usa al menos 8 caracteres" else null
        val confirmarError = if (estado.confirmacion != estado.password) "Las contraseñas no coinciden" else null
        uiState = estado.copy(
            nombreError = nombreError,
            emailError = emailError,
            passwordError = passwordError,
            confirmacionError = confirmarError
        )
        if (listOf(nombreError, emailError, passwordError, confirmarError).any { it != null } || estado.cargando) return

        viewModelScope.launch {
            uiState = uiState.copy(cargando = true)
            try {
                val db = AppDatabase.getDatabase(context)
                val existe = withContext(Dispatchers.IO) { db.usuarioDao().buscarPorCorreo(email) != null }
                if (existe) {
                    uiState = uiState.copy(emailError = "Este correo ya está registrado")
                    return@launch
                }
                val hash = withContext(Dispatchers.Default) { Passwords.hash(estado.password) }
                withContext(Dispatchers.IO) {
                    db.usuarioDao().insertar(
                        UsuarioEntity(nombre = estado.nombre.trim(), email = email, passwordHash = hash)
                    )
                }
                onSuccess()
            } catch (_: Exception) {
                uiState = uiState.copy(errorMessage = "No se pudo registrar. Revisa los datos")
            } finally {
                uiState = uiState.copy(cargando = false)
            }
        }
    }
}
