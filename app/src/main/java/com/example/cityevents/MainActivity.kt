package com.example.cityevents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cityevents.databinding.ActivityMainBinding
import com.example.cityevents.fragments.AccountFragment
import com.example.cityevents.fragments.eventsFragment.EventsFragment
import com.example.cityevents.fragments.mainFragment.MapFragment
import com.example.cityevents.utils.openFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        binding.bNav.setupWithNavController(navController)
        /*onBottomNavClick()
        openFragment(MapFragment.newInstance())*/
    }

    private fun onBottomNavClick() {
        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mapFragment -> openFragment(MapFragment.newInstance())
                R.id.eventFragment -> openFragment(EventsFragment.newInstance())
                R.id.accountFragment -> openFragment(AccountFragment.newInstance())
            }
            true
        }
    }
}
