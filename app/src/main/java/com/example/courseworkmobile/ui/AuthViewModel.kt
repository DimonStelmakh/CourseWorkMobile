package com.example.courseworkmobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.courseworkmobile.data.local.NotificationDao
import com.example.courseworkmobile.data.local.NotificationEntity
import com.example.courseworkmobile.data.local.PasswordManager
import com.example.courseworkmobile.data.local.UserDao
import com.example.courseworkmobile.data.local.UserEntity
import com.example.courseworkmobile.data.local.VerificationRequestEntity
import com.example.courseworkmobile.data.local.VerificationRequestDao
import com.example.courseworkmobile.data.local.VerificationRequestWithUser
import com.example.courseworkmobile.data.local.VerificationStatus

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences,
    private val verificationRequestDao: VerificationRequestDao,
    private val notificationDao: NotificationDao,
    private val passwordManager: PasswordManager
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    val authState = _authState.asStateFlow()

    private val _verificationRequests = MutableStateFlow<List<VerificationRequestWithUser>>(emptyList())
    val verificationRequests = _verificationRequests.asStateFlow()

    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users = _users.asStateFlow()

    init {
        viewModelScope.launch {
            val savedUsername = userPreferences.getLoggedInUser()
            if (savedUsername != null) {
                val user = userDao.getUserByUsername(savedUsername)
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                }
            }
            viewModelScope.launch {
                if ((authState.value as? AuthState.Authenticated)?.user?.isAdmin == true) {
                    loadVerificationRequests()
                }
            }
            viewModelScope.launch {
                if ((authState.value as? AuthState.Authenticated)?.user?.isAdmin == true) {
                    loadUsers()
                }
            }
        }
    }

    private suspend fun loadVerificationRequests() {
        _verificationRequests.value = verificationRequestDao.getPendingRequestsWithUsers()
    }

    private suspend fun loadUsers() {
        _users.value = userDao.getAllUsers()
    }

    fun getCurrentAuthState(): AuthState {
        viewModelScope.launch {
            val savedUsername = userPreferences.getLoggedInUser()
            if (savedUsername != null) {
                val user = userDao.getUserByUsername(savedUsername)
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.NotAuthenticated
                }
            } else {
                _authState.value = AuthState.NotAuthenticated
            }
        }
        return authState.value
    }

    fun createVerificationRequest(userId: Int) {
        viewModelScope.launch {
            try {
                val latestRequest = verificationRequestDao.getLatestRequestForUser(userId)
                if (latestRequest?.status == VerificationStatus.PENDING) {
                    _uiState.value = AuthUiState.Error("У вас вже є активний запит на верифікацію")
                    return@launch
                }

                val request = VerificationRequestEntity(userId = userId)
                verificationRequestDao.createRequest(request)
                _uiState.value = AuthUiState.Success("Запит на верифікацію подано успішно")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Помилка створення запиту: ${e.localizedMessage}")
            }
        }
    }

    fun approveVerificationRequest(requestId: Int) {
        viewModelScope.launch {
            try {
                val request = verificationRequestDao.getRequestById(requestId)
                request?.let {
                    verificationRequestDao.updateRequest(
                        it.copy(
                            status = VerificationStatus.APPROVED,
                            decisionMadeAt = System.currentTimeMillis()
                        )
                    )

                    val user = userDao.getUserById(it.userId)
                    user?.let { u ->
                        userDao.updateUser(u.copy(isVerified = true))

                        val notification = NotificationEntity(
                            message = "Ваш запит на верифікацію було підтверджено!",
                            timestamp = System.currentTimeMillis(),
                            isRead = false,
                            recipientId = u.id
                        )
                        notificationDao.insertNotification(notification)
                    }

                    loadVerificationRequests()
                    _uiState.value = AuthUiState.Success("Запит підтверджено")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Помилка підтвердження: ${e.localizedMessage}")
            }
        }
    }

    fun rejectVerificationRequest(requestId: Int) {
        viewModelScope.launch {
            try {
                val request = verificationRequestDao.getRequestById(requestId)
                request?.let {
                    verificationRequestDao.updateRequest(
                        it.copy(
                            status = VerificationStatus.REJECTED,
                            decisionMadeAt = System.currentTimeMillis()
                        )
                    )

                    val user = userDao.getUserById(it.userId)
                    user?.let { u ->
                        val notification = NotificationEntity(
                            message = "Ваш запит на верифікацію було відхилено.",
                            timestamp = System.currentTimeMillis(),
                            isRead = false,
                            recipientId = u.id
                        )
                        notificationDao.insertNotification(notification)
                    }

                    loadVerificationRequests()
                    _uiState.value = AuthUiState.Success("Запит відхилено")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Помилка відхилення: ${e.localizedMessage}")
            }
        }
    }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun login(username: String, password: String, onAuthSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val user = userDao.getUserByUsername(username)

                if (user != null && passwordManager.verifyPassword(password, user.password)) {
                    _uiState.value = AuthUiState.Success("Вхід успішний!")
                    _authState.value = AuthState.Authenticated(user)
                    userPreferences.saveUser(username)
                    onAuthSuccess()
                } else {
                    _uiState.value = AuthUiState.Error("Неправильний логін або пароль")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Помилка входу: ${e.localizedMessage}")
            }
        }
    }

    fun register(
        username: String,
        password: String,
        firstname: String,
        surname: String,
        phone: String,
        email: String,
    ) {
        viewModelScope.launch {
            try {
                if (username.isBlank() || password.isBlank() || firstname.isBlank() ||
                    surname.isBlank() || phone.isBlank() || email.isBlank()) {
                    _uiState.value = AuthUiState.Error("Всі поля повинні бути заповнені")
                    return@launch
                }

                val validationErrors = mutableListOf<String>()

                Validators.getValidationError("username", username)?.let {
                    validationErrors.add(it)
                }
                Validators.getValidationError("phone", phone)?.let {
                    validationErrors.add(it)
                }
                Validators.getValidationError("email", email)?.let {
                    validationErrors.add(it)
                }
                Validators.getValidationError("password", password)?.let {
                    validationErrors.add(it)
                }

                if (validationErrors.isNotEmpty()) {
                    _uiState.value = AuthUiState.Error(validationErrors.joinToString("\n"))
                    return@launch
                }

                val existingUser = userDao.getUserByUsername(username)
                if (existingUser != null) {
                    _uiState.value = AuthUiState.Error("Користувач з таким логіном вже існує")
                    return@launch
                }

                val hashedPassword = passwordManager.hashPassword(password)
                val newUser = UserEntity(
                    username = username,
                    password = hashedPassword,
                    firstname = firstname,
                    surname = surname,
                    phone = phone,
                    email = email,
                    isVerified = false
                )

                userDao.insertUser(newUser)
                _authState.value = AuthState.Authenticated(newUser)
                _uiState.value = AuthUiState.Success("Реєстрація успішна!")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Помилка реєстрації: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Success("Ви успішно вийшли з системи")
            delay(500)
            _authState.value = AuthState.NotAuthenticated
            userPreferences.clearUser()
            _uiState.value = AuthUiState.Initial
        }
    }

    fun updateUserData(updatedUser: UserEntity) {
        viewModelScope.launch {
            try {
                val validationErrors = mutableListOf<String>()

                Validators.getValidationError("username", updatedUser.username)?.let {
                    validationErrors.add(it)
                }
                Validators.getValidationError("phone", updatedUser.phone)?.let {
                    validationErrors.add(it)
                }
                Validators.getValidationError("email", updatedUser.email)?.let {
                    validationErrors.add(it)
                }

                if (updatedUser.password != (authState.value as? AuthState.Authenticated)?.user?.password) {
                    Validators.getValidationError("password", updatedUser.password)?.let {
                        validationErrors.add(it)
                    }
                }

                if (validationErrors.isNotEmpty()) {
                    _uiState.value = AuthUiState.Error(validationErrors.joinToString("\n"))
                    return@launch
                }

                val existingUser = userDao.getUserByUsername(updatedUser.username)
                if (existingUser != null && existingUser.id != updatedUser.id) {
                    _uiState.value = AuthUiState.Error("Користувач з таким логіном вже існує")
                    return@launch
                }

                userDao.updateUser(updatedUser)
                _authState.value = AuthState.Authenticated(updatedUser)
                userPreferences.saveUser(updatedUser.username)
                _uiState.value = AuthUiState.Success("Дані успішно оновлено!")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Помилка оновлення даних: ${e.localizedMessage}")
            }
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Initial
    }
}

object Validators {
    private val usernameRegex = Regex("^[a-zA-Z0-9_]+$")
    private val phoneRegex = Regex("^\\+?[0-9]+$")
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    private fun validateUsername(username: String): Boolean =
        username.matches(usernameRegex)

    private fun validatePhone(phone: String): Boolean =
        phone.matches(phoneRegex)

    private fun validateEmail(email: String): Boolean =
        email.matches(emailRegex)

    private fun validatePassword(password: String): Boolean =
        password.length >= 8

    fun getValidationError(field: String, value: String): String? {
        return when(field) {
            "username" -> if (!validateUsername(value))
                "Ім'я користувача може містити тільки латинські літери, цифри та підкреслення" else null
            "phone" -> if (!validatePhone(value))
                "Телефон може містити тільки цифри та '+'" else null
            "email" -> if (!validateEmail(value))
                "Неправильний формат email" else null
            "password" -> if (!validatePassword(value))
                "Пароль повинен містити мінімум 8 символів" else null
            else -> null
        }
    }
}

sealed class AuthState {
    data object NotAuthenticated : AuthState()
    data class Authenticated(val user: UserEntity) : AuthState()
}

sealed class AuthUiState {
    data object Initial : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}