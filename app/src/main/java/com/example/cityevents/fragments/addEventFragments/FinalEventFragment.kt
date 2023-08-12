package com.example.cityevents.fragments.addEventFragments

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cityevents.R
import com.example.cityevents.adapters.FirebaseImageAdapter
import com.example.cityevents.adapters.ViewPagerAdapter
import com.example.cityevents.data.Event
import com.example.cityevents.databinding.FragmentFinalEventBinding
import com.example.cityevents.firebase.Firebase
import com.example.cityevents.firebase.FirebaseStorageManager
import com.example.cityevents.fragments.MapFragment
import com.example.cityevents.utils.openFragment
import java.text.DateFormatSymbols
import java.util.Locale

class FinalEventFragment(private var event: Event? = null) : Fragment() {
    private lateinit var binding: FragmentFinalEventBinding
    private val eventModel: EventModel by activityViewModels()

    private val daysOfWeek = arrayOf(
        "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinalEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupEventInfo()
        setupBackButton()
        setupNextButton()
    }

    private fun setupViewPager() {
        if (event != null)
            binding.eventPicture.adapter = event!!.images?.values?.let { FirebaseImageAdapter(it.toList()) }
        else
            binding.eventPicture.adapter = eventModel.images.value?.let { ViewPagerAdapter(it) }
        binding.wormDotsIndicator.setViewPager2(binding.eventPicture)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupEventInfo() {
        if (event == null)
            event = eventModel.event.value!!
        binding.apply {
            category.text = event!!.category
            eventName.text = event!!.name
            timeTv.text = "${event!!.dateTime!!.hour}:${event!!.dateTime!!.minute}"
            adressTv.text = event!!.placeAddress
            whereAdress.text = event!!.placeName

            val selectedCalendar = Calendar.getInstance().apply {
                set(event!!.dateTime!!.year!!, event!!.dateTime!!.month!!, event!!.dateTime!!.day!!)
            }
            val dateFormatSymbols = DateFormatSymbols.getInstance(Locale("ru"))
            val monthName = dateFormatSymbols.months[event!!.dateTime!!.month!!]
            val dayOfWeek = if (selectedCalendar.get(Calendar.DAY_OF_WEEK) - 2 <= -1)
                getString(R.string.sunday)
            else
                daysOfWeek[selectedCalendar.get(Calendar.DAY_OF_WEEK) - 2]
            timeDate.text = "$dayOfWeek, ${event!!.dateTime!!.day!!} $monthName, ${event!!.dateTime!!.year!!}"
        }
    }

    private fun setupBackButton() {
        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupNextButton() {
        binding.nextStepBtn.setOnClickListener {
            val event = eventModel.event.value!!
            val eventKey = eventModel.eventKey.value!!
            Firebase().sendEventToFirebase(event, eventKey)
            FirebaseStorageManager().uploadImagesToFirebase(eventKey, eventModel.images.value!!)
            openFragment(MapFragment.newInstance())
            requireActivity().supportFragmentManager.clearBackStack("")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FinalEventFragment()
    }
}
