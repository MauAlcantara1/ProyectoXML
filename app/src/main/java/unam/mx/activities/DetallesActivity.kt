package unam.mx.activities

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import unam.mx.api.ServiceApi
import unam.mx.databinding.ActivityDetallesBinding
import unam.mx.model.ModeloPersonaje
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class DetallesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallesBinding
    private lateinit var rotacionAnim: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        binding = ActivityDetallesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rotacionAnim = ObjectAnimator.ofFloat(
            binding.progressBar, "rotation", 0f, 360f
        ).apply {
            duration = 800
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        val id = intent.getIntExtra("character_id", -1)
        if (id != -1) {
            loadPersonaje(id)
        }

        binding.btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dragonball-api.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun loadPersonaje(id: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        rotacionAnim.start()

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val response = getRetrofit()
                    .create(ServiceApi::class.java)
                    .getPersonajeById(id)

                val personaje = response.body()

                runOnUiThread {
                    rotacionAnim.cancel()
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    if (response.isSuccessful && personaje != null) {
                        showPersonaje(personaje)
                    } else {
                        Toast.makeText(
                            this@DetallesActivity,
                            "Error al cargar personaje",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    rotacionAnim.cancel()
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    Toast.makeText(
                        this@DetallesActivity,
                        "Error de conexión, verifica tu conexion a internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showPersonaje(personaje: ModeloPersonaje) {
        binding.nombrePersonaje.text = personaje.name
        binding.razaPersonaje.text = personaje.race
        binding.genPersonaje.text = personaje.gender
        binding.kiPersonaje.text = personaje.ki
        binding.maxKiPersonaje.text = personaje.maxKi
        binding.descPersonaje.text = personaje.description

        Glide.with(this)
            .load(personaje.image)
            .into(binding.idPersonaje)
    }

    override fun onDestroy() {
        super.onDestroy()
        rotacionAnim.cancel()
    }
}

