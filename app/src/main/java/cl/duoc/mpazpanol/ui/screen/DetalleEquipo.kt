package cl.duoc.mpazpanol.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cl.duoc.mpazpanol.data.local.entity.EquipoEntity
import cl.duoc.mpazpanol.ui.components.AccionPrincipal
import cl.duoc.mpazpanol.ui.components.EstadoChip
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.components.Tarjeta

@Composable
fun DetalleEquipo(equipo: EquipoEntity?, onBack: () -> Unit, onSolicitar: () -> Unit) {
    Pantalla("Detalle de equipo", onBack) {
        if (equipo == null) {
            Text("No se encontró el equipo")
        } else {
            Text(equipo.nombre, style = MaterialTheme.typography.headlineMedium)
            EstadoChip(if (equipo.stockDisponible > 0) equipo.estado else "RESERVADO")
            Tarjeta {
                Text("Información", style = MaterialTheme.typography.titleMedium)
                Text("Categoría: ${equipo.categoria}")
                Text(equipo.descripcion)
                Text("Ubicación en pañol: ${equipo.ubicacion}")
                Text("Unidades disponibles: ${equipo.stockDisponible} de ${equipo.stockTotal}")
            }
            AccionPrincipal("Solicitar este equipo", onSolicitar,
                habilitado = equipo.stockDisponible > 0 && equipo.estado == "DISPONIBLE")
        }
    }
}
