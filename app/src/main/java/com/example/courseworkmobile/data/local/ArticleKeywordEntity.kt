package com.example.courseworkmobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "article_keywords",
    foreignKeys = [
        ForeignKey(
            entity = NewsEntity::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ArticleKeyword(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val articleId: Int,
    val keyword: String
)
