package com.example.yonjarchat.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

class ResourceProvider(
    @ApplicationContext private val context: Context
) {
    fun getString(resId: Int): String = context.getString(resId)
}