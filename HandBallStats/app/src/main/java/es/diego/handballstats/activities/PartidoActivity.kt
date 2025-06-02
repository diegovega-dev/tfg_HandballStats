package es.diego.handballstats.activities

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.diego.handballstats.R
import es.diego.handballstats.adapters.ListaStatsAdapter
import es.diego.handballstats.databinding.PartidoBinding
import es.diego.handballstats.fragments.ElegirJugadorFragment
import es.diego.handballstats.models.Entrenador
import es.diego.handballstats.models.Equipo
import es.diego.handballstats.models.EstadisticaAtaque
import es.diego.handballstats.models.EstadisticaDefensa
import es.diego.handballstats.models.EstadisticaPortero
import es.diego.handballstats.models.Jugador
import es.diego.handballstats.models.Partido
import es.diego.handballstats.models.enums.Distancia
import es.diego.handballstats.models.enums.PosicionAtaque
import es.diego.handballstats.models.enums.PosicionDefensa
import es.diego.handballstats.models.enums.TipoEstadisticaAtaque
import es.diego.handballstats.models.enums.TipoEstadisticaDefensa
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.services.Sesion
import kotlinx.coroutines.launch
import java.io.File

class PartidoActivity : AppCompatActivity() {

    private lateinit var binding: PartidoBinding

    // Variables para los elementos del layout
    private lateinit var btnStats: ImageButton
    private lateinit var golesFavor: TextView
    private lateinit var golesContra: TextView
    private lateinit var minutero: TextView
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnStop: ImageButton
    private lateinit var drawerLayout: DrawerLayout

    // Ataque - ImageViews y TextViews
    private lateinit var extremoIzq: ImageView
    private lateinit var extremoIzqNombre: TextView
    private lateinit var pivote: ImageView
    private lateinit var pivoteNombre: TextView
    private lateinit var extremoDch: ImageView
    private lateinit var extremoDchNombre: TextView
    private lateinit var lateralIzq: ImageView
    private lateinit var lateralIzqNombre: TextView
    private lateinit var central: ImageView
    private lateinit var centralNombre: TextView
    private lateinit var lateralDch: ImageView
    private lateinit var lateralDchNombre: TextView

    // Defensa - ImageViews y TextViews
    private lateinit var exteriorIzq: ImageView
    private lateinit var exteriorIzqNombre: TextView
    private lateinit var defLateralIzq: ImageView
    private lateinit var defLateralIzqNombre: TextView
    private lateinit var posteIzq: ImageView
    private lateinit var posteIzqNombre: TextView
    private lateinit var posteDch: ImageView
    private lateinit var posteDchNombre: TextView
    private lateinit var defLateralDch: ImageView
    private lateinit var defLateralDchNombre: TextView
    private lateinit var exteriorDch: ImageView
    private lateinit var exteriorDchNombre: TextView

    // Botones abajo
    private lateinit var btnAtaqueBien: ImageButton
    private lateinit var btnAtaqueMal: ImageButton
    private lateinit var btnDefensaBien: ImageButton
    private lateinit var btnDefensaMal: ImageButton

    private lateinit var recyclerEstadisticas: RecyclerView
    private lateinit var adapter: ListaStatsAdapter
    private lateinit var partido: PartidoManager
    private var atacanteSel: Jugador? = null
    private var atacanteSel2: Jugador? = null
    private var defensaSel: Jugador? = null
    private var pausado: Boolean = true
    private var posAtaque: PosicionAtaque = PosicionAtaque.CENTRAL
    private var posAtaque2: PosicionAtaque = PosicionAtaque.CENTRAL
    private var posDefensa: PosicionDefensa = PosicionDefensa.POSTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PartidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        partido = ViewModelProvider(this)[PartidoManager::class.java]

        asignarVariables()

        //parte de arriba
        partido.minutos.observe(this) {min -> minutero.text = "$min'"}
        partido.golesFavor.observe(this) {gol -> golesFavor.text = gol.toString()}
        partido.golesContra.observe(this) {gol -> golesContra.text = gol.toString()}

        partido.iniciarTemporizador()

        btnPlayPause.setOnClickListener{
            if (pausado) {
                btnPlayPause.setImageResource(R.drawable.pause)
                minutero.setTextColor(Color.parseColor("#4CAF50"))
            } else {

                btnPlayPause.setImageResource(R.drawable.play)
                minutero.setTextColor(Color.parseColor("#F44336"))
            }
            partido.pausado = !partido.pausado
            pausado = !pausado
        }

