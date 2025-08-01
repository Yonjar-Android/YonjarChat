package com.example.yonjarchat.presentation.register

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavHostController
import com.example.yonjarchat.R
import com.example.yonjarchat.sharedComponents.ButtonEdit
import com.example.yonjarchat.sharedComponents.ChargeScreen
import com.example.yonjarchat.sharedComponents.TextButtonEdit
import com.example.yonjarchat.sharedComponents.TextFieldEdit

@Composable
fun RegisterScreen(
    navHostController: NavHostController,
    viewModel: RegisterScreenViewModel = hiltViewModel()
) {

    val message by viewModel.message.collectAsState()
    val context = LocalContext.current

    var loading by remember { mutableStateOf(false) }

    if (message == stringResource(
        R.string.userCreatedSuccessStr
    )) {
        loading = false
        navHostController.navigate("loginScreen")
        viewModel.clearMessage()
    }

    if (message.isNotEmpty()) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
        loading = false
    }

    // variables
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var showRepeatPassword by remember { mutableStateOf(false) }



    if (loading) {
        ChargeScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()) // Evita superposición con la barra de estado
                .padding(horizontal = 8.dp), // Espaciado horizontal opcional
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        navHostController.navigateUp()
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = stringResource(id = R.string.createAccountStr),
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextFieldEdit(stringResource(id = R.string.usernameStr), username) {
                username = it
            }

            TextFieldEdit(
                stringResource(id = R.string.emailStr), email, keyboardType = KeyboardType.Email,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Email, contentDescription = "Email",
                        tint = Color.Black
                    )
                }) {
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

            TextFieldEdit(
                stringResource(id = R.string.repeatPwd),
                repeatPassword,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (showRepeatPassword) VisualTransformation.None else PasswordVisualTransformation(),
                icon = {
                    IconButton(onClick = {
                        showRepeatPassword = !showRepeatPassword
                    }) {
                        Icon(
                            imageVector = if (showRepeatPassword) Icons.Rounded.Close else Icons.Rounded.Face,
                            contentDescription = "Show repeat password",
                            tint = Color.Black
                        )
                    }
                }
            ) {
                repeatPassword = it
            }

            ButtonEdit(
                buttonText = stringResource(id = R.string.createAccountStr),
                function = {
                    viewModel.registerUser(email, password, username, repeatPassword)
                    loading = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButtonEdit(
                text = stringResource(id = R.string.alreadyHaveAnAccountStr),
                function = {
                    navHostController.navigate("loginScreen") {
                        popUpTo("loginScreen") {
                            inclusive = true
                        }
                    }
                })
        }
    }
}




