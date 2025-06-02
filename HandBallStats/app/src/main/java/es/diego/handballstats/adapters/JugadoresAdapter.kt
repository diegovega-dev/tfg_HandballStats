package es.diego.handballstats.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.diego.handballstats.R
import es.diego.handballstats.activities.JugadorActivity
import es.diego.handballstats.services.Sesion
import es.diego.handballstats.databinding.CardJugadorBinding
import es.diego.handballstats.models.Jugador
import java.io.File

class JugadoresAdapter (
    private val jugadores: MutableList<Jugador>,
    private val lifeCycleOwner: LifecycleOwner
) : RecyclerView.Adapter<JugadoresAdapter.JugadoresViewHolder>() {

    inner class JugadoresViewHolder(val vista: View): RecyclerView.ViewHolder(vista) {

        val binding = CardJugadorBinding.bind(vista)

        fun bind(jugador: Jugador) {
            binding.jugadorNombreText.text = jugador.nombre
            binding.jugadorAnioText.text = jugador.anio.toString()
            binding.jugadorDorsalText.text = jugador.dorsal.toString()

            if (!jugador.foto.isNullOrEmpty()) {
                val fotoFile = File(jugador.foto!!)
                if (fotoFile.exists()) {
                    Glide.with(vista)
                        .load(fotoFile)
                        .circleCrop()
                        .into(binding.jugadorImageView)
                }
            }

            vista.setOnClickListener() {
                Sesion.jugador = jugador
                val intent = Intent(vista.context, JugadorActivity::class.java)
                vista.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): JugadoresViewHolder {
        return JugadoresViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_jugador, parent, false)
        )
    }

    override fun onBindViewHolder(holder: JugadoresViewHolder, position: Int) {
        holder.bind(jugadores[position])
    }

    override fun getItemCount(): Int = jugadores.size
}