package cl.duoc.mpazpanol.data.remote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** 10.0.2.2 es la IP del computador host desde el emulador Android Studio.
 * En un telefono fisico sustituir por la IP LAN del PC (mismo Wi-Fi).
 * Estas URLs HTTP son SOLO para desarrollo local, no produccion.
 */
object RetrofitBackend {
    private const val HOST = "10.0.2.2"
    val equipos: EquiposApi by lazy {
        Retrofit.Builder().baseUrl("http://$HOST:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(EquiposApi::class.java)
    }
    val solicitudes: SolicitudesApi by lazy {
        Retrofit.Builder().baseUrl("http://$HOST:8082/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SolicitudesApi::class.java)
    }
}
