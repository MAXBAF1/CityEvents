package com.example.cityevents.firebase

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage

class FirebaseStorageManager {
    private val firebase = Firebase()
    private val storageRef = FirebaseStorage.getInstance().reference

    private fun uploadImageToFirebase(eventKey: String, imageId: String, imageUri: Uri) {
        val imageRef = storageRef.child("${firebase.username}/events/$eventKey/images/$imageId.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    // imageUrl содержит ссылку на загруженную картинку

                    // Добавьте ссылку на изображение в базу данных
                    val imagesRef = firebase.eventsRef.child(eventKey).child("images")
                    val imageIds = imagesRef.push().key
                    imagesRef.child(imageIds ?: "errorId").setValue(imageUrl.toString())
                }
            }
            .addOnFailureListener { exception ->
                // Произошла ошибка загрузки
            }
    }

    fun uploadImagesToFirebase(eventKey: String, uris: List<Uri>) {
        uris.forEachIndexed { index, uri ->
            uploadImageToFirebase(eventKey, index.toString(), uri)
        }
    }
}