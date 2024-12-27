package com.example.courseworkmobile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleKeywordDao {
    @Query("SELECT keyword FROM article_keywords WHERE articleId = :articleId")
    fun getKeywordsForArticle(articleId: Int): Flow<List<String>>

    @Insert
    suspend fun insertKeywords(keywords: List<ArticleKeyword>)

    @Query("DELETE FROM article_keywords WHERE articleId = :articleId")
    suspend fun deleteKeywordsForArticle(articleId: Int)
}