package es.diego.handballstats.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.diego.handballstats.R
import es.diego.handballstats.services.Sesion
import es.diego.handballstats.adapters.InicioAdapter
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.databinding.InicioBinding
import es.diego.handballstats.models.Equipo
import es.diego.handballstats.services.PhotoPickerHelper
import kotlinx.coroutines.launch
import java.io.File

class InicioActivity: AppCompatActivity(){
    private lateinit var miBinding:InicioBinding
    private lateinit var boton: Button
    private lateinit var recycler: RecyclerView
    private lateinit var perfilBoton: ImageButton
    private var equipos: MutableList<Equipo> = mutableListOf()
    private lateinit var photoPicker: PhotoPickerHelper
    private lateinit var imagenPerfil: ImageView
    private var foto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        miBinding= InicioBinding.inflate(layoutInflater)
        setContentView(miBinding.root)

        photoPicker = PhotoPickerHelper(this) { bitmap, path ->
            Glide.with(this)
                .load(File(path))
                .circleCrop()
                .into(imagenPerfil)
            foto = path
        }

        perfilBoton = miBinding.profileButton
        boton = miBinding.crearButton
        recycler = miBinding.teamsRecyclerView

        cargarEquipos()

        recycler.layoutManager = GridLayoutManager(this, 2)

        cargarAdapter()

        if (!Sesion.user?.foto.isNullOrEmpty()) {
            val fotoPath = Sesion.user!!.foto!!
            val fotoFile = File(fotoPath)
            if (fotoFile.exists()) {
                Glide.with(this)
                    .load(fotoFile)
                    .circleCrop()
                    .into(perfilBoton)
            }
        }

        perfilBoton.setOnClickListener() {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        boton.setOnClickListener() {
            val dialogView = layoutInflater.inflate(R.layout.dialog_crear_equipo, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val nombreEditText = dialogView.findViewById<EditText>(R.id.teamNameEditText)
            val categoriaEditText = dialogView.findViewById<EditText>(R.id.teamCategoryEditText)
            val guardarButton = dialogView.findViewById<Button>(R.id.saveTeamButton)
            imagenPerfil = dialogView.findViewById<ImageView>(R.id.teamPhotoImageView)

            imagenPerfil.setOnClickListener{
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

            guardarButton.setOnClickListener {
                val nombre = nombreEditText.text.toString()
                val categoria = categoriaEditText.text.toString()

                if (nombre.isNotEmpty() && categoria.isNotEmpty()) {
                    guardarEquipo(nombre, categoria)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }
    }

    private fun guardarEquipo(nombre: String, categoria: String) {
        var a: Equipo = Equipo(null, nombre, categoria, foto, Sesion.user!!, null, null)

        lifecycleScope.launch {
            try {
                val response = ApiClient.equipoService.crearEquipo(Sesion.token!!, a)

                if (response.isSuccessful) {
                    Sesion.user!!.equipos.add(response.body()!!)
                    cargarEquipos()
                }
            } catch (e:Exception) {Log.e("ERROR", "Fallo al hacer login: ${e.message}")}
        }
    }

    private fun cargarEquipos() {
        if (Sesion.user!!.equipos.isNotEmpty()) {
            equipos = Sesion.user!!.equipos
            cargarAdapter()
        } else {
            lifecycleScope.launch {
                try {
                    equipos = ApiClient.equipoService.buscarEquipos(Sesion.token!!, Sesion.user!!.id!!).body()!!
                    Sesion.user!!.equipos.addAll(equipos)
                    cargarAdapter()
                } catch (e: Exception) {Log.e("ERROR", "Fallo al hacer login: ${e.message}")}
            }
        }
    }

    private fun cargarAdapter() {
        val adapter = InicioAdapter(equipos.sortedBy{it.id}.toMutableList())
        recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarEquipos()
        if (!Sesion.user?.foto.isNullOrEmpty()) {
            val fotoPath = Sesion.user!!.foto!!
            val fotoFile = File(fotoPath)
            if (fotoFile.exists()) {
                Glide.with(this)
                    .load(fotoFile)
                    .circleCrop()
                    .into(perfilBoton)
            }
        }
    }
}