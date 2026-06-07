package com.windowsphonelauncher.onboarding

import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingReducerTest {
    @Test
    fun initialStateStartsOnWelcome() {
        assertEquals(OnboardingStep.Welcome, OnboardingState().step)
    }

    @Test
    fun useAsDefaultRequestsDefaultLauncherFlow() {
        val state = reduceOnboarding(
            state = OnboardingState(),
            action = OnboardingAction.UseAsDefaultLauncher,
        )

        assertEquals(OnboardingStep.RequestingDefaultLauncher, state.step)
    }

    @Test
    fun returnWithoutDefaultShowsPreviewExplanation() {
        val state = reduceOnboarding(
            state = OnboardingState(step = OnboardingStep.RequestingDefaultLauncher),
            action = OnboardingAction.DefaultLauncherFlowReturned(isDefaultLauncher = false),
        )

        assertEquals(OnboardingStep.PreviewExplanation, state.step)
    }

    @Test
    fun returnAsDefaultContinuesToStartScreenPreviewStep() {
        val state = reduceOnboarding(
            state = OnboardingState(step = OnboardingStep.RequestingDefaultLauncher),
            action = OnboardingAction.DefaultLauncherFlowReturned(isDefaultLauncher = true),
        )

        assertEquals(OnboardingStep.Preview, state.step)
    }

    @Test
    fun tryAgainReRequestsDefaultLauncherFlow() {
        val state = reduceOnboarding(
            state = OnboardingState(step = OnboardingStep.PreviewExplanation),
            action = OnboardingAction.TryAgain,
        )

        assertEquals(OnboardingStep.RequestingDefaultLauncher, state.step)
    }

    @Test
    fun continuePreviewMovesToPreviewState() {
        val state = reduceOnboarding(
            state = OnboardingState(step = OnboardingStep.PreviewExplanation),
            action = OnboardingAction.ContinuePreview,
        )

        assertEquals(OnboardingStep.Preview, state.step)
    }
}
