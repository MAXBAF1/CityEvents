package com.example.cityevents.fragments.addEventFragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cityevents.data.Event

open class EventModel : ViewModel() {
    val event: MutableLiveData<Event> by lazy {
        MutableLiveData<Event>()
    }
}