package es.diego.handballstats.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.diego.handballstats.R
import es.diego.handballstats.services.Sesion
import es.diego.handballstats.adapters.JugadoresAdapter
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.models.Jugador
import kotlinx.coroutines.launch

class JugadoresFragment(): Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JugadoresAdapter
    private var jugadores: MutableList<Jugador> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.content, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        cargarJugadores()

        return view
    }

    private fun cargarJugadores() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.jugadorService.buscarJugadores(Sesion.token!!, Sesion.equipo?.id!!)
                if (response.isSuccessful) {
                    jugadores = response.body()!!

                    jugadores.sortBy { it.dorsal }

                    Sesion.jugadores = jugadores

                    adapter = JugadoresAdapter(jugadores, viewLifecycleOwner)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            } catch (e:Exception) {
                Log.e("ERROR", "error: ${e.message}")}
        }
    }
}