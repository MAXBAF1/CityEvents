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
import androidx.viewpager2.widget.ViewPager2
import com.example.cityevents.R
import com.example.cityevents.adapters.ImageAdapter
import com.example.cityevents.adapters.ViewPagerAdapter
import com.example.cityevents.databinding.FragmentFinalEventBinding
import com.example.cityevents.firebase.Firebase
import com.example.cityevents.firebase.FirebaseStorageManager
import com.example.cityevents.fragments.mainFragment.MapFragment
import com.example.cityevents.utils.openFragment
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import java.text.DateFormatSymbols
import java.util.Locale

class FinalEventFragment : Fragment() {
    private lateinit var binding: FragmentFinalEventBinding
    private val eventModel: EventModel by activityViewModels()

    lateinit var viewPager: ViewPager2
    lateinit var dotsIndicator: WormDotsIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinalEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.eventPicture
        dotsIndicator = binding.wormDotsIndicator

        val daysOfWeek = arrayOf(
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота",
            "Воскресенье"
        )

        val adapter = eventModel.images.value?.let { ViewPagerAdapter(it) }
        viewPager.adapter = adapter
        dotsIndicator.setViewPager2(viewPager)

        binding.category.text = eventModel.event.value!!.category
        binding.eventName.text = eventModel.event.value!!.name
        binding.timeTv.text = "${eventModel.event.value!!.dateTime!!.hour}:${eventModel.event.value!!.dateTime!!.minute}"
        binding.adressTv.text = eventModel.event.value!!.placeAddress
        binding.whereAdress.text = eventModel.event.value!!.placeName

        var selectedYear = eventModel.event.value!!.dateTime!!.year
        var selectedMonth = eventModel.event.value!!.dateTime!!.month
        var selectedDay = eventModel.event.value!!.dateTime!!.day

        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(selectedYear!!, selectedMonth!!, selectedDay!!)

        val dateFormatSymbols = DateFormatSymbols.getInstance(Locale("ru"))
        val monthName = dateFormatSymbols.months[selectedMonth!!]
        if (selectedCalendar.get(Calendar.DAY_OF_WEEK) - 2 <= -1) eventModel.event.value?.dateTime!!.dayOfWeek =
            getString(R.string.sunday)
        else eventModel.event.value?.dateTime!!.dayOfWeek = daysOfWeek[selectedCalendar.get(Calendar.DAY_OF_WEEK) - 2]


        binding.timeDate.text =
            "${eventModel.event.value?.dateTime!!.dayOfWeek}, $selectedDay $monthName, $selectedYear"

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.nextStepBtn.setOnClickListener {
            Firebase().sendEventToFirebase(eventModel.event.value!!, eventModel.eventKey.value!!)
            FirebaseStorageManager().uploadImagesToFirebase(eventModel.eventKey.value!!, eventModel.images.value!!)
            openFragment(MapFragment.newInstance())
            requireActivity().supportFragmentManager.clearBackStack("")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FinalEventFragment()
    }
}