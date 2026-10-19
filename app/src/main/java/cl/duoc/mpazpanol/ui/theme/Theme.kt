package cl.duoc.mpazpanol.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val VerdeBosque = Color(0xFF173D2E)
val Grafito = Color(0xFF171D1A)
val Marfil = Color(0xFFF5F3EC)
val VerdeSuave = Color(0xFFA7C9B5)
val TextoSecundario = Color(0xFF536158)

private val Colores = lightColorScheme(
    primary = VerdeBosque,
    onPrimary = Color.White,
    secondary = Color(0xFF496D5C),
    onSecondary = Color.White,
    background = Marfil,
    onBackground = Grafito,
    surface = Color.White,
    onSurface = Grafito,
    surfaceVariant = Color(0xFFE8EEE8),
    onSurfaceVariant = TextoSecundario,
    error = Color(0xFFAA3535),
    onError = Color.White
)

private val Tipografia = Typography(
    headlineLarge = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 17.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 22.sp),
    labelLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
)

@Composable
fun MPAZTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colores, typography = Tipografia, content = content)
}
