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
import cl.duoc.mpazpanol.data.local.entity.SolicitudEntity
import cl.duoc.mpazpanol.data.local.entity.UsuarioEntity
import cl.duoc.mpazpanol.platform.RecursosNativos
import cl.duoc.mpazpanol.util.Passwords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PanolViewModel : ViewModel() {
    var equipos by mutableStateOf<List<EquipoEntity>>(emptyList())
        private set
    var solicitudes by mutableStateOf<List<SolicitudEntity>>(emptyList())
        private set
    var usuarios by mutableStateOf<List<UsuarioEntity>>(emptyList())
        private set
    var cargando by mutableStateOf(true)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    private var inicializando = false

    fun iniciar(context: Context) {
        if (inicializando) return
        inicializando = true
        viewModelScope.launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val haceFaltaAdmin = withContext(Dispatchers.IO) {
                    db.usuarioDao().buscarPorCorreo("admin@mpaz.cl") == null
                }
                if (haceFaltaAdmin) {
                    val hash = withContext(Dispatchers.Default) { Passwords.hash("Admin123!") }
                    withContext(Dispatchers.IO) {
                        db.usuarioDao().insertar(UsuarioEntity(
                            nombre = "Encargado del pañol", email = "admin@mpaz.cl",
                            passwordHash = hash, rol = "ADMIN"
                        ))
                    }
                }
                withContext(Dispatchers.IO) {
                    if (db.equipoDao().contar() == 0) {
                        db.equipoDao().insertarTodos(listOf(
                            EquipoEntity(nombre = "Proyector Epson", categoria = "Proyección", descripcion = "Proyector para salas de clases", ubicacion = "Estante A1", stockTotal = 5, stockDisponible = 5),
                            EquipoEntity(nombre = "Notebook Lenovo", categoria = "Computación", descripcion = "Notebook para actividades docentes", ubicacion = "Gabinete B2", stockTotal = 8, stockDisponible = 8),
                            EquipoEntity(nombre = "Parlante portátil", categoria = "Audio", descripcion = "Parlante para actividades escolares", ubicacion = "Estante A3", stockTotal = 4, stockDisponible = 4),
                            EquipoEntity(nombre = "Kit LEGO WeDo 2.0", categoria = "Robótica", descripcion = "Kit educativo de robótica", ubicacion = "Gabinete C1", stockTotal = 3, stockDisponible = 3),
                            EquipoEntity(nombre = "Placa ESP32", categoria = "Electrónica", descripcion = "Microcontrolador para talleres", ubicacion = "Gabinete C2", stockTotal = 6, stockDisponible = 6),
                            EquipoEntity(nombre = "Alargador eléctrico", categoria = "Accesorios", descripcion = "Extensión eléctrica para salas", ubicacion = "Estante A4", stockTotal = 10, stockDisponible = 10)
                        ))
                    }
                }
                cargarDatos(context)
            } catch (_: Exception) {
                mensaje = "No se pudieron cargar los datos locales"
            } finally {
                cargando = false
            }
        }
    }

    fun actualizar(context: Context) {
        viewModelScope.launch { cargarDatos(context) }
    }

    private suspend fun cargarDatos(context: Context) {
        val db = AppDatabase.getDatabase(context)
        val datos = withContext(Dispatchers.IO) {
            Triple(db.equipoDao().listar(), db.solicitudDao().listarTodas(), db.usuarioDao().listar())
        }
        equipos = datos.first
        solicitudes = datos.second
        usuarios = datos.third
    }

    fun nombreEquipo(id: Long): String = equipos.firstOrNull { it.id == id }?.nombre ?: "Equipo"
    fun nombreUsuario(id: Long): String = usuarios.firstOrNull { it.id == id }?.nombre ?: "Solicitante"
    fun limpiarMensaje() { mensaje = null }

    fun aprobar(context: Context, solicitudId: Long) {
        operar(context, solicitudId) { db, actual ->
            if (actual.estado != "PENDIENTE") return@operar "Esta solicitud ya fue gestionada"
            val equipo = db.equipoDao().buscar(actual.equipoId) ?: return@operar "Equipo no encontrado"
            if (equipo.estado == "MANTENIMIENTO" || equipo.estado == "FUERA_SERVICIO") return@operar "Equipo fuera de servicio"
            if (equipo.stockDisponible < actual.cantidad) return@operar "No hay suficientes unidades disponibles"
            val restante = equipo.stockDisponible - actual.cantidad
            db.equipoDao().actualizar(equipo.copy(stockDisponible = restante, estado = if (restante == 0) "RESERVADO" else "DISPONIBLE"))
            db.solicitudDao().actualizar(actual.copy(estado = "APROBADA", aprobadaEn = System.currentTimeMillis()))
            "Solicitud aprobada y stock actualizado"
        }
    }

    fun rechazar(context: Context, solicitudId: Long, motivo: String) {
        if (motivo.trim().length < 5) {
            mensaje = "Escribe el motivo del rechazo (al menos 5 caracteres)"
            return
        }
        operar(context, solicitudId) { db, actual ->
            if (actual.estado != "PENDIENTE") return@operar "Esta solicitud ya fue gestionada"
            db.solicitudDao().actualizar(actual.copy(estado = "RECHAZADA", motivoRechazo = motivo.trim()))
            "Solicitud rechazada con justificación"
        }
    }

    fun entregar(context: Context, solicitudId: Long) {
        operar(context, solicitudId) { db, actual ->
            if (actual.estado != "APROBADA") return@operar "Primero debes aprobar la solicitud"
            db.solicitudDao().actualizar(actual.copy(estado = "ENTREGADA", entregadaEn = System.currentTimeMillis()))
            "Entrega registrada"
        }
    }

    fun devolver(context: Context, solicitudId: Long) {
        operar(context, solicitudId) { db, actual ->
            if (actual.estado != "ENTREGADA") return@operar "Esta solicitud no está entregada"
            val equipo = db.equipoDao().buscar(actual.equipoId) ?: return@operar "Equipo no encontrado"
            db.equipoDao().actualizar(equipo.copy(
                stockDisponible = (equipo.stockDisponible + actual.cantidad).coerceAtMost(equipo.stockTotal),
                estado = "DISPONIBLE"
            ))
            db.solicitudDao().actualizar(actual.copy(estado = "DEVUELTA", devueltaEn = System.currentTimeMillis()))
            "Devolución registrada y stock restaurado"
        }
    }

    private fun operar(
        context: Context,
        solicitudId: Long,
        accion: suspend (AppDatabase, SolicitudEntity) -> String
    ) {
        viewModelScope.launch {
            try {
                val resultado = withContext(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(context)
                    db.withTransaction {
                        val actual = db.solicitudDao().buscar(solicitudId)
                            ?: return@withTransaction "Solicitud no encontrada"
                        accion(db, actual)
                    }
                }
                mensaje = resultado
                cargarDatos(context)
                if (resultado.contains("registrad") || resultado.contains("aprobada") || resultado.contains("rechazada")) {
                    RecursosNativos.vibrarConfirmacion(context)
                }
            } catch (_: Exception) {
                mensaje = "No fue posible completar esta acción"
            }
        }
    }
}
