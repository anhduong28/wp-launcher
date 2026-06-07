package com.windowsphonelauncher.startscreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class StartScreenShellScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun shellShowsSeededTileGridWithoutPlaceholderCopy() {
        composeRule.setContent {
            MaterialTheme {
                StartScreenShellScreen()
            }
        }

        composeRule.onNodeWithTag(StartScreenShellTestTags.Root).assertIsDisplayed()
        composeRule.onNodeWithTag(StartScreenShellTestTags.Grid).assertIsDisplayed()
        composeRule.onNodeWithTag(StartScreenShellTestTags.tile("weather")).assertIsDisplayed()
        composeRule.onNodeWithTag(StartScreenShellTestTags.tile("phone")).assertIsDisplayed()
        composeRule.onNodeWithTag(StartScreenShellTestTags.tile("messages")).assertIsDisplayed()
        composeRule.onNodeWithTag(StartScreenShellTestTags.tile("calendar")).assertIsDisplayed()
        composeRule.onAllNodesWithText("Start Screen shell").assertCountEquals(0)
        composeRule.onAllNodesWithText("Tile Grid placeholder").assertCountEquals(0)
        composeRule.onAllNodesWithText("Story 1.4 will replace this surface with the Start Screen shell.")
            .assertCountEquals(0)
    }
}
