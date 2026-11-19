package cl.duoc.mpazpanol.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReglasSolicitudTest {
    @Test fun cantidadValida() {
        assertNull(ReglasSolicitud.errorCantidad(2, 5, "DISPONIBLE"))
    }

    @Test fun cantidadVacia() {
        assertEquals("Indica al menos una unidad", ReglasSolicitud.errorCantidad(null, 5, "DISPONIBLE"))
    }

    @Test fun cantidadCero() {
        assertEquals("Indica al menos una unidad", ReglasSolicitud.errorCantidad(0, 5, "DISPONIBLE"))
    }

    @Test fun equipoFueraDeServicio() {
        assertEquals("El equipo no está disponible", ReglasSolicitud.errorCantidad(1, 5, "FUERA_SERVICIO"))
    }

    @Test fun inventarioInsuficiente() {
        assertEquals("Solo hay 2 unidades disponibles", ReglasSolicitud.errorCantidad(3, 2, "DISPONIBLE"))
    }

    @Test fun salaValida() {
        assertNull(ReglasSolicitud.errorSala("  Laboratorio 1 "))
    }

    @Test fun salaInvalida() {
        assertEquals("Indica la sala o laboratorio", ReglasSolicitud.errorSala(" a "))
    }

    @Test fun horarioSinFecha() {
        assertEquals("Selecciona fecha, hora de inicio y devolución", ReglasSolicitud.errorHorario(null, null, 1000L))
    }

    @Test fun horarioMenosDeTresHoras() {
        assertEquals("Solicita con al menos 3 horas de anticipación", ReglasSolicitud.errorHorario(1000L + 2 * 3600000L, 1000L + 5 * 3600000L, 1000L))
    }

    @Test fun devolucionAntesDelInicio() {
        assertEquals("La devolución debe ser posterior al inicio", ReglasSolicitud.errorHorario(1000L + 4 * 3600000L, 1000L + 3 * 3600000L, 1000L))
    }

    @Test fun horarioValido() {
        assertNull(ReglasSolicitud.errorHorario(1000L + 4 * 3600000L, 1000L + 5 * 3600000L, 1000L))
    }
}
