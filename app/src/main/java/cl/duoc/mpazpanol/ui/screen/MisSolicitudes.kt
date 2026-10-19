package cl.duoc.mpazpanol.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cl.duoc.mpazpanol.ui.components.EstadoChip
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.components.Tarjeta
import cl.duoc.mpazpanol.ui.components.fechaLegible
import cl.duoc.mpazpanol.ui.viewmodel.PanolViewModel

@Composable
fun MisSolicitudes(usuarioId: Long, vm: PanolViewModel, onBack: () -> Unit) {
    val mias = vm.solicitudes.filter { it.usuarioId == usuarioId }
    Pantalla("Mis solicitudes", onBack) {
        if (mias.isEmpty()) Text("Aún no tienes solicitudes. Entra al catálogo para crear una.")
        mias.forEach { solicitud ->
            Tarjeta {
                Text(vm.nombreEquipo(solicitud.equipoId), style = MaterialTheme.typography.titleMedium)
                EstadoChip(solicitud.estado)
                Text("Cantidad: ${solicitud.cantidad} · Sala: ${solicitud.sala}")
                Text("Uso: ${fechaLegible(solicitud.inicio)}")
                Text("Devolución prevista: ${fechaLegible(solicitud.devolucionEstimada)}")
                if (solicitud.motivoRechazo.isNotBlank()) {
                    Text("Motivo del rechazo: ${solicitud.motivoRechazo}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
