package es.diego.handballstats.models

import com.google.gson.annotations.Expose

data class Entrenador(
    val id: Int?,
    var nombre: String,
    var apellidos: String?,
    val email: String,
    var password: String,
    var foto: String?,
    val equipos: MutableList<Equipo> = mutableListOf()
)