package cl.duoc.mpazpanol.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazpanol.data.database.AppDatabase
import cl.duoc.mpazpanol.data.local.entity.EquipoEntity
import cl.duoc.mpazpanol.data.local.entity.SolicitudEntity
import cl.duoc.mpazpanol.model.SolicitudModel
import cl.duoc.mpazpanol.platform.RecursosNativos
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SolicitudViewModel : ViewModel() {
    var uiState by mutableStateOf(SolicitudModel())
        private set

    fun seleccionarEquipo(id: Long) { uiState = uiState.copy(equipoId = id, cantidadError = null) }
    fun cambiarCantidad(valor: String) { uiState = uiState.copy(cantidad = valor.filter(Char::isDigit), cantidadError = null) }
    fun cambiarSala(valor: String) { uiState = uiState.copy(sala = valor, salaError = null) }
    fun cambiarFecha(valor: String) { uiState = uiState.copy(fecha = valor, horarioError = null) }
    fun cambiarInicio(valor: String) { uiState = uiState.copy(horaInicio = valor, horarioError = null) }
    fun cambiarFin(valor: String) { uiState = uiState.copy(horaFin = valor, horarioError = null) }

    fun enviar(context: Context, usuarioId: Long, equipos: List<EquipoEntity>, onSuccess: () -> Unit) {
        val estado = uiState
        val equipo = equipos.firstOrNull { it.id == estado.equipoId }
        val cantidad = estado.cantidad.toIntOrNull()
        val cantidadError = when {
            equipo == null -> "Selecciona un equipo"
            cantidad == null || cantidad < 1 -> "Indica al menos una unidad"
            cantidad > equipo.stockDisponible -> "Solo hay ${equipo.stockDisponible} unidades disponibles"
            else -> null
        }
        val salaError = if (estado.sala.trim().length < 2) "Indica la sala o laboratorio" else null
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).apply { isLenient = false }
        val inicio = runCatching { formato.parse("${estado.fecha} ${estado.horaInicio}")?.time }.getOrNull()
        val fin = runCatching { formato.parse("${estado.fecha} ${estado.horaFin}")?.time }.getOrNull()
        val horarioError = when {
            inicio == null || fin == null -> "Selecciona fecha, hora de inicio y devolución"
            inicio < System.currentTimeMillis() + 3 * 60 * 60 * 1000L -> "Solicita con al menos 3 horas de anticipación"
            fin <= inicio -> "La devolución debe ser posterior al inicio"
            else -> null
        }
        uiState = estado.copy(cantidadError = cantidadError, salaError = salaError, horarioError = horarioError)
        if (cantidadError != null || salaError != null || horarioError != null || cantidad == null || inicio == null || fin == null || estado.guardando) return

        viewModelScope.launch {
            uiState = uiState.copy(guardando = true)
            try {
                withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(context).solicitudDao().insertar(SolicitudEntity(
                        usuarioId = usuarioId, equipoId = estado.equipoId,
                        cantidad = cantidad, sala = estado.sala.trim(),
                        inicio = inicio, devolucionEstimada = fin
                    ))
                }
                RecursosNativos.vibrarConfirmacion(context)
                uiState = SolicitudModel()
                onSuccess()
            } catch (_: Exception) {
                uiState = uiState.copy(mensaje = "No se pudo guardar la solicitud")
            } finally {
                uiState = uiState.copy(guardando = false)
            }
        }
    }
}
