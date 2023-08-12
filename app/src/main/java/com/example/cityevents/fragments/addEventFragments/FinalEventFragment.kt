package com.example.cityevents.fragments.addEventFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.cityevents.Firebase
import com.example.cityevents.R
import com.example.cityevents.adapters.ImageAdapter
import com.example.cityevents.adapters.StringImageAdapter
import com.example.cityevents.databinding.FragmentDateTimePickerBinding
import com.example.cityevents.databinding.FragmentFinalEventBinding
import com.example.cityevents.fragments.mainFragment.MapFragment
import com.example.cityevents.utils.openFragment
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class FinalEventFragment : Fragment() {
    private lateinit var binding: FragmentFinalEventBinding
    private val eventModel: EventModel by activityViewModels()

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: WormDotsIndicator
    private lateinit var imageList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinalEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.eventPicture
        dotsIndicator = binding.wormDotsIndicator

        imageList = eventModel.event.value?.images!!

        val adapter = StringImageAdapter(imageList)
        viewPager.adapter = adapter
        dotsIndicator.setViewPager2(viewPager)

        binding.nextStepBtn.setOnClickListener {
            Firebase().sendEventToFirebase(eventModel.event.value!!)
            openFragment(MapFragment.newInstance())
            requireActivity().supportFragmentManager.clearBackStack("")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FinalEventFragment()
    }
}