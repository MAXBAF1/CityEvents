package com.example.cityevents

import android.util.Log
import com.example.cityevents.data.Event
import com.example.cityevents.utils.AccountType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class Firebase {
    private var username: String
    private var eventsRef: DatabaseReference
    var userRef: DatabaseReference

    private var auth: FirebaseAuth = Firebase.auth
    private val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())

    init {
        val database = FirebaseDatabase.getInstance()
        username = auth.currentUser!!.displayName.toString()
        eventsRef = database.getReference("events")
        userRef = database.getReference("users").child(username)
    }

    fun loadUser() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Если пользователя нет в базе данных, создаем новый узел для него
                    userRef.setValue("")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MyLog", databaseError.toString())
            }
        })
        Log.e("item", auth.currentUser?.displayName.toString())
    }

    fun sendAccountTypeToFirebase(accountType: AccountType) {
        userRef.child("accountType").setValue(accountType.name)
    }

    fun sendEventToFirebase(event: Event) {
        val query = eventsRef.child(userRef.push().key ?: "blablabla")
        query.setValue(event)
    }

    fun getAccountTypeFromFirebase(callback: (AccountType) -> Unit) {
        userRef.child("accountType").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.value as String
                callback(AccountType.valueOf(value))
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e("MyLog", error.toString())
            }
        })
    }

    fun getEventsFromFirebase(callback: (List<Event>) -> Unit) {
        eventsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                callback(snapshot.children.map { data -> data.getValue(Event::class.java)!! })
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}