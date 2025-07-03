package com.example.yonjarchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yonjarchat.presentation.chat.ChatScreen
import com.example.yonjarchat.presentation.chatList.ChatListScreen
import com.example.yonjarchat.presentation.forgotPassword.ForgotPasswordScreen
import com.example.yonjarchat.presentation.login.LoginScreen
import com.example.yonjarchat.presentation.register.RegisterScreen
import com.example.yonjarchat.ui.theme.YonjarChatTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YonjarChatTheme {

                val controller = rememberNavController()

                var currentUser = firebaseAuth.currentUser

                firebaseAuth.currentUser?.reload()?.addOnCompleteListener {
                    currentUser = firebaseAuth.currentUser
                }

                NavHost(
                    navController = controller,
                    startDestination = if (currentUser != null) "chatListScreen" else "loginScreen"
                ){
                    composable("registerScreen"){
                        RegisterScreen(
                            navHostController = controller
                        )
                    }

                    composable("loginScreen"){
                        LoginScreen(
                            navHostController = controller
                        )
                    }

                    composable("forgotPasswordScreen"){
                        ForgotPasswordScreen(
                            navHostController = controller
                        )
                    }

                    composable("chatListScreen"){
                        ChatListScreen(
                            navHostController = controller
                        )
                    }

                    composable("chatScreen"){
                        ChatScreen(
                            navHostController = controller
                        )
                    }
                }
            }
        }
    }
}

