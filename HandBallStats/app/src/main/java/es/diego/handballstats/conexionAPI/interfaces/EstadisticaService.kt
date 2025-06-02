package es.diego.handballstats.conexionAPI.interfaces

import es.diego.handballstats.models.Estadistica
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface EstadisticaService {

    @POST("estadisticas/crear")
    suspend fun crearEstadistica(
        @Header("Authorization") authHeader: String,
        @Body estadisticas: MutableList<Estadistica>
    ): Response<List<Estadistica>>

    @GET("estadisticas/buscar-jugador")
    suspend fun buscarEstadisticaJugador(
        @Header("Authorization") authHeader: String,
        @Query("id_jugador") idJugador: Int
    ): Response<List<Estadistica>>

    @GET("estadisticas/buscar-equipo")
    suspend fun buscarEstadisticaEquipo(
        @Header("Authorization") authHeader: String,
        @Query("id_equipo") idEquipo: Int
    ): Response<List<Estadistica>>
}
