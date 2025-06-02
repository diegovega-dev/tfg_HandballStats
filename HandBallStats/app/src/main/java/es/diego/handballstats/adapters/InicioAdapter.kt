package es.diego.handballstats.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.diego.handballstats.R
import es.diego.handballstats.services.Sesion
import es.diego.handballstats.activities.EquipoActivity
import es.diego.handballstats.databinding.CardEquipoBinding
import es.diego.handballstats.models.Equipo
import java.io.File

class InicioAdapter(private val equipos: MutableList<Equipo>) : RecyclerView.Adapter<InicioAdapter.InicioViewHolder>(){

    inner class InicioViewHolder(val vista: View): RecyclerView.ViewHolder(vista) {
        val binding = CardEquipoBinding.bind(vista)

        fun bind(equipo: Equipo) {
            binding.equipoNombreText.text = equipo.nombre
            binding.equipoCategoriaText.text = equipo.categoria

            if (!equipo.foto.isNullOrEmpty()) {
                val fotoFile = File(equipo.foto!!)
                if (fotoFile.exists()) {
                    Glide.with(vista)
                        .load(fotoFile)
                        .circleCrop()
                        .into(binding.equipoImageView)
                }
            }

            vista.setOnClickListener() {
                Sesion.equipo = equipo
                val context = vista.context
                val intent = Intent(context, EquipoActivity::class.java)
                Sesion.equipo = equipo
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InicioViewHolder {
        return InicioViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_equipo, parent, false)
        )
    }

    fun agregarEquipo(equipo: Equipo) {
        equipos.add(equipo)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = equipos.size

    override fun onBindViewHolder(holder: InicioViewHolder, position: Int) {
        holder.bind(equipos[position])
    }
}