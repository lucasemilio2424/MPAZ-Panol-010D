package cl.duoc.mpazpanol.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazpanol.data.remote.ClimaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ClimaViewModel : ViewModel() {
    var informacion by mutableStateOf("Consultando condiciones meteorológicas...")
        private set

    fun cargar() {
        viewModelScope.launch {
            informacion = "Consultando condiciones meteorológicas..."
            try {
                val respuesta = withContext(Dispatchers.IO) {
                    Retrofit.Builder()
                        .baseUrl("https://api.open-meteo.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(ClimaApi::class.java)
                        .consultar(
                            -33.45,
                            -70.66,
                            "temperature_2m,precipitation,wind_speed_10m",
                            "America/Santiago"
                        )
                }
                informacion = respuesta.current?.let { datos ->
                    "Santiago: ${datos.temperature_2m} °C · Precipitación: ${datos.precipitation} mm · Viento: ${datos.wind_speed_10m} km/h"
                } ?: "No hay condiciones meteorológicas disponibles"
            } catch (_: Exception) {
                informacion = "No se pudo consultar el clima. El pañol continúa funcionando."
            }
        }
    }
}
