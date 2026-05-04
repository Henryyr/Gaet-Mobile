package com.example.gaetdriver.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.deviceDataStore: DataStore<Preferences> by preferencesDataStore(name = "device_settings")

/**
 * Manager to handle device-specific settings like theme preferences.
 */
class DeviceManager(private val context: Context) {

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode") // "light", "dark", "system"
    }

    suspend fun saveThemeMode(mode: String) {
        context.deviceDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    val themeMode: Flow<String> = context.deviceDataStore.data.map { preferences ->
        preferences[THEME_MODE_KEY] ?: "system"
    }
}
