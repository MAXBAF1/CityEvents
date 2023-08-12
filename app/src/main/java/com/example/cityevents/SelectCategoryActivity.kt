package com.example.cityevents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import com.example.cityevents.databinding.ActivitySelectCategoryBinding
import com.example.cityevents.firebase.Firebase
import com.example.cityevents.utils.AccountType

class SelectCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectCategoryBinding
    private lateinit var firebase: Firebase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSharedPreferences()
        checkAndStartMainActivity()

        binding.saveButton.setOnClickListener {
            val selectedCategories = getSelectedCategories()
            saveSelectedCategories(selectedCategories)
            sendUserCategories(selectedCategories)
            startMainActivity()
        }
    }

    private fun initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(SignInActivity.APP_NAME, Context.MODE_PRIVATE)
    }

    private fun checkAndStartMainActivity() {
        val categorySelected = sharedPreferences.getBoolean(IS_CATEGORY_SELECTED, false)
        val typeSelected = sharedPreferences.getInt(WelcomeActivity.IS_TYPE_SELECTED_TAG, -1)

        if (categorySelected || typeSelected == AccountType.Organizer.ordinal) {
            startMainActivity()
        }
    }

    private fun getSelectedCategories(): List<String> {
        val selectedCategories = mutableListOf<String>()
        val binding = binding // Avoiding potential nullability issues

        if (binding.sportCheckbox.isChecked) {
            selectedCategories.add("Sport")
        }
        if (binding.cultureCheckbox.isChecked) {
            selectedCategories.add("Culture")
        }
        if (binding.educationCheckbox.isChecked) {
            selectedCategories.add("Education")
        }
        if (binding.entertainmentCheckbox.isChecked) {
            selectedCategories.add("Entertainment")
        }
        if (binding.familyCheckbox.isChecked) {
            selectedCategories.add("Family")
        }
        if (binding.socialCheckbox.isChecked) {
            selectedCategories.add("Social")
        }

        return selectedCategories
    }

    private fun saveSelectedCategories(selectedCategories: List<String>) {
        sharedPreferences.edit {
            putBoolean(IS_CATEGORY_SELECTED, true)
        }
    }

    private fun sendUserCategories(selectedCategories: List<String>) {
        firebase = Firebase() // Initialize Firebase instance here if necessary
        firebase.sendUserCategory(selectedCategories)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val IS_CATEGORY_SELECTED = "IS_CATEGORY_SELECTED"
    }
}
