---
baseline_commit: 48c27c6
---

# Story 1.4: Initial Start Screen Shell

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a user,
I want to land on a simple Windows Phone-style Start Screen shell after onboarding,
so that I can see the launcher's core visual direction before full Tile behavior exists.

## Acceptance Criteria

1. Given onboarding is completed or preview mode is selected, when the app navigates to the Start Screen, then the app shows a dedicated Start Screen shell surface instead of onboarding placeholder copy.
2. The Start Screen displays the classic black background (`#000000`) and preserves a Windows Phone-style visual direction without Material cards, rounded Tile surfaces, decorative gradients, shadows, or header chrome.
3. The screen is Android phone portrait-first and accounts for status bars, navigation bars, display cutouts, and gesture navigation areas so content is not obscured and no future interaction target is placed under system gesture insets.
4. No header, persistent search bar, global settings shortcut, toolbar, or top app chrome appears above the future grid area.
5. Temporary placeholder content is clearly internal to the shell and does not imply completed Tile Grid, app discovery, Tile launch, pinning, editing, live data, or persistence behavior.
6. The shell remains usable when optional permissions have not been granted and this story does not request Notification access, Calendar permission, Location permission, SMS/Phone/Call Log permissions, package visibility queries, Room, DataStore, WorkManager, App List, app discovery, Tile Grid layout engine, Edit Mode, or Live Tile behavior.
7. The debug build, debug unit tests, debug instrumentation tests where available, and debug lint pass after the shell change.

## Tasks / Subtasks

- [x] Create a dedicated Start Screen shell surface in the `startscreen` package (AC: 1, 2, 4, 5)
  - [x] Add production UI under `app/src/main/java/com/windowsphonelauncher/startscreen/`, for example `StartScreenShellScreen.kt`.
  - [x] Keep the shell surface separate from `onboarding`; do not leave the post-onboarding destination implemented as `PreviewPlaceholderScreen` in `onboarding`.
  - [x] Use a full-screen classic black canvas. Draw the background behind system bars; inset readable/placeholder content away from unsafe areas.
  - [x] Use square, restrained visual treatment. Do not introduce Material cards, rounded tiles, shadows, gradients, wallpaper, or decorative chrome.
  - [x] If showing placeholder blocks, make them non-interactive and label/copy them as shell placeholder content. Do not use app names, app icons, live counts, weather, clock, SMS, phone, calendar, or settings labels that imply real Tile behavior.
- [x] Route onboarding completion and preview mode to the Start Screen shell (AC: 1, 6)
  - [x] Update `WindowsPhoneLauncherAppContent` in `MainActivity.kt` so `OnboardingStep.Preview` renders the new Start Screen shell.
  - [x] Preserve the Story 1.3 reducer semantics: default-home success and `Continue preview` both reach `OnboardingStep.Preview`.
  - [x] Do not add persistence in this story; `rememberSaveable` in-memory/saved-instance state remains acceptable until Story 1.5.
  - [x] Do not change the default-home request gateway or manifest launcher/home declarations unless a test needs a non-behavioral import/path update.
- [x] Handle edge-to-edge/system inset behavior for the shell (AC: 2, 3)
  - [x] Use the current AndroidX Activity/Compose stack already in the project. If needed, call `enableEdgeToEdge()` from `MainActivity` before `setContent` so behavior is consistent below Android 15 while target SDK 36 already enforces edge-to-edge on Android 15+.
  - [x] In Compose, use window inset APIs such as `WindowInsets.safeDrawing`, `windowInsetsPadding`, or equivalent so shell content avoids status bars, navigation bars, cutouts, and gesture regions.
  - [x] Keep the black shell background full-bleed; only content/placeholder layout should respect safe drawing insets.
  - [x] Do not hide system bars or enter immersive mode for this story.
