package com.example.yonjarchat.presentation.chatList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.domain.models.User
import com.example.yonjarchat.domain.repositories.FcmRepository
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val fcmRepository: FcmRepository,
    private val firebaseAuth: FirebaseAuth

): ViewModel() {

    private var _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    init {
        getUsers()

        fcmRepository.getCurrentToken { token ->
            if (token != null) {
                fcmRepository.saveTokenToDatabase(firebaseAuth.currentUser?.uid ?: "", token)
            }
        }
    }

    fun getUsers() {
        viewModelScope.launch {
            _users.value = firebaseRepository.getUsers()
            if (_users.value.isEmpty()) _message.value = "No se encontraron usuarios"
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

}