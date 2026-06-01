package mx.unam.model

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import unam.mx.R
import unam.mx.model.ModeloPersonaje

class PersonajeViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    val btnDetalles: Button =
        view.findViewById(R.id.btnDetalles)

    private val idPersonaje: ImageView =
        view.findViewById(R.id.idPersonaje)

    private val nombrePresonaje: TextView =
        view.findViewById(R.id.nombrePersonaje)

    private val razaPersonaje: TextView =
        view.findViewById(R.id.razaPersonaje)

    fun bind(personaje: ModeloPersonaje) {

        nombrePresonaje.text = personaje.name
        razaPersonaje.text = personaje.race

        Glide.with(itemView.context)
            .load(personaje.image)
            .into(idPersonaje)
    }
}