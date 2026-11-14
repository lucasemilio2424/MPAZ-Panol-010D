package cl.duoc.mpazpanol.data.remote
import retrofit2.http.*
interface EquiposApi {
    @GET("api/equipos") suspend fun listar(): List<EquipoRemoto>
    @GET("api/equipos/{id}") suspend fun obtener(@Path("id") id: Long): EquipoRemoto
    @POST("api/equipos") suspend fun crear(@Body equipo: EquipoNuevo): EquipoRemoto
    @PUT("api/equipos/{id}") suspend fun actualizar(@Path("id") id: Long, @Body equipo: EquipoNuevo): EquipoRemoto
}
