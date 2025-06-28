package com.example.yonjarchat.sharedComponents

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldEdit(
    textTitle: String, value: String,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = {
            onValueChange.invoke(it)
        },
        label = { Text(textTitle, color = Color(0XFF4A709C)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedContainerColor = Color(0XFFE8EDF5),
            unfocusedContainerColor = Color(0XFFE8EDF5),
            disabledContainerColor = Color(0XFFE8EDF5),
            errorContainerColor = Color(0XFFE8EDF5),
            focusedTextColor = Color(0XFF4A709C),
            unfocusedTextColor = Color(0XFF4A709C),
            disabledTextColor = Color(0XFF4A709C),
            errorTextColor = Color(0XFF4A709C),
        ),
        singleLine = singleLine,
        maxLines = maxLines,
        trailingIcon = icon
    )

    Spacer(modifier = Modifier.height(16.dp))
}