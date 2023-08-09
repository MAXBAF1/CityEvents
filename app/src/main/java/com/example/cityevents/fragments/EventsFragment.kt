package com.example.cityevents.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cityevents.adapters.EventsAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setEvents(events: List<Event>) {
        if (activity != null) {
            val adapter = EventsAdapter(requireContext(), events.reversed())
            binding.eventsRecyclerView.adapter = adapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EventsFragment()
    }
}