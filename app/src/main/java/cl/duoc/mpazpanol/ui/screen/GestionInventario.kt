
package cl.duoc.mpazpanol.ui.screen

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
import cl.duoc.mpazpanol.ui.components.AccionPrincipal
import cl.duoc.mpazpanol.ui.components.Campo
import cl.duoc.mpazpanol.ui.components.EstadoChip
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.components.Tarjeta

@Composable
fun GestionInventario(
    equipos: List<EquipoEntity>,
    onBack: () -> Unit,
    onAgregar: () -> Unit,
    onEditar: (Long) -> Unit
) {

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    val filtrados = equipos.filter { equipo ->
        equipo.nombre.contains(busqueda, ignoreCase = true) ||
                equipo.categoria.contains(busqueda, ignoreCase = true) ||
                equipo.ubicacion.contains(busqueda, ignoreCase = true)
    }.sortedBy { it.nombre }

    Pantalla("Gestionar inventario", onBack) {

        Text(
            "Administra los equipos y materiales del colegio.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AccionPrincipal(
            texto = "Agregar nuevo equipo",
            onClick = onAgregar
        )

        Campo(
            valor = busqueda,
            cambiar = { busqueda = it },
            etiqueta = "Buscar equipo o ubicacion"
        )

        Text(
            "${filtrados.size} equipos encontrados",
            style = MaterialTheme.typography.titleMedium
        )

        filtrados.forEach { equipo ->

            Tarjeta {

                Text(
                    equipo.nombre,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    equipo.categoria,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text("Ubicacion: ${equipo.ubicacion}")

                EstadoChip(equipo.estado)

                Text(
                    "Stock total: ${equipo.stockTotal}"
                )

                Text(
                    "Disponibles: ${equipo.stockDisponible}"
                )

                Text(
                    "Prestadas o reservadas: ${
                        equipo.stockTotal - equipo.stockDisponible
                    }"
                )

                OutlinedButton(
                    onClick = { onEditar(equipo.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar equipo")
                }
            }
        }

        if (filtrados.isEmpty()) {
            Text(
                "No se encontraron equipos con esa busqueda."
            )
        }
    }
}
