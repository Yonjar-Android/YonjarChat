package com.example.yonjarchat.data.repositories

import android.content.Context
import android.net.Uri
import com.example.yonjarchat.R
import com.example.yonjarchat.data.retrofit.interfaces.ImgbbApi
import com.example.yonjarchat.domain.models.ChatDomain
import com.example.yonjarchat.domain.models.MessageModel
import com.example.yonjarchat.domain.models.User
import com.example.yonjarchat.domain.models.UserDomain
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.example.yonjarchat.utils.ResourceProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class FirebaseRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val imgbbApi: ImgbbApi,
    private val resourceProvider: ResourceProvider
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
            // Verificar si el error es por correo duplicado
            if (e.message?.contains("email") == true) {
                "Error: Ya existe una cuenta con este correo"
            } else {
                "Error al crear el usuario: ${e.message}"
            }
        }
    }

    override fun loginUser(email: String, password: String, onResult: (String) -> Unit) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult.invoke(
                        resourceProvider.getString(
                            R.string.youLoggedInStr
                        )
                    )
                } else {
                    onResult.invoke("Error al iniciar sesión")

                }
            }
    }

    override suspend fun forgotPassword(email: String): String {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            "Se le ha enviado un correo para restablecer su contraseña"

        } catch (e: Exception) {
            "Error: ${e.message}"
        }

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
                User(id, user.username, user.email, user.imageUrl)
            } else {
                throw Exception("No se encontró el usuario con ID $id")
            }
        } catch (e: Exception) {
            println("Error al obtener el usuario con ID $id: ${e.message}")
            null
        }
    }

    override suspend fun updateUsername(
        id: String,
        username: String,
        onResult: (String) -> Unit
    ) {
        try {
            val userRef = firestore.collection("Users").document(id)
            userRef.update("username", username).await()
            onResult("Nombre de usuario actualizado exitosamente")
        } catch (e: Exception) {
            onResult("Error al actualizar el nombre de usuario: ${e.message}")
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
                val messages = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(MessageModel::class.java)
                }
                onResult(messages)
            } else {
                onResult(emptyList())
            }
        }
    }

    override suspend fun updatePicture(
        id: String,
        image: Uri,
        context: Context,
        onResult: (String) -> Unit
    ) {
        try {
            // Paso 1: Convertir Uri a File temporal
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(image)
            val file = File.createTempFile("profile_image", ".jpg", context.cacheDir)
            inputStream.use {
                file.outputStream().use { output ->
                    it?.copyTo(output)
                }
            }

            // Paso 2: Crear Multipart para Retrofit
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            // Paso 3: Crear los RequestBody para los campos "key" y "name"
            val apiKey = "78e5c23bd951392cb3c03ebb6f428714"
            val apiKeyBody = apiKey.toRequestBody("text/plain".toMediaType())
            val nameBody = "profile_picture".toRequestBody("text/plain".toMediaType())

            // Paso 4: Subir imagen a ImgBB
            val response = imgbbApi.uploadImage(
                apiKey = apiKeyBody,
                image = imagePart,
                name = nameBody
            )

            // Paso 5: Verificar la respuesta y actualizar Firestore
            if (response.isSuccessful && response.body()?.data?.url != null) {
                val imageUrl = response.body()!!.data.url
                val deleteUrl = response.body()!!.data.delete_url

                val updates = mapOf(
                    "imageUrl" to imageUrl,
                    "profileImageDeleteUrl" to deleteUrl
                )

                val userRef = firestore.collection("Users").document(id)
                userRef.update(updates).await()
                onResult("Imagen actualizada exitosamente")
            } else {
                onResult("Error al subir la imagen: ${response.errorBody()?.string()}")
            }

        } catch (e: Exception) {
            onResult("Error al actualizar la imagen: ${e.message}")
        }
    }


    fun generateChatId(user1: String, user2: String): String {
        return listOf(user1, user2).sorted().joinToString("_")
    }
}