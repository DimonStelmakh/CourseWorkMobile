package com.example.courseworkmobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState

import com.example.courseworkmobile.data.local.NotificationEntity
import com.example.courseworkmobile.ui.theme.OnlyNewsGreen


@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Сповіщення",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
        )

        Button(
            onClick = { viewModel.markAllAsRead() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OnlyNewsGreen
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "Позначити всі як прочитані",
                color = Color.Black
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(notifications) { notification ->
                NotificationCard(
                    notification = notification
                )
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Від: OnlyNews",
                    style = MaterialTheme.typography.titleMedium
                )
                if (!notification.isRead) {
                    Text(
                        text = "Нове!",
                        modifier = Modifier
                            .background(
                                OnlyNewsGreen,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatDate(notification.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}