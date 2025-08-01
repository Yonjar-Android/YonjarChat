package com.example.yonjarchat.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yonjarchat.data.room.dao.ChatDao
import com.example.yonjarchat.data.room.dao.MessageDao
import com.example.yonjarchat.data.room.dao.UserDao
import com.example.yonjarchat.data.room.entities.ChatEntity
import com.example.yonjarchat.data.room.entities.MessageEntity
import com.example.yonjarchat.data.room.entities.UserEntity

@Database(entities = [UserEntity::class, ChatEntity::class, MessageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}