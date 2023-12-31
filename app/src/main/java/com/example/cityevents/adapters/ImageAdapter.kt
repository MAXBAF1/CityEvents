package com.example.cityevents.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cityevents.R
import com.example.cityevents.utils.loadImage

class ImageAdapter(private val images: List<Uri>) :
    RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    private lateinit var itemNumbers: List<Int>

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoView: ImageView = itemView.findViewById(R.id.photoImageView)

        fun bind(urlImage: Uri) {
            photoView.loadImage(urlImage.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return ImageHolder(view)
    }

    fun updateItemNumbers() {
        for (position in itemNumbers.indices) {
            notifyItemChanged(position)
        }
    }


    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(images[position])
        itemNumbers = (1..images.size).toList()
        val itemNumber = itemNumbers[position]

        holder.itemView.findViewById<TextView>(R.id.numberTextView).text = itemNumber.toString()
    }
}