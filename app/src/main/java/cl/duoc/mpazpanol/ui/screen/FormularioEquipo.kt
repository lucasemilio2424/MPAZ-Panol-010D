
package cl.duoc.mpazpanol.ui.screen

import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.mpazpanol.data.local.entity.EquipoEntity
import cl.duoc.mpazpanol.ui.components.AccionPrincipal
import cl.duoc.mpazpanol.ui.components.Campo
import cl.duoc.mpazpanol.ui.components.MensajeError
import cl.duoc.mpazpanol.ui.components.Pantalla
import cl.duoc.mpazpanol.ui.viewmodel.InventarioViewModel

@Composable
fun FormularioEquipo(
    equipo: EquipoEntity?,
    onBack: () -> Unit,
    onGuardado: () -> Unit,
    vm: InventarioViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(equipo?.id) {
        vm.preparar(equipo)
    }

    val estado = vm.uiState
    val editando = equipo != null

    Pantalla(
        titulo = if (editando) "Editar equipo" else "Agregar equipo",
        onBack = onBack
    ) {

        Text(
            if (editando)
                "Modifica los datos del equipo seleccionado."
            else
                "Registra un nuevo equipo o material del colegio.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            "Datos del equipo",
            style = MaterialTheme.typography.titleMedium
        )

        Campo(
            valor = estado.nombre,
            cambiar = vm::cambiarNombre,
            etiqueta = "Nombre del equipo",
            error = estado.nombreError
        )

        Campo(
            valor = estado.categoria,
            cambiar = vm::cambiarCategoria,
            etiqueta = "Categoria",
            error = estado.categoriaError
        )

        Campo(
            valor = estado.descripcion,
            cambiar = vm::cambiarDescripcion,
            etiqueta = "Descripcion",
            error = estado.descripcionError
        )

        Campo(
            valor = estado.ubicacion,
            cambiar = vm::cambiarUbicacion,
            etiqueta = "Ubicacion en el pañol",
            error = estado.ubicacionError
        )

        Text(
            "Control de inventario",
            style = MaterialTheme.typography.titleMedium
        )

        Campo(
            valor = estado.cantidad,
            cambiar = vm::cambiarCantidad,
            etiqueta = "Cantidad total",
            error = estado.cantidadError
        )

        if (editando && equipo != null) {
            Text(
                "Stock actual: ${equipo.stockTotal} unidades",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                "Disponibles: ${equipo.stockDisponible} unidades",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            "Estado del equipo",
            style = MaterialTheme.typography.titleMedium
        )

        listOf(
            "DISPONIBLE" to "Disponible",
            "MANTENIMIENTO" to "En mantenimiento",
            "FUERA_SERVICIO" to "Fuera de servicio"
        ).forEach { (valor, etiqueta) ->

            FilterChip(
                selected = estado.estado == valor,
                onClick = { vm.cambiarEstado(valor) },
                label = { Text(etiqueta) }
            )
        }

        MensajeError(estado.mensaje)

        AccionPrincipal(
            texto = when {
                estado.guardando -> "Guardando..."
                editando -> "Guardar cambios"
                else -> "Registrar equipo"
            },
            onClick = {
                vm.guardar(
                    context = context,
                    equipoId = equipo?.id ?: 0L,
                    onGuardado = onGuardado
                )
            },
            habilitado = !estado.guardando
        )
    }
}
