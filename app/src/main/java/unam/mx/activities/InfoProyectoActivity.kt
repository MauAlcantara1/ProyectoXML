package unam.mx.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import unam.mx.databinding.ActivityInfoProyectoBinding
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class InfoProyectoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoProyectoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        binding = ActivityInfoProyectoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRegresar.setOnClickListener {
            finish()
        }
    }
}