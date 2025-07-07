package com.example.yonjarchat.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    fun loginUser(email: String, password: String, context: Context) {

        firebaseRepository.loginUser(email, password){
            _message.value = it
            if (it == "Inicio de sesión exitoso"){
                if (firebaseAuth.currentUser != null){
                    viewModelScope.launch {
                        val userPreferences = UserPreferences(context)
                        userPreferences.saveUserId(firebaseAuth.currentUser!!.uid)
                    }
                }
            }
        }
    }

    fun resetMessage(){
        _message.value = ""
    }

}