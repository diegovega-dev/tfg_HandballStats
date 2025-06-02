package es.diego.handballstats.models

import com.google.gson.annotations.SerializedName
import es.diego.handballstats.models.enums.*

sealed class Estadistica()

data class EstadisticaAtaque(
    @SerializedName("minuto") val minuto: Int,
    @SerializedName("jugador") val jugador: Jugador?,
    @SerializedName("tipo") val tipo: TipoEstadisticaAtaque,
    @SerializedName("posicion") val posicion: PosicionAtaque,
    @SerializedName("contrataque") val contrataque: Boolean = false,
    @SerializedName("distancia") val distancia: Distancia? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("partido") var partido: Partido? = null
) : Estadistica()

data class EstadisticaDefensa(
    @SerializedName("minuto") val minuto: Int,
    @SerializedName("jugador") val jugador: Jugador,
    @SerializedName("tipo") val tipo: TipoEstadisticaDefensa,
    @SerializedName("posicion") val posicion: PosicionDefensa,
    @SerializedName("partido") var partido: Partido? = null,
    @SerializedName("id") val id: Int? = null
) : Estadistica()

data class EstadisticaPortero(
    @SerializedName("minuto") val minuto: Int,
    @SerializedName("jugador") val jugador: Jugador,
    @SerializedName("partido") var partido: Partido?,
    @SerializedName("tipo") val tipo: TipoEstadisticaPortero?,
    @SerializedName("posicion") val posicion: PosicionAtaque?,
    @SerializedName("distancia") val distancia: Distancia?,
    @SerializedName("lado") val lado: LadoPorteria?,
    @SerializedName("id") val id: Int? = null
) : Estadistica()
