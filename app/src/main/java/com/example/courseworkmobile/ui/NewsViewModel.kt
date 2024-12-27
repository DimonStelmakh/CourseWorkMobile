package com.example.courseworkmobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.courseworkmobile.data.local.ArticleKeyword
import com.example.courseworkmobile.data.local.ArticleKeywordDao
import com.example.courseworkmobile.data.local.CategoryDao
import com.example.courseworkmobile.data.local.FavoriteDao
import com.example.courseworkmobile.data.local.FavoriteEntity
import com.example.courseworkmobile.data.local.KeywordSubscription
import com.example.courseworkmobile.data.local.KeywordSubscriptionDao
import com.example.courseworkmobile.data.local.LanguageDao
import com.example.courseworkmobile.data.local.NewsEntity
import com.example.courseworkmobile.data.local.NewsDao
import com.example.courseworkmobile.data.local.NotificationDao
import com.example.courseworkmobile.data.local.NotificationEntity
import com.example.courseworkmobile.data.local.UserDao

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject


data class NewsState(
    val news: List<NewsEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class NewsUiState {
    data object Initial : NewsUiState()
    data class Success(val message: String) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

@HiltViewModel
class NewsViewModel @Inject constructor(
    languageDao: LanguageDao,
    private val newsDao: NewsDao,
    private val categoryDao: CategoryDao,
    private val favoriteDao: FavoriteDao,
    private val notificationDao: NotificationDao,
    private val articleKeywordDao: ArticleKeywordDao,
    private val keywordSubscriptionDao: KeywordSubscriptionDao,
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(NewsState(news = emptyList()))
    val state = _state.asStateFlow()

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Initial)
    val uiState = _uiState.asStateFlow()

    private val _userNews = MutableStateFlow<List<NewsEntity>>(emptyList())
    val userNews = _userNews.asStateFlow()

    val categories = categoryDao.getAllCategories()
    val languages = languageDao.getAllLanguages()

    init {
        getAllNews()
        loadUserNews()
    }

    suspend fun getCategoryName(categoryId: Int): String {
        return categoryDao.getCategoryNameById(categoryId) ?: "Unknown Category"
    }

    private fun getAllNews() {
        viewModelScope.launch {
            newsDao.getAllNews()
                .collect { news ->
                    _state.update { it.copy(news = news) }
                }
        }
    }

    private fun loadUserNews() {
        viewModelScope.launch {
            try {
                val username = userPreferences.getLoggedInUser()
                val currentUser = username?.let { userDao.getUserByUsername(it) }
                if (currentUser != null) {
                    _userNews.value = newsDao.getNewsByAuthor("${currentUser.firstname} ${currentUser.surname}")
                }
            } catch (e: Exception) {
                _uiState.value = NewsUiState.Error("Помилка завантаження новин")
            }
        }
    }

    fun searchNews(
        query: String,
        categoryId: Int? = null,
        languageId: Int? = null
    ) {
        viewModelScope.launch {
            newsDao.getFilteredNews(
                categoryId = categoryId,
                languageId = languageId,
                searchQuery = query.takeIf { it.isNotBlank() }
            ).collect { news ->
                _state.update { it.copy(news = news) }
            }
        }
    }

    fun getKeywordsForArticle(articleId: Int): Flow<List<String>> =
        articleKeywordDao.getKeywordsForArticle(articleId)

    fun createNews(
        title: String,
        content: String,
        imageUrl: String,
        categoryId: Int,
        languageId: Int,
        keywords: List<String>
    ) {
        viewModelScope.launch {
            try {
                val username = userPreferences.getLoggedInUser()
                val currentUser = username?.let { userDao.getUserByUsername(it) }

                if (currentUser != null) {
                    val news = NewsEntity(
                        title = title,
                        content = content,
                        imageUrl = imageUrl.ifBlank { null },
                        categoryId = categoryId,
                        languageId = languageId,
                        author = "${currentUser.firstname} ${currentUser.surname}",
                        timestamp = System.currentTimeMillis(),
                        views = 0
                    )
                    val newsId: Int = newsDao.insertNews(news).toInt()

                    val articleKeywords = keywords.map {
                        ArticleKeyword(articleId = newsId, keyword = it)
                    }
                    articleKeywordDao.insertKeywords(articleKeywords)

                    notifyAuthor(title, currentUser.id)
                    notifySubscribedUsers(title, keywords)

                    _uiState.value = NewsUiState.Success("Новину успішно створено")
                    loadUserNews()
                }
            } catch (e: Exception) {
                _uiState.value = NewsUiState.Error("Помилка створення новини: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun notifyAuthor(newsTitle: String, userId: Int) {
        val notification = NotificationEntity(
            message = "Вашу статтю \"${newsTitle}\" успішно опубліковано!",
            timestamp = System.currentTimeMillis(),
            isRead = false,
            recipientId = userId
        )
        notificationDao.insertNotification(notification)
    }

    private suspend fun notifySubscribedUsers(newsTitle: String, keywords: List<String>) {
        val subscribedUsers = keywordSubscriptionDao.getUsersSubscribedToKeywords(keywords)
        subscribedUsers.forEach { userId ->
            notificationDao.insertNotification(
                NotificationEntity(
                    message = "Нова стаття \"$newsTitle\" містить ключові слова, що вас цікавлять",
                    timestamp = System.currentTimeMillis(),
                    recipientId = userId
                )
            )
        }
    }

    fun updateNews(
        newsId: Int,
        title: String,
        content: String,
        imageUrl: String,
        categoryId: Int,
        languageId: Int,
        keywords: List<String>
    ) {
        viewModelScope.launch {
            try {
                val existingNews = newsDao.getNewsById(newsId)
                existingNews?.let { news ->
                    val updatedNews = news.copy(
                        title = title,
                        content = content,
                        imageUrl = imageUrl.ifBlank { null },
                        categoryId = categoryId,
                        languageId = languageId
                    )
                    newsDao.updateNews(updatedNews)

                    articleKeywordDao.deleteKeywordsForArticle(newsId)
                    val articleKeywords = keywords.map {
                        ArticleKeyword(articleId = newsId, keyword = it)
                    }
                    articleKeywordDao.insertKeywords(articleKeywords)

                    _uiState.value = NewsUiState.Success("Новину успішно оновлено")
                    loadUserNews()
                }
            } catch (e: Exception) {
                _uiState.value = NewsUiState.Error("Помилка оновлення новини: ${e.localizedMessage}")
            }
        }
    }

    fun deleteNews(newsId: Int) {
        viewModelScope.launch {
            try {
                newsDao.deleteNewsById(newsId)
                _uiState.value = NewsUiState.Success("Новину видалено")
                loadUserNews()
            } catch (e: Exception) {
                _uiState.value = NewsUiState.Error("Помилка видалення новини: ${e.localizedMessage}")
            }
        }
    }

    fun getNewsById(id: Int) = newsDao.getNewsByIdFlow(id)

    fun incrementViews(newsId: Int) {
        viewModelScope.launch {
            try {
                newsDao.incrementViews(newsId)
            } catch (e: Exception) {
                // pass
            }
        }
    }

    fun isNewsFavorite(newsId: Int) = flow {
        val username = userPreferences.getLoggedInUser()
        val userId = username?.let { userDao.getUserByUsername(it)?.id }
        if (userId != null) {
            favoriteDao.isNewsFavorite(userId, newsId).collect { emit(it) }
        } else {
            emit(false)
        }
    }

    fun toggleFavorite(newsId: Int) {
        viewModelScope.launch {

            val username = userPreferences.getLoggedInUser()
            val userId = username?.let { userDao.getUserByUsername(it)?.id }
            if (userId != null) {
                val isFavorite = favoriteDao.isNewsFavorite(userId, newsId).first()
                if (isFavorite) {
                    favoriteDao.removeFromFavorites(userId, newsId)
                } else {
                    favoriteDao.addToFavorites(FavoriteEntity(userId = userId, newsId = newsId))
                }
            }
            else {
                _uiState.value = NewsUiState.Error("Для того, щоб вподобати новину, потрібно увійти")
            }
        }
    }

    fun getUserKeywordSubscriptions(): Flow<List<String>> = flow {
        val username = userPreferences.getLoggedInUser()
        val userId = username?.let { userDao.getUserByUsername(it)?.id }
        if (userId != null) {
            keywordSubscriptionDao.getSubscribedKeywords(userId).collect {
                emit(it)
            }
        } else {
            emit(emptyList())
        }
    }

    suspend fun updateKeywordSubscriptions(keywords: List<String>) {
        val username = userPreferences.getLoggedInUser()
        val userId = username?.let { userDao.getUserByUsername(it)?.id }
        if (userId != null) {
            keywordSubscriptionDao.deleteAllUserSubscriptions(userId)

            keywords.forEach { keyword ->
                keywordSubscriptionDao.subscribeToKeyword(
                    KeywordSubscription(
                        userId = userId,
                        keyword = keyword
                    )
                )
            }
        }
    }
}