package es.diego.handballstats.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import es.diego.handballstats.services.Sesion
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.databinding.CrearCuentaBinding
import es.diego.handballstats.models.Entrenador
import kotlinx.coroutines.launch

class RegisterActivity: AppCompatActivity() {
    private lateinit var miBinding: CrearCuentaBinding
    private lateinit var atras: ImageButton
    private lateinit var nombre: EditText
    private lateinit var nombreError: TextView
    private lateinit var email: EditText
    private lateinit var emailError: TextView
    private lateinit var pass: EditText
    private lateinit var passError: TextView
    private lateinit var repPass: EditText
    private lateinit var repPassError: TextView
    private lateinit var submit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        miBinding = CrearCuentaBinding.inflate(layoutInflater)
        setContentView(miBinding.root)

        atras = miBinding.backButton
        nombre = miBinding.nameEditText
        nombreError = miBinding.nameErrorText
        email = miBinding.emailEditText
        emailError = miBinding.emailErrorText
        pass = miBinding.passwordEditText
        passError = miBinding.passwordErrorText
        repPass = miBinding.repeatPasswordEditText
        repPassError = miBinding.repeatPasswordErrorText
        submit = miBinding.createAccountButton

        atras.setOnClickListener(){onBackPressedDispatcher.onBackPressed()}
        submit.setOnClickListener(){IntentarRegistro()}
        nombre.setOnClickListener(){LimpiarErrores()}
        email.setOnClickListener(){LimpiarErrores()}
        pass.setOnClickListener(){LimpiarErrores()}
        repPass.setOnClickListener(){LimpiarErrores()}

    }

    private fun IntentarRegistro() {

        val nombreTexto = nombre.text.toString()
        val emailTexto = email.text.toString()
        val passTexto = pass.text.toString()
        val repPassTexto = repPass.text.toString()

        when {
            nombreTexto.isEmpty() -> {
                nombreError.text = "El campo de nombre no puede estar vacío"
                nombreError.visibility = View.VISIBLE
            }
            emailTexto.isEmpty() || !emailTexto.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) -> {
                emailError.text = "El email no puede estar vacío o es incorrecto"
                emailError.visibility = View.VISIBLE
            }
            passTexto.isEmpty() -> {
                passError.text = "El campo de contraseña no puede estar vacío"
                passError.visibility = View.VISIBLE
            }
            !passTexto.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,}$")) -> {
                passError.text = "La contraseña debe tener mínimo 6 caracteres:\n- 1 mayúscula\n- 1 minúscula\n- 1 número"
                passError.visibility = View.VISIBLE
            }
            repPassTexto.isEmpty() -> {
                repPassError.text = "Este campo no puede estar vacío"
                repPassError.visibility = View.VISIBLE
            }
            passTexto != repPassTexto -> {
                repPassError.text = "Las contraseñas no coinciden"
                repPassError.visibility = View.VISIBLE
            }
            else -> {
                existeEmail(emailTexto) { existe ->
                    runOnUiThread {
                        if (existe) {
                            emailError.text = "El correo electrónico ya está registrado"
                            emailError.visibility = View.VISIBLE
                        } else {
                            Registrar()
                        }
                    }
                }
            }
        }
    }

    private fun LimpiarErrores() {
        nombreError.text = ""
        passError.text = ""
        emailError.text = ""
        repPassError.text = ""
        nombreError.visibility = View.GONE
        passError.visibility = View.GONE
        emailError.visibility = View.GONE
        repPassError.visibility = View.GONE
    }

    private fun Registrar() {
        lifecycleScope.launch {
            try {
                val a: Entrenador = Entrenador(null, nombre.text.toString(), null, email.text.toString(), pass.text.toString(), null)
                val response = ApiClient.authService.register(a)

                if (response.isSuccessful) {
                    val token = response.body()?.string()
                    Log.e("TOKENS", "Token devuelto: $token")

                    if (token != null) {
                        val user: Entrenador = ApiClient.entrenadorService.buscarEntrenador(token, email.text.toString()).body() as Entrenador

                        Sesion.token = token
                        Sesion.user = user

                        Log.e("TOKENS", "Token en sesion: " + Sesion.token)
                        irInicio()
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Fallo al hacer login: ${e.message}")
            }
        }
    }

    private fun existeEmail(email: String, callback: (Boolean) -> Unit) {
        lifecycleScope.launch {
            try {
                val exists = ApiClient.entrenadorService.existeEntrenador(email).body() == true
                callback(exists)
            } catch (e: Exception) {
                Log.e("ERROR", "Fallo al comprobar email: ${e.message}")
                callback(true) // Por seguridad, asumimos que existe
            }
        }
    }

    private fun irInicio () {
        //abre la pantalla de inicio y elimina de la pila todas las actividades que habia (login/registro)
        val intent = Intent(this, InicioActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}