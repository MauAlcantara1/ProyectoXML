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
import com.google.firebase.firestore.FirebaseFirestore
import unam.mx.R

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private fun passwordValida(password: String): Boolean {
        val specialCharacter = Regex(".*[!@#\$%^&*()_+=|<>?{}\\[\\]~-].*")
        return password.length >= 6 && specialCharacter.matches(password)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etPaterno = findViewById<EditText>(R.id.etApellidoPaterno)
        val etMaterno = findViewById<EditText>(R.id.etApellidoMaterno)
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvRegresar = findViewById<TextView>(R.id.tvRegresar)





        btnRegister.setOnClickListener {

            val nombre = etNombre.text.toString().trim()
            val paterno = etPaterno.text.toString().trim()
            val materno = etMaterno.text.toString().trim()
            val usuario = etUsuario.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nombre.isEmpty() || paterno.isEmpty() || materno.isEmpty() ||
                usuario.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (usuario.length < 3) {
                Toast.makeText(this, "El usuario debe tener mínimo 3 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!passwordValida(password)) {
                Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres y un símbolo especial", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            db.collection("usuarios")
                .whereEqualTo("usuario", usuario)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                                    auth.currentUser?.sendEmailVerification()
                                        ?.addOnSuccessListener {

                                            val usuarioData = hashMapOf(
                                                "nombre" to nombre,
                                                "paterno" to paterno,
                                                "materno" to materno,
                                                "usuario" to usuario,
                                                "email" to email
                                            )

                                            db.collection("usuarios")
                                                .document(uid)
                                                .set(usuarioData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this,
                                                        "Cuenta creada. Revisa tu correo (SPAM) para verificarla.",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    finish()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(
                                                        this,
                                                        "Usuario creado pero error al guardar perfil",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }

                                        }
                                        ?.addOnFailureListener { e ->
                                            Toast.makeText(
                                                this,
                                                "Cuenta creada pero no se pudo enviar el correo: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            finish()
                                        }

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Error al crear usuario: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al verificar nombre de usuario", Toast.LENGTH_SHORT).show()
                }

            }

        tvRegresar.setOnClickListener {
            startActivity(Intent(this, IniciarSesionActivity::class.java))
        }
    }
}