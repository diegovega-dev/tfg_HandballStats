package es.diego.handballstats.services

import es.diego.handballstats.models.Entrenador
import es.diego.handballstats.models.Equipo
import es.diego.handballstats.models.EstadisticaAtaque
import es.diego.handballstats.models.EstadisticaDefensa
import es.diego.handballstats.models.Jugador

object Sesion {
    var token: String? = null
    var user: Entrenador? = null
    var equipo: Equipo? =null
    var jugador: Jugador? = null
    var jugadores: MutableList<Jugador> = mutableListOf()
    var statsAtaque: List<EstadisticaAtaque> = listOf()
    var statsDefensa: List<EstadisticaDefensa> = listOf()
}
