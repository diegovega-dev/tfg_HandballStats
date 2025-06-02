package es.diego.handballstats.models

import com.google.gson.annotations.Expose

data class Equipo(
    val id: Int?,
    var nombre: String,
    var categoria: String,
    var foto: String?,
    var entrenador: Entrenador?,
    var jugadores: MutableList<Jugador>? = mutableListOf(),
    var partidos: MutableList<Partido>? = mutableListOf()
) {
    override fun toString(): String {
        return nombre + ' ' + categoria + " entrenador: ${entrenador?.nombre}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Equipo) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }
}
