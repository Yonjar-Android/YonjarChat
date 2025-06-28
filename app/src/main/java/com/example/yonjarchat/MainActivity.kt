package com.example.yonjarchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yonjarchat.presentation.chat.ChatScreen
import com.example.yonjarchat.presentation.chatList.ChatListScreen
import com.example.yonjarchat.presentation.forgotPassword.ForgotPasswordScreen
import com.example.yonjarchat.presentation.login.LoginScreen
import com.example.yonjarchat.presentation.register.RegisterScreen
import com.example.yonjarchat.ui.theme.YonjarChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YonjarChatTheme {

                val controller = rememberNavController()

                NavHost(
                    navController = controller,
                    startDestination = "loginScreen"
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

