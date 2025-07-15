package com.example.yonjarchat.data.repositories

import com.example.yonjarchat.domain.repositories.FcmRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.messaging
import jakarta.inject.Inject

class FcmRepositoryImp @Inject constructor(
    private val firestore: FirebaseFirestore
): FcmRepository {
    override fun getCurrentToken(onComplete: (String?) -> Unit) {
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(task.result)
            } else {
                onComplete(null)
            }
        }
    }

    override fun saveTokenToDatabase(userId: String, token: String) {
        firestore.collection("Users")
            .document(userId)
            .update("fcmToken", token)
            .addOnFailureListener { e ->
                println("Error guardando token: ${e.message}")
            }
    }
}