package com.example.courseworkmobile.data.local

import androidx.room.*

@Dao
interface VerificationRequestDao {
    @Insert
    suspend fun createRequest(request: VerificationRequestEntity)

    @Query("SELECT * FROM verification_requests WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestRequestForUser(userId: Int): VerificationRequestEntity?

    @Query("""
        SELECT * FROM verification_requests 
        WHERE status = 'PENDING' 
        ORDER BY createdAt DESC
    """)
    suspend fun getPendingRequestsWithUsers(): List<VerificationRequestWithUser>

    @Query("SELECT * FROM verification_requests WHERE id = :requestId")
    suspend fun getRequestById(requestId: Int): VerificationRequestEntity?

    @Update
    suspend fun updateRequest(request: VerificationRequestEntity)
}

data class VerificationRequestWithUser(
    @Embedded val request: VerificationRequestEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity
)