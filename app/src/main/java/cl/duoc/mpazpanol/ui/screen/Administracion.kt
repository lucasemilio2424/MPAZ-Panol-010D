package cl.duoc.mpazpanol.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.mpazpanol.ui.components.EstadoChip
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.components.Tarjeta
import cl.duoc.mpazpanol.ui.components.Campo
import cl.duoc.mpazpanol.ui.components.fechaLegible
import cl.duoc.mpazpanol.ui.viewmodel.PanolViewModel

@Composable
fun Administracion(vm: PanolViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var idRechazo by remember { mutableStateOf<Long?>(null) }
    var motivo by remember { mutableStateOf("") }
    var idAprobacion by remember { mutableStateOf<Long?>(null) }

    Pantalla("Gestionar préstamos", onBack) {
        Text("Aprueba, rechaza y registra la entrega o devolución de los equipos.",
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        vm.mensaje?.let { aviso ->
            Surface(color = MaterialTheme.colorScheme.surfaceVariant,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
                Text(aviso, modifier = Modifier.fillMaxWidth().padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (vm.solicitudes.isEmpty()) Text("No hay solicitudes por gestionar todavía.")
        vm.solicitudes.forEach { solicitud ->
            Tarjeta {
                Text(vm.nombreEquipo(solicitud.equipoId), style = MaterialTheme.typography.titleMedium)
                EstadoChip(solicitud.estado)
                Text("Solicita: ${vm.nombreUsuario(solicitud.usuarioId)}")
                Text("${solicitud.cantidad} unidad(es) · ${solicitud.sala}")
                Text("Inicio: ${fechaLegible(solicitud.inicio)}")
                when (solicitud.estado) {
                    "PENDIENTE" -> {
                        OutlinedButton(onClick = { idAprobacion = solicitud.id }, modifier = Modifier.fillMaxWidth()) {
                            Text("Aprobar solicitud")
                        }
                        OutlinedButton(onClick = { idRechazo = solicitud.id; motivo = "" }, modifier = Modifier.fillMaxWidth()) {
                            Text("Rechazar con motivo")
                        }
                    }
                    "APROBADA" -> OutlinedButton(
                        onClick = { vm.entregar(context, solicitud.id) }, modifier = Modifier.fillMaxWidth()
                    ) { Text("Registrar entrega") }
                    "ENTREGADA" -> OutlinedButton(
                        onClick = { vm.devolver(context, solicitud.id) }, modifier = Modifier.fillMaxWidth()
                    ) { Text("Registrar devolución") }
                }
            }
        }
    }
    if (idAprobacion != null) {
        AlertDialog(
            onDismissRequest = { idAprobacion = null },
            title = { Text("Confirmar aprobación") },
            text = { Text("Se descontará el stock disponible. ¿Deseas aprobar?") },
            confirmButton = { TextButton(onClick = {
                idAprobacion?.let { vm.aprobar(context, it) }
                idAprobacion = null
            }) { Text("Aprobar") } },
            dismissButton = { TextButton(onClick = { idAprobacion = null }) { Text("Cancelar") } }
        )
    }
    if (idRechazo != null) {
        AlertDialog(
            onDismissRequest = { idRechazo = null },
            title = { Text("Justificar rechazo") },
            text = { Campo(motivo, { motivo = it }, "Motivo del rechazo") },
            confirmButton = { TextButton(onClick = {
                if (motivo.trim().length >= 5) {
                    idRechazo?.let { vm.rechazar(context, it, motivo) }
                    idRechazo = null
                }
            }, enabled = motivo.trim().length >= 5) { Text("Rechazar") } },
            dismissButton = { TextButton(onClick = { idRechazo = null }) { Text("Cancelar") } }
        )
    }
}
