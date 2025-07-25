package com.example.yonjarchat

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferences(private val context: Context) {

    companion object{
        private val USER_ID = stringPreferencesKey("user_id")
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    suspend fun clearUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
        }
    }

    // 💡 Agregado: flujo para saber si el darkTheme está activo
    val darkTheme: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_THEME] ?: false // false por defecto
    }

    // 💡 Agregado: guardar el valor del darkTheme
    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME] = enabled
        }
    }

}