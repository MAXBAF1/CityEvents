package com.example.cityevents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.cityevents.SignInActivity.Companion.APP_NAME
import com.example.cityevents.databinding.ActivityWelcomeBinding
import com.example.cityevents.utils.AccountType

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var firebase: Firebase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        val typeSelected = sharedPreferences.getInt(IS_TYPE_SELECTED_TAG, -1)

        if (typeSelected != -1) {
            startMainActivity()
        }

        firebase = Firebase()
        firebase.loadUser()

        binding.organizerBtn.setOnClickListener {
            onAccountTypeSelected(AccountType.Organizer)
        }

        binding.participantBtn.setOnClickListener {
            onAccountTypeSelected(AccountType.Participant)
        }
    }

    private fun onAccountTypeSelected(accountType: AccountType) {
        firebase.sendAccountTypeToFirebase(accountType)
        startMainActivity()
        sharedPreferences.edit {
            putInt(IS_TYPE_SELECTED_TAG, accountType.ordinal)
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val IS_TYPE_SELECTED_TAG = "IS_TYPE_SELECTED"
    }
}