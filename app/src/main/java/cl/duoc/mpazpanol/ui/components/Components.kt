package cl.duoc.mpazpanol.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.mpazpanol.ui.theme.VerdeBosque

@Composable
fun Header(titulo: String, onBack: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().background(VerdeBosque).padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        } else {
            Icon(Icons.Filled.Inventory2, contentDescription = null, tint = Color.White)
        }
        Text(titulo, color = Color.White, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun Footer() {
    Text(
        "MPAZ Pañol · Escuela Escritora Marcela Paz",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
fun Pantalla(titulo: String, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(topBar = { Header(titulo, onBack) }, bottomBar = { Footer() }) { interior ->
        Column(
            modifier = Modifier.padding(interior).fillMaxWidth().verticalScroll(rememberScrollState())
                .imePadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
fun AccionPrincipal(texto: String, onClick: () -> Unit, habilitado: Boolean = true) {
    Button(
        onClick = onClick, enabled = habilitado,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(14.dp)
    ) { Text(texto) }
}

@Composable
fun Tarjeta(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
fun MensajeError(mensaje: String?) {
    AnimatedVisibility(mensaje != null) {
        if (mensaje != null) {
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.errorContainer) {
                Text(mensaje, color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.fillMaxWidth().padding(12.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun Campo(
    valor: String,
    cambiar: (String) -> Unit,
    etiqueta: String,
    error: String? = null,
    transformacion: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier,
    soloLectura: Boolean = false
) {
    OutlinedTextField(
        value = valor, onValueChange = cambiar, label = { Text(etiqueta) },
        modifier = modifier.fillMaxWidth(), singleLine = true, isError = error != null,
        visualTransformation = transformacion, readOnly = soloLectura,
        supportingText = if (error != null) ({ Text(error) }) else null,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun EstadoChip(estado: String) {
    Crossfade(targetState = estado, label = "cambio-de-estado") { valor ->
        val (fondo, texto) = when (valor) {
            "DISPONIBLE", "DEVUELTA" -> Color(0xFFE0F2E6) to Color(0xFF21563A)
            "APROBADA", "RESERVADO" -> Color(0xFFE2EAF8) to Color(0xFF214D8A)
            "PENDIENTE" -> Color(0xFFFFF0CC) to Color(0xFF805100)
            "RECHAZADA", "FUERA_SERVICIO" -> Color(0xFFFDE3E1) to Color(0xFF9C2828)
            else -> Color(0xFFEBECEA) to Color(0xFF4A514D)
        }
        Surface(color = fondo, shape = RoundedCornerShape(100.dp)) {
            Text(valor.replace('_', ' '), color = texto, style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
        }
    }
}
