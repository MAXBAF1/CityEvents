package com.example.cityevents.adapters

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.cityevents.R
import com.example.cityevents.data.Event

class EventsAdapter(
    private val context: Context,
    private val events: List<Event>
) : RecyclerView.Adapter<EventsAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.photoImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        private val categoryTv: TextView = itemView.findViewById(R.id.eventCategoryTv)

        fun bind(event: Event) {
            loadImage(event.images.first(), imageView)
            nameTextView.text = event.name
            categoryTv.text = event.category
        }
    }

    private fun loadImage(url: String?, imageView: ImageView) {
        if (url.isNullOrEmpty()) {
            Glide.with(context)
                .load(R.mipmap.ic_launcher)
                .transform(RoundedCorners(10))
                .into(imageView)
        } else {
            Glide.with(context)
                .load(url)
                .transform(RoundedCorners(10))
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int = events.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
        holder.itemView.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context).create()
            val layoutInflater = LayoutInflater.from(context)
            val view = layoutInflater.inflate(R.layout.event_item_detailed, null)
            initView(view, event)
            alertDialog.setView(view)

            alertDialog.window?.setBackgroundDrawableResource(R.drawable.card_background)
            alertDialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView(view: View, event: Event) {
        Glide.with(context)
            .load(event.images)
            .transform(RoundedCorners(30))
            .into(view.findViewById(R.id.event_image))

        view.findViewById<TextView>(R.id.eventCategoryTv).text = event.category
        view.findViewById<TextView>(R.id.eventNameTv).text = event.name
        view.findViewById<TextView>(R.id.eventTimeTv).text = event.date.hour.toString()
        view.findViewById<TextView>(R.id.eventDateTv).text = event.date.toString()
        view.findViewById<TextView>(R.id.eventPlaceTv).text = event.placeAddress
        view.findViewById<TextView>(R.id.eventPlaceNameTv).text = event.placeName
        view.findViewById<TextView>(R.id.eventDescriptionTv).text = event.description
    }
}