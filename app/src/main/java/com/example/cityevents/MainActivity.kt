package com.example.cityevents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cityevents.databinding.ActivityMainBinding
import com.example.cityevents.fragments.AccountFragment
import com.example.cityevents.fragments.EventFragment
import com.example.cityevents.fragments.MainFragment
import com.example.cityevents.utils.openFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBottomNavClick()
        openFragment(MainFragment.newInstance())
    }

    private fun onBottomNavClick() {
        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mode -> openFragment(MainFragment.newInstance())
                R.id.events -> openFragment(EventFragment.newInstance())
                R.id.account -> openFragment(AccountFragment.newInstance())
            }
            true
        }
    }
}
