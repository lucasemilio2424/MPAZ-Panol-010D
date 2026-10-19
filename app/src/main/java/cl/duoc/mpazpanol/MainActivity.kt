package cl.duoc.mpazpanol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.duoc.mpazpanol.ui.navigation.AppNavigation
import cl.duoc.mpazpanol.ui.theme.MPAZTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MPAZTheme { AppNavigation() }
        }
    }
}
