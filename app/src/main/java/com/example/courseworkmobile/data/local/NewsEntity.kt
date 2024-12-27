package com.example.courseworkmobile.data.local

import androidx.room.*

@Entity(
    tableName = "news",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = LanguageEntity::class,
            parentColumns = ["id"],
            childColumns = ["languageId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val timestamp: Long,
    val author: String,
    val views: Int = 0,
    val categoryId: Int,
    val languageId: Int
)

data class NewsWithCategory(
    @Embedded val news: NewsEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)