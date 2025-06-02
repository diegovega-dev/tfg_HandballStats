package es.diego.handballstats.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.diego.handballstats.R
import es.diego.handballstats.activities.PartidoManager
import es.diego.handballstats.databinding.CardStatBinding
import es.diego.handballstats.models.Estadistica
import es.diego.handballstats.models.EstadisticaAtaque
import es.diego.handballstats.models.EstadisticaDefensa
import es.diego.handballstats.models.EstadisticaPortero
import es.diego.handballstats.models.enums.TipoEstadisticaAtaque

class ListaStatsAdapter(private var estadisticas: MutableList<Estadistica>, private val partido:PartidoManager) : RecyclerView.Adapter<ListaStatsAdapter.ViewHolder>() {

    inner class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val binding = CardStatBinding.bind(vista)

        fun bind(stat:Estadistica, pos:Int) {
            when(stat){
                is EstadisticaAtaque -> {
                    binding.tipoStat.text = "${stat.tipo} min: ${stat.minuto}"
                    binding.nombreJugador.text = stat.jugador?.nombre.toString()
                }

                is EstadisticaDefensa -> {
                    binding.tipoStat.text = "${stat.tipo} min: ${stat.minuto}"
                    binding.nombreJugador.text = stat.jugador?.nombre.toString()
                }
                is EstadisticaPortero -> {
                    binding.tipoStat.text = "${stat.tipo} min: ${stat.minuto}"
                    binding.nombreJugador.text = stat.jugador?.nombre.toString()
                }
            }

            binding.borrarPartido.setOnClickListener{
                if (stat is EstadisticaAtaque && stat.tipo == TipoEstadisticaAtaque.GOL) {
                    partido.restarGolAFavor()
                }
                partido.eliminarEstadistica(pos)
                actualizarLista(partido.estadisticas.value ?: emptyList())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_stat, parent, false)
        return ViewHolder(vista)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(estadisticas[position], position)
    }

    override fun getItemCount(): Int = estadisticas.size

    fun actualizarLista(nuevaLista: List<Estadistica>) {
        estadisticas.clear()
        estadisticas.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}