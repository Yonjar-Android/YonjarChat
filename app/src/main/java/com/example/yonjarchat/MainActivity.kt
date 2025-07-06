package com.example.yonjarchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yonjarchat.presentation.chat.ChatScreen
import com.example.yonjarchat.presentation.chatList.ChatListScreen
import com.example.yonjarchat.presentation.forgotPassword.ForgotPasswordScreen
import com.example.yonjarchat.presentation.login.LoginScreen
import com.example.yonjarchat.presentation.register.RegisterScreen
import com.example.yonjarchat.ui.theme.YonjarChatTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await
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

                // Estado para controlar el destino inicial
                var startDestination by remember { mutableStateOf<String>("") }

                LaunchedEffect(Unit) {
                    // Esperar que Firebase actualice el estado del usuario
                    firebaseAuth.currentUser?.reload()?.await()
                    startDestination = if (firebaseAuth.currentUser != null) {
                        "chatListScreen"
                    } else {
                        "loginScreen"
                    }
                }

                if (startDestination == "") {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    NavHost(
                        navController = controller,
                        startDestination = startDestination
                    ) {
                        composable("registerScreen") {
                            RegisterScreen(
                                navHostController = controller
                            )
                        }

                        composable("loginScreen") {
                            LoginScreen(
                                navHostController = controller
                            )
                        }

                        composable("forgotPasswordScreen") {
                            ForgotPasswordScreen(
                                navHostController = controller
                            )
                        }

                        composable("chatListScreen") {
                            ChatListScreen(
                                navHostController = controller
                            )
                        }

                        composable("chatScreen/{userId}", arguments = listOf(
                            navArgument("userId") {type = NavType.StringType}
                        )) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            ChatScreen(
                                navHostController = controller,
                                chatUserId = userId ?: ""
                            )
                        }
                    }
                }

            }
        }
    }
}

