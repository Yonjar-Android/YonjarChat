package com.example.yonjarchat.utils

object ImageHelper {
    fun isImageUrl(content: String): Boolean {
        return content.matches(Regex("https?://.*\\.(png|jpg|jpeg|gif|bmp)"))
    }
}