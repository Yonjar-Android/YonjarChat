package com.example.yonjarchat.presentation.forgotPassword

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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.yonjarchat.sharedComponents.ButtonEdit
import com.example.yonjarchat.sharedComponents.TextFieldEdit

@Composable
fun ForgotPasswordScreen(
    navHostController: NavHostController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    // variables
    var email by remember { mutableStateOf("") }

    val message by viewModel.message.collectAsStateWithLifecycle()

    if (message.isNotEmpty()) {
        Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
    }

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
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Reset Password",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextFieldEdit("Email", email, onValueChange = {
            email = it
        })

        Spacer(modifier = Modifier.weight(1f))

        ButtonEdit(
            buttonText = "Send reset link",
            function = {
                viewModel.forgotPassword(email)
            }
        )

    }
}