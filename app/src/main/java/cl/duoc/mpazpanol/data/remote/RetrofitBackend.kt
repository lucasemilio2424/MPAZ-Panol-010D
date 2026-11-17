
package cl.duoc.mpazpanol.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBackend {

    private val clienteHttp = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .callTimeout(150, TimeUnit.SECONDS)
        .build()

    val equipos: EquiposApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://mpaz-equipos-010d.onrender.com/")
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EquiposApi::class.java)
    }

    val solicitudes: SolicitudesApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://mpaz-solicitudes-010d.onrender.com/")
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SolicitudesApi::class.java)
    }
}
