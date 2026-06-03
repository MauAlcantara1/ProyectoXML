package unam.mx.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.text.Editable
import android.text.TextWatcher
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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PersonajeAdapter
    private val personajes = mutableListOf<ModeloPersonaje>()
    private lateinit var auth: FirebaseAuth

    private val personajesOriginales = mutableListOf<ModeloPersonaje>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initReciclerView()

        loadPersonajes()

        // Aquí van los binding/listeners
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                search(binding.etSearch.text.toString())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
                binding.etSearch.clearFocus()
                true
            } else {
                false
            }
        }

        binding.btnSearch.setOnClickListener {
            search(binding.etSearch.text.toString())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
            binding.etSearch.clearFocus()
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
                auth.signOut()
                val intent = Intent(this, IniciarSesionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
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
        if (nombre.isBlank()) {
            personajes.clear()
            personajes.addAll(personajesOriginales)
            adapter.notifyDataSetChanged()
            return
        }

        val filtrados = personajesOriginales.filter {
            it.name.contains(nombre, ignoreCase = true)
        }

        personajes.clear()
        personajes.addAll(filtrados)
        adapter.notifyDataSetChanged()
    }


    private fun msgError(message: String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }
}