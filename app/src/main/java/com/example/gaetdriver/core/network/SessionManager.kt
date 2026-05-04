package com.example.gaetdriver.core.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

/**
 * Manager to handle local user session using DataStore.
 */
class SessionManager(private val context: Context) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("email")
    }

    suspend fun saveSession(userId: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[EMAIL_KEY] = email
        }
    }

    val userSession: Flow<UserSession?> = context.dataStore.data.map { preferences ->
        val userId = preferences[USER_ID_KEY]
        val email = preferences[EMAIL_KEY]
        if (userId != null && (email != null)) {
            UserSession(userId, email)
        } else {
            null
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class UserSession(
    val userId: String,
    val email: String,
)
