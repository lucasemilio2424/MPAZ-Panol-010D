package cl.duoc.mpazpanol.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.mpazpanol.data.local.entity.UsuarioEntity

class SessionViewModel : ViewModel() {
    var usuario by mutableStateOf<UsuarioEntity?>(null)
        private set

    fun iniciar(usuarioActivo: UsuarioEntity) { usuario = usuarioActivo }
    fun cerrar() { usuario = null }
}
