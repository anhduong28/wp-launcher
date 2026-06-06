package com.windowsphonelauncher

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
                onboardingState = OnboardingState(step = OnboardingStep.Preview),
                onUseAsDefaultLauncher = {},
                onTryAgain = {},
                onContinuePreview = {},
            )
        }

        composeRule.onNodeWithTag(StartScreenShellTestTags.Root).assertIsDisplayed()
    }
}
