---
baseline_commit: 48c27c6
---

# Story 1.5: Persist First-Run and Preview State

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a returning user,
I want the launcher to remember whether onboarding was completed or preview mode was selected,
so that I do not repeat first-run setup unnecessarily.

## Acceptance Criteria

1. Given the user completes default-launcher onboarding successfully, when the app is closed and reopened, then the app routes directly to the Start Screen shell instead of restarting onboarding.
2. Given the user selects `Continue preview`, when the app is closed and reopened, then the app routes directly to the Start Screen shell in preview mode instead of restarting onboarding.
3. Given the user cancels or returns from Android launcher selection without making WindowsPhone Launcher the home app, when preview explanation is shown, then the user can still choose `Try again` or `Continue preview`; cancellation alone must not persist onboarding completion.
4. Given no persisted first-run state exists, when the app opens, then onboarding starts at the welcome step.
5. The persisted state is stored locally using the architecture-approved lightweight settings approach: Preferences DataStore, not Room.
6. This story does not introduce Room schema, Tile layout persistence, app discovery, App List, package visibility declarations, sensitive permissions, notification listener declarations, Calendar/location permission requests, WorkManager, or Live Tile behavior.
7. The debug build, debug unit tests, debug instrumentation tests where available, and debug lint pass after the persistence change.

## Tasks / Subtasks

- [x] Add Preferences DataStore as the lightweight settings persistence foundation (AC: 5, 6)
  - [x] Add `androidx.datastore:datastore-preferences` to the Gradle version catalog and app dependencies. Use the current stable Jetpack DataStore release, `1.2.1`, unless a newer stable version is already selected in project configuration at implementation time.
  - [x] Keep DataStore dependency declarations centralized in `gradle/libs.versions.toml`; do not hard-code versions in `app/build.gradle.kts`.
  - [x] Do not add Room, WorkManager, serialization, Proto DataStore, or app-discovery dependencies in this story.
- [x] Create an onboarding/settings persistence boundary (AC: 1-6)
  - [x] Add a small model under `onboarding` or `datacore` representing first-run routing state, for example `OnboardingDestination` or `FirstRunState`.
  - [x] Add a repository/data source that owns Preferences DataStore access for onboarding flags. Keep UI from reading DataStore directly.
  - [x] Persist enough information to distinguish first-run incomplete from completed/preview mode. A boolean such as `has_completed_first_run` is sufficient only if preview/default-home distinction is not needed by current UI; if stored, keep any route enum stable and explicit.
  - [x] Treat DataStore read failures conservatively: default to onboarding welcome, not a crash or a false completed state.
  - [x] Do not persist UI-facing copy strings as logic.
- [x] Route app startup from persisted first-run state (AC: 1, 2, 4)
  - [x] Replace the current unconditional `OnboardingState()` initialization in `MainActivity` with a startup state that waits for persisted first-run state.
  - [x] If state is incomplete or missing, show onboarding welcome.
  - [x] If state says default onboarding completed or preview selected, render `StartScreenShellScreen()`.
  - [x] Keep navigation minimal and launcher-like; no navigation library is required for this story.
  - [x] Keep Story 1.4 edge-to-edge/system inset handling intact.
- [x] Persist only completed/default or explicit preview choices (AC: 1-4)
  - [x] When `DefaultLauncherFlowReturned(isDefaultLauncher = true)` transitions to `OnboardingStep.Preview`, persist first-run completion before or as part of rendering the Start Screen shell.
  - [x] When `Continue preview` transitions to `OnboardingStep.Preview`, persist preview/first-run completion.
  - [x] When default-home flow returns with `isDefaultLauncher = false`, show `PreviewExplanation` but do not persist completion.
  - [x] `Try again` must re-enter the same default-home request path and must not clear or corrupt persisted state.
