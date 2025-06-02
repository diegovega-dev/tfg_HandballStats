package es.diego.handballstats.conexionAPI.interfaces

import es.diego.handballstats.models.Entrenador
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @POST("auth/login")
    suspend fun login(
        @Query("email") email: String,
        @Query("pass") pass: String
    ): Response<ResponseBody>

    @POST("auth/register")
    suspend fun register(
        @Body entrenador: Entrenador
    ): Response<ResponseBody>
}