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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.cityevents.R
import com.example.cityevents.data.Event

class EventsAdapter(private val events: List<Event?>, context: Context) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    private val context = context
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.photoImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        private val categoryTv: TextView = itemView.findViewById(R.id.eventCategoryTv)

        fun bind(event: Event, context: Context) {
            loadImage(event.images?.values!!.first(), imageView, context)
            nameTextView.text = event.name
            categoryTv.text = event.category
        }
    }

    private fun loadImage(url: String, imageView: ImageView, context: Context) {
        if (url == null) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return EventViewHolder(view)
    }

    override fun getItemCount(): Int = events.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]!!
        holder.bind(event, context)

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
        /*Glide.with(context)
            .load(event.images)
            .transform(RoundedCorners(30))
            .into(view.findViewById(R.id.event_image))*/

        view.findViewById<TextView>(R.id.eventCategoryTv).text = event.category
        view.findViewById<TextView>(R.id.eventNameTv).text = event.name
        view.findViewById<TextView>(R.id.eventTimeTv).text = event.dateTime?.hour.toString()
        view.findViewById<TextView>(R.id.eventDateTv).text = event.dateTime.toString()
        view.findViewById<TextView>(R.id.eventPlaceTv).text = event.placeAddress
        view.findViewById<TextView>(R.id.eventPlaceNameTv).text = event.placeName
        view.findViewById<TextView>(R.id.eventDescriptionTv).text = event.description
    }
}