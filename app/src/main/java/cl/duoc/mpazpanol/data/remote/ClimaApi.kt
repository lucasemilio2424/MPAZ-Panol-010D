package cl.duoc.mpazpanol.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class ClimaActual(
    val temperature_2m: Double,
    val precipitation: Double,
    val wind_speed_10m: Double
)

data class RespuestaClima(val current: ClimaActual?)

interface ClimaApi {
    @GET("v1/forecast")
    suspend fun consultar(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") variables: String,
        @Query("timezone") zonaHoraria: String
    ): RespuestaClima
}
