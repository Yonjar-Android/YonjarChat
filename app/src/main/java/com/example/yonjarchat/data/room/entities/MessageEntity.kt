package com.example.yonjarchat.data.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["chatId"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ], indices = [Index("chatId")]
)
data class MessageEntity(
    @PrimaryKey val messageId: String,
    val chatId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long
)
