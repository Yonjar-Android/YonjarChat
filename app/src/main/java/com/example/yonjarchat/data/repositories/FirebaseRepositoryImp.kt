package com.example.yonjarchat.data.repositories

import com.example.yonjarchat.domain.models.ChatDomain
import com.example.yonjarchat.domain.models.MessageModel
import com.example.yonjarchat.domain.models.User
import com.example.yonjarchat.domain.models.UserDomain
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
    ): String {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid =
                authResult.user?.uid ?: throw Exception("No se pudo obtener el UID del usuario")
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

    override fun signOut(): String {
        return try {
            firebaseAuth.signOut()
            "Sesión cerrada exitosamente"
        } catch (e: Exception) {
            // Puedes imprimir el error para debugging
            "Error: ${e.message}"
        }
    }

    override suspend fun getUsers(): List<User> {
        return try {
            val querySnapshot = firestore.collection("Users").get().await()
            querySnapshot.documents.mapNotNull { document ->
                val user = document.toObject(UserDomain::class.java)
                if (user != null && document.id != firebaseAuth.currentUser?.uid) User(
                    document.id,
                    user.username,
                    user.email
                )
                else null
            }
        } catch (e: Exception) {
            println("Error al obtener usuarios: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getUserId(id: String): User? {
        return try {
            val documentSnapshot = firestore.collection("Users").document(id).get().await()
            val user = documentSnapshot.toObject(UserDomain::class.java)
            if (user != null) {
                User(id, user.username, user.email)
            } else {
                throw Exception("No se encontró el usuario con ID $id")
            }
        } catch (e: Exception) {
            println("Error al obtener el usuario con ID $id: ${e.message}")
            null
        }
    }

    override suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        content: String
    ) {
        val chatId = generateChatId(senderId, receiverId)
        val chatRef = firestore.collection("chats").document(chatId)
        val chatCollection = firestore.collection("chats")
        val users = listOf(senderId, receiverId).sorted()

        try {
            // Verificamos si ya existe el documento del chat
            val snapshot = chatRef.get().await()

            if (!snapshot.exists()) {
                // Chat no existe, lo creamos
                val newChat = ChatDomain(
                    lastMessage = content,
                    timestamp = System.currentTimeMillis(),
                    arrayOfUsers = users
                )
                chatRef.set(newChat).await()

            } else {
                // Chat ya existe, solo actualizamos campos
                chatRef.update(
                    mapOf(
                        "lastMessage" to content,
                        "timestamp" to System.currentTimeMillis()
                    )
                ).await()
            }

            // Agregamos el mensaje a la subcolección

            val message = MessageModel(
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                timestamp = System.currentTimeMillis()
            )

            chatCollection.document(chatId).collection("messages").add(message).await()

        } catch (e: Exception) {
            println("Error al enviar mensaje: ${e.message}")
        }
    }

    override suspend fun getMessages(
        user1: String,
        user2: String,
        onResult: (List<MessageModel>) -> Unit
    ): ListenerRegistration {

        val chatId = generateChatId(user1, user2)
        val messagesRef = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")

        return messagesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error al obtener mensajes: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val messages = snapshot.documents.mapNotNull{ doc ->
                    doc.toObject(MessageModel::class.java)
                }
                onResult(messages)
            } else{
                onResult(emptyList())
            }
        }
    }

    fun generateChatId(user1: String, user2: String): String {
        return listOf(user1, user2).sorted().joinToString("_")
    }
}