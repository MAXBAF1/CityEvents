package com.example.cityevents.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.icu.util.Calendar
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.cityevents.R
import com.example.cityevents.data.Event
import com.example.cityevents.firebase.Firebase
import com.example.cityevents.fragments.addEventFragments.FinalEventFragment
import com.example.cityevents.fragments.eventsFragment.EventsFragment
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import java.text.DateFormatSymbols
import java.util.Locale

class EventsAdapter(private val events: List<Event?>, context: Context) :
    RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    private val context = context

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewPager: ViewPager2 = itemView.findViewById(R.id.event_picture)
        private val nameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        private val categoryImv: ImageView = itemView.findViewById(R.id.category_imv)
        private val eventInfo: TextView = itemView.findViewById(R.id.event_info_tv)
        private val favorite: ImageView = itemView.findViewById(R.id.unfavorite_button)
        private val unfavorite: ImageView = itemView.findViewById(R.id.favorite_button)
        private val wormDotsIndicator: WormDotsIndicator = itemView.findViewById(R.id.worm_dots_indicator)

        private val gradientDrawable = GradientDrawable().apply {
            cornerRadius = context.resources.getDimension(R.dimen.corner_radius)
        }

        init {
            favorite.setOnClickListener {
                val event = events[adapterPosition]!!
                if (event.isLiked) {
                    event.isLiked = false
                    unfavorite.visibility = View.VISIBLE
                    favorite.visibility = View.GONE
                    Firebase().removeLikedEvent(event.name!!)
                }
            }

            unfavorite.setOnClickListener {


                val event = events[adapterPosition]!!
                if (event.isLiked) {
                    event.isLiked = false
                    favorite.visibility = View.GONE
                    unfavorite.visibility = View.VISIBLE
                    Firebase().removeLikedEvent(event.name!!)
                } else {
                    event.isLiked = true
                    unfavorite.visibility = View.GONE
                    favorite.visibility = View.VISIBLE
                    Firebase().addLikedEvent(event.name!!)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(event: Event) {
            val adapter = ViewPagerEventAdapter(event.images?.values!!.toList())
            viewPager.adapter = adapter
            wormDotsIndicator.setViewPager2(viewPager)

            viewPager.background = gradientDrawable

            val categories = context.resources.getStringArray(R.array.event_categories)
            when (event.category) {
                categories[1] -> categoryImv.setImageResource(R.drawable.baseline_palette_24)
                categories[2] -> categoryImv.setImageResource(R.drawable.baseline_fitness_center_24)
                categories[3] -> categoryImv.setImageResource(R.drawable.baseline_school_24)
                categories[4] -> categoryImv.setImageResource(R.drawable.baseline_sports_esports_24)
                categories[5] -> categoryImv.setImageResource(R.drawable.baseline_people_24)
                categories[6] -> categoryImv.setImageResource(R.drawable.baseline_family_restroom_24)
                else -> categoryImv.setImageResource(R.drawable.baseline_bookmarks_24)
            }

            val dateFormatSymbols = DateFormatSymbols.getInstance(Locale("ru"))
            val monthName = dateFormatSymbols.months[event.dateTime!!.month!!]

            val placeNameText =
                if (!event.placeName.isNullOrEmpty()) " • ${event.placeName}" else ""
            eventInfo.text =
                "${event.dateTime!!.day!!} $monthName • ${event.dateTime!!.hour}:${event.dateTime!!.minute}$placeNameText"
            nameTextView.text = event.name
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
        holder.bind(event)

        holder.itemView.setOnClickListener {
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = FinalEventFragment(false, event)

            fragmentTransaction.replace(R.id.placeHolder, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView(view: View, event: Event) {

        view.findViewById<TextView>(R.id.eventNameTv).text = event.name
        view.findViewById<TextView>(R.id.eventTimeTv).text = event.dateTime?.hour.toString()
        view.findViewById<TextView>(R.id.eventDateTv).text = event.dateTime.toString()
        view.findViewById<TextView>(R.id.eventPlaceTv).text = event.placeAddress
        view.findViewById<TextView>(R.id.eventPlaceNameTv).text = event.placeName
        view.findViewById<TextView>(R.id.eventDescriptionTv).text = event.description
    }
}