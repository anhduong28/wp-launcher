package com.windowsphonelauncher

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.windowsphonelauncher.onboarding.OnboardingState
import com.windowsphonelauncher.onboarding.OnboardingStep
import com.windowsphonelauncher.startscreen.StartScreenShellTestTags
import org.junit.Rule
import org.junit.Test

class WindowsPhoneLauncherAppContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun previewStepRoutesToStartScreenShell() {
        composeRule.setContent {
            WindowsPhoneLauncherAppContent(
                appState = WindowsPhoneLauncherAppState.Ready(
                    OnboardingState(step = OnboardingStep.Preview),
                ),
                onUseAsDefaultLauncher = {},
                onTryAgain = {},
                onContinuePreview = {},
            )
        }

        composeRule.onNodeWithTag(StartScreenShellTestTags.Root).assertIsDisplayed()
    }

    @Test
    fun welcomeStepRoutesToOnboarding() {
        composeRule.setContent {
            WindowsPhoneLauncherAppContent(
                appState = WindowsPhoneLauncherAppState.Ready(OnboardingState()),
                onUseAsDefaultLauncher = {},
                onTryAgain = {},
                onContinuePreview = {},
            )
        }

        composeRule.onNodeWithText("WindowsPhone Launcher").assertIsDisplayed()
        composeRule.onNodeWithText("Use as default launcher").assertIsDisplayed()
    }
}
