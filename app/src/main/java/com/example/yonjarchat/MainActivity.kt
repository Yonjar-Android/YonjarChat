package com.example.yonjarchat

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.yonjarchat.presentation.settings.SettingsScreen
import com.example.yonjarchat.presentation.settings.SettingsScreenViewModel
import com.example.yonjarchat.ui.theme.YonjarChatTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val settingsViewModel: SettingsScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val darkTheme by settingsViewModel.darkTheme.collectAsStateWithLifecycle()

            YonjarChatTheme(darkTheme = darkTheme) {

                val controller = rememberNavController()
                val context = LocalContext.current

                // Estado para controlar el destino inicial
                var startDestination by remember { mutableStateOf<String>("") }

                LaunchedEffect(Unit) {
                    // Esperar que Firebase actualice el estado del usuario
                    val userPreferences = UserPreferences(context)
                    startDestination = if (userPreferences.userId.first() != null) {
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
                        startDestination = startDestination,
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
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

                        composable(
                            "chatScreen/{userId}", arguments = listOf(
                                navArgument("userId") { type = NavType.StringType }
                            )) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            ChatScreen(
                                navHostController = controller,
                                chatUserId = userId ?: ""
                            )
                        }

                        composable("settingsScreen") {
                            SettingsScreen(
                                navHostController = controller,
                                viewModel = settingsViewModel
                            )
                        }
                    }
                }

                var permissionStatus by remember { mutableStateOf("Permission not Requested") }

                // Android 13+ (API 33+) requiere permiso explícito
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        permissionStatus = if (isGranted) {
                            "Permission Granted"
                        } else {
                            "Permission Denied"
                        }
                    }


                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        permissionStatus = "Already Granted"
                    }

                } else {
                    // Para versiones anteriores a Android 13, el permiso es otorgado automáticamente
                    permissionStatus = "Automatically Granted (Pre-Android 13)"
                }
            }
        }
    }
}

