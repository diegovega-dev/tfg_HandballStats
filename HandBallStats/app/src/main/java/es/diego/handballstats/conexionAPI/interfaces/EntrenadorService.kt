package es.diego.handballstats.conexionAPI.interfaces

import es.diego.handballstats.models.Entrenador
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query

interface EntrenadorService {

    @GET("entrenadores/buscar")
    suspend fun buscarEntrenador(
        @Header("Authorization") authHeader: String,
        @Query("email_entrenador") email: String
    ): Response<Entrenador>

    @GET("entrenadores/existe")
    suspend fun existeEntrenador(
        @Query("email_entrenador") email: String
    ):Response<Boolean>

    @DELETE("entrenadores/borrar")
    suspend fun borrarEntrenador(
        @Header("Authorization") authHeader: String,
        @Query("id_entrenador") id: Int
    ): Response<String>

    @PUT("entrenadores/actualizar")
    suspend fun actualizarEntrenador(
        @Body entrenador: Entrenador
    ) :Response<Entrenador>
}