package com.example.courseworkmobile.ui

import androidx.compose.foundation.layout.*
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
import com.example.courseworkmobile.ui.theme.OnlyNewsGreen

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


@Composable
fun UserScreen(
    userViewModel: AuthViewModel = hiltViewModel(),
    newsViewModel: NewsViewModel = hiltViewModel(),
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

    val authState by userViewModel.authState.collectAsState()
    val currentUser = (authState as? AuthState.Authenticated)?.user
    val uiState by userViewModel.uiState.collectAsState()
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
                            userViewModel.updateUserData(
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
                        userViewModel.logout()
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

                KeywordSubscriptionSection(
                    viewModel = newsViewModel,
                    snackbarHostState = snackbarHostState
                )

                Divider(modifier = Modifier.padding(vertical = 24.dp))

                Text(
                    text = "Верифікація автора",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
                )

                Button(
                    onClick = {
                        currentUser?.let { user ->
                            userViewModel.createVerificationRequest(user.id)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OnlyNewsGreen
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Подати запит на верифікацію",
                        color = Color.Black
                    )
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
fun KeywordSubscriptionSection(
    viewModel: NewsViewModel,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    var subscribedKeywords by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        viewModel.getUserKeywordSubscriptions().collect { keywords ->
            subscribedKeywords = keywords
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Divider(modifier = Modifier.padding(vertical = 24.dp))

        Text(
            text = "Підписки на ключові слова",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
        )

        KeywordsInput(
            keywords = subscribedKeywords,
            onKeywordsChange = { subscribedKeywords = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    viewModel.updateKeywordSubscriptions(subscribedKeywords)
                    snackbarHostState.showSnackbar("Підписки оновлено")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = OnlyNewsGreen
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Зберегти підписки", color = Color.Black)
        }
    }
}