package com.example.courseworkmobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import coil.compose.AsyncImage


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArticleDetailScreen(
    newsId: Int,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var categoryName by remember { mutableStateOf("") }
    val news = state.news.find { it.id == newsId }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(news?.categoryId) {
        news?.let {
            categoryName = viewModel.getCategoryName(it.categoryId)
        }
    }

    LaunchedEffect(newsId) {
        viewModel.incrementViews(newsId)
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is NewsUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as NewsUiState.Success).message,
                    duration = SnackbarDuration.Short
                )
            }
            is NewsUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as NewsUiState.Error).message,
                    duration = SnackbarDuration.Short
                )
            }
            else -> {}
        }
    }


    if (state.news.isEmpty()) {
        Text("Loading...", modifier = Modifier.padding(16.dp))
        return
    }


    if (news == null) {
        Text("Article not found", modifier = Modifier.padding(16.dp))
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Box {
                    AsyncImage(
                        model = news.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    FavoriteButton(
                        newsId = newsId,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Переглядів: ${news.views}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = news.title,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = news.author,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDate(news.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    Text(
                        text = news.content,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )

                    val keywords by viewModel.getKeywordsForArticle(news.id).collectAsState(initial = emptyList())
                    if (keywords.isNotEmpty()) {
                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        Text(
                            text = "Ключові слова:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        )
                        {
                            keywords.forEach { keyword ->
                                AssistChip(
                                    onClick = { /* Optional: handle keyword click */ },
                                    label = { Text(keyword) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}