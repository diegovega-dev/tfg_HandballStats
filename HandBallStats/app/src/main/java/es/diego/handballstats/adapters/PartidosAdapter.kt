package es.diego.handballstats.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import es.diego.handballstats.R
import es.diego.handballstats.services.Sesion
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.databinding.CardPartidoBinding
import es.diego.handballstats.models.Partido
import kotlinx.coroutines.launch

class PartidosAdapter(private val partidos: MutableList<Partido>, private val lifeCycleOwner: LifecycleOwner) : RecyclerView.Adapter<PartidosAdapter.PartidosViewHolder>() {

    inner class PartidosViewHolder(val vista: View): RecyclerView.ViewHolder(vista) {

        val binding = CardPartidoBinding.bind(vista)

        fun bind(partido: Partido) {
            binding.partidoFechaText.text = partido.fecha
            binding.partidoNombreText.text = partido.nombre
            binding.partidoResultadoText.text = partido.goles_favor.toString() + " - " + partido.goles_contra

            binding.borrarPartido.setOnClickListener() {
                val dialog = AlertDialog.Builder(vista.context)
                    .setTitle("Eliminar partido")
                    .setMessage("¿Seguro que desea borrar este partido?")
                    .setCancelable(true)
                    .setPositiveButton("Eliminar partido") { _, _ ->
                        val dialog = AlertDialog.Builder(vista.context)
                            .setCancelable(true)
                            .setTitle("Doble confimacion")
                            .setMessage("Esta operación no se puede cancelar, se eliminarán permanentemente el partido y todas las estadísticas asociadas")
                            .setNegativeButton("Cancelar", null)
                            .setPositiveButton("Eliminar partido"){a, _ ->
                                a.dismiss()
                                eliminarPartido(partido)
                            }
                            .create()

                        dialog.show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .create()

                dialog.show()
            }
        }
    }

    private fun eliminarPartido(partido: Partido) {
        lifeCycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.partidoService.borrarPartido(Sesion.token!!, partido.id!!)
                if (response.isSuccessful) {
                    partidos.remove(partido)
                    notifyDataSetChanged()
                }
            } catch (e:Exception) {
                Log.e("ERROR", "error: ${e.message}")}
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PartidosAdapter.PartidosViewHolder {
        return PartidosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_partido, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PartidosViewHolder, position: Int) {
        holder.bind(partidos[position])
    }

    override fun getItemCount(): Int = partidos.size

    fun agregarPartido(a: Partido) {
        partidos.add(a)
        notifyDataSetChanged()
    }

}