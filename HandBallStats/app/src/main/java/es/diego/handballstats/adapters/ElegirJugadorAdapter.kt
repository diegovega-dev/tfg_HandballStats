package es.diego.handballstats.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.diego.handballstats.R
import es.diego.handballstats.models.Jugador
import java.io.File

class ElegirJugadorAdapter (
    private val listaJugadores: List<Jugador>,
    private val onClick: (Jugador) -> Unit
    ) : RecyclerView.Adapter<ElegirJugadorAdapter.JugadorViewHolder>() {

    inner class JugadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foto: ImageView = itemView.findViewById(R.id.jugadorFoto)
        private val nombre: TextView = itemView.findViewById(R.id.jugadorNombre)

        fun bind(jugador: Jugador) {
            nombre.text = jugador.nombre
            if (!jugador.foto.isNullOrEmpty()) {
                val file = File(jugador.foto)
                if (file.exists()) {
                    Glide.with(itemView.context)
                        .load(file)
                        .circleCrop()
                        .into(foto)
                } else {
                    foto.setImageResource(R.drawable.proffile_azul)
                }
            } else {
                foto.setImageResource(R.drawable.proffile_azul)
            }

            itemView.setOnClickListener {
                onClick(jugador)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jugador, parent, false)
        return JugadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: JugadorViewHolder, position: Int) {
        holder.bind(listaJugadores[position])
    }

    override fun getItemCount(): Int = listaJugadores.size
}