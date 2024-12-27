package com.example.courseworkmobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "keyword_subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class KeywordSubscription(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val keyword: String
)