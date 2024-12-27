package com.example.courseworkmobile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordSubscriptionDao {
    @Query("SELECT keyword FROM keyword_subscriptions WHERE userId = :userId")
    fun getSubscribedKeywords(userId: Int): Flow<List<String>>

    @Insert
    suspend fun subscribeToKeyword(subscription: KeywordSubscription)

    @Query("DELETE FROM keyword_subscriptions WHERE userId = :userId AND keyword = :keyword")
    suspend fun unsubscribeFromKeyword(userId: Int, keyword: String)

    @Query("DELETE FROM keyword_subscriptions WHERE userId = :userId")
    suspend fun deleteAllUserSubscriptions(userId: Int)

    @Query("SELECT DISTINCT userId FROM keyword_subscriptions WHERE keyword IN (:keywords)")
    suspend fun getUsersSubscribedToKeywords(keywords: List<String>): List<Int>


}