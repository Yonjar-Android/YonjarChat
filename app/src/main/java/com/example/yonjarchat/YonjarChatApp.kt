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
        Firebase.messaging.token.addOnCompleteListener {
            if (!it.isSuccessful) {
                println("Error al obtener el token")
                return@addOnCompleteListener
            }
                val token = it.result
                println("Token: $token")
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                "channel_id",
                "channel_name",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.description = "channel_description"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

        }
    }
}