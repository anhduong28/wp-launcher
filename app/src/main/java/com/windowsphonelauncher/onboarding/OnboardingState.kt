package com.windowsphonelauncher.onboarding

import androidx.compose.runtime.saveable.Saver

data class OnboardingState(
    val step: OnboardingStep = OnboardingStep.Welcome,
)

enum class OnboardingStep {
    Welcome,
    RequestingDefaultLauncher,
    PreviewExplanation,
    Preview,
}

sealed interface OnboardingAction {
    data object UseAsDefaultLauncher : OnboardingAction
    data object TryAgain : OnboardingAction
    data object ContinuePreview : OnboardingAction

    data class DefaultLauncherFlowReturned(
        val isDefaultLauncher: Boolean,
    ) : OnboardingAction
}

fun reduceOnboarding(
    state: OnboardingState,
    action: OnboardingAction,
): OnboardingState =
    when (action) {
        OnboardingAction.UseAsDefaultLauncher,
        OnboardingAction.TryAgain,
        -> state.copy(step = OnboardingStep.RequestingDefaultLauncher)

        OnboardingAction.ContinuePreview -> state.copy(step = OnboardingStep.Preview)

        is OnboardingAction.DefaultLauncherFlowReturned -> {
            if (action.isDefaultLauncher) {
                state.copy(step = OnboardingStep.Preview)
            } else {
                state.copy(step = OnboardingStep.PreviewExplanation)
            }
        }
    }

val OnboardingStateSaver: Saver<OnboardingState, String> = Saver(
    save = { it.step.name },
    restore = { savedStep -> OnboardingState(step = OnboardingStep.valueOf(savedStep)) },
)
