package com.example.yonjarchat.presentation.chat

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.yonjarchat.R
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.domain.MessageDomain
import com.example.yonjarchat.sharedComponents.TextFieldEdit
import com.example.yonjarchat.utils.ImageHelper
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ChatScreen(
    navHostController: NavHostController,
    chatUserId: String,
    viewModel: ChatScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    LaunchedEffect(chatUserId) {
        viewModel.getUser(chatUserId)
        viewModel.observeMessages(chatUserId, context)
    }

    var showFullImage by remember { mutableStateOf(false) }
    var imageToShow by remember { mutableStateOf("") }

    val userPreferences = UserPreferences(context)
    val myUserId by produceState<String?>(initialValue = null) {
        userPreferences.userId.collect {
            value = it
        }
    }

    val errorMessage by viewModel.message.collectAsStateWithLifecycle()

    var message by remember { mutableStateOf("") }
    val user by viewModel.user.collectAsStateWithLifecycle()

    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    if (errorMessage.isNotEmpty()) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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

            val username: String? = user?.username?.replaceFirst(
                user?.username?.first().toString(),
                user?.username?.first().toString().uppercase()
            )

            Text(
                text = username ?: "",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(
                onClick = {

                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        var animateScroll by remember { mutableIntStateOf(0) }

        HorizontalDivider()

        val listState = rememberLazyListState()

        // This lambda will be called when the list's size changes
        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                if (chatMessages.size <= 15) {
                    listState.animateScrollToItem(chatMessages.lastIndex)
                } else if (chatMessages.size - animateScroll == 1) {
                    listState.animateScrollToItem(chatMessages.lastIndex)
                } else {
                    listState.animateScrollToItem(4)
                }
                animateScroll = chatMessages.size
            }
        }

        // This effect will be triggered when the list's first visible item changes
        LaunchedEffect(Unit) {
            snapshotFlow { listState.firstVisibleItemIndex }
                .distinctUntilChanged()
                .collect { index ->
                    if (index == 0) {
                        viewModel.observeMessages(chatUserId, context)
                    }
                }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(vertical = 16.dp),
            state = listState
        ) {
            items(chatMessages) { message ->

                if (ImageHelper.isImageUrl(message.content)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = if (message.senderId == myUserId) Alignment.End else Alignment.Start
                    ) {
                        AsyncImage(
                            model = message.content,
                            contentDescription = "Imagen enviada",
                            modifier = Modifier
                                .fillMaxWidth(fraction = .85f)
                                .height(200.dp)
                                .clickable {
                                    imageToShow = message.content
                                    showFullImage = true
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    ChatMessageItem(
                        message = MessageDomain(
                            messageId = "",
                            chatId = "",
                            senderId = message.senderId,
                            receiverId = message.receiverId,
                            content = message.content,
                            timestamp = message.timestamp,
                            isSeen = false
                        ), myUserId = myUserId ?: ""
                    )
                }

            }
        }

        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var showImageDialog by remember { mutableStateOf(false) }

        // Launcher to open the image picker
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {

                selectedImageUri = it
                showImageDialog = true

                // Process the selected image URI
                Log.d("Picker", "Imagen seleccionada: $uri")

            }
        }

        TextFieldEdit(
            textTitle = stringResource(R.string.messageStr),
            value = message,
            maxLines = 5,
            singleLine = false,
            onValueChange = { message = it },
            icon = {
                Row {
                    if (message.isEmpty()) {
                        IconButton(
                            onClick = {
                                // Clean message
                                imagePickerLauncher.launch("image/*")
                                message = ""
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_image_24),
                                contentDescription = "Back",
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }


                    IconButton(
                        onClick = {
                            // Clean message
                            viewModel.sendMessage(
                                message.trim()
                            )
                            message = ""
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }

            }
        )

        if (showImageDialog && selectedImageUri != null) {
            ConfirmImage(
                selectedImageUri = selectedImageUri,
                sendPicture = {
                    if (selectedImageUri != null) {
                        viewModel.sendImage(selectedImageUri!!, context)
                    }
                    showImageDialog = false
                    selectedImageUri = null
                },
                onDismiss = {
                    showImageDialog = false
                    selectedImageUri = null
                }
            )
        }

        if (showFullImage) {
            ShowFullImage(imageUrl = imageToShow) {
                showFullImage = false
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: MessageDomain,
    myUserId: String = "",
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

@Composable
fun ConfirmImage(
    selectedImageUri: Uri?,
    sendPicture: () -> Unit = {},
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(
            stringResource(R.string.sendImageStr)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.wouldYouLikeSendImageStr),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                Log.d("EnviarImagen", "URI a enviar: $selectedImageUri")
                sendPicture.invoke()
            }) {
                Text(
                    stringResource(R.string.sendStr)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(
                    stringResource(R.string.cancelStr),
                )
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowFullImage(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 5f)

            // Limitar el desplazamiento según el tamaño escalado
            val maxOffsetX = (scale - 1f) * 2000f // puedes ajustar el 2000f según sea necesario
            val maxOffsetY = (scale - 1f) * 2000f

            val newOffsetX = (offset.x + offsetChange.x).coerceIn(-maxOffsetX, maxOffsetX)
            val newOffsetY = (offset.y + offsetChange.y).coerceIn(-maxOffsetY, maxOffsetY)

            offset = Offset(newOffsetX, newOffsetY)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onDismiss() })
                }
                .transformable(state)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen completa",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}



