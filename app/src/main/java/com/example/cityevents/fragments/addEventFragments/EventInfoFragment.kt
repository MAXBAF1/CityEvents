package com.example.cityevents.fragments.addEventFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cityevents.R
import com.example.cityevents.data.Event
import com.example.cityevents.databinding.FragmentEventInfoBinding
import com.example.cityevents.utils.openFragment

class EventInfoFragment : Fragment() {
    private lateinit var binding: FragmentEventInfoBinding
    private val eventModel: EventModel by activityViewModels()

    private lateinit var selectedCategory: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()

        binding.nextStepBtn.setOnClickListener {
            eventModel.event.value = Event(
                binding.edEventName.text.toString(), selectedCategory, binding.edDescription.text.toString(), placeName = binding.placeAdressEd.text.toString()
            )
            openFragment(AddImagesFragment.newInstance())
        }
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.event_categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                // Здесь можно выполнить действия при выборе элемента
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Здесь можно выполнить действия, если ничего не выбрано
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EventInfoFragment()
    }
}