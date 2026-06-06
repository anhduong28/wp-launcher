package com.windowsphonelauncher.startscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

object StartScreenShellTestTags {
    const val Root = "start-screen-shell-root"
}

@Composable
fun StartScreenShellScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag(StartScreenShellTestTags.Root),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeContent)
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Start Screen shell",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "Tile Grid placeholder",
                color = Color(0xFFD8D8D8),
                style = MaterialTheme.typography.bodyMedium,
            )
            ShellPlaceholderGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun ShellPlaceholderGrid(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(3) { rowIndex ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(4) { columnIndex ->
                    ShellPlaceholderCell(
                        emphasized = (rowIndex + columnIndex) % 3 == 0,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ShellPlaceholderCell(
    emphasized: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (emphasized) Color(0xFF0078D7) else Color(0xFF3A3A3A)
    Box(
        modifier = modifier
            .border(width = 1.dp, color = borderColor),
    )
}

@Preview(showBackground = true)
@Composable
private fun StartScreenShellScreenPreview() {
    MaterialTheme {
        StartScreenShellScreen()
    }
}
