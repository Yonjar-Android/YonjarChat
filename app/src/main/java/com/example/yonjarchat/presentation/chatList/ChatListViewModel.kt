package com.example.yonjarchat.presentation.chatList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.domain.models.User
import com.example.yonjarchat.domain.models.UserChatModel
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository

): ViewModel() {

    private var _chats = MutableStateFlow<List<UserChatModel>>(emptyList())
    val chats: StateFlow<List<UserChatModel>> = _chats

    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    private var listenerRegistration: ListenerRegistration? = null

    fun getChats(context: Context) {
        viewModelScope.launch {
            listenerRegistration = firebaseRepository.getChats(context) { chatList ->
                _chats.value = chatList
            }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            val response = firebaseRepository.signOut()
            _message.value = response

            if (response == "Sesión cerrada exitosamente"){
                val userPreferences = UserPreferences(context)
                userPreferences.clearUserId()
            }
        }

    }

    fun clearMessage(){
        _message.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

}