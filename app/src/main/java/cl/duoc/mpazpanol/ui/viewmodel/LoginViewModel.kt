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
import cl.duoc.mpazpanol.model.LoginModel
import cl.duoc.mpazpanol.util.Passwords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginModel())
        private set

    fun onEmailChanged(valor: String) {
        uiState = uiState.copy(email = valor, emailError = null, errorMessage = null)
    }

    fun onPasswordChanged(valor: String) {
        uiState = uiState.copy(password = valor, passwordError = null, errorMessage = null)
    }

    fun onLoginClick(context: Context, onSuccess: (UsuarioEntity) -> Unit) {
        val email = uiState.email.trim().lowercase()
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Ingresa un correo válido" else null
        val passwordError = if (uiState.password.isBlank()) "Ingresa tu contraseña" else null
        uiState = uiState.copy(emailError = emailError, passwordError = passwordError)
        if (emailError != null || passwordError != null || uiState.cargando) return

        viewModelScope.launch {
            uiState = uiState.copy(cargando = true)
            try {
                val usuario = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(context).usuarioDao().buscarPorCorreo(email)
                }
                val valido = usuario != null && withContext(Dispatchers.Default) {
                    Passwords.coincide(uiState.password, usuario.passwordHash)
                }
                if (valido && usuario != null) onSuccess(usuario)
                else uiState = uiState.copy(errorMessage = "Correo o contraseña incorrectos")
            } catch (_: Exception) {
                uiState = uiState.copy(errorMessage = "No fue posible acceder. Inténtalo otra vez")
            } finally {
                uiState = uiState.copy(cargando = false)
            }
        }
    }
}
