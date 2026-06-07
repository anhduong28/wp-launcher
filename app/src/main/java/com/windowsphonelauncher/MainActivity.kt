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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.windowsphonelauncher.onboarding.DefaultHomeGateway
import com.windowsphonelauncher.onboarding.FirstRunRoute
import com.windowsphonelauncher.onboarding.OnboardingAction
import com.windowsphonelauncher.onboarding.OnboardingScreen
import com.windowsphonelauncher.onboarding.OnboardingPreferencesRepository
import com.windowsphonelauncher.onboarding.OnboardingState
import com.windowsphonelauncher.onboarding.OnboardingStep
import com.windowsphonelauncher.onboarding.launcherSettingsDataStore
import com.windowsphonelauncher.onboarding.reduceOnboarding
import com.windowsphonelauncher.startscreen.StartScreenShellScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var defaultHomeGateway: DefaultHomeGateway
    private lateinit var onboardingPreferencesRepository: OnboardingPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        defaultHomeGateway = DefaultHomeGateway(this)
        onboardingPreferencesRepository = OnboardingPreferencesRepository(
            applicationContext.launcherSettingsDataStore,
        )
        setContent {
            var appState by remember {
                mutableStateOf<WindowsPhoneLauncherAppState>(WindowsPhoneLauncherAppState.Loading)
            }
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(onboardingPreferencesRepository) {
                appState = onboardingPreferencesRepository.firstRunRoute.first().toAppState()
            }

            fun currentOnboardingState(): OnboardingState {
                val currentState = appState
                return if (currentState is WindowsPhoneLauncherAppState.Ready) {
                    currentState.onboardingState
                } else {
                    OnboardingState()
                }
            }

            val defaultHomeRequest = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
            ) {
                val isDefaultLauncher = defaultHomeGateway.isCurrentHomeApp()
                val nextState = reduceOnboarding(
                    currentOnboardingState(),
                    OnboardingAction.DefaultLauncherFlowReturned(
                        isDefaultLauncher = isDefaultLauncher,
                    ),
                )
                if (isDefaultLauncher) {
                    appState = WindowsPhoneLauncherAppState.Loading
                    coroutineScope.launch {
                        appState = try {
                            onboardingPreferencesRepository.markDefaultLauncherAccepted()
                            WindowsPhoneLauncherAppState.Ready(nextState)
                        } catch (_: Exception) {
                            WindowsPhoneLauncherAppState.Ready(OnboardingState(OnboardingStep.PreviewExplanation))
                        }
                    }
                } else {
                    appState = WindowsPhoneLauncherAppState.Ready(nextState)
                }
            }

            fun dispatch(action: OnboardingAction) {
                appState = WindowsPhoneLauncherAppState.Ready(
                    reduceOnboarding(currentOnboardingState(), action),
                )
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
                appState = appState,
                onUseAsDefaultLauncher = ::requestDefaultHome,
                onTryAgain = ::requestDefaultHome,
                onContinuePreview = {
                    val nextState = reduceOnboarding(
                        currentOnboardingState(),
                        OnboardingAction.ContinuePreview,
                    )
                    appState = WindowsPhoneLauncherAppState.Loading
                    coroutineScope.launch {
                        appState = try {
                            onboardingPreferencesRepository.markPreviewSelected()
                            WindowsPhoneLauncherAppState.Ready(nextState)
                        } catch (_: Exception) {
                            WindowsPhoneLauncherAppState.Ready(OnboardingState(OnboardingStep.PreviewExplanation))
                        }
                    }
                },
            )
        }
    }
}

sealed interface WindowsPhoneLauncherAppState {
    data object Loading : WindowsPhoneLauncherAppState

    data class Ready(
        val onboardingState: OnboardingState,
    ) : WindowsPhoneLauncherAppState
}

fun FirstRunRoute.toAppState(): WindowsPhoneLauncherAppState =
    when (this) {
        FirstRunRoute.Onboarding -> WindowsPhoneLauncherAppState.Ready(OnboardingState())
        FirstRunRoute.StartScreenPreview -> WindowsPhoneLauncherAppState.Ready(
            OnboardingState(step = OnboardingStep.Preview),
        )
    }

@Composable
fun WindowsPhoneLauncherAppContent(
    appState: WindowsPhoneLauncherAppState,
    onUseAsDefaultLauncher: () -> Unit,
    onTryAgain: () -> Unit,
    onContinuePreview: () -> Unit,
) {
    MaterialTheme {
        when (appState) {
            WindowsPhoneLauncherAppState.Loading -> LoadingSurface()
            is WindowsPhoneLauncherAppState.Ready -> when (appState.onboardingState.step) {
                OnboardingStep.Preview -> StartScreenShellScreen()
                else -> OnboardingScreen(
                    state = appState.onboardingState,
                    onUseAsDefaultLauncher = onUseAsDefaultLauncher,
                    onTryAgain = onTryAgain,
                    onContinuePreview = onContinuePreview,
                )
            }
        }
    }
}

@Composable
private fun LoadingSurface() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    )
}

@Preview(showBackground = true)
@Composable
private fun WindowsPhoneLauncherAppContentPreview() {
    WindowsPhoneLauncherAppContent(
        appState = WindowsPhoneLauncherAppState.Ready(OnboardingState()),
        onUseAsDefaultLauncher = {},
        onTryAgain = {},
        onContinuePreview = {},
    )
}
