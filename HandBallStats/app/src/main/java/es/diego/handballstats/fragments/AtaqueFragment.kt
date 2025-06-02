package es.diego.handballstats.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import es.diego.handballstats.databinding.FragmentAtaqueBinding
import es.diego.handballstats.models.EstadisticaAtaque
import es.diego.handballstats.models.enums.TipoEstadisticaAtaque
import es.diego.handballstats.services.Sesion

class AtaqueFragment : Fragment() {

    private val stats: List<EstadisticaAtaque> = Sesion.statsAtaque
    private var binding: FragmentAtaqueBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAtaqueBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calcularDatos()
        calcularGraficoPosicion()
        calcularGraficoDistancia()
    }

    private fun partidos(): Int {
        //saca los id de partido que hay, elimina los duplicados y los cuenta
        return stats.mapNotNull { it.partido?.id }.distinct().size
    }

    private fun calcularDatos() {
        val totalPartidos = partidos().takeIf { it > 0 } ?: 1
        binding?.tvTotalPartidos?.text = "Datos recaudados en $totalPartidos partido${if (totalPartidos == 1) "" else "s"}"

        val goles = stats.count { it.tipo == TipoEstadisticaAtaque.GOL }
        val tiros = stats.count {
            it.tipo == TipoEstadisticaAtaque.GOL || it.tipo == TipoEstadisticaAtaque.TIRO_FALLADO
        }
        val asistencias = stats.count { it.tipo == TipoEstadisticaAtaque.ASISTENCIA }
        val perdidas = stats.count {
            it.tipo in listOf(
                TipoEstadisticaAtaque.PASE_FALLADO,
                TipoEstadisticaAtaque.PASOS,
                TipoEstadisticaAtaque.FALTA_ATQ
            )
        }

        val uxuBuenos = stats.count { it.tipo == TipoEstadisticaAtaque.UxU_BUENO }
        val uxuMalos = stats.count { it.tipo == TipoEstadisticaAtaque.UxU_MALO }
        val uxuTotal = uxuBuenos + uxuMalos

        val golesPorPartido = goles.toDouble() / totalPartidos
        val porcentajeGol = if (tiros > 0) goles.toDouble() / tiros * 100 else 0.0
        val asistenciasPorPartido = asistencias.toDouble() / totalPartidos
        val perdidasPorPartido = perdidas.toDouble() / totalPartidos
        val porcentajeUxU = if (uxuTotal > 0) uxuBuenos.toDouble() / uxuTotal * 100 else 0.0

        //pasa los numeros redondeados al texto
        binding?.tvGolPartidoValue?.text = String.format("%.2f", golesPorPartido)
        binding?.tvPorcentajeGolValue?.text = String.format("%.1f%%", porcentajeGol)
        binding?.tvAsistenciasValue?.text = String.format("%.2f", asistenciasPorPartido)
        binding?.tvPerdidasValue?.text = String.format("%.2f", perdidasPorPartido)
        binding?.tvPorcentajeUxUValue?.text = String.format("%.1f%%", porcentajeUxU)
    }

    private fun calcularGraficoPosicion() {
        //grafico 1 (goles por posicion)
        val goles = stats.filter { it.tipo == TipoEstadisticaAtaque.GOL }
        val porPosicion = goles.groupingBy { it.posicion }.eachCount()

        //crea los objetos que necesita el grafico que son un map con la posicion y la cantidad de goles en esa posicion
        val entries = porPosicion.map { (posicion, count) ->
            PieEntry(count.toFloat(), posicion.name.replace("_", " ").capitalize())
        }

        val dataSet = PieDataSet(entries, "Goles por posición")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 14f
        dataSet.setValueTextColors(ColorTemplate.MATERIAL_COLORS.toList())

        val data = PieData(dataSet)
        binding?.pieChart1?.data = data
        binding?.pieChart1?.description?.isEnabled = false
        binding?.pieChart1?.centerText = "Goles por posición"
        binding?.pieChart1?.setEntryLabelColor(Color.BLACK)
        binding?.pieChart1?.animateY(1000)
        binding?.pieChart1?.invalidate()
    }

    private fun calcularGraficoDistancia(){
        val goles = stats.filter { it.tipo == TipoEstadisticaAtaque.GOL }

        val porDistancia = goles.groupingBy {
            when {
                it.contrataque -> "Contraataque"
                it.distancia != null -> it.distancia.name.capitalize()
                else -> "Desconocido"
            }
        }.eachCount()

        val entries = porDistancia.map { (categoria, count) ->
            PieEntry(count.toFloat(), categoria)
        }

        val dataSet = PieDataSet(entries, "Goles por distancia")
        dataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)
        binding?.pieChart2?.data = data
        binding?.pieChart2?.description?.isEnabled = false
        binding?.pieChart2?.centerText = "Goles por distancia"
        binding?.pieChart2?.animateY(1000)
        binding?.pieChart2?.setEntryLabelColor(Color.BLACK)
        binding?.pieChart2?.invalidate()
    }

}
