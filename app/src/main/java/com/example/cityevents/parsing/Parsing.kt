package com.example.cityevents.parsing

import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cityevents.data.DateTime
import com.example.cityevents.data.Event
import com.example.cityevents.data.LocationSerializable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Parsing(val context: Context) {
    private val mainUrl = "https://afisha7.ru"
    private var events = mutableListOf<Event>()


    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsFromWeb(callback: (List<Event>) -> Unit) {
        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.Default) {
            TrustAllCertificates.enableTrustAllCertificates()

            for (i in 1..3) {

                getEventsHtml("$mainUrl/ekaterinburg?page=$i").forEachIndexed { index, eventHtml ->
                    val cnt = events.size
                    if (addCategories(eventHtml, cnt)) {
                        addNames(eventHtml, cnt)
                        addDates(eventHtml, cnt)

                        addPlaceNameAndAddress(eventHtml, cnt)
                        addImages(eventHtml, cnt)
                    }

                }
            }
            callback(events)
        }
    }

    private fun addImages(eventHtml: Element, eventIndex: Int) {
        var image = eventHtml.children()[2].children()[0].children()[0].children()[0].attr("data-src").toString()
        if (image.startsWith("/upload"))
            image = mainUrl + image
        events[eventIndex].images = hashMapOf(Pair("", image))
    }

    private fun addPlaceNameAndAddress(eventHtml: Element, eventIndex: Int) {
        val locationPageAddress =
            eventHtml.children().getOrNull(3)?.children()?.getOrNull(1)?.children()?.getOrNull(0)
                ?.children()?.getOrNull(0)?.children()?.getOrNull(1)?.children()?.getOrNull(0)
                ?.children()?.getOrNull(0)?.attr(
                    "href"
                ) ?: return
        val fullLocationPageAddress = mainUrl + locationPageAddress

        val doc = Jsoup.connect(fullLocationPageAddress).userAgent("Mozilla").get()
        val placeName = doc.body()
            .children()[2].children()[0].children()[0].children()[0].children()[1].children()[0].children()[0].childNodes()[0].toString()
            .trim()
        events[eventIndex].placeName = placeName

        val address = doc.body()
            .children()[2].children()[0].children()[0].children()[0].children()[1].children()[1].children()[0].children()[1].childNodes()[2].toString()
        events[eventIndex].placeAddress = address.replace(",", " ").replace(".", " ")
        events[eventIndex].location = getLocationFromAddress("Екатеринбург, $address")
    }

    private fun getLocationFromAddress(address: String): LocationSerializable? {
        val geocoder = Geocoder(context)

        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val lat = addresses[0].latitude
                    val lng = addresses[0].longitude
                    return LocationSerializable(lat, lng)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }


    private fun addCategories(eventHtml: Element, eventIndex: Int): Boolean {
        val dateCategoryNode = eventHtml.children()[1].children()[0].children()

        val category = dateCategoryNode[1].children()[1].childNodes()[0].toString()
        if (category == "Кино")
            return false
        events.add(Event(category = category))
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDates(eventHtml: Element, eventIndex: Int) {
        val dateCategoryNode = eventHtml.children()[1].children()[0].children()

        val dateString = dateCategoryNode[0].children()[1].childNodes()[0].toString().trim().drop(6)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val date = LocalDate.parse(dateString, formatter)
        events[eventIndex].dateTime = DateTime(
            date.year, date.monthValue, date.dayOfMonth, dayOfWeek = date.dayOfWeek.value.toString()
        )
    }

    private fun addNames(eventHtml: Element, eventIndex: Int) {
        val name = eventHtml.children()[0].children()[0].children()[0].childNodes()[0].toString()
        events[eventIndex].name = name
    }

    private fun getEventsHtml(url: String): Elements {

        val doc = Jsoup.connect(url).userAgent("Mozilla").get()

        val eventsHtml = doc.body()
            .children()[2].children()[0].children()[0].children()[0].children()[2].children()[0].children()
        eventsHtml.removeLast()

        return eventsHtml
    }
}

