package com.example.yonjarchat.presentation.login

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yonjarchat.sharedComponents.ButtonEdit
import com.example.yonjarchat.sharedComponents.TextButtonEdit
import com.example.yonjarchat.sharedComponents.TextFieldEdit

@Composable
fun LoginScreen(
    navHostController: NavHostController
) {

    // variables
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }

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
            Text(
                text = "Sign in",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextFieldEdit(
            "Email", email, keyboardType = KeyboardType.Email,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Email, contentDescription = "Email"
                )
            }
        ) {
            email = it
        }

        TextFieldEdit(
            "Password", password,
            keyboardType = KeyboardType.Password,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            icon = {
                IconButton(onClick = {
                    showPassword = !showPassword
                }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Rounded.Close else Icons.Rounded.Face,
                        contentDescription = "Show password"
                    )
                }
            }
        ) {
            password = it
        }

        ButtonEdit(
            buttonText = "Sign in",
            function = {
                navHostController.navigate("chatListScreen")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButtonEdit(
            text = "Forgot Password?",
            function = {
                navHostController.navigate("forgotPasswordScreen")
            })

        TextButtonEdit(
            text = "Don't have an account? Sign up",
            function = {
                navHostController.navigate("registerScreen")
            })

    }
}