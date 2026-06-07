package com.windowsphonelauncher.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val LauncherSettingsDataStoreName = "launcher_settings"

val Context.launcherSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = LauncherSettingsDataStoreName,
    corruptionHandler = ReplaceFileCorruptionHandler {
        emptyPreferences()
    },
)

enum class FirstRunRoute {
    Onboarding,
    StartScreenPreview,
}

class OnboardingPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val firstRunRoute: Flow<FirstRunRoute> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            if (preferences[HasCompletedFirstRunKey] == true) {
                FirstRunRoute.StartScreenPreview
            } else {
                FirstRunRoute.Onboarding
            }
        }

    suspend fun markDefaultLauncherAccepted() {
        markFirstRunCompleted()
    }

    suspend fun markPreviewSelected() {
        markFirstRunCompleted()
    }

    private suspend fun markFirstRunCompleted() {
        dataStore.edit { preferences ->
            preferences[HasCompletedFirstRunKey] = true
        }
    }

    private companion object {
        val HasCompletedFirstRunKey = booleanPreferencesKey("has_completed_first_run")
    }
}
