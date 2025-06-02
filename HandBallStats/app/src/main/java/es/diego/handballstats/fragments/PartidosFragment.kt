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
import es.diego.handballstats.adapters.PartidosAdapter
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.models.Partido
import kotlinx.coroutines.launch

class PartidosFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PartidosAdapter
    private var partidos: MutableList<Partido> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.content, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        cargarPartidos()

        return view
    }

    private fun cargarPartidos() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.partidoService.buscarPartidos(Sesion.token!!, Sesion.equipo?.id!!)
                if (response.isSuccessful) {
                    partidos = response.body()!!

                    partidos.sortByDescending { it.id }

                    Log.e("ERROR", "Fallo al hacer login: ${partidos}")

                    adapter = PartidosAdapter(partidos, viewLifecycleOwner)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            } catch (e:Exception) {Log.e("ERROR", "Fallo al hacer login: ${e.message}")}
        }
    }
}