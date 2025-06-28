package com.example.yonjarchat.sharedComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ButtonEdit(buttonText: String, function: () -> Unit = {}) {
    Button(
        onClick = {
            function.invoke()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0XFF0D78F2),
            contentColor = Color.White
        )
    ) {
        Text(
            text = buttonText,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun TextButtonEdit(
    text: String,
    function: () -> Unit = {}
) {
    TextButton(
        onClick = {
            // Acción al hacer clic en "Already have an account? Sign in"
            function.invoke()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color(0XFF0D78F2)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text)
    }
}