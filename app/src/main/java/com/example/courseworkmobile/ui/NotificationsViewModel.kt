package com.example.courseworkmobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

import com.example.courseworkmobile.data.local.NotificationDao
import com.example.courseworkmobile.data.local.NotificationEntity
import com.example.courseworkmobile.data.local.UserDao

import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject


@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationDao: NotificationDao,
    private val userPreferences: UserPreferences,
    private val userDao: UserDao
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val supervisorJob = SupervisorJob()

    init {
        try {
            viewModelScope.launch(supervisorJob + Dispatchers.IO) {
                val username = userPreferences.getLoggedInUser()
                val userId = username?.let {
                    userDao.getUserByUsername(it)?.id
                }

                userId?.let { currentUserId ->
                    notificationDao.getNotificationsForUser(currentUserId)
                        .catch { _ ->
                            // pass
                        }
                        .collect { notificationsList ->
                            _notifications.value = notificationsList
                        }
                }
            }

            refreshUnreadCount()
        } catch (e: Exception) {
            // pass
        }
    }

    override fun onCleared() {
        supervisorJob.cancel()
        super.onCleared()
    }

    fun resetState() {
        viewModelScope.launch {
            _notifications.value = emptyList()
            _unreadCount.value = 0
        }
    }

    fun refreshUnreadCount() {
        viewModelScope.launch(supervisorJob + Dispatchers.IO) {
            val username = userPreferences.getLoggedInUser()
            val userId = username?.let {
                userDao.getUserByUsername(it)?.id
            }

            if (userId != null) {
                notificationDao.getUnreadCount(userId)
                    .catch { _ ->
                        _unreadCount.value = 0
                    }
                    .collect { count ->
                        _unreadCount.value = count
                    }
            } else {
                _unreadCount.value = 0
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val username = userPreferences.getLoggedInUser()
            val userId = username?.let {
                userDao.getUserByUsername(it)?.id
            }

            userId?.let { currentUserId ->
                notificationDao.markAllAsRead(currentUserId)
            }
            refreshUnreadCount()
        }
    }
}