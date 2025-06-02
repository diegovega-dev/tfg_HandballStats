package es.diego.handballstats.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import es.diego.handballstats.R
import es.diego.handballstats.services.Sesion
import es.diego.handballstats.adapters.EquipoViewPagerAdapter
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.databinding.EquipoBinding
import es.diego.handballstats.models.Entrenador
import es.diego.handballstats.models.Equipo
import es.diego.handballstats.models.Jugador
import es.diego.handballstats.services.PhotoPickerHelper
import kotlinx.coroutines.launch
import java.io.File

class EquipoActivity: AppCompatActivity() {
    private lateinit var miBinding: EquipoBinding
    private lateinit var atras: ImageButton
    private lateinit var editar: TextView
    private lateinit var nombre: TextView
    private lateinit var categoria: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewpager: ViewPager2
    private lateinit var addButton: FloatingActionButton
    private lateinit var imagen: ImageView
    private lateinit var imagenJ: ImageView
    private lateinit var imagenD: ImageView
    private lateinit var photoPicker: PhotoPickerHelper
    private lateinit var photoPicker2: PhotoPickerHelper
    private var foto: String = ""
    private var fotoJugador: String = ""
    private var tipo: String = "jugadores"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        miBinding= EquipoBinding.inflate(layoutInflater)
        setContentView(miBinding.root)

        photoPicker = PhotoPickerHelper(this) { bitmap, path ->
            Glide.with(this)
                .load(File(path))
                .circleCrop()
                .into(imagenD)
            foto = path
        }

        photoPicker2 = PhotoPickerHelper(this) { bitmap, path ->
            Glide.with(this)
                .load(File(path))
                .circleCrop()
                .into(imagenJ)
            fotoJugador = path
        }

        nombre = miBinding.teamName
        atras = miBinding.backButton
        editar = miBinding.editText
        categoria = miBinding.teamCategory
        tabLayout = miBinding.tabLayout
        viewpager = miBinding.viewPager
        addButton = miBinding.addPlayerButton
        imagen = miBinding.teamImage

        cargarDatos()

        atras.setOnClickListener() {onBackPressedDispatcher.onBackPressed()}
        editar.setOnClickListener() {
            val dialogView = layoutInflater.inflate(R.layout.dialog_crear_equipo, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val nombreEditText = dialogView.findViewById<EditText>(R.id.teamNameEditText)
            val categoriaEditText = dialogView.findViewById<EditText>(R.id.teamCategoryEditText)
            imagenD = dialogView.findViewById<ImageView>(R.id.teamPhotoImageView)
            val guardarButton = dialogView.findViewById<Button>(R.id.saveTeamButton)
            val eliminarButton = dialogView.findViewById<ImageButton>(R.id.eliminarButton)

            guardarButton.setText("Actualizar datos")
            nombreEditText.text = Editable.Factory.getInstance().newEditable(Sesion.equipo!!.nombre)
            categoriaEditText.text = Editable.Factory.getInstance().newEditable(Sesion.equipo!!.categoria)
            eliminarButton.visibility = View.VISIBLE

            if (!Sesion.equipo?.foto.isNullOrEmpty()) {
                val fotoPath = Sesion.equipo!!.foto!!
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
                    .setTitle("Cambiar foto del equipo")
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
                    .setTitle("Eliminar equipo")
                    .setMessage("¿Seguro que desea borrar este equipo?")
                    .setCancelable(true)
                    .setPositiveButton("Eliminar equipo") { _, _ ->
                        val dialog = AlertDialog.Builder(this)
                            .setCancelable(true)
                            .setTitle("Doble confimacion")
                            .setMessage("Esta operación no se puede cancelar, se eliminarán permanentemente el equipo y todos los datos asociados a el (jugadores, estadísticas, etc)")
                            .setNegativeButton("Cancelar", null)
                            .setPositiveButton("Eliminar equipo") { _, _ ->
                                dialog.dismiss()
                                eliminarEquipo(this)
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
                val categoria = categoriaEditText.text.toString()

                if (nombre.isNotEmpty() && categoria.isNotEmpty()) {
                    actualizarEquipo(nombre, categoria)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }

        TabLayoutMediator(tabLayout, viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "Jugadores"
                1 -> "Partidos"
                else -> "otro"
            }
        }.attach()

        //para usar el mismo floating button uso este metodo para desde codigo saber que ventana esta seleccionada
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tipo = when (position) {
                    0 -> "jugadores"
                    1 -> "partidos"
                    else -> "otro"
                }
            }
        })

        addButton.setOnClickListener(){
            when (tipo){
                "jugadores" -> añadirJugador()
                "partidos" -> iniciarPartido()
                else -> Log.e("ERROR", "Fallo en la gestion interna de las pestañas, no sabe en cual está")
            }
        }
    }

