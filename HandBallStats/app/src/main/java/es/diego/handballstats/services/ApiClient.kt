package es.diego.handballstats.services

import com.google.gson.GsonBuilder
import es.diego.handballstats.conexionAPI.interfaces.AuthService
import es.diego.handballstats.conexionAPI.interfaces.EntrenadorService
import es.diego.handballstats.conexionAPI.interfaces.EquipoService
import es.diego.handballstats.conexionAPI.interfaces.EstadisticaService
import es.diego.handballstats.conexionAPI.interfaces.JugadorService
import es.diego.handballstats.conexionAPI.interfaces.PartidoService
import es.diego.handballstats.models.Estadistica
import es.diego.handballstats.models.EstadisticaAtaque
import es.diego.handballstats.models.EstadisticaDefensa
import es.diego.handballstats.models.EstadisticaPortero
import es.diego.handballstats.services.serializacion.DualGsonConverterFactory
import es.diego.handballstats.services.serializacion.EstadisticaDeserializer
import es.diego.handballstats.services.serializacion.RuntimeTypeAdapterFactory
import retrofit2.Retrofit

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/HandballStats/"

    val runtimeTypeAdapterFactory =  RuntimeTypeAdapterFactory
        .of(Estadistica::class.java, "tipo_estadistica")
        .registerSubtype(EstadisticaAtaque::class.java, "ataque")
        .registerSubtype(EstadisticaDefensa::class.java, "defensa")
        .registerSubtype(EstadisticaPortero::class.java, "portero")

    val serializationGson = GsonBuilder()
        .registerTypeAdapterFactory(runtimeTypeAdapterFactory)  // para serializar
        .serializeNulls()
        .create()

    val deserializationGson = GsonBuilder()
        .registerTypeAdapter(Estadistica::class.java, EstadisticaDeserializer())  // para deserializar
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            DualGsonConverterFactory.create(serializationGson, deserializationGson)
        )
        .build()

    val entrenadorService: EntrenadorService = retrofit.create(EntrenadorService::class.java)
    val authService: AuthService = retrofit.create(AuthService::class.java)
    val equipoService: EquipoService = retrofit.create(EquipoService::class.java)
    val partidoService: PartidoService = retrofit.create(PartidoService::class.java)
    val jugadorService: JugadorService = retrofit.create(JugadorService::class.java)
    val statService: EstadisticaService = retrofit.create(EstadisticaService::class.java)

}