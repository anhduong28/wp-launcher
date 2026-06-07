package com.windowsphonelauncher

import com.windowsphonelauncher.onboarding.FirstRunRoute
import com.windowsphonelauncher.onboarding.OnboardingStep
import org.junit.Assert.assertEquals
import org.junit.Test

class WindowsPhoneLauncherAppStateTest {
    @Test
    fun missingOrIncompleteFirstRunStateRoutesToWelcomeOnboarding() {
        val appState = FirstRunRoute.Onboarding.toAppState()

        assertEquals(
            OnboardingStep.Welcome,
            (appState as WindowsPhoneLauncherAppState.Ready).onboardingState.step,
        )
    }

    @Test
    fun completedFirstRunStateRoutesToStartScreenPreviewStep() {
        val appState = FirstRunRoute.StartScreenPreview.toAppState()

        assertEquals(
            OnboardingStep.Preview,
            (appState as WindowsPhoneLauncherAppState.Ready).onboardingState.step,
        )
    }
}
