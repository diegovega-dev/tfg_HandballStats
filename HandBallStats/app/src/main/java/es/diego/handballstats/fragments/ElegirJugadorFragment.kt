package es.diego.handballstats.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.diego.handballstats.R
import es.diego.handballstats.adapters.ElegirJugadorAdapter
import es.diego.handballstats.models.Jugador

class ElegirJugadorFragment (
    private val jugadores: List<Jugador>,
    private val onJugadorSeleccionado: (Jugador?) -> Unit
    ) : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dejarVacioButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_elegir_jugador, container, false)
        recyclerView = view.findViewById(R.id.jugadoresRecyclerView)
        dejarVacioButton = view.findViewById(R.id.dejarVacioButton)

        isCancelable = true

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 5)
        val adapter = ElegirJugadorAdapter(jugadores) { jugadorSeleccionado ->
            onJugadorSeleccionado(jugadorSeleccionado)
            dismiss()
        }
        recyclerView.adapter = adapter

        dejarVacioButton.setOnClickListener {
            onJugadorSeleccionado(null)
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}