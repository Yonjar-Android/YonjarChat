package com.example.yonjarchat.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val chatId: String,
    val user1Id: String,
    val user2Id: String,
    val lastMessage: String,
    val timestamp: Long
)
