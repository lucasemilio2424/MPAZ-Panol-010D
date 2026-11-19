package cl.duoc.mpazpanol.ui.screen

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazpanol.ui.viewmodel.ClimaViewModel
import androidx.compose.ui.platform.LocalContext
import cl.duoc.mpazpanol.data.local.entity.UsuarioEntity
import cl.duoc.mpazpanol.platform.RecursosNativos
import cl.duoc.mpazpanol.ui.components.AccionPrincipal
import cl.duoc.mpazpanol.ui.components.MensajeError
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.components.Tarjeta
import cl.duoc.mpazpanol.ui.viewmodel.PanolViewModel

@Composable
fun Inicio(
    usuario: UsuarioEntity,
    vm: PanolViewModel,
    onCatalogo: () -> Unit,
    onSolicitar: () -> Unit,
    onMisSolicitudes: () -> Unit,
    onAdministrar: () -> Unit,
    onInventario: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val clima: ClimaViewModel = viewModel()
    LaunchedEffect(Unit) { clima.cargar() }
    val bateria = RecursosNativos.bateriaPorcentaje(context)
    val pendientes = vm.solicitudes.count {
        it.estado == "PENDIENTE" && (usuario.rol == "ADMIN" || it.usuarioId == usuario.id)
    }

    Pantalla("MPAZ Pañol") {
        Text("¡Hola, ${usuario.nombre.substringBefore(' ')}!", style = MaterialTheme.typography.headlineMedium)
        Text("¿Qué necesitas hacer hoy?", color = MaterialTheme.colorScheme.onSurfaceVariant)

        MensajeError(vm.mensaje?.takeIf { it.startsWith("No se") })

        Tarjeta {
            Text("Resumen del pañol", style = MaterialTheme.typography.titleMedium)
            Text("${vm.equipos.size} tipos de equipo registrados")
            Text("${vm.equipos.sumOf { it.stockDisponible }} unidades disponibles")
            Text("$pendientes solicitudes pendientes")
        }

        AccionPrincipal("Ver equipos disponibles", onCatalogo)
        AccionPrincipal("Solicitar un equipo", onSolicitar)
        AccionPrincipal("Consultar mis solicitudes", onMisSolicitudes)

        if (usuario.rol == "ADMIN") {
            HorizontalDivider()
            Text("Área del encargado", style = MaterialTheme.typography.titleMedium)
            AccionPrincipal("Gestionar inventario", onInventario)
            AccionPrincipal("Gestionar préstamos y devoluciones", onAdministrar)
        }

        Tarjeta {
            Text("Estado del dispositivo", style = MaterialTheme.typography.titleMedium)
            Text("Batería: ${bateria?.let { "$it%" } ?: "No disponible"}")
            Text("Comprueba la batería antes de registrar entregas prolongadas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Tarjeta {
            Text("Clima actual en Santiago", style = MaterialTheme.typography.titleMedium)
            Text(clima.informacion)
            OutlinedButton(onClick = { clima.cargar() }) { Text("Actualizar clima") }
        }

        OutlinedButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
    }
}
