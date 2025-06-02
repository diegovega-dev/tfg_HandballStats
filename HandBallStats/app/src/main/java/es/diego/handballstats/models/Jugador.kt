package es.diego.handballstats.models

data class Jugador(
    var id: Int?,
    var portero: Boolean = false,
    var nombre: String,
    var dorsal: Int,
    var foto: String?,
    var anio: Int?,
    var equipo: Equipo?,
    var estadisticas: MutableList<Estadistica> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Equipo) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }
}