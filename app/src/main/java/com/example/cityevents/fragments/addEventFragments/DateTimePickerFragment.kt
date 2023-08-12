package com.example.cityevents.fragments.addEventFragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cityevents.data.DateTime
import com.example.cityevents.databinding.FragmentDateTimePickerBinding
import com.example.cityevents.fragments.mainFragment.MapFragment
import com.example.cityevents.utils.openFragment

class DateTimePickerFragment : Fragment() {
    private lateinit var binding: FragmentDateTimePickerBinding
    private val eventModel: EventModel by activityViewModels()
    private var selectedDateTime = DateTime()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDateTimePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.datePickerButton.setOnClickListener {
            showDatePicker()
        }

        binding.timePickerButton.setOnClickListener {
            showTimePicker()
        }

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.nextStepBtn.setOnClickListener {
            eventModel.event.value?.dateTime = selectedDateTime
            openFragment(FinalEventFragment.newInstance())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                selectedDateTime.year = selectedYear
                selectedDateTime.month = selectedMonth
                selectedDateTime.day = selectedDay
                val date =
                    "You picked the following date: " + selectedDay.toString() + "/" + (selectedMonth + 1).toString() + "/" + selectedYear

                binding.dateTextView.text = date
            }, year, month, day
        )
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val time =
                    "You picked the following time: " + selectedHour.toString() + "h" + selectedMinute.toString() + "m"
                selectedDateTime.hour = selectedHour
                selectedDateTime.minute = selectedMinute
                binding.timeTextView.text = time
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = DateTimePickerFragment()
    }
}