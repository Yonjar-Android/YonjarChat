package com.example.yonjarchat.di

import android.content.Context
import androidx.room.Room
import com.example.yonjarchat.data.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Provides
    fun provideChatDao(appDatabase: AppDatabase) = appDatabase.chatDao()

    @Provides
    fun provideMessageDao(appDatabase: AppDatabase) = appDatabase.messageDao()


}