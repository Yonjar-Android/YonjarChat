package com.example.yonjarchat.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.data.repositories.FirebaseRepositoryImp
import com.example.yonjarchat.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repositoryImp: FirebaseRepositoryImp
): ViewModel() {

     private var _username = MutableStateFlow<User?>(null)
     val username: StateFlow<User?> = _username

    private var _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    val darkTheme: StateFlow<Boolean> = userPreferences.darkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun getUsername(id:String) {
        viewModelScope.launch {
            val response = repositoryImp.getUserId(id)
            _username.value = response
        }
    }

    fun setUsername(id:String,username: String) {
        viewModelScope.launch {
            repositoryImp.updateUsername(id, username = username,
                onResult = {
                    _message.value = it
                })
            getUsername(id)
        }
    }

    fun setPicture(id:String,image: Uri, context: Context, actualImage: String){
        viewModelScope.launch {
                repositoryImp.updatePicture(id,image,context,
                    onResult = {
                        _message.value = it
                    })

        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkTheme(enabled)
        }
    }

    fun clearMessage(){
        _message.value = ""
    }
}