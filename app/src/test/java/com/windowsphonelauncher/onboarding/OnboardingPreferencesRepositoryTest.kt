package com.windowsphonelauncher.onboarding

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class OnboardingPreferencesRepositoryTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun missingFirstRunStateRoutesToOnboarding() = runBlocking {
        val repository = newRepository()

        assertEquals(FirstRunRoute.Onboarding, repository.firstRunRoute.first())
    }

    @Test
    fun previewSelectionPersistsStartScreenRouteAcrossDataStoreInstances() = runBlocking {
        val preferencesFile = File(temporaryFolder.root, "preview.preferences_pb")
        val firstDataStoreJob = SupervisorJob()
        val dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + firstDataStoreJob),
            produceFile = { preferencesFile },
        )
        val firstRepository = OnboardingPreferencesRepository(dataStore)
        firstRepository.markPreviewSelected()
        firstRepository.firstRunRoute.first()
        firstDataStoreJob.cancelAndJoin()

        val secondDataStoreJob = SupervisorJob()
        val secondDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + secondDataStoreJob),
            produceFile = { preferencesFile },
        )
        val secondRepository = OnboardingPreferencesRepository(secondDataStore)

        try {
            assertEquals(FirstRunRoute.StartScreenPreview, secondRepository.firstRunRoute.first())
        } finally {
            secondDataStoreJob.cancelAndJoin()
        }
    }

    @Test
    fun defaultLauncherAcceptedPersistsStartScreenRoute() = runBlocking {
        val repository = newRepository()

        repository.markDefaultLauncherAccepted()

        assertEquals(FirstRunRoute.StartScreenPreview, repository.firstRunRoute.first())
    }

    private fun newRepository(): OnboardingPreferencesRepository {
        val dataStore = PreferenceDataStoreFactory.create(
            scope = dataStoreScope,
            produceFile = { File(temporaryFolder.root, "settings.preferences_pb") },
        )
        return OnboardingPreferencesRepository(dataStore)
    }
}
