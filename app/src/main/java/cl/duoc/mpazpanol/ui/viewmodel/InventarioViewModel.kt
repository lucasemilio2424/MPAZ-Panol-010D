
package cl.duoc.mpazpanol.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import cl.duoc.mpazpanol.data.database.AppDatabase
import cl.duoc.mpazpanol.data.local.entity.EquipoEntity
import cl.duoc.mpazpanol.model.EquipoFormModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventarioViewModel : ViewModel() {

    var uiState by mutableStateOf(EquipoFormModel())
        private set

    fun preparar(equipo: EquipoEntity?) {
        uiState = if (equipo == null) {
            EquipoFormModel()
        } else {
            EquipoFormModel(
                nombre = equipo.nombre,
                categoria = equipo.categoria,
                descripcion = equipo.descripcion,
                ubicacion = equipo.ubicacion,
                cantidad = equipo.stockTotal.toString(),
                estado = if (equipo.estado == "RESERVADO")
                    "DISPONIBLE" else equipo.estado
            )
        }
    }

    fun cambiarNombre(valor: String) {
        uiState = uiState.copy(
            nombre = valor,
            nombreError = null,
            mensaje = null
        )
    }

    fun cambiarCategoria(valor: String) {
        uiState = uiState.copy(
            categoria = valor,
            categoriaError = null,
            mensaje = null
        )
    }

    fun cambiarDescripcion(valor: String) {
        uiState = uiState.copy(
            descripcion = valor,
            descripcionError = null,
            mensaje = null
        )
    }

    fun cambiarUbicacion(valor: String) {
        uiState = uiState.copy(
            ubicacion = valor,
            ubicacionError = null,
            mensaje = null
        )
    }

    fun cambiarCantidad(valor: String) {
        uiState = uiState.copy(
            cantidad = valor.filter { it.isDigit() },
            cantidadError = null,
            mensaje = null
        )
    }

    fun cambiarEstado(valor: String) {
        uiState = uiState.copy(
            estado = valor,
            mensaje = null
        )
    }

    fun guardar(
        context: Context,
        equipoId: Long,
        onGuardado: () -> Unit
    ) {
        val datos = uiState

        if (datos.guardando) return

        val nombre = datos.nombre.trim()
        val categoria = datos.categoria.trim()
        val descripcion = datos.descripcion.trim()
        val ubicacion = datos.ubicacion.trim()
        val cantidad = datos.cantidad.toIntOrNull()

        val nombreError = if (nombre.length < 3)
            "Ingresa un nombre de al menos 3 caracteres" else null

        val categoriaError = if (categoria.isBlank())
            "Selecciona o escribe una categoria" else null

        val descripcionError = if (descripcion.length < 5)
            "Describe brevemente el equipo" else null

        val ubicacionError = if (ubicacion.length < 2)
            "Indica donde se guarda el equipo" else null

        val cantidadError = if (cantidad == null || cantidad !in 1..9999)
            "Ingresa una cantidad entre 1 y 9999" else null

        uiState = datos.copy(
            nombreError = nombreError,
            categoriaError = categoriaError,
            descripcionError = descripcionError,
            ubicacionError = ubicacionError,
            cantidadError = cantidadError,
            mensaje = null
        )

        if (
            nombreError != null ||
            categoriaError != null ||
            descripcionError != null ||
            ubicacionError != null ||
            cantidadError != null
        ) return

        val total = cantidad ?: return

        viewModelScope.launch {
            uiState = uiState.copy(guardando = true)

            try {
                val error = withContext(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(context)

                    db.withTransaction<String?> {
                        val actual = if (equipoId > 0L)
                            db.equipoDao().buscar(equipoId)
                        else null

                        when {
                            equipoId > 0L && actual == null ->
                                "El equipo ya no existe"

                            db.equipoDao().contarPorNombre(
                                nombre, equipoId
                            ) > 0 ->
                                "Ya existe un equipo con ese nombre"

                            else -> {
                                val comprometidas = actual?.let {
                                    it.stockTotal - it.stockDisponible
                                }?.coerceAtLeast(0) ?: 0

                                when {
                                    total < comprometidas ->
                                        "Hay $comprometidas unidades comprometidas. No puedes reducir el total a $total"

                                    datos.estado != "DISPONIBLE" &&
                                            comprometidas > 0 ->
                                        "No puedes poner fuera de servicio un equipo con préstamos pendientes de devolución"

                                    else -> {
                                        val disponible = total - comprometidas

                                        val nuevoEstado =
                                            if (datos.estado == "DISPONIBLE" &&
                                                disponible == 0)
                                                "RESERVADO"
                                            else datos.estado

                                        val equipo = EquipoEntity(
                                            id = actual?.id ?: 0L,
                                            nombre = nombre,
                                            categoria = categoria,
                                            descripcion = descripcion,
                                            ubicacion = ubicacion,
                                            stockTotal = total,
                                            stockDisponible = disponible,
                                            estado = nuevoEstado
                                        )

                                        if (actual == null) {
                                            db.equipoDao().insertar(equipo)
                                        } else {
                                            db.equipoDao().actualizar(equipo)
                                        }

                                        null
                                    }
                                }
                            }
                        }
                    }
                }

                if (error == null) {
                    onGuardado()
                } else {
                    uiState = uiState.copy(mensaje = error)
                }

            } catch (_: Exception) {
                uiState = uiState.copy(
                    mensaje = "No se pudo guardar el equipo"
                )
            } finally {
                uiState = uiState.copy(guardando = false)
            }
        }
    }
}
