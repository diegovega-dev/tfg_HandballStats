package es.diego.handballstats.conexionAPI.interfaces

import es.diego.handballstats.models.Partido
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PartidoService {

    @POST("partidos/crear")
    suspend fun crearPartido(
        @Header("Authorization") token: String,
        @Body partido: Partido
    ): Response<Partido>

    @DELETE("partidos/borrar")
    suspend fun borrarPartido(
        @Header("Authorization") token: String,
        @Query("id_partido") idPartido: Int
    ): Response<ResponseBody>

    @GET("partidos/buscar")
    suspend fun buscarPartidos(
        @Header("Authorization") token: String,
        @Query("id_equipo") idEquipo: Int
    ): Response<MutableList<Partido>>
}
