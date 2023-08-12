package com.example.cityevents.fragments.eventsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityevents.adapters.EventsAdapter
import com.example.cityevents.data.Event
import com.example.cityevents.databinding.FragmentEventsBinding
import com.example.cityevents.firebase.Firebase

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

        val layoutManager = LinearLayoutManager(requireContext())
        binding.eventsRecyclerView.layoutManager = layoutManager

        val firebase = Firebase()
        firebase.getEventsFromFirebase { events ->
            setEvents(events)

        }
    }

    private fun setEvents(events: List<Event?>) {
        if (activity != null) {
            val adapter = EventsAdapter(events.reversed(), requireContext())
            binding.eventsRecyclerView.adapter = adapter

            val firebase = Firebase()
            firebase.getEventsFromFirebase { events ->
                val adapter = EventsAdapter(events, requireContext())
                binding.eventsRecyclerView.adapter = adapter
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EventsFragment()
    }
}