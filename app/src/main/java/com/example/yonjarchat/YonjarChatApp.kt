package com.example.yonjarchat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.messaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YonjarChatApp: Application(){
    override fun onCreate() {
        super.onCreate()

    }

}