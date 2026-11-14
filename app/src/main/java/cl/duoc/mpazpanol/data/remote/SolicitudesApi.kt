package cl.duoc.mpazpanol.data.remote
import retrofit2.http.*
interface SolicitudesApi {
    @GET("api/solicitudes") suspend fun listar(@Query("usuarioId") usuarioId: Long?=null):List<SolicitudRemota>
    @POST("api/solicitudes") suspend fun crear(@Body solicitud: SolicitudNueva):SolicitudRemota
    @PUT("api/solicitudes/{id}/aprobar") suspend fun aprobar(@Path("id") id:Long):SolicitudRemota
    @PUT("api/solicitudes/{id}/rechazar") suspend fun rechazar(@Path("id") id:Long,@Body motivo:MotivoRechazo):SolicitudRemota
    @PUT("api/solicitudes/{id}/entregar") suspend fun entregar(@Path("id") id:Long):SolicitudRemota
    @PUT("api/solicitudes/{id}/devolver") suspend fun devolver(@Path("id") id:Long):SolicitudRemota
}
