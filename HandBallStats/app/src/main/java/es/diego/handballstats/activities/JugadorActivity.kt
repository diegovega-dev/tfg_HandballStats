package es.diego.handballstats.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import es.diego.handballstats.R
import es.diego.handballstats.databinding.JugadorBinding
import es.diego.handballstats.adapters.StatsViewPagerAdapter
import es.diego.handballstats.models.EstadisticaAtaque
import es.diego.handballstats.models.EstadisticaDefensa
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.services.PhotoPickerHelper
import es.diego.handballstats.services.Sesion
import kotlinx.coroutines.launch
import java.io.File

class JugadorActivity : AppCompatActivity(){
    private lateinit var miBinding: JugadorBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewpager: ViewPager2
    private lateinit var editar: TextView
    private lateinit var nombre: TextView
    private lateinit var anio: TextView
    private lateinit var dorsal: TextView
    private lateinit var atras: ImageButton
    private lateinit var imagenD: ImageView
    private lateinit var imagen: ImageView
    private lateinit var photoPicker: PhotoPickerHelper
    private var foto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        miBinding= JugadorBinding.inflate(layoutInflater)
        setContentView(miBinding.root)


        photoPicker = PhotoPickerHelper(this) { bitmap, path ->
            Glide.with(this)
                .load(File(path))
                .circleCrop()
                .into(imagenD)
            foto = path
        }

        imagen = miBinding.playerImage
        editar = miBinding.editText
        tabLayout = miBinding.tabs
        viewpager = miBinding.viewPager
        nombre = miBinding.playerName
        anio = miBinding.playerYear
        dorsal = miBinding.playerDorsal
        atras = miBinding.backButton

        lifecycleScope.launch {
            cargarEstadisticas()
            cargarDatos()
        }

        editar.setOnClickListener() {
            val dialogView = layoutInflater.inflate(R.layout.dialog_crear_jugador, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val nombreEditText = dialogView.findViewById<EditText>(R.id.playerNameEditText)
            val anioEditText = dialogView.findViewById<EditText>(R.id.playerBirthYearEditText)
            val dorsalEditText = dialogView.findViewById<EditText>(R.id.playerDorsalEditText)
            val guardarButton = dialogView.findViewById<Button>(R.id.savePlayerButton)
            val eliminarButton = dialogView.findViewById<ImageButton>(R.id.eliminarButton)
            imagenD = dialogView.findViewById<ImageView>(R.id.playerPhotoImageView)

            guardarButton.setText("Actualizar datos")
            nombreEditText.text = Editable.Factory.getInstance().newEditable(Sesion.jugador!!.nombre)
            anioEditText.text = Editable.Factory.getInstance().newEditable(Sesion.jugador!!.anio.toString())
            dorsalEditText.text = Editable.Factory.getInstance().newEditable(Sesion.jugador!!.dorsal.toString())
            eliminarButton.visibility = View.VISIBLE

            if (!Sesion.jugador?.foto.isNullOrEmpty()) {
                val fotoPath = Sesion.jugador!!.foto!!
                val fotoFile = File(fotoPath)
                if (fotoFile.exists()) {
                    Glide.with(this)
                        .load(fotoFile)
                        .circleCrop()
                        .into(imagenD)
                }
            }

            imagenD.setOnClickListener{
                val opciones = arrayOf("Elegir desde galería", "Tomar foto con cámara")
                AlertDialog.Builder(this)
                    .setTitle("Cambiar foto del jugador")
                    .setItems(opciones) { _, which ->
                        when (which) {
                            0 -> photoPicker.pickFromGallery()
                            1 -> photoPicker.takePhoto()
                        }
                    }
                    .show()
            }

            eliminarButton.setOnClickListener(){
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Eliminar jugador")
                    .setMessage("¿Seguro que desea borrar este jugador?")
                    .setCancelable(true)
                    .setPositiveButton("Eliminar jugador") { _, _ ->
                        val dialog = AlertDialog.Builder(this)
                            .setCancelable(true)
                            .setTitle("Doble confimacion")
                            .setMessage("Esta operación no se puede cancelar, se eliminarán permanentemente el jugador y todos las estadísticas asociadas")
                            .setNegativeButton("Cancelar", null)
                            .setPositiveButton("Eliminar jugador") { _, _ ->
                                dialog.dismiss()
                                eliminarJugador()
                            }
                            .create()

                        dialog.show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .create()

                dialog.show()
            }

            guardarButton.setOnClickListener {
                val nombre = nombreEditText.text.toString()
                val anio = anioEditText.text.toString()
                val dorsal = dorsalEditText.text.toString()

                if (nombre.isNotEmpty() && anio.isNotEmpty() && dorsal.isNotEmpty()) {
                    actualizarJugador(nombre, anio, dorsal)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }

        atras.setOnClickListener{onBackPressedDispatcher.onBackPressed()}
    }

    private fun cargarDatos(){
        val adapter = StatsViewPagerAdapter(this)
        viewpager.adapter = adapter

        TabLayoutMediator(tabLayout, viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "Ataque"
                1 -> "Defensa"
                else -> "otro"
            }
        }.attach()

        if (!Sesion.jugador?.foto.isNullOrEmpty()) {
            val fotoPath = Sesion.jugador!!.foto!!
            val fotoFile = File(fotoPath)
            if (fotoFile.exists()) {
                Glide.with(this)
                    .load(fotoFile)
                    .circleCrop()
                    .into(imagen)
            }
        }

        nombre.text = Sesion.jugador?.nombre
        anio.text = Sesion.jugador?.anio.toString()
        dorsal.text = "#${Sesion.jugador?.dorsal}"

    }

    private fun actualizarJugador(nombre: String, anioStr: String, dorsalStr: String) {
        val anio = anioStr.toIntOrNull()
        val dorsal = dorsalStr.toIntOrNull()

        if (anio == null || dorsal == null) {
            Toast.makeText(this, "El año y el dorsal deben ser números válidos", Toast.LENGTH_SHORT).show()
            return
        }

        Sesion.jugador?.nombre = nombre
        Sesion.jugador?.anio = anio
        Sesion.jugador?.dorsal = dorsal
        Sesion.jugador?.foto = foto

        lifecycleScope.launch {
            try {
                val response = ApiClient.jugadorService.actualizarJugador(Sesion.token!!, Sesion.jugador!!)

                if (response.isSuccessful) {
                    Sesion.jugador = response.body()
                    cargarDatos()
                }
            } catch (e: Exception) {Log.e("ERROR", "${e.message}")}
        }
    }

    private fun eliminarJugador() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.jugadorService.borrarJugador(Sesion.token!!, Sesion.jugador?.id!!)

                if (response.isSuccessful) {
                    onBackPressedDispatcher.onBackPressed()
                }
            } catch (e: Exception) {Log.e("ERROR", "${e.message}")}
        }
    }

    private suspend fun cargarEstadisticas() {
        try {
            val response = ApiClient.statService.buscarEstadisticaJugador(Sesion.token!!, Sesion.jugador?.id!!)
            if (response.isSuccessful) {
                Sesion.statsAtaque = response.body()?.filterIsInstance<EstadisticaAtaque>() ?: listOf()
                Sesion.statsDefensa = response.body()?.filterIsInstance<EstadisticaDefensa>() ?: listOf()
            }
        } catch (e: Exception) {
            Log.e("ERROR", "${e.message}")
        }
    }
}
