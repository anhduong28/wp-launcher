package com.windowsphonelauncher.onboarding

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class OnboardingScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun welcomeStepShowsDefaultLauncherAction() {
        composeRule.setContent {
            MaterialTheme {
                OnboardingScreen(
                    state = OnboardingState(),
                    onUseAsDefaultLauncher = {},
                    onTryAgain = {},
                    onContinuePreview = {},
                )
            }
        }

        composeRule.onNodeWithText("WindowsPhone Launcher").assertIsDisplayed()
        composeRule.onNodeWithText("Use as default launcher").assertIsDisplayed()
    }

    @Test
    fun previewExplanationShowsTryAgainAndContinuePreviewActions() {
        composeRule.setContent {
            MaterialTheme {
                OnboardingScreen(
                    state = OnboardingState(step = OnboardingStep.PreviewExplanation),
                    onUseAsDefaultLauncher = {},
                    onTryAgain = {},
                    onContinuePreview = {},
                )
            }
        }

        composeRule.onNodeWithText("Continue in preview mode").assertIsDisplayed()
        composeRule.onNodeWithText("Try again").assertIsDisplayed()
        composeRule.onNodeWithText("Continue preview").assertIsDisplayed()
    }

}
