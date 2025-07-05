package com.example.yonjarchat.presentation.login

import androidx.lifecycle.ViewModel
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    fun loginUser(email: String, password: String) {
        firebaseRepository.loginUser(email, password){
            _message.value = it
        }
    }

    fun resetMessage(){
        _message.value = ""
    }

}