package com.example.yonjarchat.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid:String,
    val username:String,
    val email:String,
    val imageUrl:String?
)