- [x] Add focused tests for persistence routing and scope boundaries (AC: 1-7)
  - [x] Add unit tests around pure mapping/reducer logic for incomplete, completed, preview-selected, and default-home-canceled states.
  - [x] Add DataStore-backed repository tests using a temporary test file or test scope so persisted state survives repository recreation.
  - [x] Add instrumentation or Compose tests proving `WindowsPhoneLauncherAppContent` / app route displays onboarding for incomplete state and Start Screen shell for completed/preview state. Prefer testable state-holder seams over sleeping/waiting on real I/O.
  - [x] Keep or extend manifest regression tests asserting no `<uses-permission>`, no `<queries>`, and no notification listener service declarations.
  - [x] If DataStore dependency download is not available in the local Gradle cache, record the failure and rerun with approved network access rather than replacing DataStore with SharedPreferences.
- [x] Run validation (AC: 7)
  - [x] Run `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon`.
  - [x] If an emulator/device is available and instrumentation execution is practical, run `.\gradlew.bat :app:connectedDebugAndroidTest --no-daemon`.
  - [x] Record commands and results in the Dev Agent Record completion notes.

### Review Findings

- [x] [Review][Patch] Persist completion before rendering the Start Screen shell [app/src/main/java/com/windowsphonelauncher/MainActivity.kt:84]
- [x] [Review][Patch] Handle corrupted/non-IOException DataStore reads conservatively [app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingPreferencesRepository.kt:29]
- [x] [Review][Patch] Recreate the DataStore boundary in restart persistence tests [app/src/test/java/com/windowsphonelauncher/onboarding/OnboardingPreferencesRepositoryTest.kt:37]

## Dev Notes

### Current Repository State

- Current git `HEAD` is `48c27c6` (`Done item 1-3 of epic1`). Story 1.4 is completed in the working tree and sprint tracking but does not appear committed in `git log` at story creation time.
- `sprint-status.yaml` marks stories 1.1 through 1.4 `done`; `1-5-persist-first-run-and-preview-state` is the next backlog story.
- `MainActivity.kt` currently initializes route state with `rememberSaveable(OnboardingStateSaver) { mutableStateOf(OnboardingState()) }`. This means app process recreation can restore state, but app restart after process death or reinstall-level fresh state is not persisted locally.
- `OnboardingState.kt` has the current reducer:
  - `UseAsDefaultLauncher` / `TryAgain` -> `RequestingDefaultLauncher`
  - `DefaultLauncherFlowReturned(true)` -> `Preview`
  - `DefaultLauncherFlowReturned(false)` -> `PreviewExplanation`
  - `ContinuePreview` -> `Preview`
- `WindowsPhoneLauncherAppContent` is public and testable after Story 1.4. It renders `StartScreenShellScreen()` for `OnboardingStep.Preview`; other states render `OnboardingScreen`.
- `OnboardingScreen` intentionally rejects direct rendering of `OnboardingStep.Preview`; route preview through app-level content.
- Story 1.4 added edge-to-edge support in `MainActivity`, safe drawing insets in onboarding, and safe-content/scroll handling in the Start Screen shell. Preserve these fixes.
- No DataStore dependency currently exists in `gradle/libs.versions.toml` or `app/build.gradle.kts`.

### Previous Story Intelligence

- Story 1.4 code review found and fixed three route/inset pitfalls: global edge-to-edge must be matched by safe insets on every visible destination, `OnboardingScreen(Preview)` must not silently blank, and route behavior needs direct test coverage.
- Do not reintroduce a silent blank screen while adding startup loading/persistence state.
- Story 1.4 testing pattern added `WindowsPhoneLauncherAppContentTest` for app-level route assertions. Reuse or extend that seam for persisted routing.
- Connected instrumentation tests were not run because `adb devices` reported no attached devices. Continue to build androidTest APKs and record device availability honestly.

### Architecture Requirements

