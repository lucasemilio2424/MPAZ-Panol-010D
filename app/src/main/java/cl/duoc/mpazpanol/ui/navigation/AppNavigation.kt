package cl.duoc.mpazpanol.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.mpazpanol.ui.screen.Administracion
import cl.duoc.mpazpanol.ui.screen.Bienvenida
import cl.duoc.mpazpanol.ui.screen.Catalogo
import cl.duoc.mpazpanol.ui.screen.DetalleEquipo
import cl.duoc.mpazpanol.ui.screen.Inicio
import cl.duoc.mpazpanol.ui.screen.Login
import cl.duoc.mpazpanol.ui.screen.MisSolicitudes
import cl.duoc.mpazpanol.ui.screen.NuevaSolicitud
import cl.duoc.mpazpanol.ui.screen.Registro
import cl.duoc.mpazpanol.ui.viewmodel.PanolViewModel
import cl.duoc.mpazpanol.ui.viewmodel.SessionViewModel

object Routes {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val INICIO = "inicio"
    const val CATALOGO = "catalogo"
    const val DETALLE = "detalle/{id}"
    const val SOLICITAR = "solicitar/{id}"
    const val MIS_SOLICITUDES = "mis_solicitudes"
    const val ADMIN = "administracion"
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val sesion: SessionViewModel = viewModel()
    val panol: PanolViewModel = viewModel()

    LaunchedEffect(Unit) { panol.iniciar(context) }

    NavHost(navController = nav, startDestination = Routes.BIENVENIDA) {
        composable(Routes.BIENVENIDA) {
            Bienvenida(onLogin = { nav.navigate(Routes.LOGIN) }, onRegistro = { nav.navigate(Routes.REGISTRO) })
        }
        composable(Routes.LOGIN) {
            Login(onBack = { nav.popBackStack() }, onRegistro = { nav.navigate(Routes.REGISTRO) },
                onSuccess = { usuario ->
                    sesion.iniciar(usuario)
                    panol.actualizar(context)
                    nav.navigate(Routes.INICIO) {
                        popUpTo(Routes.BIENVENIDA) { inclusive = true }
                    }
                })
        }
        composable(Routes.REGISTRO) {
            Registro(onBack = { nav.popBackStack() }, onSuccess = {
                nav.navigate(Routes.LOGIN) { popUpTo(Routes.REGISTRO) { inclusive = true } }
            })
        }
        composable(Routes.INICIO) {
            val usuario = sesion.usuario
            if (usuario == null) Text("Debes iniciar sesión") else Inicio(
                usuario = usuario, vm = panol,
                onCatalogo = { nav.navigate(Routes.CATALOGO) },
                onSolicitar = { nav.navigate("solicitar/0") },
                onMisSolicitudes = { panol.actualizar(context); nav.navigate(Routes.MIS_SOLICITUDES) },
                onAdministrar = { panol.actualizar(context); nav.navigate(Routes.ADMIN) },
                onCerrarSesion = {
                    sesion.cerrar()
                    nav.navigate(Routes.BIENVENIDA) { popUpTo(Routes.INICIO) { inclusive = true } }
                }
            )
        }
        composable(Routes.CATALOGO) {
            Catalogo(panol.equipos, onBack = { nav.popBackStack() },
                onDetalle = { nav.navigate("detalle/$it") })
        }
        composable(Routes.DETALLE, arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            DetalleEquipo(panol.equipos.firstOrNull { it.id == id },
                onBack = { nav.popBackStack() }, onSolicitar = { nav.navigate("solicitar/$id") })
        }
        composable(Routes.SOLICITAR, arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
            val usuario = sesion.usuario
            if (usuario == null) Text("Debes iniciar sesión") else NuevaSolicitud(
                usuarioId = usuario.id,
                equipoInicialId = entry.arguments?.getLong("id") ?: 0L,
                equipos = panol.equipos,
                onBack = { nav.popBackStack() },
                onSuccess = {
                    panol.actualizar(context)
                    nav.navigate(Routes.MIS_SOLICITUDES) { popUpTo(Routes.INICIO) }
                }
            )
        }
        composable(Routes.MIS_SOLICITUDES) {
            val usuario = sesion.usuario
            if (usuario == null) Text("Debes iniciar sesión") else MisSolicitudes(
                usuarioId = usuario.id, vm = panol, onBack = { nav.popBackStack() })
        }
        composable(Routes.ADMIN) {
            if (sesion.usuario?.rol == "ADMIN") Administracion(panol, onBack = { nav.popBackStack() })
            else Text("Acceso exclusivo del encargado")
        }
    }
}
