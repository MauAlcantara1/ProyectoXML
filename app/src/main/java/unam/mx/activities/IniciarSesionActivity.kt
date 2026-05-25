package unam.mx.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import unam.mx.R

class IniciarSesionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var ultimoIntento = 0L
    private val COOLDOWN_MS = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        auth = FirebaseAuth.getInstance()

        /*
        Cuando tengamos listo el boton de cerrar sesión se descomenta, esta parte de aqui solamente guarda al usuario verificado, si cierras la app te mantendra adentro jsajkjodjojqw
        val usuarioActual = auth.currentUser
        if (usuarioActual != null && usuarioActual.isEmailVerified) {
            irAMain()
            return
        }
        */


        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)


        btnLogin.setOnClickListener {

            val ahora = System.currentTimeMillis()
            if (ahora - ultimoIntento < COOLDOWN_MS) {
                Toast.makeText(this, "Espera un momento antes de intentar de nuevo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ultimoIntento = ahora

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val user = auth.currentUser

                        if (user != null && user.isEmailVerified) {
                            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                            irAMain()
                        } else {
                            Toast.makeText(
                                this,
                                "Debes verificar tu correo electrónico antes de continuar",
                                Toast.LENGTH_LONG
                            ).show()
                            auth.signOut()
                            btnLogin.isEnabled = true
                        }

                    } else {
                        val mensajeError = when {
                            task.exception?.message?.contains("no user record") == true ->
                                "No existe una cuenta con ese correo"
                            task.exception?.message?.contains("password is invalid") == true ->
                                "Contraseña incorrecta"
                            else -> "Error al iniciar sesión. Verifica tus datos"
                        }

                        Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
                        btnLogin.isEnabled = true
                    }
                }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun irAMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}