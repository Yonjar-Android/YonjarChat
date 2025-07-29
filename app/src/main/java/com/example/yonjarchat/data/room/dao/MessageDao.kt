package com.example.yonjarchat.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yonjarchat.data.room.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("""
    SELECT * FROM messages 
    WHERE chatId = :chatId 
    ORDER BY timestamp DESC 
    LIMIT :limit OFFSET :offset
""")
    suspend fun getMessagesPaginated(chatId: String, limit: Int, offset: Int): List<MessageEntity>


}