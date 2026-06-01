package mx.unam.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import unam.mx.R
import unam.mx.model.ModeloPersonaje

class PersonajeAdapter(
    private var personajes: MutableList<ModeloPersonaje>,
    private val onDetailClick: (ModeloPersonaje) -> Unit
) : RecyclerView.Adapter<PersonajeViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonajeViewHolder {

        val layoutInflater =
            LayoutInflater.from(parent.context)

        return PersonajeViewHolder(
            layoutInflater.inflate(
                R.layout.item_personaje,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: PersonajeViewHolder,
        position: Int
    ) {

        val item = personajes[position]

        holder.bind(item)

        holder.btnDetalles.setOnClickListener {
            onDetailClick(item)
        }
    }

    override fun getItemCount(): Int {
        return personajes.size
    }

    fun updateList(newList: List<ModeloPersonaje>) {
        personajes.clear()
        personajes.addAll(newList)
        notifyDataSetChanged()
    }
}