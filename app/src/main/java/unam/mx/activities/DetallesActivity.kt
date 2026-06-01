package unam.mx.activities

import android.os.Bundle
import android.view.View
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

class DetallesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetallesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val id =
            intent.getIntExtra(
                "character_id",
                -1
            )

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
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    private fun loadPersonaje(id:Int){

        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val response =
                    getRetrofit()
                        .create(ServiceApi::class.java)
                        .getPersonajeById(id)

                val personaje =
                    response.body()

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    if(
                        response.isSuccessful &&
                        personaje != null
                    ){

                        showPersonaje(personaje)

                    }else{

                        Toast.makeText(
                            this@DetallesActivity,
                            "Error al cargar personaje",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }catch (e:Exception){

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    Toast.makeText(
                        this@DetallesActivity,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showPersonaje(
        personaje: ModeloPersonaje
    ){

        binding.nombrePersonaje.text =
            personaje.name

        binding.razaPersonaje.text =
            "Raza: ${personaje.race}"

        binding.genPersonaje.text =
            "Género: ${personaje.gender}"

        binding.kiPersonaje.text =
            "Ki: ${personaje.ki}"

        binding.maxKiPersonaje.text =
            "Máximo Ki: ${personaje.maxKi}"

        binding.descPersonaje.text =
            personaje.description

        Glide.with(this)
            .load(personaje.image)
            .into(binding.idPersonaje)
    }
}

