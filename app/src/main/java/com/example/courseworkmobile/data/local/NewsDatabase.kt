package com.example.courseworkmobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        NewsEntity::class,
        UserEntity::class,
        NotificationEntity::class,
        VerificationRequestEntity::class,
        CategoryEntity::class,
        LanguageEntity::class,
        FavoriteEntity::class,
        ArticleKeyword::class,
        KeywordSubscription::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract val newsDao: NewsDao
    abstract val userDao: UserDao
    abstract val notificationDao: NotificationDao
    abstract val verificationRequestDao: VerificationRequestDao
    abstract val categoryDao: CategoryDao
    abstract val languageDao: LanguageDao
    abstract val favoriteDao: FavoriteDao
    abstract val articleKeywordDao: ArticleKeywordDao
    abstract val keywordSubscriptionDao: KeywordSubscriptionDao
}