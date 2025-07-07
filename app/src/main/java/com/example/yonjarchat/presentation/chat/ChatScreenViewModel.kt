package com.example.yonjarchat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.domain.models.MessageModel
import com.example.yonjarchat.domain.models.User
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {
    private var _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private var _myUserId = MutableStateFlow<String?>(null)
    val myUserId: StateFlow<String?> = _myUserId

    private var _chatMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val chatMessages: StateFlow<List<MessageModel>> = _chatMessages


    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    var listener: ListenerRegistration? = null

    fun getUser(id: String) {
        viewModelScope.launch {
            _user.value = firebaseRepository.getUserId(id)
            _myUserId.value = firebaseAuth.currentUser?.uid
        }
    }

    fun observeMessages(
        userId :String
    ) {
        viewModelScope.launch {
            listener = firebaseRepository.getMessages(
                user1 = userId,
                user2 = firebaseAuth.currentUser?.uid ?: "",
                onResult = { messages ->
                    _chatMessages.value = messages
                    println("Mensajes recibidos: $messages")
                }
            )
        }
    }

    fun sendMessage(
        messageContent: String
    ){
        viewModelScope.launch {
            firebaseRepository.sendMessage(
                senderId = firebaseAuth.currentUser?.uid ?: "",
                receiverId = user.value?.uid ?: "",
                content = messageContent
            )

            println("Mensaje enviado")
        }
    }
}