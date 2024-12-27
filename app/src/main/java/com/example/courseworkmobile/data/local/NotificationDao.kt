package com.example.courseworkmobile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("""
        SELECT * FROM notifications 
        WHERE recipientId = :userId 
        ORDER BY isRead ASC, timestamp DESC
    """)
    fun getNotificationsForUser(userId: Int): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientId = :userId")
    suspend fun markAllAsRead(userId: Int)

    @Query("SELECT COUNT(*) FROM notifications WHERE recipientId = :userId AND isRead = 0")
    fun getUnreadCount(userId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM notifications WHERE recipientId = :userId AND isRead = 0")
    suspend fun getUnreadCountOnce(userId: Int): Int

    @Insert
    suspend fun insertNotification(notification: NotificationEntity)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)
}