- [x] Add focused tests for routing, shell visuals, and scope boundaries (AC: 1-7)
  - [x] Add/extend Compose instrumentation tests under `app/src/androidTest/java/com/windowsphonelauncher/startscreen/` or the closest existing package to verify the shell appears with stable text/test tags and no onboarding placeholder copy remains in the post-onboarding destination.
  - [x] Update existing onboarding UI tests that currently assert `PreviewPlaceholderScreen()` copy; they should assert the new shell destination or move shell-specific assertions into Start Screen tests.
  - [x] Add unit or instrumentation coverage that `OnboardingStep.Preview` routes to the shell from `WindowsPhoneLauncherAppContent` if feasible without making private APIs public solely for tests. Prefer testing through visible Compose output.
  - [x] Extend `PackageMarkerTest` only as needed to keep scope regressions guarded: no `<uses-permission>`, no `<queries>`, no notification listener service declarations.
  - [x] Avoid tests that depend on exact pixel color unless the project already has a reliable screenshot/pixel harness. Prefer semantic tags/text plus modifier-level code review for background and insets.
- [x] Run validation (AC: 7)
  - [x] Run `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon`.
  - [x] If an emulator/device is available and instrumentation execution is practical, run `.\gradlew.bat :app:connectedDebugAndroidTest --no-daemon`.
  - [x] Record commands and results in the Dev Agent Record completion notes.

### Review Findings

- [x] [Review][Patch] Edge-to-edge is enabled globally but onboarding content does not apply safe drawing insets [app/src/main/java/com/windowsphonelauncher/MainActivity.kt:34]
- [x] [Review][Patch] Start Screen shell uses safe drawing insets but does not account for gesture navigation side insets [app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenShellScreen.kt:44]
- [x] [Review][Patch] `OnboardingScreen` silently renders a blank screen for `OnboardingStep.Preview` if called directly [app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingScreen.kt:63]
- [x] [Review][Patch] Tests cover the shell composable directly but not the app route from `OnboardingStep.Preview` to the shell [app/src/androidTest/java/com/windowsphonelauncher/startscreen/StartScreenShellScreenTest.kt:18]
- [x] [Review][Patch] Fixed-height shell placeholder can clip in compact-height or large-font configurations [app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenShellScreen.kt:41]

## Dev Notes

### Current Repository State

- Current baseline commit is `48c27c6` (`Done item 1-3 of epic1`).
- `MainActivity.kt` currently owns the simple route state using `rememberSaveable(OnboardingStateSaver)` and renders `PreviewPlaceholderScreen()` when `onboardingState.step == OnboardingStep.Preview`.
- `OnboardingState.kt` already defines `OnboardingStep.Preview`; Story 1.3 uses it as the post-default-home or continue-preview destination. Keep that state name unless there is a strong reason to rename it across tests.
- `PreviewPlaceholderScreen()` currently lives in `app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingScreen.kt` and displays copy saying Story 1.4 will replace it. This story should replace that destination with Start Screen shell UI in `startscreen`.
- `app/src/main/java/com/windowsphonelauncher/startscreen/PackageMarker.kt` exists as a placeholder package marker. The new screen should live beside it.
- Existing tests include `OnboardingReducerTest`, `DefaultHomeRequestSelectorTest`, `PackageMarkerTest`, and `OnboardingScreenTest`. `OnboardingScreenTest.previewPlaceholderShowsSeparateSurfaceCopy` will need to change because the placeholder should no longer be the post-onboarding UI.

### Previous Story Intelligence

- Story 1.3 added a platform boundary (`DefaultHomeGateway`) and pure reducer tests. Reuse those boundaries; do not move default-home system logic into Start Screen UI.
- Story 1.3 review fixed lifecycle safety by storing onboarding state with `rememberSaveable`. Do not regress to unsaved local state for the route.
- Story 1.3 review explicitly required `Continue preview` to leave onboarding for a separate preview/placeholder surface. Story 1.4 is the replacement for that surface.
- Story 1.3 maintained strict scope: no permissions, no package visibility, no notification listener service, no App List, no Room/DataStore, no Tile Grid, and no Live Tile behavior. Keep the same scope firewall.

### Architecture Requirements

