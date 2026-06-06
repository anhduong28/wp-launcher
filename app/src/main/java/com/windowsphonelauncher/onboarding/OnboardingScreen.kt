package com.windowsphonelauncher.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onUseAsDefaultLauncher: () -> Unit,
    onTryAgain: () -> Unit,
    onContinuePreview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    require(state.step != OnboardingStep.Preview) {
        "OnboardingStep.Preview is routed by the app shell, not OnboardingScreen."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        when (state.step) {
            OnboardingStep.Welcome -> OnboardingStepContent(
                title = "WindowsPhone Launcher",
                body = "A WP-style living Start Screen for Android. Set it as your default launcher when you are ready.",
                primaryAction = "Use as default launcher",
                onPrimaryAction = onUseAsDefaultLauncher,
            )

            OnboardingStep.RequestingDefaultLauncher -> OnboardingStepContent(
                title = "Choose your home app",
                body = "Android will open its launcher selection screen. Choose WindowsPhone Launcher to use it as Home.",
            )

            OnboardingStep.PreviewExplanation -> OnboardingStepContent(
                title = "Continue in preview mode",
                body = "You can try the launcher without changing your Android Home app. You can set it as default later.",
                primaryAction = "Try again",
                onPrimaryAction = onTryAgain,
                secondaryAction = "Continue preview",
                onSecondaryAction = onContinuePreview,
            )

            OnboardingStep.Preview -> error(
                "OnboardingStep.Preview is routed by the app shell, not OnboardingScreen.",
            )
        }
    }
}

@Composable
private fun OnboardingStepContent(
    title: String,
    body: String,
    primaryAction: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryAction: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
) {
    Text(
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Medium,
    )
    Text(
        text = body,
        color = Color(0xFFD8D8D8),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .widthIn(max = 440.dp)
            .padding(top = 16.dp),
    )
    if (primaryAction != null && onPrimaryAction != null) {
        Button(
            onClick = onPrimaryAction,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0078D7),
                contentColor = Color.White,
            ),
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) {
            Text(text = primaryAction)
        }
    }
    if (secondaryAction != null && onSecondaryAction != null) {
        OutlinedButton(
            onClick = onSecondaryAction,
            shape = RectangleShape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) {
            Text(text = secondaryAction)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomePreview() {
    OnboardingScreen(
        state = OnboardingState(),
        onUseAsDefaultLauncher = {},
        onTryAgain = {},
        onContinuePreview = {},
    )
}
