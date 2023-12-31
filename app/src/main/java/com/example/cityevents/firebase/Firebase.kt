package com.example.cityevents.firebase

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
    var username: String
    var internetEventsRef: DatabaseReference
    private var usersRef: DatabaseReference
    var userRef: DatabaseReference
    var eventsRef: DatabaseReference

    private var auth: FirebaseAuth = Firebase.auth
    private val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())

    init {
        val database = FirebaseDatabase.getInstance()
        internetEventsRef = database.getReference("internetEvents")
        usersRef = database.getReference("users")
        username = auth.currentUser!!.displayName.toString()
        userRef = usersRef.child(username)
        eventsRef = userRef.child("events")
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

    fun sendInternetEvents(events: List<Event>) {
        for (event in events) {
            internetEventsRef.child(internetEventsRef.push().key ?: "").setValue(event)
        }
    }

    fun getInternetEvents(callback: (List<Event?>) -> Unit) {
        internetEventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.map {
                    it.getValue(Event::class.java)
                }

                callback(events)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })
    }

    fun getEventsFromFirebase(callback: (List<Event?>) -> Unit) {
        usersRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val usersEvents = snapshot.children.map { data ->
                    data.child("events").children.map {
                        it.getValue(Event::class.java)
                    }
                }
                callback(usersEvents.flatten())
                //callback(child.map { data -> data.getValue(Event::class.java)!! })
            }
        }
    }

    fun sendAccountTypeToFirebase(accountType: AccountType) {
        userRef.child("accountType").setValue(accountType.name)
    }

    fun sendEventToFirebase(event: Event, eventKey: String) {
        val query = eventsRef.child(eventKey)
        query.setValue(event)
    }

    fun addLikedEvent(eventKey: String) {
        userRef.child("likedEvents").child(eventKey).setValue(true)
    }

    fun removeLikedEvent(eventKey: String) {
        userRef.child("likedEvents").child(eventKey).removeValue()
    }

    fun sendUserCategory(selectedCategories: List<String>) {
        val userCategoriesRef =
            FirebaseDatabase.getInstance().getReference("users/$username/categories")
        userCategoriesRef.setValue(selectedCategories)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
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


    fun getLikedEventsFromFirebase(callback: (List<Event?>) -> Unit) {
        usersRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val usersEvents = snapshot.children.map { data ->
                    data.child("events").children.map {
                        val event = it.getValue(Event::class.java)
                        event?.isLiked = data.child("likedEvents").hasChild(event?.name!!)
                        event
                    }
                }
                callback(usersEvents.flatten())
            }
        }
    }

    private fun getUserEvents(userRef: DatabaseReference, callback: (List<Event>) -> Unit) {

    }

    fun signOut() {
        auth.signOut()
    }
}