package com.example.yonjarchat.presentation.chat

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.domain.models.MessageModel
import com.example.yonjarchat.domain.models.User
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private var _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private var _chatMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val chatMessages: StateFlow<List<MessageModel>> = _chatMessages

    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    private var lastVisible: DocumentSnapshot? = null
    private var isLoadingMore = false
    var listener: ListenerRegistration? = null

    fun getUser(id: String) {
        viewModelScope.launch {
            _user.value = firebaseRepository.getUserId(id)
        }
    }

    fun observeMessages(
        userId: String,
        context: Context
    ) {
        val userPreferences = UserPreferences(context)

        viewModelScope.launch {
            if (isLoadingMore) return@launch
            isLoadingMore = true

            listener = firebaseRepository.getMessages(
                user1 = userId,
                user2 = userPreferences.userId.first() ?: "",
                lastVisible = lastVisible,
                onResult = { messages, lastDoc ->
                    lastVisible = lastDoc

                    _chatMessages.update { old ->
                        (old + messages).distinctBy { it.timestamp }
                            .sortedBy { it.timestamp }
                    }
                    isLoadingMore = false
                }
            )
        }
    }

    fun sendMessage(
        messageContent: String
    ) {

        if (messageContent.isEmpty()) {
            _message.value = "Error Message cannot be empty"
            return
        }

        viewModelScope.launch {
            firebaseRepository.sendMessage(
                senderId = firebaseAuth.currentUser?.uid ?: "",
                receiverId = user.value?.uid ?: "",
                content = messageContent
            )

        }
    }

    fun sendImage(
        imageUri: Uri,
        context: Context,
        ){
        viewModelScope.launch {
            firebaseRepository.sendPicture(
                senderId = firebaseAuth.currentUser?.uid ?: "",
                receiverId = user.value?.uid ?: "",
                image = imageUri,
                context = context,
                onResult = {
                   _message.value = it
                }
            )
        }
    }

    fun clearMessage() {
        _message.value = ""
    }
}