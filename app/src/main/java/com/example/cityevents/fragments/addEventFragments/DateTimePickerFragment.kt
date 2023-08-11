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
import com.example.cityevents.databinding.FragmentDateTimePickerBinding


class DateTimePickerFragment : Fragment() {
    private lateinit var binding: FragmentDateTimePickerBinding

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