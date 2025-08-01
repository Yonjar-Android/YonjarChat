package com.example.yonjarchat.presentation.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.yonjarchat.R
import com.example.yonjarchat.sharedComponents.ButtonEdit
import com.example.yonjarchat.sharedComponents.ChargeScreen
import com.example.yonjarchat.sharedComponents.TextButtonEdit
import com.example.yonjarchat.sharedComponents.TextFieldEdit
import com.example.yonjarchat.utils.NetworkUtils

@Composable
fun LoginScreen(
    navHostController: NavHostController,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {

    // variables
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    var showPassword by remember { mutableStateOf(false) }
    val message by viewModel.message.collectAsStateWithLifecycle()

    var loading by remember { mutableStateOf(false) }

    if (message.isNotEmpty()) {
        if (message == stringResource(id = R.string.youLoggedInStr)) {
            navHostController.navigate("chatListScreen") {
                popUpTo("loginScreen") {
                    inclusive = true
                }
            }
        }

        loading = false
        Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
    }

    if (loading) {
        ChargeScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()) // Avoid superposition of system bar
                .padding(horizontal = 8.dp) // Espaciado horizontal opcional
                .background(MaterialTheme.colorScheme.background),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.signInStr),
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextFieldEdit(
                stringResource(id = R.string.emailStr), email, keyboardType = KeyboardType.Email,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Email, contentDescription = "Email",
                        tint = Color.Black
                    )
                }
            ) {
                email = it
            }

            TextFieldEdit(
                stringResource(id = R.string.passwordStr), password,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                icon = {
                    IconButton(onClick = {
                        showPassword = !showPassword
                    }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Rounded.Close else Icons.Rounded.Face,
                            contentDescription = "Show password",
                            tint = Color.Black
                        )
                    }
                }
            ) {
                password = it
            }

            ButtonEdit(
                buttonText = stringResource(id = R.string.signInStr),
                function = {
                    if (!NetworkUtils.isInternetAvailable(context)) {
                        Toast.makeText(context, R.string.noInternetStr, Toast.LENGTH_SHORT).show()
                        return@ButtonEdit
                    }
                    viewModel.loginUser(email, password, context)
                    loading = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButtonEdit(
                text = stringResource(id = R.string.forgotPwdStr),
                function = {
                    navHostController.navigate("forgotPasswordScreen")
                })

            TextButtonEdit(
                text = stringResource(id = R.string.dontHaveAccountStr),
                function = {
                    navHostController.navigate("registerScreen")
                })
        }
    }
}