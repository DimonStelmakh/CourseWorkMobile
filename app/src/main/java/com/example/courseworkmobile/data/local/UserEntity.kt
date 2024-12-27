package com.example.courseworkmobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val firstname: String,
    val surname: String,
    val phone: String,
    val email: String,
    val password: String,
    val isVerified: Boolean = false,
    val isAdmin: Boolean = false
)