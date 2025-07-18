package com.example.yonjarchat.domain.models

data class User(
    val uid: String,
    val username: String,
    val email: String,
    val imageUrl: String = "",
    val profileImageDeleteUrl: String = "",
    val lastMessage: String = "",
)
