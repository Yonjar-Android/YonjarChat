package com.example.yonjarchat.presentation.forgotPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            // Validate format of email
            if (!validateEmail(email)) { return@launch }

            // Send email to reset password
            _message.value = firebaseRepository.forgotPassword(email)
        }
    }

    fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            _message.value = "Email cannot be empty"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _message.value = "Invalid email format"
            return false
        }
        return true
    }

    fun clearMessage(){
        _message.value = ""
    }
}