- DataStore stores small preferences and flags, including onboarding completion. Room stores structured launcher data such as pinned tiles, tile layout, app metadata cache, and live tile snapshots. [Source: _bmad-output/planning-artifacts/architecture.md#Data Architecture]
- DataStore is the source of truth for preferences and lightweight configuration. [Source: _bmad-output/planning-artifacts/architecture.md#Data Architecture]
- Room owns tile layout and pinned tile state; DataStore owns preferences, onboarding flags, animation settings, accent color, and weather setup flags. UI must not read Room or DataStore directly. [Source: _bmad-output/planning-artifacts/architecture.md#Data Boundaries]
- UI composables emit actions only. ViewModels own screen state. Repositories own data access. [Source: _bmad-output/planning-artifacts/architecture.md#Component Boundaries]
- Repositories expose Flow-based data. ViewModels expose StateFlow UI state and SharedFlow one-shot effects. Compose sends explicit action callbacks. [Source: _bmad-output/planning-artifacts/architecture.md#Integration Points]
- UI-facing text stays localized and is never persisted as the source of truth for logic. [Source: _bmad-output/planning-artifacts/architecture.md#Format Patterns]

### UX and Product Requirements

- Default-launcher cancellation remains recoverable through preview flow. Cancellation should show preview explanation, not permanently skip onboarding. [Source: _bmad-output/planning-artifacts/epics.md#Story 1.5]
- The launcher shell must remain usable if optional permissions have not been granted. [Source: _bmad-output/planning-artifacts/epics.md#Story 1.4]
- Start Screen must remain a simple shell in this story. Do not introduce the real Tile Grid, default Tile seed data, App List, settings surface, or Live Tile permission flows.

### Latest DataStore Notes

- Jetpack DataStore stores data asynchronously, consistently, and transactionally, and is intended as a replacement for SharedPreferences.
- The latest stable Jetpack DataStore release listed by Android Developers is `1.2.1` as of 2026-05-06.
- For Preferences DataStore in Kotlin Gradle, Android Developers list `implementation("androidx.datastore:datastore-preferences:1.2.1")`.
- DataStore exposes preference data through Kotlin `Flow`; map stored preferences into a domain model instead of exposing the raw `Preferences` object to UI.
- Sources:
  - Android Developers DataStore release notes: https://developer.android.com/jetpack/androidx/releases/datastore
  - Android Developers DataStore guide: https://developer.android.com/topic/libraries/architecture/datastore

### Scope Boundaries

- Do not use `SharedPreferences` as a shortcut; the architecture-approved approach is Preferences DataStore.
- Do not add Room schema or Tile layout persistence; Epic 2 owns Tile layout persistence.
- Do not add broad package visibility declarations, permissions, notification listener services, Calendar/location permission requests, WorkManager, app discovery, App List, or Live Tile behavior.
- Do not persist whether this app is currently Android's default home as a source of truth. Android home status must still be checked through platform APIs when needed; this story only persists first-run routing/preview choice.
- Do not block the launcher shell behind optional permissions or default-home status after preview has been explicitly selected.

### Suggested Implementation Shape

- Gradle:
  - Add `androidxDatastore = "1.2.1"` under `[versions]`.
  - Add `androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "androidxDatastore" }`.
  - Add `implementation(libs.androidx.datastore.preferences)` in `app/build.gradle.kts`.
- Persistence:
  - Add an onboarding persistence model such as:
    - `enum class FirstRunRoute { Onboarding, StartScreenPreview }`, or
    - `data class FirstRunState(val hasCompletedFirstRun: Boolean)`.
  - Add a repository such as `OnboardingPreferencesRepository` that exposes `Flow<OnboardingPersistenceState>` and write methods like `markDefaultLauncherAccepted()` / `markPreviewSelected()`.
  - Use a stable DataStore file name, for example `launcher_settings`.
- Main activity/state:
  - Add a small app-level state holder or ViewModel if needed. Avoid putting DataStore reads directly in composables.
  - Use a loading/startup state that does not show the welcome screen before persisted state is read, otherwise returning users can see onboarding flicker before the shell.
  - Persist on explicit completion/preview actions. If persistence write fails, keep UI behavior conservative and record/log if a logging pattern exists.
- Tests:
  - Keep reducer tests pure.
  - Add repository tests using test temp files and coroutine test rules if adding coroutine test dependency is necessary and scoped.
  - Add app route tests for incomplete vs persisted-completed state without relying on real device persistence timing.

### References

- `_bmad-output/planning-artifacts/epics.md#Story 1.5: Persist First-Run and Preview State`
- `_bmad-output/planning-artifacts/architecture.md#Data Architecture`
- `_bmad-output/planning-artifacts/architecture.md#Data Boundaries`
- `_bmad-output/planning-artifacts/architecture.md#Component Boundaries`
- `_bmad-output/implementation-artifacts/1-4-initial-start-screen-shell.md`
- `app/src/main/java/com/windowsphonelauncher/MainActivity.kt`
- `app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingState.kt`
- `app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingScreen.kt`
- `app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenShellScreen.kt`
- `gradle/libs.versions.toml`
- `app/build.gradle.kts`

## Dev Agent Record

### Agent Model Used

Codex GPT-5

### Debug Log References

- Initial unit validation: `.\gradlew.bat :app:testDebugUnitTest --no-daemon` failed because DataStore tests used pre-created temp files; fixed tests to provide non-existent temp paths to `PreferenceDataStoreFactory`.
- Unit validation after fix: `.\gradlew.bat :app:testDebugUnitTest --no-daemon` passed.
- Full validation: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon` passed.
- Device check: `adb devices` returned no attached devices, so `connectedDebugAndroidTest` was not run.
- Code review patch validation on 2026-06-07: `.\gradlew.bat :app:testDebugUnitTest --no-daemon` passed.
- Code review patch validation on 2026-06-07: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon` passed.
- Device check on 2026-06-07: SDK `platform-tools\adb.exe devices` returned no attached devices, so `connectedDebugAndroidTest` was not run.

### Completion Notes List

- Story context created by BMad create-story workflow on 2026-06-06.
- Ultimate context engine analysis completed - comprehensive developer guide created.
- Added Preferences DataStore dependency `androidx.datastore:datastore-preferences:1.2.1` through the version catalog.
- Added `OnboardingPreferencesRepository` with a stable `launcher_settings` Preferences DataStore, `FirstRunRoute`, conservative IOException fallback, and explicit mark methods for default-launcher acceptance and preview selection.
- Reworked `MainActivity` startup routing to collect persisted first-run route before showing onboarding, preserving a neutral loading surface and routing persisted completion/preview to the Start Screen shell.
- Persisted only explicit completion paths: successful default-home return and `Continue preview`; default-home cancellation remains recoverable and does not persist completion.
- Added unit tests for DataStore-backed persistence and first-run route mapping, and updated Compose route tests for welcome vs Start Screen preview routing.
- Validation passed for debug assemble, debug unit tests, debug androidTest APK assemble, and debug lint. No connected device/emulator was attached for `connectedDebugAndroidTest`.
- Code review fixes ensure completion is persisted before rendering the Start Screen shell, corrupted DataStore files fall back to empty preferences, and persistence tests recreate DataStore from the same file.

### File List

- `_bmad-output/implementation-artifacts/1-5-persist-first-run-and-preview-state.md`
- `_bmad-output/implementation-artifacts/sprint-status.yaml`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `app/src/main/java/com/windowsphonelauncher/MainActivity.kt`
- `app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingPreferencesRepository.kt`
- `app/src/test/java/com/windowsphonelauncher/WindowsPhoneLauncherAppStateTest.kt`
- `app/src/test/java/com/windowsphonelauncher/onboarding/OnboardingPreferencesRepositoryTest.kt`
- `app/src/test/java/com/windowsphonelauncher/onboarding/OnboardingReducerTest.kt`
- `app/src/androidTest/java/com/windowsphonelauncher/WindowsPhoneLauncherAppContentTest.kt`

## Change Log

- 2026-06-06: Added DataStore-backed first-run persistence and startup routing.
- 2026-06-06: Added unit and Compose route coverage for persisted onboarding/start screen behavior.
- 2026-06-06: Validated debug build, unit tests, androidTest APK build, and lint.
