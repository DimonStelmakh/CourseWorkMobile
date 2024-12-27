package com.example.courseworkmobile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.example.courseworkmobile.data.local.CategoryEntity
import com.example.courseworkmobile.data.local.LanguageEntity
import com.example.courseworkmobile.ui.theme.OnlyNewsGreen

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNewsScreen(
    newsId: Int,
    viewModel: NewsViewModel = hiltViewModel(),
    navController: NavController
) {
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var selectedLanguage by remember { mutableStateOf<LanguageEntity?>(null) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var languageDropdownExpanded by remember { mutableStateOf(false) }
    var keywords by remember { mutableStateOf(listOf<String>()) }

    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val languages by viewModel.languages.collectAsState(initial = emptyList())

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    val news by viewModel.getNewsById(newsId).collectAsState(initial = null)

    LaunchedEffect(news, categories, languages) {
        news?.let { currentNews ->
            title = currentNews.title
            content = currentNews.content
            imageUrl = currentNews.imageUrl ?: ""
            selectedCategory = categories.find { it.id == currentNews.categoryId }
            selectedLanguage = languages.find { it.id == currentNews.languageId }

            viewModel.getKeywordsForArticle(currentNews.id).collect { loadedKeywords ->
                keywords = loadedKeywords
            }
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is NewsUiState.Success -> {
                snackbarHostState.showSnackbar((uiState as NewsUiState.Success).message)
                delay(1500)
                navController.navigateUp()
            }
            is NewsUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as NewsUiState.Error).message)
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
                text = "Редагування новини",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.Black
                )
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Текст новини") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.Black
                )
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL зображення") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.Black
                )
            )

            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категорія") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = languageDropdownExpanded,
                onExpandedChange = { languageDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedLanguage?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Мова") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageDropdownExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = languageDropdownExpanded,
                    onDismissRequest = { languageDropdownExpanded = false }
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language.name) },
                            onClick = {
                                selectedLanguage = language
                                languageDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "Ключові слова",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            KeywordsInput(
                keywords = keywords,
                onKeywordsChange = { newKeywords -> keywords = newKeywords },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    if (title.isBlank() || content.isBlank() || selectedCategory == null || selectedLanguage == null) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Заповніть усі обов'язкові поля")
                        }
                        return@Button
                    }

                    viewModel.updateNews(
                        newsId = newsId,
                        title = title,
                        content = content,
                        imageUrl = imageUrl,
                        categoryId = selectedCategory!!.id,
                        languageId = selectedLanguage!!.id,
                        keywords = keywords
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OnlyNewsGreen
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Зберегти", color = Color.Black)
            }
        }
    }
}