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
import cl.duoc.mpazpanol.data.local.entity.UsuarioEntity
import cl.duoc.mpazpanol.data.remote.EquipoRemoto
import cl.duoc.mpazpanol.data.remote.MotivoRechazo
import cl.duoc.mpazpanol.data.remote.RetrofitBackend
import cl.duoc.mpazpanol.data.remote.SolicitudRemota
import cl.duoc.mpazpanol.platform.RecursosNativos
import cl.duoc.mpazpanol.util.Passwords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class PanolViewModel : ViewModel() {
    var equipos by mutableStateOf<List<EquipoEntity>>(emptyList())
        private set
    var solicitudes by mutableStateOf<List<SolicitudEntity>>(emptyList())
        private set
    var usuarios by mutableStateOf<List<UsuarioEntity>>(emptyList())
        private set
    var cargando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    private var inicializando = false

    fun iniciar(context: Context) {
        if (inicializando) return
        inicializando = true
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val dao = AppDatabase.getDatabase(context).usuarioDao()
                    if (dao.buscarPorCorreo("admin@mpaz.cl") == null) {
                        dao.insertar(
                            UsuarioEntity(
                                nombre = "Encargado del pañol",
                                email = "admin@mpaz.cl",
                                passwordHash = Passwords.hash("Admin123!"),
                                rol = "ADMIN"
                            )
                        )
                    }
                }
                cargarDatos(context)
            } catch (e: Exception) {
                mensaje = mensajeError(e)
            }
        }
    }

    fun actualizar(context: Context) {
        viewModelScope.launch { cargarDatos(context) }
    }

    private suspend fun cargarDatos(context: Context) {
        cargando = true
        try {
            val datos = withContext(Dispatchers.IO) {
                Triple(
                    RetrofitBackend.equipos.listar(),
                    RetrofitBackend.solicitudes.listar(),
                    AppDatabase.getDatabase(context).usuarioDao().listar()
                )
            }
            equipos = datos.first.map { it.aEntidad() }
            solicitudes = datos.second.map { it.aEntidad() }
            usuarios = datos.third
            mensaje = null
        } catch (e: Exception) {
            mensaje = mensajeError(e)
        } finally {
            cargando = false
        }
    }

    fun nombreEquipo(id: Long): String = equipos.firstOrNull { it.id == id }?.nombre ?: "Equipo"
    fun nombreUsuario(id: Long): String = usuarios.firstOrNull { it.id == id }?.nombre ?: "Solicitante"
    fun limpiarMensaje() { mensaje = null }

    fun aprobar(context: Context, solicitudId: Long) {
        gestionar(context, solicitudId, "aprobada") {
            RetrofitBackend.solicitudes.aprobar(solicitudId)
        }
    }

    fun rechazar(context: Context, solicitudId: Long, motivo: String) {
        if (motivo.trim().length < 5) {
            mensaje = "Ingresa un motivo de al menos 5 caracteres"
            return
        }
        gestionar(context, solicitudId, "rechazada") {
            RetrofitBackend.solicitudes.rechazar(solicitudId, MotivoRechazo(motivo.trim()))
        }
    }

    fun entregar(context: Context, solicitudId: Long) {
        gestionar(context, solicitudId, "entregada") {
            RetrofitBackend.solicitudes.entregar(solicitudId)
        }
    }

    fun devolver(context: Context, solicitudId: Long) {
        gestionar(context, solicitudId, "devuelta") {
            RetrofitBackend.solicitudes.devolver(solicitudId)
        }
    }

    private fun gestionar(context: Context, solicitudId: Long, resultado: String, accion: suspend () -> Unit) {
        if (cargando) return
        viewModelScope.launch {
            cargando = true
            try {
                withContext(Dispatchers.IO) { accion() }
                RecursosNativos.vibrarConfirmacion(context)
                cargarDatos(context)
                if (mensaje == null) mensaje = "Solicitud $resultado correctamente"
            } catch (e: Exception) {
                mensaje = mensajeError(e)
            } finally {
                cargando = false
            }
        }
    }

    private fun EquipoRemoto.aEntidad() = EquipoEntity(
        id = id,
        nombre = nombre,
        categoria = categoria,
        descripcion = descripcion,
        ubicacion = ubicacion,
        stockTotal = stockTotal,
        stockDisponible = stockDisponible,
        estado = estado
    )

    private fun SolicitudRemota.aEntidad() = SolicitudEntity(
        id = id,
        usuarioId = usuarioId,
        equipoId = equipoId,
        cantidad = cantidad,
        sala = sala,
        inicio = inicio,
        devolucionEstimada = devolucionEstimada,
        estado = estado,
        motivoRechazo = motivoRechazo.orEmpty()
    )

    private fun mensajeError(error: Exception): String = when (error) {
        is HttpException -> "No fue posible completar la operación (${error.code()})"
        else -> "No se pudo conectar al pañol. Revisa que el servidor esté encendido"
    }
}
