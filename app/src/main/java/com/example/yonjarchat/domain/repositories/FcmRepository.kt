package com.example.yonjarchat.domain.repositories

interface FcmRepository {
    fun getCurrentToken(onComplete: (String?) -> Unit)

    fun saveTokenToDatabase(userId: String, token: String)
}