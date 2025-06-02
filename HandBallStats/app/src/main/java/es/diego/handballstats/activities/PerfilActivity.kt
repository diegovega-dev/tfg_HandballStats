package es.diego.handballstats.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.bumptech.glide.Glide
import es.diego.handballstats.services.Sesion
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.databinding.PerfilBinding
import es.diego.handballstats.models.Entrenador
import es.diego.handballstats.services.PhotoPickerHelper
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest

class PerfilActivity: AppCompatActivity(){
    private lateinit var miBinding: PerfilBinding
    private lateinit var actualPass: EditText
    private lateinit var actualPassError: TextView
    private lateinit var newPass: EditText
    private lateinit var newPassError: TextView
    private lateinit var repPass: EditText
    private lateinit var repPassError: TextView
    private lateinit var submit: Button
    private lateinit var deleteAccount: Button
    private lateinit var logout: ImageButton
    private lateinit var atras: ImageButton
    private lateinit var imagenPerfil: ImageView
    private lateinit var photoPicker: PhotoPickerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        miBinding= PerfilBinding.inflate(layoutInflater)
        setContentView(miBinding.root)

        //gestion de imagenes
        photoPicker = PhotoPickerHelper(this) { bitmap, path ->
            Sesion.user!!.foto?.let { oldPath ->
                if (oldPath.isNotEmpty() && oldPath != path) {
                    val oldFile = File(oldPath)
                    if (oldFile.exists()) oldFile.delete()
                }
            }

            Glide.with(this)
                .load(File(path))
                .circleCrop()
                .into(imagenPerfil)
            Sesion.user!!.foto = path
            actualizarDatos()
        }

        actualPass = miBinding.actualPassEditText
        actualPassError = miBinding.actualPassErrorText
        newPass = miBinding.passwordEditText
        newPassError = miBinding.passError
        repPass = miBinding.repeatPasswordEditText
        repPassError = miBinding.repeatPasswordErrorText
        submit = miBinding.updateAccountButton
        deleteAccount = miBinding.deleteAccountButton
        logout = miBinding.logoutButton
        atras = miBinding.backButton
        imagenPerfil = miBinding.profileImageView

        logout.setOnClickListener(){

            val dialog = AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Seguro que desea cerrar sesión?")
                .setCancelable(true)
                .setPositiveButton("Cerrar Sesion") { _, _ ->
                    limpiarPreferencias()
                    irLogin()
                }
                .setNegativeButton("Cancelar", null)
                .create()

            dialog.show()
        }

        deleteAccount.setOnClickListener() {
            if (Sesion.user!!.password != encriptar(actualPass.text.toString())) {
                actualPassError.text = "Ingrese la contraseña"
                actualPassError.visibility = View.VISIBLE

                val dialog = AlertDialog.Builder(this)
                    .setTitle("Ingrese contraseña")
                    .setMessage("Ingrese la contraseña actual para poder eliminar la cuenta.")
                    .setCancelable(true)
                    .setPositiveButton("Vale", null)
                    .create()

                dialog.show()
            } else {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Borrar Cuenta")
                    .setMessage("¿Seguro que desea borrar cuenta?")
                    .setCancelable(true)
                    .setPositiveButton("Eliminar cuenta") { _, _ ->
                        val dialog = AlertDialog.Builder(this)
                            .setCancelable(true)
                            .setTitle("Doble confimacion")
                            .setMessage("Esta operación no se puede cancelar, se eliminarán permanentemente la cuenta y todos los datos asociados a ella (equipos, jugadores, etc)")
                            .setNegativeButton("Cancelar", null)
                            .setPositiveButton("Eliminar cuenta") { _, _ ->
                                limpiarPreferencias()
                                irLogin()
                                borrarCuenta()
                            }
                            .create()

                        dialog.show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .create()

                dialog.show()
            }
        }

        atras.setOnClickListener() {onBackPressedDispatcher.onBackPressed()}

        submit.setOnClickListener() {intentarActualizar()}

        actualPass.setOnClickListener(){limpiarErrores()}
        newPass.setOnClickListener(){limpiarErrores()}
        repPass.setOnClickListener(){limpiarErrores()}

        //gestion de imagenes
        if (!Sesion.user?.foto.isNullOrEmpty()) {
            val fotoPath = Sesion.user!!.foto!!
            val fotoFile = File(fotoPath)
            if (fotoFile.exists()) {
                Glide.with(this)
                    .load(fotoFile)
                    .circleCrop()
                    .into(imagenPerfil)
            }
        }

        imagenPerfil.setOnClickListener{
            val opciones = arrayOf("Elegir desde galería", "Tomar foto con cámara")
            AlertDialog.Builder(this)
                .setTitle("Cambiar foto de perfil")
                .setItems(opciones) { _, which ->
                    when (which) {
                        0 -> photoPicker.pickFromGallery()
                        1 -> photoPicker.takePhoto()
                    }
                }
                .show()
        }
    }

