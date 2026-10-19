package cl.duoc.mpazpanol.ui.screen

import android.content.Context
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import cl.duoc.mpazpanol.data.local.entity.UsuarioEntity
import cl.duoc.mpazpanol.platform.RecursosNativos
import cl.duoc.mpazpanol.ui.components.AccionPrincipal
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
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val bateria = RecursosNativos.bateriaPorcentaje(context)
    Pantalla("MPAZ Pañol") {
        Text("¡Hola, ${usuario.nombre.substringBefore(' ')}!", style = MaterialTheme.typography.headlineMedium)
        Text("¿Qué necesitas hacer hoy?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Tarjeta {
            Text("Resumen del pañol", style = MaterialTheme.typography.titleMedium)
            Text("${vm.equipos.size} tipos de equipo registrados")
            Text("${vm.equipos.sumOf { it.stockDisponible }} unidades disponibles")
            Text("${vm.solicitudes.count { it.estado == "PENDIENTE" }} solicitudes pendientes")
        }
        AccionPrincipal("Ver equipos disponibles", onCatalogo)
        AccionPrincipal("Solicitar un equipo", onSolicitar)
        AccionPrincipal("Consultar mis solicitudes", onMisSolicitudes)
        if (usuario.rol == "ADMIN") {
            HorizontalDivider()
            Text("Área del encargado", style = MaterialTheme.typography.titleMedium)
            AccionPrincipal("Gestionar préstamos y devoluciones", onAdministrar)
        }
        Tarjeta {
            Text("Estado del dispositivo", style = MaterialTheme.typography.titleMedium)
            Text("Batería: ${bateria?.let { "$it%" } ?: "No disponible"}")
            Text("Comprueba la batería antes de registrar entregas prolongadas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        androidx.compose.material3.OutlinedButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
    }
}
