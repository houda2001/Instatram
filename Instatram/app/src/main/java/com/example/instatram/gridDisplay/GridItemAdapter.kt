package com.example.instatram.gridDisplay

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.instatram.displayImage.DisplayImageViewModel
import com.example.instatram.R
import com.example.instatram.ViewImageActivity
import com.example.instatram.ui.home.STATION_ID
import com.example.instatram.ui.home.STATION_NAME


const val PHOTO_ID = "com.example.instatram.PHOTO_ID"

class GridItemAdapter(val viewModel: DisplayImageViewModel, val cardTitles :MutableList<String>, val cardImages :MutableList<String>, val cardDates :MutableList<String>, val cardIds: MutableList<Int>, val stationId: Int, val stationName: String?): RecyclerView.Adapter<GridItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage :ImageView =itemView.findViewById(R.id.cardImage)
        val cardTitle : TextView =itemView.findViewById(R.id.cardTitle)
        val cardDate : TextView =itemView.findViewById(R.id.cardDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_view,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cardTitles.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardTitle.text = cardTitles[position]
        holder.cardDate.text = cardDates[position]
        val opt = BitmapFactory.Options()
        opt.inSampleSize = 6
        holder.cardImage.setImageBitmap(BitmapFactory.decodeFile(cardImages[position].toUri().path, opt))

        val photoId = cardIds[position]
        with(holder) {
            itemView.setOnClickListener {
                if (!viewModel.wasClicked){
                    val intent = Intent(it.context, ViewImageActivity::class.java).apply {
                        putExtra(PHOTO_ID, photoId)
                        putExtra(STATION_ID, stationId)
                        putExtra(STATION_NAME, stationName)
                    }
                    viewModel.wasClicked = true
                    it.context.startActivity(intent)
                }
            }
        }
    }
}