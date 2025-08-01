package com.example.yonjarchat.data.repositories

import android.content.Context
import android.net.Uri
import com.example.yonjarchat.R
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.data.retrofit.interfaces.ImgbbApi
import com.example.yonjarchat.data.room.dao.ChatDao
import com.example.yonjarchat.data.room.dao.MessageDao
import com.example.yonjarchat.data.room.dao.UserDao
import com.example.yonjarchat.data.room.entities.ChatEntity
import com.example.yonjarchat.data.room.entities.MessageEntity
import com.example.yonjarchat.data.room.entities.UserEntity
import com.example.yonjarchat.domain.models.ChatDomain
import com.example.yonjarchat.domain.models.MessageModel
import com.example.yonjarchat.domain.models.User
import com.example.yonjarchat.domain.models.UserChatModel
import com.example.yonjarchat.domain.models.UserDomain
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.example.yonjarchat.utils.CombinedListener
import com.example.yonjarchat.utils.DummyListenerRegistration
import com.example.yonjarchat.utils.ImageHelper
import com.example.yonjarchat.utils.NetworkUtils
import com.example.yonjarchat.utils.ResourceProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    private val resourceProvider: ResourceProvider,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val userDao: UserDao
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

            resourceProvider.getString(
                R.string.userCreatedSuccessStr
            )
        } catch (e: Exception) {
            firebaseAuth.currentUser?.delete()
            // Check if the error is due to duplicate mail
            if (e.message?.contains("email") == true) {
                resourceProvider.getString(R.string.errorEmailAlreadyExistStr)
            } else {
                "${
                    resourceProvider.getString(
                        R.string.errorCreatingUserStr
                    )
                }${e.message}"
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
                    onResult.invoke(
                        resourceProvider.getString(
                            R.string.errorLogginInStr
                        )
                    )

                }
            }
    }

    override suspend fun forgotPassword(email: String): String {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            resourceProvider.getString(R.string.passwordResetEmailSentStr)

        } catch (e: Exception) {
            "Error: ${e.message}"
        }

    }

    override fun signOut(): String {
        return try {
            firebaseAuth.signOut()
            resourceProvider.getString(R.string.loggedOutSuccessStr)
        } catch (e: Exception) {

            "Error: ${e.message}"
        }
    }

    override suspend fun getUsers(): List<User> {
        return try {
            val querySnapshot = firestore.collection("Users").get().await()
            val currentUserId = firebaseAuth.currentUser?.uid ?: return emptyList()

            querySnapshot.documents.mapNotNull { document ->
                val user = document.toObject(UserDomain::class.java)
                val userId = document.id

                if (user != null && userId != currentUserId) {
                    val chatId = generateChatId(currentUserId, userId)
                    val lastMessageSnapshot = firestore.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .await()

                    val lastMessage = lastMessageSnapshot.documents.firstOrNull()
                        ?.toObject(MessageModel::class.java)

                    User(
                        uid = document.id,
                        username = user.username,
                        email = user.email,
                        imageUrl = user.imageUrl,
                        lastMessage = lastMessage?.content ?: ""
                    )
                } else {
                    null
                }
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
            println("Error ${e.message}")
            null
        }
    }

    override suspend fun getUserByUsername(username: String, onResult: (String) -> Unit): User? {
        return try {
            val querySnapshot = firestore.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .await()

            val user = querySnapshot.documents.firstOrNull()?.toObject(UserDomain::class.java)
            if (user != null) {
                User(querySnapshot.documents.first().id,
                    user.username,
                    user.email,
                    user.imageUrl)
            } else {
                onResult("Error user not found")
                null
            }
        } catch (e: Exception) {
            onResult("Error ${e.message}")
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
            onResult(
                resourceProvider.getString(R.string.usernameUpdatedsuccessfullyStr)
            )
        } catch (e: Exception) {
            onResult("${resourceProvider.getString(R.string.errorUpdatingUsernameStr)} ${e.message}")
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
            // We check if the chat document already exists.
            val snapshot = chatRef.get().await()

            if (!snapshot.exists()) {
                // Chat does not exist, then we create it
                val newChat = ChatDomain(
                    lastMessage = content,
                    timestamp = System.currentTimeMillis(),
                    arrayOfUsers = users
                )
                chatRef.set(newChat).await()

            } else {
                // Chat already exist, we only update fields
                val message = if (ImageHelper.isImageUrl(content)) "Imagen" else content

                chatRef.update(
                    mapOf(
                        "lastMessage" to message,
                        "timestamp" to System.currentTimeMillis()
                    )
                ).await()
            }

            // Add the message to the subcollection.

            val message = MessageModel(
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                timestamp = System.currentTimeMillis()
            )

            chatCollection.document(chatId).collection("messages").add(message).await()

        } catch (e: Exception) {
            println("${resourceProvider.getString(R.string.errorSendingMessageStr)} ${e.message}")
        }
    }

    override suspend fun getMessages(
        user1: String,
        user2: String,
        lastVisible: DocumentSnapshot?,
        context: Context,
        onResult: (List<MessageModel>, DocumentSnapshot?) -> Unit
    ): ListenerRegistration {

        val chatId = generateChatId(user1, user2)

        var query = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(15)
        if (lastVisible != null) {
            query = query.startAfter(lastVisible)
        }

        return query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error al obtener mensajes: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {

                val messages = snapshot.documents.mapNotNull { doc ->
                    val message = doc.toObject(MessageModel::class.java)
                    message?.copy(id = doc.id)
                }
                val last = snapshot.documents.lastOrNull()

                CoroutineScope(Dispatchers.IO).launch {
                    for (msg in messages) {
                        messageDao.insertMessage(
                            MessageEntity(
                                messageId = msg.id,
                                chatId = chatId,
                                senderId = msg.senderId,
                                receiverId = msg.receiverId,
                                content = msg.content,
                                timestamp = msg.timestamp
                            )
                        )
                    }
                }

                onResult(messages, last)
            } else {
                onResult(emptyList(), null)
            }
        }

    }

    override suspend fun getMessagesFromRoom(
        user1: String,
        user2: String,
        offset: Int,
        limit: Int,
        onResult: (List<MessageModel>) -> Unit
    ) {
        val chatId = generateChatId(user1, user2)

        val messages = messageDao.getMessagesPaginated(
            chatId,
            limit = limit, offset = offset
        ).map { entity ->
            MessageModel(
                id = entity.messageId,
                senderId = entity.senderId,
                receiverId = entity.receiverId,
                content = entity.content,
                timestamp = entity.timestamp
            )
        }

        onResult(messages)
    }

    override suspend fun updatePicture(
        id: String,
        image: Uri,
        context: Context,
        onResult: (String) -> Unit
    ) {
        try {
            // Step 1: Convert Uri to temporary file
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(image)
            val file = File.createTempFile("profile_image", ".jpg", context.cacheDir)
            inputStream.use {
                file.outputStream().use { output ->
                    it?.copyTo(output)
                }
            }

            // Step 2: Create Multipart for Retrofit
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            // Step 3: Create the RequestBody for fields "key" and "name"
            val apiKey = "78e5c23bd951392cb3c03ebb6f428714"
            val apiKeyBody = apiKey.toRequestBody("text/plain".toMediaType())
            val nameBody = "profile_picture".toRequestBody("text/plain".toMediaType())

            // Step 4: Upload image to ImgBB
            val response = imgbbApi.uploadImage(
                apiKey = apiKeyBody,
                image = imagePart,
                name = nameBody
            )

            // Step 5: Verify the response and update Firestore
            if (response.isSuccessful && response.body()?.data?.url != null) {
                val imageUrl = response.body()!!.data.url
                val deleteUrl = response.body()!!.data.delete_url

                val updates = mapOf(
                    "imageUrl" to imageUrl,
                    "profileImageDeleteUrl" to deleteUrl
                )

                val userRef = firestore.collection("Users").document(id)
                userRef.update(updates).await()
                onResult(
                    resourceProvider.getString(R.string.imageUpdateSuccessStr)
                )
            } else {
                onResult(
                    "${resourceProvider.getString(R.string.imageUploadErrorResponseStr)} ${
                        response.errorBody()?.string()
                    }"
                )
            }

        } catch (e: Exception) {
            onResult("${resourceProvider.getString(R.string.imageUpdateErrorResponseStr)} ${e.message}")
        }
    }

    override suspend fun sendPicture(
        senderId: String,
        receiverId: String,
        image: Uri,
        context: Context,
        onResult: (String) -> Unit
    ) {
        try {
            // Step 1: Convert Uri to temporary file
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(image)
            val file = File.createTempFile("profile_image", ".jpg", context.cacheDir)
            inputStream.use {
                file.outputStream().use { output ->
                    it?.copyTo(output)
                }
            }

            // Step 2: Create Multipart for Retrofit
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            // Step 3: Create the RequestBody for fields "key" and "name"
            val apiKey = "78e5c23bd951392cb3c03ebb6f428714"
            val apiKeyBody = apiKey.toRequestBody("text/plain".toMediaType())
            val nameBody = "image_picture".toRequestBody("text/plain".toMediaType())

            // Step 4: Upload image to ImgBB
            val response = imgbbApi.uploadImage(
                apiKey = apiKeyBody,
                image = imagePart,
                name = nameBody
            )

            // Step 5: Verify the response and update Firestore
            if (response.isSuccessful && response.body()?.data?.url != null) {
                val imageUrl = response.body()!!.data.url

                sendMessage(
                    senderId = senderId,
                    receiverId = receiverId,
                    content = imageUrl
                )
                onResult(
                    resourceProvider.getString(R.string.imageUploadSuccessStr)
                )
            } else {
                onResult(
                    "${resourceProvider.getString(R.string.imageUploadErrorResponseStr)} ${
                        response.errorBody()?.string()
                    }"
                )
            }
        } catch (e: Exception) {
            onResult("${resourceProvider.getString(R.string.imageUploadErrorResponseStr)} ${e.message}")
        }
    }

    override suspend fun getChats(
        context: Context,
        onResult: (List<UserChatModel>) -> Unit
    ): ListenerRegistration {

        val userPreferences = UserPreferences(context)

        val currentUserId = userPreferences.userId.first() ?: return DummyListenerRegistration()

        if (NetworkUtils.isInternetAvailable(context)) {
            val userRef = firestore.collection("Users")
            val chatRef = firestore.collection("chats")

            // We keep a map to store the results
            val resultMap = mutableMapOf<String, UserChatModel>()

            // Listen to changes in the user collection
            val userListener = userRef.addSnapshotListener { userSnapshot, userError ->
                if (userError != null || userSnapshot == null) return@addSnapshotListener

                CoroutineScope(Dispatchers.IO).launch {
                    // Update data of the user (name and image)
                    for (userDoc in userSnapshot.documents) {
                        val user = userDoc.toObject(UserDomain::class.java)
                        val userId = userDoc.id

                        if (userId == currentUserId) {
                            userDao.insertUser(
                                UserEntity(
                                    uid = userId,
                                    username = user?.username ?: "",
                                    email = user?.email ?: "",
                                    imageUrl = user?.imageUrl ?: ""
                                )
                            )
                        }

                        if (user != null && userId != currentUserId) {
                            val existing = resultMap[userId]
                            resultMap[userId] = UserChatModel(
                                username = user.username,
                                imageUrl = user.imageUrl,
                                lastMessage = existing?.lastMessage ?: "",
                                timestamp = existing?.timestamp ?: 0L,
                                uid = userId
                            )

                            // Insert data in room for use app without internet
                            userDao.insertUser(
                                UserEntity(
                                    uid = userId,
                                    username = user.username,
                                    email = user.email,
                                    imageUrl = user.imageUrl
                                )
                            )
                        }
                    }
                    onResult(resultMap.values.toList())
                }
            }

            // Listen for changes in the chat collection where the user is included
            val chatListener = chatRef
                .whereArrayContains("arrayOfUsers", currentUserId)
                .addSnapshotListener { chatSnapshot, chatError ->
                    if (chatError != null || chatSnapshot == null) return@addSnapshotListener

                    CoroutineScope(Dispatchers.IO).launch {
                        // Update data of the chat

                        for (chatDoc in chatSnapshot.documents) {
                            val chat = chatDoc.toObject(ChatDomain::class.java)

                            val otherUserId =
                                chat?.arrayOfUsers?.firstOrNull { it != currentUserId } ?: continue

                            val existing = resultMap[otherUserId]
                            if (chat != null) {
                                resultMap[otherUserId] = UserChatModel(
                                    username = existing?.username ?: "",
                                    imageUrl = existing?.imageUrl ?: "",
                                    lastMessage = chat.lastMessage,
                                    timestamp = chat.timestamp,
                                    uid = otherUserId
                                )

                                // Insert data in room for use app without internet
                                chatDao.insertChat(
                                    ChatEntity(
                                        chatId = chatDoc.id,
                                        user1Id = currentUserId,
                                        user2Id = otherUserId,
                                        lastMessage = chat.lastMessage,
                                        timestamp = chat.timestamp
                                    )
                                )
                            }
                        }
                    }
                    onResult(resultMap.values.toList())
                }

            // We return a combined listener
            return CombinedListener(userListener, chatListener)
        } else {
            val resultMap = mutableMapOf<String, UserChatModel>()

            val users = userDao.getAllUsers()

            for (user in users) {
                if (user.uid == currentUserId) continue

                val existing = resultMap[user.uid]

                resultMap[user.uid] = UserChatModel(
                    username = user.username,
                    imageUrl = user.imageUrl ?: "",
                    lastMessage = existing?.lastMessage ?: "",
                    timestamp = existing?.timestamp ?: 0L,
                    uid = user.uid
                )
            }
            onResult(resultMap.values.toList())
            val chats = chatDao.getAllChats()

            for (chat in chats) {
                if (userPreferences.userId.first() == chat.user1Id) {
                    val existing = resultMap[chat.user2Id]

                    resultMap[chat.user2Id] = UserChatModel(
                        username = existing?.username ?: "",
                        imageUrl = existing?.imageUrl ?: "",
                        lastMessage = chat.lastMessage,
                        timestamp = chat.timestamp,
                        uid = chat.user2Id
                    )
                } else {
                    val existing = resultMap[chat.user1Id]

                    resultMap[chat.user1Id] = UserChatModel(
                        username = existing?.username ?: "",
                        imageUrl = existing?.imageUrl ?: "",
                        lastMessage = chat.lastMessage,
                        timestamp = chat.timestamp,
                        uid = chat.user2Id
                    )
                }

            }
            onResult(resultMap.values.toList())

            return DummyListenerRegistration()
        }
    }


    fun generateChatId(user1: String, user2: String): String {
        return listOf(user1, user2).sorted().joinToString("_")
    }
}