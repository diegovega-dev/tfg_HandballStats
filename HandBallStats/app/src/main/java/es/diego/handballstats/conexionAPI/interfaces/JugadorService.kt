package es.diego.handballstats.conexionAPI.interfaces

import es.diego.handballstats.models.Jugador
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface JugadorService {

    @POST("jugadores/crear")
    suspend fun crearJugador(
        @Header("Authorization") authHeader: String,
        @Body jugador: Jugador
    ): Response<Jugador>

    @DELETE("jugadores/borrar")
    suspend fun borrarJugador(
        @Header("Authorization") authHeader: String,
        @Query("id_jugador") idJugador: Int
    ): Response<ResponseBody>

    @GET("jugadores/buscar")
    suspend fun buscarJugadores(
        @Header("Authorization") authHeader: String,
        @Query("id_equipo") idEquipo: Int
    ): Response<MutableList<Jugador>>

    @PUT("jugadores/actualizar")
    suspend fun actualizarJugador(
        @Header("Authorization") authHeader: String,
        @Body jugador: Jugador
    ) :Response<Jugador>
}