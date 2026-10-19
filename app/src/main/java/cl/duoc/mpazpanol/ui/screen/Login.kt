package cl.duoc.mpazpanol.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazpanol.data.local.entity.UsuarioEntity
import cl.duoc.mpazpanol.ui.components.AccionPrincipal
import cl.duoc.mpazpanol.ui.components.Campo
import cl.duoc.mpazpanol.ui.components.MensajeError
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.viewmodel.LoginViewModel

@Composable
fun Login(onBack: () -> Unit, onRegistro: () -> Unit, onSuccess: (UsuarioEntity) -> Unit,
          vm: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    val estado = vm.uiState
    Pantalla("Iniciar sesión", onBack) {
        Text("Ingresa con tu correo institucional o cuenta de demostración.")
        Campo(estado.email, vm::onEmailChanged, "Correo electrónico", estado.emailError)
        Campo(estado.password, vm::onPasswordChanged, "Contraseña", estado.passwordError,
            transformacion = PasswordVisualTransformation())
        MensajeError(estado.errorMessage)
        AccionPrincipal(if (estado.cargando) "Ingresando..." else "Entrar",
            onClick = { vm.onLoginClick(context, onSuccess) }, habilitado = !estado.cargando)
        TextButton(onClick = onRegistro) { Text("¿No tienes cuenta? Regístrate") }
        Spacer(Modifier.height(8.dp))
        Text("Acceso de prueba para el encargado: admin@mpaz.cl / Admin123!")
    }
}
