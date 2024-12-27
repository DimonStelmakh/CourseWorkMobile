package com.example.courseworkmobile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND newsId = :newsId)")
    fun isNewsFavorite(userId: Int, newsId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToFavorites(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND newsId = :newsId")
    suspend fun removeFromFavorites(userId: Int, newsId: Int)
}