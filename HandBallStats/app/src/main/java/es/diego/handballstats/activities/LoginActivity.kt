package es.diego.handballstats.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import es.diego.handballstats.services.Sesion
import es.diego.handballstats.services.ApiClient
import es.diego.handballstats.databinding.LoginBinding
import es.diego.handballstats.models.Entrenador
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var miBinding:LoginBinding
    private lateinit var email:EditText
    private lateinit var emailError:TextView
    private lateinit var pass:EditText
    private lateinit var passError:TextView
    private lateinit var mostrarPass:CheckBox
    private lateinit var guardarSesion:CheckBox
    private lateinit var submit:Button
    private lateinit var txtCrearCuenta:TextView
    private lateinit var preferencias: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferencias = EncryptedSharedPreferences.create(
            "preferencias",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        miBinding=LoginBinding.inflate(layoutInflater)
        setContentView(miBinding.root)

        email = miBinding.emailEditText
        emailError = miBinding.emailErrorTextView
        pass = miBinding.passwordEditText
        passError = miBinding.passwordErrorTextView
        mostrarPass = miBinding.showPasswordCheckbox
        guardarSesion = miBinding.rememberMeCheckbox
        submit = miBinding.loginButton
        txtCrearCuenta = miBinding.createAccountText

        //si hay datos guardados intenta loggear directamente
        val a: String = preferencias.getString("email", "").toString()
        val b: String = preferencias.getString("pass", "").toString()
        if (a.isNotEmpty() && b.isNotEmpty()) {
            try {
                email.text = Editable.Factory.getInstance().newEditable(a)
                pass.text = Editable.Factory.getInstance().newEditable(b)

                intentarInicio()
            } catch (e: Exception) {
                email.text = Editable.Factory.getInstance().newEditable("")
                pass.text = Editable.Factory.getInstance().newEditable("")
            }
        }

        submit.setOnClickListener(){intentarInicio()}

        txtCrearCuenta.setOnClickListener(){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        email.setOnClickListener(){limpiarErrores()}
        pass.setOnClickListener(){limpiarErrores()}

        mostrarPass.setOnCheckedChangeListener { _, isChecked ->
            pass.inputType = if (isChecked) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            pass.setSelection(pass.text.length)
        }
    }

    private fun intentarInicio(){
        if (email.text.isEmpty()) {
            emailError.text = "El campo de email no puede estar vacio"
            emailError.visibility = View.VISIBLE
        } else if (pass.text.isEmpty()) {
            passError.text = "El campo de contraseña no puede estar vacio"
            passError.visibility = View.VISIBLE

        } else {
            lifecycleScope.launch {
                try {
                    val response = ApiClient.authService.login(email.text.toString(), pass.text.toString())

                    if (response.isSuccessful) {
                        val token = response.body()?.string()

                        if (token != null) {
                            val u: Entrenador = ApiClient.entrenadorService.buscarEntrenador(token, email.text.toString()).body() as Entrenador
                            iniciarSesion(token, guardarSesion.isChecked, u)
                        } else {
                            Log.e("ERROR", "El cuerpo de la respuesta está vacío")
                        }
                    } else {
                        passError.text = "Correo/contraseña incorrectos"
                        passError.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", "Fallo al hacer login: ${e.message}")
                }
            }
        }
    }

    private fun limpiarErrores() {
        emailError.text = null
        passError.text = null
        emailError.visibility = View.GONE
        passError.visibility = View.GONE
    }

    private fun iniciarSesion(token: String, guardar: Boolean, user: Entrenador){
        if (guardar) {
            preferencias.edit()
                .putString("email", email.text.toString())
                .putString("pass", pass.text.toString())
                .apply()
        }

        Sesion.token = token
        Sesion.user = user

        //abre la pantalla de inicio y elimina de la pila todas las actividades que habia (login/registro)
        val intent = Intent(this, InicioActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}