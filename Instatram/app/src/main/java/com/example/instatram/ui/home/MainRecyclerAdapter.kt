package com.example.instatram.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instatram.gridDisplay.PhotosActivity
import com.example.instatram.R
import com.example.instatram.data.Station


const val STATION_ID = "com.example.instatram.STATION_ID"
const val STATION_NAME = "com.example.instatram.STATION_NAME"

class MainRecyclerAdapter(val context: Context,
                          val viewModel: HomeViewModel,
                          val stations: List<Station>):
    RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>()
{

    // Nombre d’éléments de données à afficher
    override fun getItemCount() = stations.size

    // Retourne un ViewHolder et fait une association avec le fichier layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.station_grid_item, parent, false)
        return ViewHolder(view)
    }

    // Fait le lien entre les composants et les données à afficher dans le recyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val station = stations[position]
        with(holder) {
            nameText?.let {
                it.text = station.name
                it.contentDescription = station.name
            }
            itemView.setOnClickListener {
                if (!viewModel.wasClicked){
                    val intent = Intent(it.context, PhotosActivity::class.java).apply {
                        putExtra(STATION_ID, station.id)
                        putExtra(STATION_NAME, station.name)
                    }
                    viewModel.wasClicked = true
                    it.context.startActivity(intent)
                }
            }
        }
    }


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val nameText = itemView.findViewById<TextView>(R.id.nameText)
    }
}