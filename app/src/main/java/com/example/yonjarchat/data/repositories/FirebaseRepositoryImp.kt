package com.example.yonjarchat.data.repositories

import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirebaseRepository {

    override suspend fun registerUser(
        email: String,
        password: String,
        username: String
    ):String {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("No se pudo obtener el UID del usuario")

            val user = mapOf(
                "username" to username,
                "email" to email
            )
            firestore.collection("Users").document(uid).set(user).await()

            "Usuario creado exitosamente"
        } catch (e: Exception) {
            firebaseAuth.currentUser?.delete()
            "Error al crear el usuario: ${e.message}"
        }
    }

    override fun loginUser(email: String, password: String, onResult: (String) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult.invoke("Inicio de sesión exitoso")
                } else {
                    onResult.invoke("Error al iniciar sesión")
                }
            }
    }

    override fun forgotPassword(email: String): String {
        return ""
    }
}