package cl.duoc.mpazpanol.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazpanol.data.local.entity.EquipoEntity
import cl.duoc.mpazpanol.data.remote.EquipoNuevo
import cl.duoc.mpazpanol.data.remote.RetrofitBackend
import cl.duoc.mpazpanol.model.EquipoFormModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class InventarioViewModel : ViewModel() {
    var uiState by mutableStateOf(EquipoFormModel())
        private set

    fun preparar(equipo: EquipoEntity?) {
        uiState = equipo?.let {
            EquipoFormModel(
                nombre = it.nombre,
                categoria = it.categoria,
                descripcion = it.descripcion,
                ubicacion = it.ubicacion,
                cantidad = it.stockTotal.toString(),
                estado = if (it.estado == "RESERVADO") "DISPONIBLE" else it.estado
            )
        } ?: EquipoFormModel()
    }

    fun cambiarNombre(valor: String) {
        uiState = uiState.copy(nombre = valor, nombreError = null, mensaje = null)
    }

    fun cambiarCategoria(valor: String) {
        uiState = uiState.copy(categoria = valor, categoriaError = null, mensaje = null)
    }

    fun cambiarDescripcion(valor: String) {
        uiState = uiState.copy(descripcion = valor, descripcionError = null, mensaje = null)
    }

    fun cambiarUbicacion(valor: String) {
        uiState = uiState.copy(ubicacion = valor, ubicacionError = null, mensaje = null)
    }

    fun cambiarCantidad(valor: String) {
        uiState = uiState.copy(cantidad = valor.filter(Char::isDigit), cantidadError = null, mensaje = null)
    }

    fun cambiarEstado(valor: String) {
        uiState = uiState.copy(estado = valor, mensaje = null)
    }

    fun guardar(context: Context, equipoId: Long, onGuardado: () -> Unit) {
        val datos = uiState
        if (datos.guardando) return

        val nombre = datos.nombre.trim()
        val categoria = datos.categoria.trim()
        val descripcion = datos.descripcion.trim()
        val ubicacion = datos.ubicacion.trim()
        val cantidad = datos.cantidad.toIntOrNull()
        val nombreError = if (nombre.length < 3) "Ingresa un nombre de al menos 3 caracteres" else null
        val categoriaError = if (categoria.isBlank()) "Ingresa la categoría" else null
        val descripcionError = if (descripcion.length < 5) "Describe brevemente el equipo" else null
        val ubicacionError = if (ubicacion.length < 2) "Indica la ubicación" else null
        val cantidadError = if (cantidad == null || cantidad !in 1..9999) "Ingresa una cantidad entre 1 y 9999" else null

        uiState = datos.copy(
            nombreError = nombreError,
            categoriaError = categoriaError,
            descripcionError = descripcionError,
            ubicacionError = ubicacionError,
            cantidadError = cantidadError,
            mensaje = null
        )

        if (listOf(nombreError, categoriaError, descripcionError, ubicacionError, cantidadError).any { it != null }) return
        val total = cantidad ?: return

        viewModelScope.launch {
            uiState = uiState.copy(guardando = true)
            try {
                val equipo = EquipoNuevo(nombre, categoria, descripcion, ubicacion, total, datos.estado)
                withContext(Dispatchers.IO) {
                    if (equipoId > 0) {
                        RetrofitBackend.equipos.actualizar(equipoId, equipo)
                    } else {
                        RetrofitBackend.equipos.crear(equipo)
                    }
                }
                onGuardado()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    mensaje = when (e) {
                        is HttpException -> if (e.code() == 409 || e.code() == 400)
                            "Revisa el nombre, el estado y las unidades comprometidas" else "No fue posible guardar el equipo (${e.code()})"
                        else -> "No se pudo conectar con el servidor"
                    }
                )
            } finally {
                uiState = uiState.copy(guardando = false)
            }
        }
    }
}
