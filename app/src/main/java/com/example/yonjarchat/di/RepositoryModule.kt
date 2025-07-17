package com.example.yonjarchat.di

import android.content.Context
import com.example.yonjarchat.UserPreferences
import com.example.yonjarchat.data.repositories.FcmRepositoryImp
import com.example.yonjarchat.data.repositories.FirebaseRepositoryImp
import com.example.yonjarchat.domain.repositories.FcmRepository
import com.example.yonjarchat.domain.repositories.FirebaseRepository
import com.example.yonjarchat.utils.ResourceProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        resourceProvider: ResourceProvider
    ): FirebaseRepository {
        return FirebaseRepositoryImp(
            firebaseAuth, firestore, resourceProvider
        )
    }

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProvider {
        return ResourceProvider(context)
    }

    @Singleton
    @Provides
    fun provideFcmRepository(
        firestore: FirebaseFirestore
    ): FcmRepository = FcmRepositoryImp(
        firestore
    )

    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

}