    private fun limpiarErrores(){
        actualPassError.text = ""
        actualPassError.visibility = View.GONE
        newPassError.text = ""
        newPassError.visibility = View.GONE
        repPassError.text = ""
        repPassError.visibility = View.GONE
    }

    private fun intentarActualizar() {
        val aP: String = encriptar(actualPass.text.toString())
        val nP: String = newPass.text.toString()
        val rP: String = repPass.text.toString()

        when {
            actualPass.text.toString().isEmpty() -> {
                actualPassError.text = "Rellene el campo"
                actualPassError.visibility = View.VISIBLE
            }
            nP.isEmpty() -> {
                newPassError.text = "Rellene el campo"
                newPassError.visibility = View.VISIBLE
            }
            rP.isEmpty() -> {
                repPassError.text = "Rellene el campo"
                repPassError.visibility = View.VISIBLE
            }
            !nP.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,}$")) -> {
                newPassError.text = "La contraseña debe tener mínimo 6 caracteres:\n- 1 mayúscula\n- 1 minúscula\n- 1 número"
                newPassError.visibility = View.VISIBLE
            }
            rP != nP -> {
                newPassError.text = "Las contraseñas no coinciden"
                newPassError.visibility = View.VISIBLE
                repPassError.text = "Las contraseñas no coinciden"
                repPassError.visibility = View.VISIBLE
            }
            aP != Sesion.user!!.password -> {
                actualPassError.text = "Contraseña incorrecta"
                actualPassError.visibility = View.VISIBLE
            }
            else -> {
                Sesion.user!!.password = encriptar(nP)
                actualizarDatos()
            }
        }
    }

    private fun actualizarDatos() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.entrenadorService.actualizarEntrenador(Sesion.user!!)

                if (response.isSuccessful) {
                    val a = response.body() as Entrenador
                    Sesion.user = a
                    onBackPressedDispatcher.onBackPressed()
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Fallo al hacer login: ${e.message}")
            }
        }
    }

    private fun encriptar(a: String): String {
        val bytes = a.toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        val hexString = StringBuilder()

        for (b in digest) {
            val hex = Integer.toHexString(0xff and b.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }

        return hexString.toString()
    }

    private fun borrarCuenta() {
        lifecycleScope.launch {
            try {
                val id: Int? = Sesion.user?.id

                if (id != null) {
                    ApiClient.entrenadorService.borrarEntrenador(Sesion.token!!, id)

                    //eliminar la foto de perfil del almacenamiento
                    Sesion.user!!.foto?.let { fotoPath ->
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
                        Sesion.user!!.foto = null
                    } ?: run {
                        Log.w("EliminarFoto", "El jugador no tiene una foto asignada.")
                    }

                    irLogin()
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Fallo al hacer login: ${e.message}")
            }
        }
    }

    private fun irLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun limpiarPreferencias() {
        val preferencias = EncryptedSharedPreferences.create(
            "preferencias",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        preferencias.edit().clear().apply()
    }

}