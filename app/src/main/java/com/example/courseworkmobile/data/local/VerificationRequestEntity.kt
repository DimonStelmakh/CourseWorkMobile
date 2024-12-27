package com.example.courseworkmobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "verification_requests")
data class VerificationRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val decisionMadeAt: Long? = null,
    val status: VerificationStatus = VerificationStatus.PENDING
)

enum class VerificationStatus {
    PENDING,
    APPROVED,
    REJECTED
}