package es.diego.handballstats.conexionAPI.interfaces

import es.diego.handballstats.models.Equipo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface EquipoService {

    @POST("equipos/crear")
    suspend fun crearEquipo(
        @Header("Authorization") authHeader: String,
        @Body equipo: Equipo
    ): Response<Equipo>

    @DELETE("equipos/borrar")
    suspend fun borrarEquipo(
        @Header("Authorization") authHeader: String,
        @Query("id_equipo") id: Int
    ): Response<Void>

    @GET("equipos/buscar")
    suspend fun buscarEquipos(
        @Header("Authorization") authHeader: String,
        @Query("id_entrenador") idEntrenador: Int
    ): Response<MutableList<Equipo>>

    @GET("equipos/id")
    suspend fun buscarEquipoId(
        @Header("Authorization") authHeader: String,
        @Query("id_equipo") idEquipo: Int
    ): Response<Equipo>

    @PUT("equipos/actualizar")
    suspend fun actualizarEquipo(
        @Header("Authorization") authHeader: String,
        @Body equipo: Equipo
    ) :Response<Equipo>
}