        btnStop.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
                .setTitle("Finalizar partido")
                .setMessage("¿Desea guardar los datos y salir, o salir sin guardar nada?")
                .setCancelable(true)
                .setPositiveButton("Guardar y Salir") {_, _ -> acabarPartido()}
                .setNegativeButton("Salir sin guardar") {_, _ ->
                    val dialog2 = AlertDialog.Builder(this)
                        .setTitle("Salir sin guardar")
                        .setMessage("¿Desea salir sin guardar? Los datos introducidas se perderan para siempre")
                        .setCancelable(true)
                        .setPositiveButton("Salir sin guardar") {_, _ -> onBackPressedDispatcher.onBackPressed()}
                        .setNegativeButton("Cancelar", null)
                        .create()

                    dialog2.show()
                }
                .setNeutralButton("Cancelar", null)
                .create()

            dialog.show()
        }

        //abrir drawer
        btnStats.setOnClickListener{
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        //estadisticas-drawer
        adapter = ListaStatsAdapter(mutableListOf(), partido)
        recyclerEstadisticas.adapter = adapter
        partido.estadisticas.observe(this) {lista ->
            adapter.actualizarLista(lista)
        }

        //jugadores atacantes
        partido.extremoIzq.observe(this){j ->
            if (j != null) {
                extremoIzqNombre.text = j.nombre
                if (j.foto!!.isNotEmpty()){
                    val fotoPath = j.foto!!
                    val fotoFile = File(fotoPath)
                    if (fotoFile.exists()) {
                        Glide.with(this)
                            .load(fotoFile)
                            .circleCrop()
                            .into(extremoIzq)
                    } else {extremoIzq.setImageResource(R.drawable.proffile_azul)}
                }
            }
        }

        partido.lateralIzq.observe(this) { j ->
            if (j != null) {
                lateralIzqNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(lateralIzq)
                    } else {
                        lateralIzq.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    lateralIzq.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        partido.central.observe(this) { j ->
            if (j != null) {
                centralNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(central)
                    } else {
                        central.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    central.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        partido.pivote.observe(this) { j ->
            if (j != null) {
                pivoteNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(pivote)
                    } else {
                        pivote.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    pivote.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        partido.lateralDch.observe(this) { j ->
            if (j != null) {
                lateralDchNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(lateralDch)
                    } else {
                        lateralDch.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    lateralDch.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        partido.extremoDch.observe(this) { j ->
            if (j != null) {
                extremoDchNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(extremoDch)
                    } else {
                        extremoDch.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    extremoDch.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        //jugadores defensas
        partido.exteriorIzq.observe(this) { j ->
            if (j != null) {
                exteriorIzqNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(exteriorIzq)
                    } else {
                        exteriorIzq.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    exteriorIzq.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        partido.defLateralIzq.observe(this) { j ->
            if (j != null) {
                defLateralIzqNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(defLateralIzq)
                    } else {
                        defLateralIzq.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    defLateralIzq.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        partido.posteIzq.observe(this) { j ->
            if (j != null) {
                posteIzqNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(posteIzq)
                    } else {
                        posteIzq.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    posteIzq.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        partido.posteDch.observe(this) { j ->
            if (j != null) {
                posteDchNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(posteDch)
                    } else {
                        posteDch.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    posteDch.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        partido.defLateralDch.observe(this) { j ->
            if (j != null) {
                defLateralDchNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(defLateralDch)
                    } else {
                        defLateralDch.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    defLateralDch.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        partido.exteriorDch.observe(this) { j ->
            if (j != null) {
                exteriorDchNombre.text = j.nombre
                if (!j.foto.isNullOrEmpty()) {
                    val fotoFile = File(j.foto!!)
                    if (fotoFile.exists()) {
                        Glide.with(this).load(fotoFile).circleCrop().into(exteriorDch)
                    } else {
                        exteriorDch.setImageResource(R.drawable.proffile_azul)
                    }
                } else {
                    exteriorDch.setImageResource(R.drawable.proffile_azul)
                }
            }
        }

        //clics en los jugadores atacantes
        extremoIzq.setOnClickListener{
            //si no hay un jugador asignado se abre la ventana para seleccionar
            if (partido.extremoIzq.value?.nombre != null) {
                //si no hay nadie seleccionado lo selecciona
                if (atacanteSel == null) {
                    atacanteSel = partido.extremoIzq.value
                    posAtaque = PosicionAtaque.EXTREMO_IZQ
                    extremoIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (atacanteSel2 == null && atacanteSel != partido.extremoIzq.value) { //si hay alguien ya seleccionado y no es el mismo
                    atacanteSel2 = partido.extremoIzq.value
                    posAtaque2 = PosicionAtaque.EXTREMO_IZQ
                    extremoIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.secundario))
                } else if (atacanteSel2 == partido.extremoIzq.value) {
                    limpiarSeleccionados()
                    atacanteSel = partido.extremoIzq.value
                    posAtaque = PosicionAtaque.EXTREMO_IZQ
                    extremoIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else{
                    limpiarSeleccionados()
                    atacanteSel = partido.extremoIzq.value
                    posAtaque = PosicionAtaque.EXTREMO_IZQ
                    extremoIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }

            } else {
                cambiarJugador("extremoIzq", 0)
            }
        }
        extremoIzq.setOnLongClickListener{
            cambiarJugador("extremoIzq", 0)
            true
        }

        pivote.setOnClickListener {
            if (partido.pivote.value?.nombre != null) {
                if (atacanteSel == null) {
                    atacanteSel = partido.pivote.value
                    posAtaque = PosicionAtaque.PIVOTE
                    pivoteNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (atacanteSel2 == null && atacanteSel != partido.pivote.value) {
                    atacanteSel2 = partido.pivote.value
                    posAtaque2 = PosicionAtaque.PIVOTE
                    pivoteNombre.setTextColor(ContextCompat.getColor(this, R.color.secundario))
                } else if (atacanteSel2 == partido.pivote.value) {
                    limpiarSeleccionados()
                    atacanteSel = partido.pivote.value
                    posAtaque = PosicionAtaque.PIVOTE
                    pivoteNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else{
                    limpiarSeleccionados()
                    atacanteSel = partido.pivote.value
                    posAtaque = PosicionAtaque.PIVOTE
                    pivoteNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("pivote", 0)
            }
        }
        pivote.setOnLongClickListener {
            cambiarJugador("pivote", 0)
            true
        }

        extremoDch.setOnClickListener {
            if (partido.extremoDch.value?.nombre != null) {
                if (atacanteSel == null) {
                    atacanteSel = partido.extremoDch.value
                    posAtaque = PosicionAtaque.EXTREMO_DCH
                    extremoDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (atacanteSel2 == null && atacanteSel != partido.extremoDch.value) {
                    atacanteSel2 = partido.extremoDch.value
                    posAtaque2 = PosicionAtaque.EXTREMO_DCH
                    extremoDchNombre.setTextColor(ContextCompat.getColor(this, R.color.secundario))
                } else if (atacanteSel2 == partido.extremoDch.value) {
                    limpiarSeleccionados()
                    atacanteSel = partido.extremoDch.value
                    posAtaque = PosicionAtaque.EXTREMO_DCH
                    extremoDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else {
                    limpiarSeleccionados()
                    atacanteSel = partido.extremoDch.value
                    posAtaque = PosicionAtaque.EXTREMO_DCH
                    extremoDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("extremoDch", 0)
            }
        }
        extremoDch.setOnLongClickListener {
            cambiarJugador("extremoDch", 0)
            true
        }

        lateralIzq.setOnClickListener {
            if (partido.lateralIzq.value?.nombre != null) {
                if (atacanteSel == null) {
                    atacanteSel = partido.lateralIzq.value
                    posAtaque = PosicionAtaque.LATERAL_IZQ
                    lateralIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (atacanteSel2 == null && atacanteSel != partido.lateralIzq.value) {
                    atacanteSel2 = partido.lateralIzq.value
                    posAtaque2 = PosicionAtaque.LATERAL_IZQ
                    lateralIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.secundario))
                } else if (atacanteSel2 == partido.lateralIzq.value) {
                    limpiarSeleccionados()
                    atacanteSel = partido.lateralIzq.value
                    posAtaque = PosicionAtaque.LATERAL_IZQ
                    lateralIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else {
                    limpiarSeleccionados()
                    atacanteSel = partido.lateralIzq.value
                    posAtaque = PosicionAtaque.LATERAL_IZQ
                    lateralIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("lateralIzq", 0)
            }
        }
        lateralIzq.setOnLongClickListener {
            cambiarJugador("lateralIzq", 0)
            true
        }

        central.setOnClickListener {
            if (partido.central.value?.nombre != null) {
                if (atacanteSel == null) {
                    atacanteSel = partido.central.value
                    posAtaque = PosicionAtaque.CENTRAL
                    centralNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (atacanteSel2 == null && atacanteSel != partido.central.value) {
                    atacanteSel2 = partido.central.value
                    posAtaque2 = PosicionAtaque.CENTRAL
                    centralNombre.setTextColor(ContextCompat.getColor(this, R.color.secundario))
                } else if (atacanteSel2 == partido.central.value) {
                    limpiarSeleccionados()
                    atacanteSel = partido.central.value
                    posAtaque = PosicionAtaque.CENTRAL
                    centralNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else {
                    limpiarSeleccionados()
                    atacanteSel = partido.central.value
                    posAtaque = PosicionAtaque.CENTRAL
                    centralNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("central", 0)
            }
        }
        central.setOnLongClickListener {
            cambiarJugador("central", 0)
            true
        }

        lateralDch.setOnClickListener {
            if (partido.lateralDch.value?.nombre != null) {
                if (atacanteSel == null) {
                    atacanteSel = partido.lateralDch.value
                    posAtaque = PosicionAtaque.LATERAL_DCH
                    lateralDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (atacanteSel2 == null && atacanteSel != partido.lateralDch.value) {
                    atacanteSel2 = partido.lateralDch.value
                    posAtaque2 = PosicionAtaque.LATERAL_DCH
                    lateralDchNombre.setTextColor(ContextCompat.getColor(this, R.color.secundario))
                } else if (atacanteSel2 == partido.lateralDch.value) {
                    limpiarSeleccionados()
                    atacanteSel = partido.lateralDch.value
                    posAtaque = PosicionAtaque.LATERAL_DCH
                    lateralDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else {
                    limpiarSeleccionados()
                    atacanteSel = partido.lateralDch.value
                    posAtaque = PosicionAtaque.LATERAL_DCH
                    lateralDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("lateralDch", 0)
            }
        }
        lateralDch.setOnLongClickListener {
            cambiarJugador("lateralDch", 0)
            true
        }

        //clic en los jugadores defensas
        exteriorDch.setOnClickListener{
            if (partido.exteriorDch.value?.nombre != null) {
                if (defensaSel == null) {
                    defensaSel = partido.exteriorDch.value
                    posDefensa = PosicionDefensa.EXTERIOR
                    exteriorDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (defensaSel != partido.exteriorDch.value) {
                    limpiarSeleccionados()
                    defensaSel = partido.exteriorDch.value
                    posDefensa = PosicionDefensa.EXTERIOR
                    exteriorDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {cambiarJugador("exteriorDch", 1)}
        }
        exteriorDch.setOnLongClickListener{
            cambiarJugador("exteriorDch", 1)
            true
        }

        exteriorIzq.setOnClickListener {
            if (partido.exteriorIzq.value?.nombre != null) {
                if (defensaSel == null) {
                    defensaSel = partido.exteriorIzq.value
                    posDefensa = PosicionDefensa.EXTERIOR
                    exteriorIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (defensaSel != partido.exteriorIzq.value) {
                    limpiarSeleccionados()
                    defensaSel = partido.exteriorIzq.value
                    posDefensa = PosicionDefensa.EXTERIOR
                    exteriorIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("exteriorIzq", 1)
            }
        }
        exteriorIzq.setOnLongClickListener {
            cambiarJugador("exteriorIzq", 1)
            true
        }

        defLateralIzq.setOnClickListener {
            if (partido.defLateralIzq.value?.nombre != null) {
                if (defensaSel == null) {
                    defensaSel = partido.defLateralIzq.value
                    posDefensa = PosicionDefensa.LATERAL
                    defLateralIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (defensaSel != partido.defLateralIzq.value) {
                    limpiarSeleccionados()
                    defensaSel = partido.defLateralIzq.value
                    posDefensa = PosicionDefensa.LATERAL
                    defLateralIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("defLateralIzq", 1)
            }
        }
        defLateralIzq.setOnLongClickListener {
            cambiarJugador("defLateralIzq", 1)
            true
        }

        posteIzq.setOnClickListener {
            if (partido.posteIzq.value?.nombre != null) {
                if (defensaSel == null) {
                    defensaSel = partido.posteIzq.value
                    posDefensa = PosicionDefensa.POSTE
                    posteIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (defensaSel != partido.posteIzq.value) {
                    limpiarSeleccionados()
                    defensaSel = partido.posteIzq.value
                    posDefensa = PosicionDefensa.POSTE
                    posteIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("posteIzq", 1)
            }
        }
        posteIzq.setOnLongClickListener {
            cambiarJugador("posteIzq", 1)
            true
        }

        posteDch.setOnClickListener {
            if (partido.posteDch.value?.nombre != null) {
                if (defensaSel == null) {
                    defensaSel = partido.posteDch.value
                    posDefensa = PosicionDefensa.POSTE
                    posteDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (defensaSel != partido.posteDch.value) {
                    limpiarSeleccionados()
                    defensaSel = partido.posteDch.value
                    posDefensa = PosicionDefensa.POSTE
                    posteDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("posteDch", 1)
            }
        }
        posteDch.setOnLongClickListener {
            cambiarJugador("posteDch", 1)
            true
        }

        defLateralDch.setOnClickListener {
            if (partido.defLateralDch.value?.nombre != null) {
                if (defensaSel == null) {
                    defensaSel = partido.defLateralDch.value
                    posDefensa = PosicionDefensa.LATERAL
                    defLateralDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                } else if (defensaSel != partido.defLateralDch.value) {
                    limpiarSeleccionados()
                    defensaSel = partido.defLateralDch.value
                    posDefensa = PosicionDefensa.LATERAL
                    defLateralDchNombre.setTextColor(ContextCompat.getColor(this, R.color.primario))
                }
            } else {
                cambiarJugador("defLateralDch", 1)
            }
        }
        defLateralDch.setOnLongClickListener {
            cambiarJugador("defLateralDch", 1)
            true
        }

        //botones de abajo
        btnAtaqueBien.setOnClickListener {
            if (atacanteSel == null) {
                Toast.makeText(this, "No hay ningun jugador seleccionado", Toast.LENGTH_SHORT).show()
            } else {
                val c = Entrenador(Sesion.user!!.id, "", null, Sesion.user?.email ?: "", "", null)
                val b = Equipo(Sesion.equipo!!.id, "", "", null, c)
                atacanteSel?.equipo = b

                val view = layoutInflater.inflate(R.layout.dialog_botones_stats, null)
                val mainDialog = AlertDialog.Builder(this)
                    .setView(view)
                    .create()

                val boton1 = view.findViewById<Button>(R.id.boton1)
                val boton2 = view.findViewById<Button>(R.id.boton2)
                val boton3 = view.findViewById<Button>(R.id.boton3)
                val boton4 = view.findViewById<Button>(R.id.boton4)
                val boton5 = view.findViewById<Button>(R.id.boton5)

                boton1.text = "GOL"
                boton2.text = "PENALTI PROVOCADO"
                boton3.text = "BUEN 1vs1"
                boton4.visibility = View.GONE
                boton5.visibility = View.GONE

                boton1.setOnClickListener{tiroDialog(mainDialog, TipoEstadisticaAtaque.GOL)}

                boton2.setOnClickListener{
                    val stat: EstadisticaAtaque = EstadisticaAtaque(
                        partido.minutos.value ?: 0,
                        atacanteSel,
                        TipoEstadisticaAtaque.PENALTI_PROVOCADO,
                        posAtaque)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton3.setOnClickListener{
                    val stat: EstadisticaAtaque = EstadisticaAtaque(
                        partido.minutos.value ?: 0,
                        atacanteSel,
                        TipoEstadisticaAtaque.UxU_BUENO,
                        posAtaque)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                mainDialog.show()
            }
        }

        btnAtaqueMal.setOnClickListener{
            Log.d("DEBUG", "ATAQUE MAL: atacanteSel = $atacanteSel")
            if (atacanteSel != null) {
                val c: Entrenador = Entrenador(Sesion.user!!.id, "", null, Sesion.user!!.email, "", null)
                val b: Equipo = Equipo(Sesion.equipo!!.id, "", "", null, c)
                atacanteSel!!.equipo = b

                val view = layoutInflater.inflate(R.layout.dialog_botones_stats, null)
                val mainDialog = AlertDialog.Builder(this)
                    .setView(view)
                    .create()

                val boton1 = view.findViewById<Button>(R.id.boton1)
                val boton2 = view.findViewById<Button>(R.id.boton2)
                val boton3 = view.findViewById<Button>(R.id.boton3)
                val boton4 = view.findViewById<Button>(R.id.boton4)
                val boton5 = view.findViewById<Button>(R.id.boton5)

                boton1.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
                boton2.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
                boton3.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
                boton4.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
                boton5.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))

                boton1.text = "TIRO FALLADO"
                boton2.text = "PASE FALLADO"
                boton3.text = "PASOS"
                boton4.text = "FALTA ATQ"
                boton5.text = "MAL 1vs1"

                boton1.setOnClickListener{tiroDialog(mainDialog, TipoEstadisticaAtaque.TIRO_FALLADO)}

                boton2.setOnClickListener{
                    val stat: EstadisticaAtaque = EstadisticaAtaque(
                        partido.minutos.value ?: 0,
                        atacanteSel,
                        TipoEstadisticaAtaque.PASE_FALLADO,
                        posAtaque)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton3.setOnClickListener{
                    val stat: EstadisticaAtaque = EstadisticaAtaque(
                        partido.minutos.value ?: 0,
                        atacanteSel,
                        TipoEstadisticaAtaque.PASOS,
                        posAtaque)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton4.setOnClickListener{
                    val stat: EstadisticaAtaque = EstadisticaAtaque(
                        partido.minutos.value ?: 0,
                        atacanteSel,
                        TipoEstadisticaAtaque.FALTA_ATQ,
                        posAtaque)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton5.setOnClickListener{
                    val stat: EstadisticaAtaque = EstadisticaAtaque(
                        partido.minutos.value ?: 0,
                        atacanteSel,
                        TipoEstadisticaAtaque.UxU_MALO,
                        posAtaque)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                mainDialog.show()
            } else {Toast.makeText(this, "No hay ningun jugador seleccionado", Toast.LENGTH_SHORT).show()}
        }

        btnDefensaBien.setOnClickListener{
            if (defensaSel != null) {
                val c: Entrenador = Entrenador(Sesion.user!!.id, "", null, Sesion.user!!.email, "", null)
                val b: Equipo = Equipo(Sesion.equipo!!.id, "", "", null, c)
                defensaSel!!.equipo = b

                val view = layoutInflater.inflate(R.layout.dialog_botones_stats, null)
                val mainDialog = AlertDialog.Builder(this)
                    .setView(view)
                    .create()

                val boton1 = view.findViewById<Button>(R.id.boton1)
                val boton2 = view.findViewById<Button>(R.id.boton2)
                val boton3 = view.findViewById<Button>(R.id.boton3)
                val boton4 = view.findViewById<Button>(R.id.boton4)
                val boton5 = view.findViewById<Button>(R.id.boton5)

                boton5.visibility = View.GONE

                boton1.text = "ROBO"
                boton2.text = "FALTA ATQ"
                boton3.text = "BLOCAJE"
                boton4.text = "BUEN 1v1"

                boton1.setOnClickListener{
                    val stat: EstadisticaDefensa = EstadisticaDefensa(
                        partido.minutos.value ?: 0,
                        defensaSel!!,
                        TipoEstadisticaDefensa.ROBO,
                        posDefensa)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton2.setOnClickListener{
                    val stat: EstadisticaDefensa = EstadisticaDefensa(
                        partido.minutos.value ?: 0,
                        defensaSel!!,
                        TipoEstadisticaDefensa.FALTA_ATQ,
                        posDefensa)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton3.setOnClickListener{
                    val stat: EstadisticaDefensa = EstadisticaDefensa(
                        partido.minutos.value ?: 0,
                        defensaSel!!,
                        TipoEstadisticaDefensa.BLOCAJE,
                        posDefensa)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton4.setOnClickListener{
                    val stat: EstadisticaDefensa = EstadisticaDefensa(
                        partido.minutos.value ?: 0,
                        defensaSel!!,
                        TipoEstadisticaDefensa.UxU_BUENO,
                        posDefensa)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                mainDialog.show()
            } else {Toast.makeText(this, "No hay ningun jugador seleccionado", Toast.LENGTH_SHORT).show()}
        }

        btnDefensaMal.setOnClickListener{
            if (defensaSel != null) {
                val c: Entrenador = Entrenador(Sesion.user!!.id, "", null, Sesion.user!!.email, "", null)
                val b: Equipo = Equipo(Sesion.equipo!!.id, "", "", null, c)
                defensaSel!!.equipo = b

                val view = layoutInflater.inflate(R.layout.dialog_botones_stats, null)
                val mainDialog = AlertDialog.Builder(this)
                    .setView(view)
                    .create()

                val boton1 = view.findViewById<Button>(R.id.boton1)
                val boton2 = view.findViewById<Button>(R.id.boton2)
                val boton3 = view.findViewById<Button>(R.id.boton3)
                val boton4 = view.findViewById<Button>(R.id.boton4)
                val boton5 = view.findViewById<Button>(R.id.boton5)

                boton1.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
                boton2.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
                boton3.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
                boton4.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
                boton5.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))

                boton1.text = "SANCION"
                boton2.text = "PENALTI PROVOCADO"
                boton3.text = "ROBO FALLADO"
                boton4.text = "MAL 1v1"
                boton5.text = "GOL"

                boton1.setOnClickListener{
                    val sancionView = layoutInflater.inflate(R.layout.dialog_sanciones, null)
                    val sancionDialog = AlertDialog.Builder(this)
                        .setView(sancionView)
                        .create()

                    val radios = sancionView.findViewById<RadioGroup>(R.id.radioGroupSanciones)
                    val aceptar = sancionView.findViewById<Button>(R.id.btnAceptarSancion)

                    aceptar.setOnClickListener{
                        val sancion = when (radios.checkedRadioButtonId) {
                            R.id.radio2Min -> TipoEstadisticaDefensa.EXCLUSION
                            R.id.radioRoja -> TipoEstadisticaDefensa.ROJA
                            R.id.radioAmarilla -> TipoEstadisticaDefensa.AMARILLA
                            R.id.radioAzul -> TipoEstadisticaDefensa.AZUL
                            else -> null
                        }

                        if (sancion != null) {
                            val stat: EstadisticaDefensa = EstadisticaDefensa(
                                partido.minutos.value ?: 0,
                                defensaSel!!,
                                sancion,
                                posDefensa)

                            partido.agregarEstadistica(stat)
                            limpiarSeleccionados()
                            sancionDialog.dismiss()
                        } else {Toast.makeText(this, "Seleccione una sanción", Toast.LENGTH_SHORT).show()}
                    }

                    sancionDialog.show()
                    mainDialog.dismiss()
                }

                boton2.setOnClickListener{
                    val stat: EstadisticaDefensa = EstadisticaDefensa(
                        partido.minutos.value ?: 0,
                        defensaSel!!,
                        TipoEstadisticaDefensa.PENALTI_PROVOCADO,
                        posDefensa)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton3.setOnClickListener{
                    val stat: EstadisticaDefensa = EstadisticaDefensa(
                        partido.minutos.value ?: 0,
                        defensaSel!!,
                        TipoEstadisticaDefensa.ROBO_FALLADO,
                        posDefensa)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton4.setOnClickListener{
                    val stat: EstadisticaDefensa = EstadisticaDefensa(
                        partido.minutos.value ?: 0,
                        defensaSel!!,
                        TipoEstadisticaDefensa.UxU_MALO,
                        posDefensa)

                    partido.agregarEstadistica(stat)
                    limpiarSeleccionados()
                    mainDialog.dismiss()
                }

                boton5.setOnClickListener{
                    partido.agregarGolEnContra()
                    mainDialog.dismiss()
                }

                mainDialog.show()
            } else {Toast.makeText(this, "No hay ningun jugador seleccionado", Toast.LENGTH_SHORT).show()}
        }
    }

    private fun limpiarSeleccionados() {
        atacanteSel = null
        atacanteSel2 = null
        defensaSel = null
        extremoIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        lateralIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        centralNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        pivoteNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        lateralDchNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        extremoDchNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        exteriorIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        defLateralIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        posteIzqNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        posteDchNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        defLateralDchNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
        exteriorDchNombre.setTextColor(ContextCompat.getColor(this, R.color.texto))
    }

    private fun cambiarJugador(pos: String, a: Int) {
        val listaJugadores: MutableList<Jugador> = mutableListOf()
        listaJugadores.addAll(Sesion.jugadores)

        if (a == 0) {
            listaJugadores.remove(partido.extremoIzq.value)
            listaJugadores.remove(partido.lateralIzq.value)
            listaJugadores.remove(partido.central.value)
            listaJugadores.remove(partido.pivote.value)
            listaJugadores.remove(partido.lateralDch.value)
            listaJugadores.remove(partido.extremoDch.value)
        } else {
            listaJugadores.remove(partido.exteriorIzq.value)
            listaJugadores.remove(partido.defLateralIzq.value)
            listaJugadores.remove(partido.posteIzq.value)
            listaJugadores.remove(partido.posteDch.value)
            listaJugadores.remove(partido.defLateralDch.value)
            listaJugadores.remove(partido.exteriorDch.value)
        }

        val dialog = ElegirJugadorFragment(listaJugadores) { jugadorSeleccionado ->
            if (jugadorSeleccionado == null) {
                partido.setJugador(pos, null)
                when (pos) {
                    "extremoIzq" -> {
                        extremoIzq.setImageDrawable(null) // quitar foto para que se vea el background
                        extremoIzqNombre.text = "extremo izq"
                    }
                    "pivote" -> {
                        pivote.setImageDrawable(null)
                        pivoteNombre.text = "pivote"
                    }
                    "extremoDch" -> {
                        extremoDch.setImageDrawable(null)
                        extremoDchNombre.text = "extremo dch"
                    }
                    "lateralIzq" -> {
                        lateralIzq.setImageDrawable(null)
                        lateralIzqNombre.text = "lateral izq"
                    }
                    "central" -> {
                        central.setImageDrawable(null)
                        centralNombre.text = "central"
                    }
                    "lateralDch" -> {
                        lateralDch.setImageDrawable(null)
                        lateralDchNombre.text = "lateral dch"
                    }
                    "exteriorIzq" -> {
                        exteriorIzq.setImageDrawable(null)
                        exteriorIzqNombre.text = "exterior izq"
                    }
                    "defLateralIzq" -> {
                        defLateralIzq.setImageDrawable(null)
                        defLateralIzqNombre.text = "def lateral izq"
                    }
                    "posteIzq" -> {
                        posteIzq.setImageDrawable(null)
                        posteIzqNombre.text = "poste izq"
                    }
                    "posteDch" -> {
                        posteDch.setImageDrawable(null)
                        posteDchNombre.text = "poste dch"
                    }
                    "defLateralDch" -> {
                        defLateralDch.setImageDrawable(null)
                        defLateralDchNombre.text = "def lateral dch"
                    }
                    "exteriorDch" -> {
                        exteriorDch.setImageDrawable(null)
                        exteriorDchNombre.text = "exterior dch"
                    }
                }
            } else {
                partido.setJugador(pos, jugadorSeleccionado)
            }
            limpiarSeleccionados()
        }

        dialog.show(supportFragmentManager, "SeleccionarJugadorDialog")
    }

    private fun acabarPartido() {
        val c: Entrenador = Entrenador(Sesion.user!!.id, "", null, Sesion.user!!.email, "", null)
        val b: Equipo = Equipo(Sesion.equipo!!.id, "", "", null, c)

        val editText = EditText(this)
        editText.hint = "Nombre"

        AlertDialog.Builder(this)
            .setTitle("Nombre del partido")
            .setMessage("Introduce un nombre para guardar este partido")
            .setView(editText)
            .setPositiveButton("Aceptar") { dialog, _ ->
                val nombre = editText.text.toString()
                if (nombre.isNotEmpty()) {
                    val d: Partido = Partido(null, nombre, partido.golesFavor.value!!, partido.golesContra.value!!, b, null)
                    guardarSalir(d)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()

    }

    private fun asignarVariables() {
        btnStats = binding.btnStats
        golesFavor = binding.golesFavor
        golesContra = binding.golesContra
        minutero = binding.minutero
        btnPlayPause = binding.btnPlayPause
        btnStop = binding.btnStop
        drawerLayout = binding.drawerLayout

        extremoIzq = binding.extremoIzq
        extremoIzqNombre = binding.extremoIzqNombre
        pivote = binding.pivote
        pivoteNombre = binding.pivoteNombre
        extremoDch = binding.extremoDch
        extremoDchNombre = binding.extremoDchNombre
        lateralIzq = binding.lateralIzq
        lateralIzqNombre = binding.lateralIzqNombre
        central = binding.central
        centralNombre = binding.centralNombre
        lateralDch = binding.lateralDch
        lateralDchNombre = binding.lateralDchNombre

        exteriorIzq = binding.exteriorIzq
        exteriorIzqNombre = binding.exteriorIzqNombre
        defLateralIzq = binding.defLateralIzq
        defLateralIzqNombre = binding.defLateralIzqNombre
        posteIzq = binding.posteIzq
        posteIzqNombre = binding.posteIzqNombre
        posteDch = binding.posteDch
        posteDchNombre = binding.posteDchNombre
        defLateralDch = binding.defLateralDch
        defLateralDchNombre = binding.defLateralDchNombre
        exteriorDch = binding.exteriorDch
        exteriorDchNombre = binding.exteriorDchNombre

        btnAtaqueBien = binding.btnAtaqueBien
        btnAtaqueMal = binding.btnAtaqueMal
        btnDefensaBien = binding.btnDefensaBien
        btnDefensaMal = binding.btnDefensaMal

        recyclerEstadisticas = binding.recyclerEstadisticas
        recyclerEstadisticas.layoutManager = LinearLayoutManager(this)
    }

    private fun guardarSalir(a: Partido) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.partidoService.crearPartido(Sesion.token!!, a)

                if (response.isSuccessful) {
                    val equipo = response.body()
                    partido.estadisticas.value?.forEach{ it ->
                        when(it) {
                            is EstadisticaAtaque -> {it.partido = equipo}
                            is EstadisticaDefensa -> {it.partido = equipo}
                            is EstadisticaPortero -> {it.partido = equipo}
                        }

                    }

                    val response2 = ApiClient.statService.crearEstadistica(Sesion.token!!, partido.estadisticas.value)

                    if (response2.isSuccessful) {
                        irAtras()
                    }

                } else {Log.e("ERROR: ${response.code()}", response.message())}
            } catch (e:Exception) {Log.e("ERROR", "${e.message}")}
        }



    }

    private fun irAtras() {
        Toast.makeText(this, "Datos guardados con exito", Toast.LENGTH_SHORT).show()
        onBackPressedDispatcher.onBackPressed()
    }

    private fun tiroDialog(parentDialog: Dialog, tipo: TipoEstadisticaAtaque) {
        val golView = layoutInflater.inflate(R.layout.dialog_gol, null)
        val golDialog = AlertDialog.Builder(this)
            .setView(golView)
            .create()

        val contra = golView.findViewById<CheckBox>(R.id.checkBoxContraataque)
        val radios = golView.findViewById<RadioGroup>(R.id.radioGroupDistancia)
        val aceptar = golView.findViewById<Button>(R.id.buttonAceptar)

        contra.setOnCheckedChangeListener { _, isChecked ->
            radios.children.forEach {
                it.isEnabled = !isChecked
                if (isChecked) {
                    it.visibility = View.INVISIBLE
                } else {it.visibility = View.VISIBLE}
            }
        }

        aceptar.setOnClickListener {
            val distancia = when (radios.checkedRadioButtonId) {
                R.id.radioDistancia6 -> Distancia.SEIS
                R.id.radioDistancia9 -> Distancia.NUEVE
                R.id.radioDistancia7 -> Distancia.PENALTY
                else -> null
            }

            if (contra.isChecked || distancia != null) {
                val stat = EstadisticaAtaque(
                    partido.minutos.value ?: 0,
                    atacanteSel,
                    tipo,
                    posAtaque,
                    contra.isChecked,
                    distancia
                )

                partido.agregarEstadistica(stat)

                if (tipo == TipoEstadisticaAtaque.GOL) {partido.agregarGolAFavor()}

                if (atacanteSel2 != null && tipo == TipoEstadisticaAtaque.GOL) {
                    val stat2 = EstadisticaAtaque(
                        partido.minutos.value ?: 0,
                        atacanteSel2,
                        TipoEstadisticaAtaque.ASISTENCIA,
                        posAtaque2,
                        false,
                        distancia
                    )

                    partido.agregarEstadistica(stat2)
                }

                limpiarSeleccionados()

                golDialog.dismiss()
                parentDialog.dismiss()
            } else {Toast.makeText(this, "Selecciona distancia o contraataque", Toast.LENGTH_SHORT).show()}
        }

        golDialog.show()
    }
}