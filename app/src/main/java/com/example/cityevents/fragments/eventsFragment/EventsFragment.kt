package com.example.cityevents.fragments.eventsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cityevents.data.Event
import com.example.cityevents.databinding.FragmentEventsBinding

class EventsFragment : Fragment() {
    private lateinit var binding: FragmentEventsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setEvents(events: List<Event>) {
        if (activity != null) {
            val adapter = LastFoodsAdapter(requireContext(), events.reversed())
            binding.eventsRecyclerView.adapter = adapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EventsFragment()
    }
}