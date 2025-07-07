package com.example.yonjarchat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.domain.models.User
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
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

    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    fun getUser(id: String) {
        viewModelScope.launch {
            _user.value = firebaseRepository.getUserId(id)
            _myUserId.value = firebaseAuth.currentUser?.uid
        }
    }

    fun sendMessage(
        messageContent: String
    ){
        viewModelScope.launch {
            firebaseRepository.sendMessage(
                senderId = user.value?.uid ?: "",
                receiverId = firebaseAuth.currentUser?.uid ?: "",
                content = messageContent
            )
            println("mYUSER: ${firebaseAuth.currentUser?.uid}")
            println("USER: ${user.value?.uid}")
            println("Mensaje enviado")
        }
    }
}