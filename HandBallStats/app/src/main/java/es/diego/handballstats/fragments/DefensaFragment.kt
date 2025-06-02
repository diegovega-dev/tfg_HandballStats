package es.diego.handballstats.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import es.diego.handballstats.databinding.FragmentDefensaBinding
import es.diego.handballstats.models.EstadisticaDefensa
import es.diego.handballstats.models.enums.TipoEstadisticaDefensa
import es.diego.handballstats.services.Sesion

class DefensaFragment: Fragment() {
    private var binding: FragmentDefensaBinding? = null
    private val stats: List<EstadisticaDefensa> = Sesion.statsDefensa

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefensaBinding.inflate(inflater, container, false)
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val partidos = partidosUnicos()
        binding?.tvPartidosDefensa?.text = "Datos recaudados en $partidos partidos"

        val robos = stats.count { it.tipo == TipoEstadisticaDefensa.ROBO }
        val blocajes = stats.count { it.tipo == TipoEstadisticaDefensa.BLOCAJE }
        val penaltis = stats.count { it.tipo == TipoEstadisticaDefensa.PENALTI_PROVOCADO }

        val uxuBuenos = stats.filter { it.tipo == TipoEstadisticaDefensa.UxU_BUENO }
        val uxuMalos = stats.filter { it.tipo == TipoEstadisticaDefensa.UxU_MALO }

        val totalUxU = uxuBuenos.size + uxuMalos.size
        val porcentajeUxU = if (totalUxU > 0) (uxuBuenos.size * 100 / totalUxU) else 0

        binding?.tvRobosValue?.text = (robos / partidos.toFloat()).toStringFormatted()
        binding?.tvBlocajesValue?.text = (blocajes / partidos.toFloat()).toStringFormatted()
        binding?.tvPenaltisValue?.text = (penaltis / partidos.toFloat()).toStringFormatted()
        binding?.tvPorcentajeUxUValue?.text = "$porcentajeUxU%"

        cargarGraficoPorPosicion(binding?.pieChartUxUBuenos!!, uxuBuenos, "1vs1 Buenos")
        cargarGraficoPorPosicion(binding?.pieChartUxUMalos!!, uxuMalos, "1vs1 Malos")
    }

    private fun partidosUnicos(): Int {
        return stats.mapNotNull { it.partido?.id }.distinct().size.coerceAtLeast(1)
    }

    private fun Float.toStringFormatted(): String = String.format("%.2f", this)

    private fun cargarGraficoPorPosicion(chart: PieChart, lista: List<EstadisticaDefensa>, titulo: String) {
        val porPosicion = lista.groupingBy { it.posicion }.eachCount()

        val entries = porPosicion.map { (posicion, count) ->
            PieEntry(count.toFloat(), posicion.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
        }

        val dataSet = PieDataSet(entries, titulo)
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 14f
        val data = PieData(dataSet)

        chart.data = data
        chart.centerText = titulo
        chart.description.isEnabled = false
        chart.setUsePercentValues(true)
        chart.setEntryLabelColor(Color.BLACK)
        chart.animateY(1000)
        chart.invalidate()
    }
}