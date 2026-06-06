package com.windowsphonelauncher.startscreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class StartScreenShellScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun shellShowsInternalPlaceholderWithoutOnboardingCopy() {
        composeRule.setContent {
            MaterialTheme {
                StartScreenShellScreen()
            }
        }

        composeRule.onNodeWithTag(StartScreenShellTestTags.Root).assertIsDisplayed()
        composeRule.onNodeWithText("Start Screen shell").assertIsDisplayed()
        composeRule.onNodeWithText("Tile Grid placeholder").assertIsDisplayed()
        composeRule.onAllNodesWithText("Story 1.4 will replace this surface with the Start Screen shell.")
            .assertCountEquals(0)
    }
}
