package com.example.cityevents

import android.util.Log
import com.example.cityevents.utils.AccountType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class Firebase {
    var username: String
    var database: FirebaseDatabase
    var userRef: DatabaseReference
    var dateRef: DatabaseReference

    private var auth: FirebaseAuth = Firebase.auth
    private val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
    private var date = sdf.format(Calendar.getInstance().time)

    init {
        username = auth.currentUser!!.displayName.toString()
        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("users").child(username)
        dateRef = userRef.child(date)
    }

    fun loadUser() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Если пользователя нет в базе данных, создаем новый узел для него
                    userRef.setValue("")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        Log.e("item", auth.currentUser?.displayName.toString())
    }

    fun sendAccountTypeToFirebase(accountType: AccountType) {
        userRef.child("accountType").setValue(accountType.name)
    }
}