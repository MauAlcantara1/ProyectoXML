package unam.mx.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import unam.mx.databinding.ActivityInfoProyectoBinding

class InfoProyectoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoProyectoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoProyectoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRegresar.setOnClickListener {
            finish()
        }
    }
}