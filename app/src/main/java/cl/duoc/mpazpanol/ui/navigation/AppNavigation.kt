
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
import cl.duoc.mpazpanol.ui.screen.FormularioEquipo
import cl.duoc.mpazpanol.ui.screen.GestionInventario
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

    const val INVENTARIO = "inventario"
    const val FORMULARIO_EQUIPO = "formulario_equipo/{id}"
}

@Composable
fun AppNavigation() {

    val nav = rememberNavController()
    val context = LocalContext.current

    val sesion: SessionViewModel = viewModel()
    val panol: PanolViewModel = viewModel()

    LaunchedEffect(Unit) {
        panol.iniciar(context)
    }

    NavHost(
        navController = nav,
        startDestination = Routes.BIENVENIDA
    ) {

        composable(Routes.BIENVENIDA) {
            Bienvenida(
                onLogin = {
                    nav.navigate(Routes.LOGIN)
                },
                onRegistro = {
                    nav.navigate(Routes.REGISTRO)
                }
            )
        }

        composable(Routes.LOGIN) {
            Login(
                onBack = {
                    nav.popBackStack()
                },
                onRegistro = {
                    nav.navigate(Routes.REGISTRO)
                },
                onSuccess = { usuario ->
                    sesion.iniciar(usuario)
                    panol.actualizar(context)

                    nav.navigate(Routes.INICIO) {
                        popUpTo(Routes.BIENVENIDA) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.REGISTRO) {
            Registro(
                onBack = {
                    nav.popBackStack()
                },
                onSuccess = {
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTRO) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.INICIO) {
            val usuario = sesion.usuario

            if (usuario == null) {
                Text("Debes iniciar sesión")
            } else {
                Inicio(
                    usuario = usuario,
                    vm = panol,

                    onCatalogo = {
                        panol.actualizar(context)
                        nav.navigate(Routes.CATALOGO)
                    },

                    onSolicitar = {
                        nav.navigate("solicitar/0")
                    },

                    onMisSolicitudes = {
                        panol.actualizar(context)
                        nav.navigate(Routes.MIS_SOLICITUDES)
                    },

                    onAdministrar = {
                        panol.actualizar(context)
                        nav.navigate(Routes.ADMIN)
                    },

                    onInventario = {
                        panol.actualizar(context)
                        nav.navigate(Routes.INVENTARIO)
                    },

                    onCerrarSesion = {
                        sesion.cerrar()

                        nav.navigate(Routes.BIENVENIDA) {
                            popUpTo(Routes.INICIO) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        composable(Routes.CATALOGO) {
            Catalogo(
                equipos = panol.equipos,
                onBack = {
                    nav.popBackStack()
                },
                onDetalle = { id ->
                    nav.navigate("detalle/$id")
                }
            )
        }

        composable(
            route = Routes.DETALLE,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { entry ->

            val id = entry.arguments?.getLong("id") ?: 0L

            DetalleEquipo(
                equipo = panol.equipos.firstOrNull {
                    it.id == id
                },
                onBack = {
                    nav.popBackStack()
                },
                onSolicitar = {
                    nav.navigate("solicitar/$id")
                }
            )
        }

        composable(
            route = Routes.SOLICITAR,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { entry ->

            val usuario = sesion.usuario

            if (usuario == null) {
                Text("Debes iniciar sesión")
            } else {
                NuevaSolicitud(
                    usuarioId = usuario.id,
                    equipoInicialId =
                        entry.arguments?.getLong("id") ?: 0L,
                    equipos = panol.equipos,
                    onBack = {
                        nav.popBackStack()
                    },
                    onSuccess = {
                        panol.actualizar(context)

                        nav.navigate(Routes.MIS_SOLICITUDES) {
                            popUpTo(Routes.INICIO)
                        }
                    }
                )
            }
        }

        composable(Routes.MIS_SOLICITUDES) {

            val usuario = sesion.usuario

            if (usuario == null) {
                Text("Debes iniciar sesión")
            } else {
                MisSolicitudes(
                    usuarioId = usuario.id,
                    vm = panol,
                    onBack = {
                        nav.popBackStack()
                    }
                )
            }
        }

        composable(Routes.ADMIN) {
            if (sesion.usuario?.rol == "ADMIN") {
                Administracion(
                    vm = panol,
                    onBack = {
                        nav.popBackStack()
                    }
                )
            } else {
                Text("Acceso exclusivo del encargado")
            }
        }

        // Gestion del inventario

        composable(Routes.INVENTARIO) {

            if (sesion.usuario?.rol == "ADMIN") {

                GestionInventario(
                    equipos = panol.equipos,

                    onBack = {
                        nav.popBackStack()
                    },

                    onAgregar = {
                        nav.navigate("formulario_equipo/0")
                    },

                    onEditar = { id ->
                        nav.navigate("formulario_equipo/$id")
                    }
                )

            } else {
                Text("Acceso exclusivo del encargado")
            }
        }

        // Agregar o editar equipos

        composable(
            route = Routes.FORMULARIO_EQUIPO,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { entry ->

            if (sesion.usuario?.rol == "ADMIN") {

                val id = entry.arguments?.getLong("id") ?: 0L

                val equipo = panol.equipos.firstOrNull {
                    it.id == id
                }

                FormularioEquipo(
                    equipo = equipo,

                    onBack = {
                        nav.popBackStack()
                    },

                    onGuardado = {
                        panol.actualizar(context)
                        nav.popBackStack()
                    }
                )

            } else {
                Text("Acceso exclusivo del encargado")
            }
        }
    }
}