    private fun iniciarPartido() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Iniciar partido")
            .setMessage("¿Desea iniciar un partido?")
            .setCancelable(true)
            .setPositiveButton("Comenzar") { _, _ ->
                val intent = Intent(this, PartidoActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun añadirJugador() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_crear_jugador, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val nombreEditText = dialogView.findViewById<EditText>(R.id.playerNameEditText)
        val anioEditText = dialogView.findViewById<EditText>(R.id.playerBirthYearEditText)
        val dorsal = dialogView.findViewById<EditText>(R.id.playerDorsalEditText)
        val guardarButton = dialogView.findViewById<Button>(R.id.savePlayerButton)
        imagenJ = dialogView.findViewById<ImageView>(R.id.playerPhotoImageView)

        imagenJ.setOnClickListener{
            val opciones = arrayOf("Elegir desde galería", "Tomar foto con cámara")
            AlertDialog.Builder(this)
                .setTitle("Cambiar foto de perfil")
                .setItems(opciones) { _, which ->
                    when (which) {
                        0 -> photoPicker2.pickFromGallery()
                        1 -> photoPicker2.takePhoto()
                    }
                }
                .show()
        }

        guardarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val anio = anioEditText.text
            val dorsal = dorsal.text

            if (nombre.isNotEmpty() && anio.isNotEmpty() && dorsal.isNotEmpty()) {
                //creo estos objetos para evitar errores de bucles en la serializacion a la hora de hacer el insert en la BD
                val c: Entrenador = Entrenador(Sesion.user!!.id, "", null, Sesion.user!!.email, "", null)
                val b: Equipo = Equipo(Sesion.equipo!!.id, "", "", null, c)
                val jugador: Jugador = Jugador(null, false, nombre, dorsal.toString().toInt(), fotoJugador, anio.toString().toInt(), b)

                lifecycleScope.launch {
                    try {
                        val response = ApiClient.jugadorService.crearJugador(Sesion.token!!, jugador)

                        if (response.isSuccessful) {
                            cargarDatos()
                        }
                    } catch (e:Exception) {Log.e("ERROR", "${e.message}")}
                }

                dialog.dismiss()
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

    }

    private fun eliminarEquipo(context: Context) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.equipoService.borrarEquipo(Sesion.token!!, Sesion.equipo?.id!!)

                if(response.isSuccessful) {
                    Sesion.user!!.equipos.remove(Sesion.equipo)

                    //eliminar la foto del equipo del almacenamiento
                    Sesion.equipo!!.foto?.let { fotoPath ->
                        val file = File(fotoPath)
                        if (file.exists()) {
                            val eliminado = file.delete()
                            if (eliminado) {
                                Log.d("EliminarFoto", "Foto eliminada correctamente.")
                            } else {
                                Log.e("EliminarFoto", "No se pudo eliminar la foto.")
                            }
                        } else {
                            Log.w("EliminarFoto", "Archivo no encontrado en la ruta: $fotoPath")
                        }
                        Sesion.equipo!!.foto = null
                    } ?: run {
                        Log.w("EliminarFoto", "El jugador no tiene una foto asignada.")
                    }

                    Sesion.equipo = null

                    val intent = Intent(context, InicioActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {Log.e("ERROR", "response no succesful")}
            } catch (e:Exception) {Log.e("ERROR", "${e.message}")}
        }
    }

    private fun actualizarEquipo(nombre: String, categoria: String) {
        Sesion.user!!.equipos.remove(Sesion.equipo)
        Sesion.equipo!!.nombre = nombre
        Sesion.equipo!!.categoria = categoria
        Sesion.equipo!!.foto = foto

        lifecycleScope.launch {
            try {
                val response = ApiClient.equipoService.actualizarEquipo(Sesion.token!!, Sesion.equipo!!)
                if (response.isSuccessful) {
                    Sesion.equipo = response.body()
                    Sesion.user!!.equipos.add(Sesion.equipo!!)
                    cargarDatos()
                }
            } catch (e:Exception) {Log.e("ERROR", "${e.message}")}
        }
    }

    private fun cargarDatos() {
        nombre.text = Sesion.equipo!!.nombre
        categoria.text = Sesion.equipo!!.categoria

        if (Sesion.equipo!!.foto!!.isNotEmpty()) {
            val fotoPath = Sesion.equipo!!.foto!!
            val fotoFile = File(fotoPath)
            if (fotoFile.exists()) {
                Glide.with(this)
                    .load(fotoFile)
                    .circleCrop()
                    .into(imagen)
            }
        }
        val adapter = EquipoViewPagerAdapter(this)
        viewpager.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }
}