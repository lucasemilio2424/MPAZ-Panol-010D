package cl.duoc.mpazpanol.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun fechaLegible(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy · HH:mm", Locale("es", "CL")).format(Date(millis))
