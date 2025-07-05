package com.example.yonjarchat.presentation.chatList

import androidx.lifecycle.ViewModel
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    fun signOut() {
        val response = firebaseRepository.signOut()
        _message.value = response
    }

    fun resetMessage(){
        _message.value = ""
    }

}