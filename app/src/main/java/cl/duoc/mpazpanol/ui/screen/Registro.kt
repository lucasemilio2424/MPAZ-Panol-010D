package cl.duoc.mpazpanol.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazpanol.ui.components.AccionPrincipal
import cl.duoc.mpazpanol.ui.components.Campo
import cl.duoc.mpazpanol.ui.components.MensajeError
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.viewmodel.RegisterViewModel

@Composable
fun Registro(onBack: () -> Unit, onSuccess: () -> Unit, vm: RegisterViewModel = viewModel()) {
    val context = LocalContext.current
    val estado = vm.uiState
    Pantalla("Crear cuenta", onBack) {
        Text("Completa tus datos. Todos los usuarios nuevos serán solicitantes.")
        Campo(estado.nombre, vm::onNombreChanged, "Nombre completo", estado.nombreError)
        Campo(estado.email, vm::onEmailChanged, "Correo electrónico", estado.emailError)
        Campo(estado.password, vm::onPasswordChanged, "Contraseña (mínimo 8 caracteres)",
            estado.passwordError, PasswordVisualTransformation())
        Campo(estado.confirmacion, vm::onConfirmacionChanged, "Repetir contraseña",
            estado.confirmacionError, PasswordVisualTransformation())
        MensajeError(estado.errorMessage)
        AccionPrincipal(if (estado.cargando) "Guardando..." else "Registrarme",
            onClick = { vm.onRegisterClick(context, onSuccess) }, habilitado = !estado.cargando)
    }
}
