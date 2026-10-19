package cl.duoc.mpazpanol.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cl.duoc.mpazpanol.data.local.entity.EquipoEntity
import cl.duoc.mpazpanol.ui.components.Campo
import cl.duoc.mpazpanol.ui.components.EstadoChip
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.components.Tarjeta

@Composable
fun Catalogo(equipos: List<EquipoEntity>, onBack: () -> Unit, onDetalle: (Long) -> Unit) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    val filtrados = equipos.filter {
        it.nombre.contains(busqueda, ignoreCase = true) || it.categoria.contains(busqueda, ignoreCase = true)
    }
    Pantalla("Catálogo de equipos", onBack) {
        Campo(busqueda, { busqueda = it }, "Buscar por nombre o categoría")
        Text("${filtrados.size} resultados", color = MaterialTheme.colorScheme.onSurfaceVariant)
        filtrados.forEach { equipo ->
            Tarjeta {
                Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
                Text(equipo.categoria, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    EstadoChip(if (equipo.stockDisponible > 0) equipo.estado else "RESERVADO")
                    Text("${equipo.stockDisponible} disponible(s)")
                }
                OutlinedButton(onClick = { onDetalle(equipo.id) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver detalles")
                }
            }
        }
        if (filtrados.isEmpty()) Text("No encontramos equipos con ese nombre. Prueba otra búsqueda.")
    }
}
