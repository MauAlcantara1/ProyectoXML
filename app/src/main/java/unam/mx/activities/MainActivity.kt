package unam.mx.activities

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.unam.model.PersonajeAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import unam.mx.R
import unam.mx.api.ServiceApi
import unam.mx.databinding.ActivityMainBinding
import unam.mx.model.ModeloPersonaje
import kotlin.jvm.java
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PersonajeAdapter
    private val personajes = mutableListOf<ModeloPersonaje>()

    private val personajesOriginales = mutableListOf<ModeloPersonaje>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Dragon Ball API"

        initReciclerView()

        loadPersonajes()

        binding.btnSearch.setOnClickListener {
            val texto =
                binding.etSearch.text
                    .toString()
                    .trim()
            search(texto)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.main_menu,
            menu
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_info -> {
                startActivity(
                    Intent(
                        this,
                        InfoProyectoActivity::class.java
                    )
                )
                return true
            }
            R.id.menu_salir -> {
                finishAffinity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initReciclerView() {

        adapter = PersonajeAdapter(personajes){ personaje ->
            val intent = Intent(this,
                DetallesActivity::class.java)
            intent.putExtra("character_id",personaje.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://dragonball-api.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun loadPersonajes() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val call = getRetrofit()
                    .create(ServiceApi::class.java)
                    .getPersonajes()

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    val respuesta = call.body()
                    if (call.isSuccessful && respuesta != null) {
                        personajes.clear()
                        personajes.addAll(respuesta.items)

                        personajesOriginales.clear()
                        personajesOriginales.addAll(respuesta.items)

                        adapter.notifyDataSetChanged()
                    } else {
                        msgError("No se pudieron cargar los personajes")
                    }
                }

            } catch (e: Exception) {

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    msgError("Error de conexión")
                }
            }
        }
    }

    private fun search(nombre:String){
        if(nombre.isBlank()){

            personajes.clear()
            personajes.addAll(personajesOriginales)

            adapter.notifyDataSetChanged()

            return
        }

        val filtrados =
            personajesOriginales.filter {

                it.name.contains(
                    nombre,
                    ignoreCase = true
                )
            }

        personajes.clear()
        personajes.addAll(filtrados)

        adapter.notifyDataSetChanged()
    }


    private fun msgError(message: String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }
}