- Compose is the default UI layer. Major surfaces, including Start Screen and onboarding, should be screen-level composables in their feature packages. [Source: _bmad-output/planning-artifacts/architecture.md#Frontend Architecture]
- Navigation is intentionally minimal and launcher-like, not a deep app router. For this story, the existing `when (onboardingState.step)` routing is sufficient. [Source: _bmad-output/planning-artifacts/architecture.md#Frontend Architecture]
- UI composables emit actions only; state remains immutable and explicit. This story likely needs no new ViewModel because there is no persisted data, app list, tile grid, or repository yet. [Source: _bmad-output/planning-artifacts/architecture.md#Implementation Patterns & Consistency Rules]
- Feature-first package organization is required. Start Screen UI belongs under `startscreen`; onboarding UI remains under `onboarding`; shared primitives move to `core` only when reused. [Source: _bmad-output/planning-artifacts/architecture.md#File Organization Patterns]
- The launcher shell must remain usable offline and when live sources/permissions are unavailable. [Source: _bmad-output/planning-artifacts/architecture.md#Infrastructure & Deployment]

### UX and Visual Requirements

- Classic black is the default canvas (`#000000`). The Start Screen has no header, no persistent search bar, and no settings shortcut above the grid. Tiles begin near the top with minimal safe-edge breathing room. [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md#Layout & Spacing]
- The product identity is sharp, dense, and square. Avoid Material card surfaces, rounded widgets, gradients, shadows, decorative chrome, beige themes, and one-note gradients. [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md#Brand & Style]
- MVP is Android phone portrait-first. Landscape/tablet are deferred, but layout must account for status/navigation bars and Android gesture navigation. [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md#Responsive & Platform]
- Do not add the real first-run tile set from the UX flow yet. Weather, Clock, SMS, Phone, Calendar, Settings, App List, edit controls, and live motion belong to later stories/epics. [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md#Key Flows]

### Latest Android/Compose Notes

- This project targets SDK 36. Android documentation notes that edge-to-edge is enforced for apps targeting SDK 35+ on Android 15+, so the shell must intentionally handle system bars instead of assuming legacy opaque bars.
- Android's edge-to-edge guidance recommends drawing backgrounds and scrolling content under system bars while insetting important content and controls so they are not obscured. It also warns not to place tap/drag targets under system gesture insets.
- Compose inset guidance recommends handling insets directly with Compose APIs; `WindowInsets.safeDrawing` or related APIs are appropriate for content that must avoid system UI and cutouts.

### Technical Stack and Versions

- Android Gradle Plugin: `8.13.2`
- Kotlin and Compose Compiler plugin: `2.3.21`
- Compile SDK / target SDK: `36`; min SDK: `23`
- Compose BOM: `2026.05.00`
- AndroidX Activity Compose: `1.12.0`
- AndroidX Core KTX: `1.17.0`
- AndroidX Lifecycle Runtime KTX: `2.10.0`
- Tests: JUnit4, AndroidX Test, Espresso, Compose UI test JUnit4
- Source: `gradle/libs.versions.toml` and `app/build.gradle.kts`

### Scope Boundaries

- Do not implement Tile Grid layout, tile sizes, app launching, app discovery, pin/unpin, edit mode, live tile adapters, weather setup, settings, Room, DataStore, or WorkManager.
- Do not request or declare Notification access, Calendar permission, Location permission, SMS/Phone/Call Log permissions, or broad package visibility queries.
- Do not add a global header, search bar, or settings shortcut above the Start Screen area.
- Do not persist onboarding completion or preview state; Story 1.5 owns local persistence.
- Do not present placeholder UI as user-configurable or launchable Tiles.

### Suggested Implementation Shape

- `StartScreenShellScreen(modifier: Modifier = Modifier)`:
  - `Box(Modifier.fillMaxSize().background(Color.Black))`
  - Inner content uses safe drawing/window inset padding plus the existing `8.dp` screen-edge token from UX.
  - Placeholder copy should be concise, for example `Start Screen shell` and `Tile Grid placeholder`. Avoid "Weather", "Clock", "Messages", app names, counts, and launch verbs.
  - Use test tags or stable text for instrumentation tests.
- `MainActivity.kt`:
  - Import the new Start Screen shell.
  - Replace `PreviewPlaceholderScreen()` with `StartScreenShellScreen()`.
  - Consider `enableEdgeToEdge()` before `setContent`; verify system bar icon contrast remains light on black.
- `OnboardingScreen.kt`:
  - Remove `PreviewPlaceholderScreen()` if no longer used, or leave it only if a test/preview still needs it. Prefer removing stale copy that says Story 1.4 is pending.

### References

- `_bmad-output/planning-artifacts/epics.md#Story 1.4: Initial Start Screen Shell`
- `_bmad-output/planning-artifacts/architecture.md#Frontend Architecture`
- `_bmad-output/planning-artifacts/architecture.md#Implementation Patterns & Consistency Rules`
- `_bmad-output/planning-artifacts/architecture.md#File Organization Patterns`
- `_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md#Layout & Spacing`
- `_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md#Responsive & Platform`
- `app/src/main/java/com/windowsphonelauncher/MainActivity.kt`
- `app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingScreen.kt`
- `app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingState.kt`
- `app/src/test/java/com/windowsphonelauncher/onboarding/OnboardingReducerTest.kt`
- `app/src/androidTest/java/com/windowsphonelauncher/onboarding/OnboardingScreenTest.kt`
- Android Developers: Edge-to-edge design, https://developer.android.com/design/ui/mobile/guides/layout-and-content/edge-to-edge
- Android Developers: Set up edge-to-edge in Compose, https://developer.android.com/develop/ui/compose/system/setup-e2e
- Android Developers: About window insets in Compose, https://developer.android.com/develop/ui/compose/layouts/insets

## Dev Agent Record

### Agent Model Used

Codex GPT-5

### Debug Log References

- RED phase: `.\gradlew.bat :app:compileDebugAndroidTestKotlin --no-daemon` failed as expected before implementation because `StartScreenShellScreen` and `StartScreenShellTestTags` did not exist.
- Validation: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon` passed.
- Device check: `adb devices` returned no attached devices, so `connectedDebugAndroidTest` was not run.

### Completion Notes List

- Story context created by BMad create-story workflow on 2026-06-06.
- Ultimate context engine analysis completed - comprehensive developer guide created.
- Implemented a dedicated `startscreen` shell surface with full-screen black background, safe drawing insets, square placeholder grid, and stable Compose test tag.
- Routed `OnboardingStep.Preview` to the new Start Screen shell while preserving Story 1.3 reducer behavior and in-memory/saved-instance routing state.
- Removed stale onboarding preview placeholder UI so post-onboarding no longer displays Story 1.4 pending copy.
- Added Compose instrumentation coverage for the shell and updated onboarding tests to stop asserting the removed placeholder.
- Validation passed for debug assemble, debug unit tests, debug androidTest APK assemble, and debug lint. No connected device/emulator was attached for `connectedDebugAndroidTest`.
- Resolved all 5 code review patch findings: onboarding safe drawing insets, Start Screen safe content/gesture inset handling, explicit preview route contract, app-route instrumentation coverage, and scrollable shell content for compact-height/large-font configurations.
- Post-review validation passed for `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon`. `adb devices` still reported no attached devices, so `connectedDebugAndroidTest` was not run.

### File List

- `_bmad-output/implementation-artifacts/1-4-initial-start-screen-shell.md`
- `_bmad-output/implementation-artifacts/sprint-status.yaml`
- `app/src/main/java/com/windowsphonelauncher/MainActivity.kt`
- `app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingScreen.kt`
- `app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenShellScreen.kt`
- `app/src/androidTest/java/com/windowsphonelauncher/onboarding/OnboardingScreenTest.kt`
- `app/src/androidTest/java/com/windowsphonelauncher/WindowsPhoneLauncherAppContentTest.kt`
- `app/src/androidTest/java/com/windowsphonelauncher/startscreen/StartScreenShellScreenTest.kt`

## Change Log

- 2026-06-06: Implemented initial Start Screen shell and routed onboarding preview/default-home completion to it.
- 2026-06-06: Added shell instrumentation coverage and updated onboarding UI tests.
- 2026-06-06: Validated debug build, unit tests, androidTest APK build, and lint.
- 2026-06-06: Resolved all code review patch findings and marked story done.
