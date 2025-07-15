package com.example.yonjarchat.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _message = MutableStateFlow("")
    val message:StateFlow<String> = _message

    fun registerUser(
        email: String, password: String, username: String, repeatPassword: String
    ){

        // if validations are correct call the repository to register the user
        if (validations(email, password, username, repeatPassword)){
            viewModelScope.launch {
                _message.value = firebaseRepository.registerUser(email, password, username)
            }
        }

    }

    private fun validations(
        email: String, password: String, username: String, repeatPassword: String
    ): Boolean{
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || repeatPassword.isEmpty()){
            _message.value = "Please fill all the fields"
            return false
        }
        if (password.length < 6){
            _message.value = "Password must be at least 6 characters"
            return false
        }

        if (password != repeatPassword){
            _message.value = "Passwords do not match"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _message.value = "Email is not valid"
            return false
        }

        if (username.length < 3){
            _message.value = "Username must be at least 3 characters"
            return false
        }

        return true
    }

    fun clearMessage(){
        _message.value = ""
    }

}