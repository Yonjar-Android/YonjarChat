package com.example.yonjarchat.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.R
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.example.yonjarchat.utils.ResourceProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val firebaseAuth: FirebaseAuth,
    private val resourceProvider: ResourceProvider
): ViewModel() {

    private var _message = MutableStateFlow<String>("")
    val message: StateFlow<String> = _message

    fun loginUser(email: String, password: String, context: Context) {

        if (validations(email, password)){
            firebaseRepository.loginUser(email, password){
                if (it == resourceProvider.getString(R.string.youLoggedInStr)){
                    if (firebaseAuth.currentUser?.uid != null){
                        viewModelScope.launch {
                            val userPreferences = UserPreferences(context)
                            userPreferences.saveUserId(firebaseAuth.currentUser!!.uid)
                            _message.value = it
                            println("User id viewModel ${userPreferences.userId.first()}")
                        }
                    }
                }
            }
        }
    }


    fun validations(email: String, password: String): Boolean{
        if (email.isEmpty() || password.isEmpty()){
            _message.value = "Please fill all the fields"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _message.value = "Email is not valid"
            return false
        }
        return true
    }

    fun clearMessage(){
        _message.value = ""
    }

}