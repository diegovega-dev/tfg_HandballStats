package es.diego.handballstats.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Partido(
    var id: Int?,
    val nombre: String,
    val goles_favor: Int,
    val goles_contra: Int,
    val equipo: Equipo,
    val estadisticas: List<Estadistica>? = listOf(),
    val fecha: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)