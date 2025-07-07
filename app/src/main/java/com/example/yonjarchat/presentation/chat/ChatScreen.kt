package com.example.yonjarchat.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.yonjarchat.domain.MessageDomain
import com.example.yonjarchat.sharedComponents.TextFieldEdit

@Composable
fun ChatScreen(
    navHostController: NavHostController,
    chatUserId: String,
    viewModel: ChatScreenViewModel = hiltViewModel()
) {

    LaunchedEffect(chatUserId) {
        viewModel.getUser(chatUserId)
    }


    var message by remember { mutableStateOf("") }
    val user by viewModel.user.collectAsStateWithLifecycle()
    val myUserId by viewModel.myUserId.collectAsStateWithLifecycle()


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
                text = user?.username ?: "",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = {

                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Back"
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            items(1) { message ->
                ChatMessageItem(message = MessageDomain(
                    messageId = "",
                    chatId = "",
                    senderId = "",
                    receiverId = "",
                    content = "Hola",
                    timestamp = System.currentTimeMillis(),
                    isSeen = false
                ), myUserId = myUserId ?: "")
            }
        }

        TextFieldEdit(
            textTitle = "Message",
            value = message,
            maxLines = 5,
            singleLine = false,
            onValueChange = { message = it },
            icon = {
                IconButton(
                    onClick = {
                        println("Función llamada")
                        // Clean message
                        viewModel.sendMessage(
                         message
                        )
                        message = ""
                    }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Back"
                    )
                }
            }
        )
    }
}

@Composable
fun ChatMessageItem(
    message: MessageDomain,
    myUserId: String = "yonjar",

    ) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.senderId == myUserId) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(fraction = .85f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (message.senderId == myUserId) Color.Blue else Color(0XFFE8EDF5))
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                message.content,
                fontSize = 16.sp,
                color = if (message.senderId == myUserId) Color.White else Color.Black
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}