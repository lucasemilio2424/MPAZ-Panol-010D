package cl.duoc.mpazpanol.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazpanol.data.local.entity.EquipoEntity
import cl.duoc.mpazpanol.ui.components.AccionPrincipal
import cl.duoc.mpazpanol.ui.components.Campo
import cl.duoc.mpazpanol.ui.components.MensajeError
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.components.Tarjeta
import cl.duoc.mpazpanol.ui.viewmodel.SolicitudViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun NuevaSolicitud(
    usuarioId: Long,
    equipoInicialId: Long,
    equipos: List<EquipoEntity>,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    vm: SolicitudViewModel = viewModel()
) {
    val context = LocalContext.current
    val estado = vm.uiState
    var menuExpandido by remember { mutableStateOf(false) }
    val seleccionado = equipos.firstOrNull { it.id == estado.equipoId }

    LaunchedEffect(equipoInicialId, equipos) {
        if (equipoInicialId > 0 && estado.equipoId == 0L && equipos.any { it.id == equipoInicialId }) {
            vm.seleccionarEquipo(equipoInicialId)
        }
    }

    Pantalla("Nueva solicitud", onBack) {
        Text("Completa estos datos para pedir un equipo al encargado del pañol.")
        Tarjeta {
            Text("1. ¿Qué equipo necesitas?", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(onClick = { menuExpandido = true }, modifier = Modifier.fillMaxWidth()) {
                Text(seleccionado?.nombre ?: "Seleccionar equipo")
            }
            DropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                equipos.filter { it.stockDisponible > 0 && it.estado == "DISPONIBLE" }.forEach { equipo ->
                    DropdownMenuItem(text = { Text("${equipo.nombre} (${equipo.stockDisponible})") },
                        onClick = { vm.seleccionarEquipo(equipo.id); menuExpandido = false })
                }
            }
            if (estado.cantidadError != null && seleccionado == null) {
                Text(estado.cantidadError, color = MaterialTheme.colorScheme.error)
            }
            Campo(estado.cantidad, vm::cambiarCantidad, "Cantidad de unidades", estado.cantidadError)
        }
        Tarjeta {
            Text("2. ¿Dónde lo usarás?", style = MaterialTheme.typography.titleMedium)
            Campo(estado.sala, vm::cambiarSala, "Sala o laboratorio", estado.salaError)
        }
        Tarjeta {
            Text("3. ¿Cuándo lo necesitas?", style = MaterialTheme.typography.titleMedium)
            Text("Debes solicitarlo con al menos 3 horas de anticipación.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick = {
                val calendario = Calendar.getInstance()
                DatePickerDialog(context, { _, anio, mes, dia ->
                    vm.cambiarFecha(String.format(Locale("es", "CL"), "%02d/%02d/%04d", dia, mes + 1, anio))
                }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH))
                    .apply { datePicker.minDate = System.currentTimeMillis() - 60_000L }.show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (estado.fecha.isBlank()) "Seleccionar fecha" else "Fecha: ${estado.fecha}")
            }
            OutlinedButton(onClick = {
                val c = Calendar.getInstance()
                TimePickerDialog(context, { _, hora, minuto ->
                    vm.cambiarInicio(String.format(Locale.US, "%02d:%02d", hora, minuto))
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (estado.horaInicio.isBlank()) "Seleccionar hora de inicio" else "Inicio: ${estado.horaInicio}")
            }
            OutlinedButton(onClick = {
                val c = Calendar.getInstance()
                TimePickerDialog(context, { _, hora, minuto ->
                    vm.cambiarFin(String.format(Locale.US, "%02d:%02d", hora, minuto))
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (estado.horaFin.isBlank()) "Seleccionar devolución prevista" else "Devolución: ${estado.horaFin}")
            }
            MensajeError(estado.horarioError)
        }
        MensajeError(estado.mensaje)
        AccionPrincipal(if (estado.guardando) "Enviando..." else "Enviar solicitud",
            onClick = { vm.enviar(context, usuarioId, equipos, onSuccess) }, habilitado = !estado.guardando)
        Text("Recibirás el estado de la solicitud dentro de la aplicación.",
            color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
    }
}
