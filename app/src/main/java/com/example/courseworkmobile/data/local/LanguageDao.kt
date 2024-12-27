package com.example.courseworkmobile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageDao {
    @Query("SELECT * FROM languages ORDER BY name ASC")
    fun getAllLanguages(): Flow<List<LanguageEntity>>

    @Insert
    suspend fun insertLanguage(language: LanguageEntity)

    @Query("SELECT * FROM languages WHERE id = :id")
    suspend fun getLanguageById(id: Int): LanguageEntity?
}