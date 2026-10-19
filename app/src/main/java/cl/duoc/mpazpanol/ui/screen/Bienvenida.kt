package cl.duoc.mpazpanol.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.mpazpanol.ui.components.AccionPrincipal
import cl.duoc.mpazpanol.ui.components.Pantalla

@Composable
fun Bienvenida(onLogin: () -> Unit, onRegistro: () -> Unit) {
    Pantalla("MPAZ Pañol") {
        Spacer(Modifier.height(24.dp))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Icon(Icons.Filled.Inventory2, contentDescription = null,
                modifier = Modifier.height(100.dp).fillMaxWidth(0.35f), tint = MaterialTheme.colorScheme.primary)
            Text("Todo el equipamiento en un solo lugar",
                style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Text("Consulta disponibilidad, solicita materiales y registra préstamos de forma simple y segura.",
                style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(18.dp))
        AccionPrincipal("Iniciar sesión", onLogin)
        OutlinedButton(onClick = onRegistro, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Crear cuenta")
        }
        Text("Prototipo educativo · Datos de demostración", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
    }
}
