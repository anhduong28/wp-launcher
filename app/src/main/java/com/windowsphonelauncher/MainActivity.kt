package com.windowsphonelauncher

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.windowsphonelauncher.onboarding.DefaultHomeGateway
import com.windowsphonelauncher.onboarding.OnboardingAction
import com.windowsphonelauncher.onboarding.OnboardingStep
import com.windowsphonelauncher.onboarding.OnboardingScreen
import com.windowsphonelauncher.onboarding.OnboardingState
import com.windowsphonelauncher.onboarding.OnboardingStateSaver
import com.windowsphonelauncher.onboarding.reduceOnboarding
import com.windowsphonelauncher.startscreen.StartScreenShellScreen

class MainActivity : ComponentActivity() {
    private lateinit var defaultHomeGateway: DefaultHomeGateway

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        defaultHomeGateway = DefaultHomeGateway(this)
        setContent {
            var onboardingState by rememberSaveable(stateSaver = OnboardingStateSaver) {
                mutableStateOf(OnboardingState())
            }
            val defaultHomeRequest = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
            ) {
                onboardingState = reduceOnboarding(
                    onboardingState,
                    OnboardingAction.DefaultLauncherFlowReturned(
                        isDefaultLauncher = defaultHomeGateway.isCurrentHomeApp(),
                    ),
                )
            }

            fun dispatch(action: OnboardingAction) {
                onboardingState = reduceOnboarding(onboardingState, action)
            }

            fun launchDefaultHomeRequest(intents: List<Intent>) {
                val nextIntent = intents.firstOrNull()
                    ?: run {
                        dispatch(
                            OnboardingAction.DefaultLauncherFlowReturned(
                                isDefaultLauncher = false,
                            ),
                        )
                        return
                    }

                try {
                    defaultHomeRequest.launch(nextIntent)
                } catch (_: ActivityNotFoundException) {
                    launchDefaultHomeRequest(intents.drop(1))
                }
            }

            fun requestDefaultHome() {
                dispatch(OnboardingAction.UseAsDefaultLauncher)
                launchDefaultHomeRequest(defaultHomeGateway.createRequestIntents())
            }

            WindowsPhoneLauncherAppContent(
                onboardingState = onboardingState,
                onUseAsDefaultLauncher = ::requestDefaultHome,
                onTryAgain = ::requestDefaultHome,
                onContinuePreview = {
                    dispatch(OnboardingAction.ContinuePreview)
                },
            )
        }
    }
}

@Composable
fun WindowsPhoneLauncherAppContent(
    onboardingState: OnboardingState,
    onUseAsDefaultLauncher: () -> Unit,
    onTryAgain: () -> Unit,
    onContinuePreview: () -> Unit,
) {
    MaterialTheme {
        when (onboardingState.step) {
            OnboardingStep.Preview -> StartScreenShellScreen()
            else -> OnboardingScreen(
                state = onboardingState,
                onUseAsDefaultLauncher = onUseAsDefaultLauncher,
                onTryAgain = onTryAgain,
                onContinuePreview = onContinuePreview,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WindowsPhoneLauncherAppContentPreview() {
    WindowsPhoneLauncherAppContent(
        onboardingState = OnboardingState(),
        onUseAsDefaultLauncher = {},
        onTryAgain = {},
        onContinuePreview = {},
    )
}
