package com.example.courseworkmobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import com.example.courseworkmobile.Screen
import com.example.courseworkmobile.data.local.VerificationRequestWithUser
import com.example.courseworkmobile.ui.theme.OnlyNewsGreen
import com.example.courseworkmobile.data.local.UserEntity

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}


@Composable
fun AdminScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    navController: NavController
) {
    var username by remember { mutableStateOf("") }
    var firstname by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val currentUser = (authState as? AuthState.Authenticated)?.user
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            username = it.username
            firstname = it.firstname
            surname = it.surname
            phone = it.phone
            email = it.email
        }
    }

    LaunchedEffect(authState) {
        delay(500)
        if (authState is AuthState.NotAuthenticated) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.News.route)
            }
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as AuthUiState.Success).message,
                    duration = SnackbarDuration.Short
                )
            }

            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as AuthUiState.Error).message,
                    duration = SnackbarDuration.Short
                )
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(padding)
        ) {
            Text(
                text = "Особистий кабінет",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Зміна Ваших даних",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Нікнейм") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = OnlyNewsGreen
                    )
                )

                OutlinedTextField(
                    value = firstname,
                    onValueChange = { firstname = it },
                    label = { Text("Реальне ім'я") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = OnlyNewsGreen
                    )
                )

                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Реальне прізвище") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = OnlyNewsGreen
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Номер телефону") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = OnlyNewsGreen
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Електронна пошта") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = OnlyNewsGreen
                    )
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Введіть новий пароль") },
                    visualTransformation = PasswordVisualTransformation('\u2217'),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = OnlyNewsGreen
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Повторіть новий пароль") },
                    visualTransformation = PasswordVisualTransformation('\u2217'),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = OnlyNewsGreen
                    )
                )

                Button(
                    onClick = {
                        if (newPassword.isNotBlank() && newPassword != confirmPassword) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Паролі не співпадають")
                            }
                            return@Button
                        }

                        currentUser?.let { user ->
                            viewModel.updateUserData(
                                user.copy(
                                    username = username,
                                    firstname = firstname,
                                    surname = surname,
                                    phone = phone,
                                    email = email,
                                    password = newPassword.ifBlank { user.password }
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnlyNewsGreen
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Зберегти",
                        color = Color.Black
                    )
                }

                Button(
                    onClick = {
                        viewModel.logout()
                        notificationsViewModel.refreshUnreadCount()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnlyNewsGreen
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Вийти",
                        color = Color.Black
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Запити на верифікацію",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    val verificationRequests by viewModel.verificationRequests.collectAsState()

                    if (verificationRequests.isEmpty()) {
                        Text(
                            text = "Запити наразі відсутні",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(250.dp)
                        ) {
                            items(verificationRequests) { request ->
                                VerificationRequestCard(
                                    request = request,
                                    onApprove = { viewModel.approveVerificationRequest(request.request.id) },
                                    onReject = { viewModel.rejectVerificationRequest(request.request.id) }
                                )
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .heightIn(max = 300.dp)
                ) {
                    Text(
                        text = "Користувачі OnlyNews",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
                    )

                    val users by viewModel.users.collectAsState()
                    LazyColumn(
                        modifier = Modifier.height(250.dp)
                    ) {
                        items(users) { user ->
                            UserCard(user = user)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "+38 (098) 11 22 3333",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "м. Київ, вул. Борщагівська, 128/3",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun VerificationRequestCard(
    request: VerificationRequestWithUser,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Користувач: ${request.user.firstname} ${request.user.surname}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Запит створено: ${formatDate(request.request.createdAt)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnlyNewsGreen
                    )
                ) {
                    Text("Підтвердити")
                }
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.8f)
                    )
                ) {
                    Text("Відхилити")
                }
            }
        }
    }
}


@Composable
private fun UserCard(
    user: UserEntity
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${user.firstname} ${user.surname}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Нікнейм: ${user.username}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Email: ${user.email}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (user.isVerified) {
                    Text(
                        text = "Верифікований",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                if (user.isAdmin) {
                    Text(
                        text = "Адміністратор",
                        color = Color.Blue,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}