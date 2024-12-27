package com.example.courseworkmobile

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.courseworkmobile.ui.AdminScreen
import com.example.courseworkmobile.ui.ArticleDetailScreen
import com.example.courseworkmobile.ui.AuthScreen
import com.example.courseworkmobile.ui.AuthState
import com.example.courseworkmobile.ui.AuthViewModel
import com.example.courseworkmobile.ui.CreateNewsScreen
import com.example.courseworkmobile.ui.EditNewsScreen
import com.example.courseworkmobile.ui.UserScreen
import com.example.courseworkmobile.ui.NotificationsScreen
import com.example.courseworkmobile.ui.MainScreen
import com.example.courseworkmobile.ui.NotificationsViewModel
import com.example.courseworkmobile.ui.VerifiedEditorScreen
import com.example.courseworkmobile.ui.theme.CourseWorkMobileTheme
import com.example.courseworkmobile.ui.theme.OnlyNewsGreen

import kotlinx.coroutines.launch

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()
            CourseWorkMobileTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val notificationsViewModel: NotificationsViewModel = hiltViewModel()
                val authState by authViewModel.authState.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var selectedTab by remember { mutableStateOf<Screen>(Screen.News) }

                    LaunchedEffect(authState) {
                        when (authState) {
                            is AuthState.NotAuthenticated -> {
                                notificationsViewModel.resetState()
                            }
                            is AuthState.Authenticated -> {
                                notificationsViewModel.refreshUnreadCount()
                            }
                        }
                    }

                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(75.dp)
                            ) {
                                TabButton(
                                    icon = Icons.Default.MailOutline,
                                    isSelected = selectedTab == Screen.Notifications,
                                    onClick = {
                                        when (authViewModel.getCurrentAuthState()) {
                                            is AuthState.Authenticated -> {
                                                selectedTab = Screen.Notifications
                                                navController.navigate(Screen.Notifications.route) {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                    popUpTo(Screen.News.route) {
                                                        saveState = true
                                                    }
                                                }
                                            }
                                            else -> {
                                                notificationsViewModel.resetState()
                                                selectedTab = Screen.Auth
                                                navController.navigate(Screen.Auth.route) {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                    popUpTo(Screen.News.route) {
                                                        saveState = true
                                                    }
                                                }
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        message = "Увійдіть або зареєструйтесь для доступу до сповіщень",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    notificationsViewModel = notificationsViewModel
                                )

                                TabButton(
                                    isSelected = selectedTab == Screen.News,
                                    onClick = {
                                        selectedTab = Screen.News
                                        navController.navigate(Screen.News.route) {
                                            launchSingleTop = true
                                            popUpTo(Screen.News.route) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(2f),
                                    content = {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "OnlyNews",
                                                style = MaterialTheme.typography.headlineLarge.copy(
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                            Text(
                                                text = "since 2024",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    },
                                    notificationsViewModel = notificationsViewModel
                                )

                                TabButton(
                                    icon = Icons.Default.Person,
                                    isSelected = selectedTab == Screen.User || selectedTab == Screen.Auth || selectedTab == Screen.Admin,
                                    onClick = {
                                        when (authViewModel.getCurrentAuthState()) {
                                            is AuthState.Authenticated -> {
                                                selectedTab = Screen.User
                                                navController.navigate(Screen.User.route) {
                                                    popUpTo(Screen.News.route)
                                                }
                                            }
                                            else -> {
                                                selectedTab = Screen.Auth
                                                navController.navigate(Screen.Auth.route) {
                                                    popUpTo(Screen.News.route)
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    notificationsViewModel = notificationsViewModel
                                )
                            }

                            NavHost(
                                navController = navController,
                                startDestination = Screen.News.route
                            ) {
                                composable(Screen.News.route) {
                                    MainScreen(
                                        onArticleClick = { id ->
                                            navController.navigate("article/$id")
                                        }
                                    )
                                }
                                composable(Screen.User.route) {
                                    val currentAuthState = authViewModel.getCurrentAuthState()
                                    val user = (currentAuthState as? AuthState.Authenticated)?.user
                                    if (user?.isAdmin == true) {
                                        AdminScreen(navController = navController)
                                    } else {
                                        if (user?.isVerified == true) {
                                            VerifiedEditorScreen(
                                                navController = navController,
                                                onArticleClick = { id ->
                                                    navController.navigate("article/$id")
                                                }
                                            )
                                        } else {
                                            UserScreen(navController = navController)
                                        }
                                    }
                                }
                                composable(Screen.Auth.route) {
                                    AuthScreen(
                                        onAuthSuccess = {
                                            selectedTab = Screen.User
                                            navController.navigate(Screen.User.route) {
                                                popUpTo(Screen.Auth.route) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    )
                                }
                                composable(Screen.Admin.route) {
                                    AdminScreen(
                                        navController = navController
                                    )
                                }
                                composable(Screen.Notifications.route) {
                                    NotificationsScreen()
                                }
                                composable(Screen.CreateNews.route) {
                                    CreateNewsScreen(navController = navController)
                                }
                                composable(
                                    route = "article/{id}",
                                    arguments = listOf(
                                        navArgument("id") { type = NavType.IntType }
                                    )
                                ) { backStackEntry ->
                                    ArticleDetailScreen(
                                        newsId = backStackEntry.arguments?.getInt("id") ?: return@composable
                                    )
                                }

                                composable(
                                    route = Screen.EditNews.route,
                                    arguments = listOf(
                                        navArgument("id") { type = NavType.IntType }
                                    )
                                ) { backStackEntry ->
                                    EditNewsScreen(
                                        newsId = backStackEntry.arguments?.getInt("id") ?: return@composable,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnreadNotificationsBadge(
    viewModel: NotificationsViewModel
) {
    val unreadCount by viewModel.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshUnreadCount()
    }

    if (unreadCount > 0) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(OnlyNewsGreen, CircleShape)
                .offset(x = (-0.7).dp, y = (-2.5).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                color = Color.Black,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}

@Composable
fun TabButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    content: @Composable (() -> Unit)? = null,
    notificationsViewModel: NotificationsViewModel
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(if (isSelected) OnlyNewsGreen else Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(40.dp)
                )
                if (icon == Icons.Default.MailOutline) {
                    UnreadNotificationsBadge(viewModel = notificationsViewModel)
                }
            }
        } else {
            content?.invoke()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(if (isSelected) Color.Black else Color.LightGray)
                .align(Alignment.BottomCenter)
        )
    }
}