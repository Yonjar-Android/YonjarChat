@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.yonjarchat.presentation.settings

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.yonjarchat.R
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.sharedComponents.ButtonEdit

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    viewModel: SettingsScreenViewModel
) {

    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
    val user by viewModel.username.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    var showPicture by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userPreferences = UserPreferences(context)
    val myUserId by produceState<String?>(initialValue = null) {
        userPreferences.userId.collect {
            value = it
        }
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    if (myUserId != null) {
        LaunchedEffect(Unit) {
            viewModel.getUsername(myUserId!!)
        }
    }

    var showEditUsernameDialog by remember { mutableStateOf(false) }

    if (message.isNotEmpty()) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues()),
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
                text = stringResource(R.string.settingsStr),
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

        }

        println("Imagen: ${user?.imageUrl}")

        AsyncImage(
            model = user?.imageUrl,
            contentDescription = "Settings",
            modifier = Modifier
                .padding(16.dp)
                .size(150.dp)
                .clip(CircleShape)
                .clickable {
                    showPicture = true
                },
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.user),
            error = painterResource(R.drawable.user)
        )

        Spacer(
            modifier = Modifier.size(24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                user?.username ?: "",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = {
                    showEditUsernameDialog = true
                },
                modifier = Modifier.background(
                    if (darkTheme) Color.Gray else Color.LightGray, CircleShape
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        Spacer(
            modifier = Modifier.size(24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.darkThemeStr),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Switch(
                checked = darkTheme,
                onCheckedChange =
                    {
                        viewModel.setDarkTheme(it)
                    }
            )
        }

        Spacer(
            modifier = Modifier.size(24.dp)
        )

        val locale = LocalConfiguration.current.locales[0]
        val currentLanguage = when (locale.language) {
            "en" -> "English"
            "es" -> "Español"
            else -> locale.displayLanguage // por si acaso
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.languageStr),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(currentLanguage, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }

    }

    if (showEditUsernameDialog) {
        EditUsernameDialog(
            currentName = user?.username ?: "",
            onDismiss = { showEditUsernameDialog = false },
            onSave = { newName ->
                if (myUserId != null && newName.isNotEmpty()) {
                    viewModel.setUsername(myUserId ?: "", newName)
                    showEditUsernameDialog = false
                } else {
                    Toast.makeText(context, "Invalid username", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (showPicture) {
        PictureDialog(
            imageUrl = user?.imageUrl,
            onDismiss = { showPicture = false },
            setImage = { imageUri ->
                selectedImageUri = imageUri
                if (myUserId != null && selectedImageUri != null) {
                    viewModel.setPicture(
                        myUserId ?: "",
                        selectedImageUri!!,
                        context,
                        user?.imageUrl ?: ""
                    )
                    showPicture = false
                }
            }
        )
    }

    BackHandler {
        navHostController.navigateUp()
    }
}

@Composable
fun EditUsernameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.editUserStr)
            )
        },
        text = {
            Column {
                Text(
                    stringResource(R.string.enterUsernameStr),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(newName)
                onDismiss()
            }) {
                Text(stringResource(R.string.saveStr))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancelStr))
            }
        }
    )
}

@Composable
fun PictureDialog(
    imageUrl: String? = null,
    onDismiss: () -> Unit,
    setImage: (Uri?) -> Unit
) {

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            setImage(uri)

            // Procesar imagen seleccionada
            Log.d("Picker", "Imagen seleccionada: $uri")
            // Aquí puedes mostrar la imagen o guardarla
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onDismiss.invoke()
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = imageUrl,
                contentDescription = "Settings",
                modifier = Modifier
                    .padding(16.dp)
                    .size(250.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.user),
                error = painterResource(R.drawable.user)
            )

            ButtonEdit(
                buttonText = stringResource(R.string.updatePictureStr),
                function = {
                    imagePickerLauncher.launch("image/*")
                }
            )

        }
    }
}
