@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.yonjarchat.presentation.chatList

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.yonjarchat.R
import com.example.yonjarchat.domain.models.UserChatModel
import com.example.yonjarchat.sharedComponents.TextFieldEdit

@Composable
fun ChatListScreen(
    navHostController: NavHostController,
    viewModel: ChatListViewModel = hiltViewModel()
) {

    var username by remember { mutableStateOf("") }

    val chats by viewModel.chats.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    if (message.isNotEmpty()) {
        if (message == "Sesión cerrada exitosamente") {
            navHostController.navigate("loginScreen") {
                popUpTo("chatListScreen") {
                    inclusive = true
                }
            }
        }

        Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
    }

    LaunchedEffect(Unit) {
        viewModel.getChats(context = context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()) // Evita superposición con la barra de estado
            .padding(horizontal = 8.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    showDialog = true
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
                text = "Chats",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(
                onClick = {
                    navHostController.navigate("settingsScreen")
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldEdit(
            textTitle = stringResource(R.string.searchStr),
            value = username,
            onValueChange = {
                username = it
            },
            icon = {
                IconButton(
                    onClick = {
                        //Clean search
                        username = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(chats) {
                ChatItem(
                    user = it,
                    navigateToChat = {
                        navHostController.navigate("chatScreen/${it.uid}")
                    })
            }
        }
    }

    if (showDialog) {
        DialogSignOut(
            onDismissRequest = {
                showDialog = false
            },
            onConfirm = {
                viewModel.signOut(context)
                showDialog = false
            }
        )
    }

    BackHandler {
        showDialog = true
    }
}

@Composable
fun ChatItem(
    user: UserChatModel,
    navigateToChat: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigateToChat.invoke()
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically)
    {


        println("Foto de perfil: ${user.imageUrl}")
        AsyncImage(
            model = user.imageUrl,
            contentDescription = "User Image",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape), placeholder = painterResource(R.drawable.user),
            error = painterResource(R.drawable.user),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.username, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (user.lastMessage.isEmpty()) "Last Message" else user.lastMessage,
                fontSize = 16.sp,
                color = Color(0XFF4A709C),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun DialogSignOut(
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.areYouSureSignOutStr), textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        onDismissRequest.invoke()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text(text = stringResource(R.string.cancelStr))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        onConfirm.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text(
                        text = stringResource(R.string.signOutStr),
                        color = Color.Black
                    )
                }
            }
        }
    }
}