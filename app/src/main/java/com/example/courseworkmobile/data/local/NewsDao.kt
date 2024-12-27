package com.example.courseworkmobile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY timestamp DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE categoryId = :categoryId ORDER BY timestamp DESC")
    fun getNewsByCategory(categoryId: Int): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE languageId = :languageId ORDER BY timestamp DESC")
    fun getNewsByLanguage(languageId: Int): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE author = :author ORDER BY timestamp DESC")
    suspend fun getNewsByAuthor(author: String): List<NewsEntity>

    @Query("SELECT * FROM news WHERE id = :id")
    fun getNewsByIdFlow(id: Int): Flow<NewsEntity>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getNewsById(id: Int): NewsEntity?

    @Update
    suspend fun updateNews(news: NewsEntity)

    @Query("DELETE FROM news WHERE id = :id")
    suspend fun deleteNewsById(id: Int)

    @Query("SELECT * FROM news ORDER BY timestamp DESC")
    fun getAllNewsWithCategory(): Flow<List<NewsWithCategory>>

    @Query("UPDATE news SET views = views + 1 WHERE id = :newsId")
    suspend fun incrementViews(newsId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity): Long

    @Delete
    suspend fun deleteNews(news: NewsEntity)

    @Query("""
        SELECT * FROM news 
        WHERE (:categoryId IS NULL OR categoryId = :categoryId)
        AND (:languageId IS NULL OR languageId = :languageId)
        AND (:searchQuery IS NULL OR title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%')
        ORDER BY timestamp DESC
    """)
    fun getFilteredNews(
        categoryId: Int? = null,
        languageId: Int? = null,
        searchQuery: String? = null
    ): Flow<List<NewsEntity>>
}