package com.example.cityevents

import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cityevents.databinding.ActivityMainBinding
import com.example.cityevents.fragments.AccountFragment
import com.example.cityevents.fragments.EventStatisticsFragment
import com.example.cityevents.fragments.EventsFragment
import com.example.cityevents.fragments.addEventFragments.AddImagesFragment
import com.example.cityevents.fragments.mainFragment.MapFragment
import com.example.cityevents.utils.AccountType
import com.example.cityevents.utils.openFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val navController = findNavController(R.id.nav_host_fragment)
        binding.bNav.setupWithNavController(navController)*/
        onBottomNavClick()
        openFragment(MapFragment.newInstance())
        val sharedPref = getSharedPreferences(SignInActivity.APP_NAME, Context.MODE_PRIVATE)
        val accountType = sharedPref.getInt(WelcomeActivity.IS_TYPE_SELECTED_TAG, -1)

        addsBottomNavElements(accountType)
    }

    private fun addsBottomNavElements(accountType: Int) {
        if (accountType == AccountType.Participant.ordinal) {
            addBottomNavEl(R.id.eventFragment, R.string.events, R.drawable.ic_tracks)
            addBottomNavEl(R.id.mapFragment, R.string.map, R.drawable.ic_marker)
            addBottomNavEl(R.id.accountFragment, R.string.account, R.drawable.ic_settings)
        } else if (accountType == AccountType.Organizer.ordinal) {
            addBottomNavEl(R.id.createEventFragment, R.string.add_event, R.drawable.ic_plus)
            addBottomNavEl(R.id.eventStatisticsFragment, R.string.statistic, R.drawable.ic_staistic)
            addBottomNavEl(R.id.mapFragment, R.string.map, R.drawable.ic_marker)
            addBottomNavEl(R.id.eventFragment, R.string.events, R.drawable.ic_tracks)
            addBottomNavEl(R.id.accountFragment, R.string.account, R.drawable.ic_settings)
        }

        binding.bNav.selectedItemId = R.id.mapFragment
    }

    private fun addBottomNavEl(fragmentId: Int, labelId: Int, icon: Int) {
        binding.bNav.menu.add(Menu.NONE, fragmentId, Menu.NONE, getString(labelId)).setIcon(icon)
    }

    private fun onBottomNavClick() {
        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.createEventFragment -> openFragment(AddImagesFragment.newInstance())
                R.id.eventStatisticsFragment -> openFragment(EventStatisticsFragment.newInstance())
                R.id.eventFragment -> openFragment(EventsFragment.newInstance())
                R.id.mapFragment -> openFragment(MapFragment.newInstance())
                R.id.accountFragment -> openFragment(AccountFragment.newInstance())
            }
            true
        }
    }
}
