---
baseline_commit: 730108050cfd38d0ab9890b40a6ee778b53205c1
---

# Story 1.3: First-Run Welcome and Default Launcher Flow

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a first-time user,
I want a simple onboarding path to set the app as my default launcher or continue in preview mode,
so that I can start using the launcher without being blocked.

## Acceptance Criteria

1. Given the user opens the app for the first time, when onboarding starts, then the user sees a plain welcome step describing the WP-style living Start Screen promise.
2. The user can choose `Use as default launcher` to enter Android's default launcher selection flow.
3. If the user cancels or returns from Android launcher selection without making WindowsPhone Launcher the home app, the app shows a preview-mode explanation.
4. The preview-mode state offers `Try again` and `Continue preview` paths.
5. Copy is low-pressure and does not imply optional permissions are required.
6. This story does not introduce broad package visibility declarations, sensitive permissions, notification listener declarations, Calendar permission requests, location permission requests, Room schema, DataStore persistence, App List, app discovery, Tile Grid, or Live Tile behavior.
7. The debug build, debug unit tests, debug instrumentation tests where added, and debug lint pass after the onboarding change.

## Tasks / Subtasks

- [x] Build the first-run onboarding UI in the `onboarding` feature package (AC: 1, 5)
  - [x] Replace the current single placeholder screen in `MainActivity` with a minimal route that starts on onboarding for this story.
  - [x] Add onboarding composables under `app/src/main/java/com/windowsphonelauncher/onboarding/` rather than keeping feature UI in `MainActivity.kt`.
  - [x] Use plain, low-pressure copy. Required button labels: `Use as default launcher`, `Try again`, `Continue preview`. `Skip for now` may be used only for optional permission copy if a passive explanatory placeholder is shown; do not implement permission routing in this story.
  - [x] Keep the visual treatment simple and square: no card-heavy layout, no rounded card surfaces, no decorative gradients, no fake Start Screen Tile Grid.
- [x] Implement default-launcher request routing without forcing or spoofing default status (AC: 2, 3, 4)
  - [x] Create a small platform boundary in `onboarding` or `permissions` that can request the home role/settings and report whether this app is currently the home app.
  - [x] On API 29+, prefer `RoleManager` with `RoleManager.ROLE_HOME` when the role is available and not already held; launch `createRequestRoleIntent(RoleManager.ROLE_HOME)` through Activity Result APIs.
  - [x] When `RoleManager` is unavailable, not usable, or does not resolve, fall back to `Settings.ACTION_HOME_SETTINGS`; if that cannot resolve, fall back to `Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS`.
  - [x] After the system UI returns, check whether WindowsPhone Launcher is the current home app. If not, show the preview-mode explanation instead of looping or nagging.
  - [x] `Try again` must re-enter the same platform request path. `Continue preview` must leave onboarding for an in-app preview/placeholder state that Story 1.4 can replace with the Start Screen shell.
- [x] Preserve strict story scope and privacy boundaries (AC: 5, 6)
  - [x] Do not request notification access, Calendar permission, location permission, SMS/Phone/Call Log permissions, or package visibility queries.
  - [x] Do not add `LauncherApps`, app discovery, App List search, tile pinning, tile layout, weather setup, Room, DataStore, WorkManager, or live tile adapters.
  - [x] Do not persist onboarding completion or preview selection; Story 1.5 owns local persistence. State may be in-memory only for this story.
  - [x] Keep existing manifest launcher/home declarations from Story 1.2 unchanged except if tests need to assert they remain present.
- [x] Add focused tests for onboarding decisions and platform-scope regressions (AC: 1-7)
  - [x] Add unit tests for pure onboarding state transitions: welcome -> request default, canceled/returned-not-home -> preview explanation, try again -> request default, continue preview -> preview state.
  - [x] Add unit tests for the platform request selector where feasible: API/role available chooses `RoleManager`, fallback chooses Home Settings or Default Apps Settings.
  - [x] Extend manifest tests to assert this story still adds no `<uses-permission>`, no `<queries>`, and no notification listener service declarations.
  - [x] If Compose UI instrumentation is added, keep it narrow: verify visible onboarding labels/actions and preview fallback copy using existing Compose test dependencies.
- [x] Run validation (AC: 7)
  - [x] Run `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug --no-daemon` if an emulator/device is available for instrumentation.
  - [x] If no emulator/device is available, run `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon` and record why connected tests were not run.
  - [x] Record commands and results in the Dev Agent Record completion notes.

### Review Findings

- [x] [Review][Patch] Default-home request flow preflights system settings with `resolveActivity`, which can fail on Android 11+ without package visibility queries [app/src/main/java/com/windowsphonelauncher/onboarding/DefaultHomeGateway.kt:16]
- [x] [Review][Patch] Activity-result return path is not lifecycle-safe across rotation or process recreation [app/src/main/java/com/windowsphonelauncher/MainActivity.kt:22]
- [x] [Review][Patch] Continue preview never leaves the onboarding surface into a separate preview placeholder [app/src/main/java/com/windowsphonelauncher/MainActivity.kt:57]

## Dev Notes

### Current Repository State

- Current baseline commit is `730108050cfd38d0ab9890b40a6ee778b53205c1` (`Done item 1-2 of epic1`).
- `MainActivity` currently renders a centered `Text("WindowsPhone Launcher")` placeholder inside Material theme.
- `AndroidManifest.xml` already declares `.MainActivity` as:
  - `android:exported="true"`
  - `android:launchMode="singleTask"`
  - normal development filter: `MAIN` + `LAUNCHER`
  - home filter: `MAIN` + `HOME` + `DEFAULT`
- Required feature package placeholders already exist, including `onboarding` and `permissions`.
- `PackageMarkerTest` already parses `AndroidManifest.xml` and verifies the launcher/home declarations plus absence of `<queries>` and `<uses-permission>`.
- The project uses Kotlin, Compose, Material3, Activity Compose, JUnit4, AndroidX test, and Compose UI test dependencies already declared in `gradle/libs.versions.toml`.

### Story Scope Boundaries

- This story owns the first-run welcome, default-launcher request path, cancellation/return fallback, and preview-mode choice.
- Story 1.4 owns the real Start Screen shell: classic black background, system bar/gesture handling, no header/search/settings shortcut, and placeholder content rules.
- Story 1.5 owns persistence of onboarding completion and preview state using the approved lightweight settings approach.
- Epic 5 owns optional Live Tile permissions: notification listener, Calendar permission, and later permission-state degradation.
- Do not implement default-launcher revoked handling beyond checking on return from the system request; UX notes mention later revoked state, but this story is first-run flow only unless implementation needs a small helper to detect current home status.

### Architecture Guardrails

- Keep Compose as the UI layer for onboarding.
- Keep `MainActivity` thin. It may host Activity Result launchers and platform lifecycle callbacks, but feature UI and state transition logic should live in `onboarding`.
- Prefer a testable split:
  - Pure Kotlin model/state reducer for onboarding steps and user actions.
  - Small Android platform gateway for `RoleManager`, `Settings.ACTION_HOME_SETTINGS`, and current-home detection.
  - Compose UI consumes immutable state and emits explicit actions.
- Do not introduce a ViewModel unless it removes real complexity. If introduced, keep it Android-agnostic and expose immutable state; use one-shot effects for launching system settings/role UI.
- Do not add Room or DataStore dependencies to app code in this story. Existing architecture assigns onboarding persistence to DataStore later, but Story 1.5 owns it.
- User-facing errors should be short and non-blocking. If no settings activity can be resolved, show preview fallback with a calm explanation rather than crashing.

### Android Default-Launcher Technical Notes

- Android recognizes home launchers through `ACTION_MAIN` + `CATEGORY_HOME`; this was implemented in Story 1.2.
- On API 29+, `RoleManager.ROLE_HOME` is the platform role name for the home app. `RoleManager.createRequestRoleIntent(roleName)` returns an intent suitable for prompting the user to grant that role.
- Before launching a role request, check `RoleManager.isRoleAvailable(RoleManager.ROLE_HOME)` and `RoleManager.isRoleHeld(RoleManager.ROLE_HOME)`.
- `Settings.ACTION_HOME_SETTINGS` is available from API 21 and shows Home selection settings when multiple activities satisfy `CATEGORY_HOME`.
- `Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS` is available from API 24 and is an acceptable broader fallback if Home Settings cannot resolve.
- Always resolve settings intents before launching because Android docs note some settings actions may not have a matching Activity on every device.
- Do not claim the app became default until the platform check confirms it. The Android system owns the user's default-home choice.

### UX Requirements

- Welcome step purpose: establish the promise of a WP-style living Start Screen on Android.
- Required flow: Welcome -> Set as default launcher -> optional preview fallback if canceled/returned without default.
- Preview fallback must explain preview mode and offer `Try again` and `Continue preview`.
- Microcopy must be low-pressure. Use examples from UX docs:
  - `Use as default launcher`
  - `Skip for now`
  - Avoid coercive language such as `Activate full experience now!` or copy implying optional permissions are required.
- Onboarding screen should have a clear heading, concise body, one primary action, and secondary path where applicable.
- Accessibility floor: interactive targets at least 48dp; readable text; predictable focus order; no visual-only actions.
- Keep onboarding visually compatible with the product identity: classic black canvas is acceptable, square controls are preferred, but do not build the Tile Grid or Live Tiles in this story.

### Files to Update

- `app/src/main/java/com/windowsphonelauncher/MainActivity.kt`
  - Replace the placeholder entry screen with the onboarding route host.
  - Keep Activity platform responsibilities narrow.
- `app/src/main/java/com/windowsphonelauncher/onboarding/`
  - Add onboarding state, actions, composables, and any pure reducer/helper tests need to exercise.
- `app/src/main/java/com/windowsphonelauncher/permissions/` or `onboarding/`
  - Add the default-home platform gateway only if keeping it separate improves testability.
- `app/src/main/res/values/strings.xml`
  - Add user-facing onboarding strings if the implementation localizes strings through resources. Avoid hardcoding repeated user-facing copy if resources are already the local pattern.
- `app/src/test/java/com/windowsphonelauncher/`
  - Add or update unit tests for state transitions, default-home request selection, and manifest privacy regressions.
- `app/src/androidTest/java/com/windowsphonelauncher/`
  - Add focused Compose UI test only if it can run reliably with the existing test stack.

### Testing Requirements

- Unit tests should cover behavior that does not need Android system UI:
  - initial state is welcome
  - `Use as default launcher` emits/request state
  - return/cancel while not default shows preview explanation
  - `Try again` re-requests
  - `Continue preview` routes to preview state
  - fallback selector chooses an available settings intent when role request is not available
- Manifest regression tests must keep Story 1.2 guarantees:
  - exactly one `HOME` category on `MainActivity`
  - `MAIN` + `LAUNCHER` development entry remains
  - `MAIN` + `HOME` + `DEFAULT` home entry remains
  - no `<queries>`, no `<uses-permission>`, no notification listener service
- Build validation:
  - Required: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon`
  - Run `:app:connectedDebugAndroidTest` when an emulator/device is available. If unavailable, document the skip.

### Previous Story Intelligence

- Story 1.1 review removed an empty `WindowsPhoneLauncherApp.kt`; do not reintroduce an `Application` class unless this story has a real initialization need.
- Story 1.1 established the Gradle wrapper as the primary build path. Use `.\gradlew.bat` from the project root.
- Story 1.1 disabled backup by default and excluded shared preferences from backup/data extraction; keep those manifest/XML settings unchanged.
- Story 1.2 added `singleTask` to `MainActivity` to avoid duplicate Home/launcher task instances; keep it.
- Story 1.2 explicitly deferred default-launcher selection UI, cancellation handling, revoked/default state UX, preview mode, onboarding, and `Use as default launcher` microcopy to this story.
- Known environment warning: Gradle may warn about Android analytics or Kotlin daemon access outside the repo, but previous validation succeeded through fallback compilation.

### Project Structure Notes

- Keep all Kotlin code under `app/src/main/java/com/windowsphonelauncher`.
- Use feature-first packages. Onboarding-specific UI/state belongs in `onboarding`; generic permission/default-app platform helpers may live in `permissions` only if reused or clearly separated.
- Do not put feature state logic into `PackageMarker.kt` placeholders; replace placeholders only when the package gains real code.
- Keep tests mirrored under `app/src/test/java/com/windowsphonelauncher` or feature subpackages.
- Do not add generated build/cache files to git.

### References

- [Source: _bmad-output/planning-artifacts/epics.md#Story 1.3: First-Run Welcome and Default Launcher Flow]
- [Source: _bmad-output/planning-artifacts/epics.md#Epic 1: Launcher Foundation & First Start]
- [Source: _bmad-output/planning-artifacts/epics.md#UX Design Requirements]
- [Source: _bmad-output/planning-artifacts/architecture.md#API & Communication Patterns]
- [Source: _bmad-output/planning-artifacts/architecture.md#Frontend Architecture]
- [Source: _bmad-output/planning-artifacts/architecture.md#Project Structure & Boundaries]
- [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md#Information Architecture]
- [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md#State Patterns]
- [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md#Platform Permission Model]
- [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md#Accessibility Floor]
- [Source: _bmad-output/implementation-artifacts/1-2-configure-android-launcher-entry-point.md#Previous Story Intelligence]
- [Official Android: RoleManager](https://developer.android.com/reference/android/app/role/RoleManager)
- [Official Android: Settings ACTION_HOME_SETTINGS and ACTION_MANAGE_DEFAULT_APPS_SETTINGS](https://developer.android.com/reference/android/provider/Settings)
- [Official Android: Intent ACTION_MAIN and CATEGORY_HOME](https://developer.android.com/reference/android/content/Intent)

## Dev Agent Record

### Agent Model Used

Codex GPT-5

### Debug Log References

- RED phase: `.\gradlew.bat :app:testDebugUnitTest --no-daemon` failed as expected because onboarding reducer and default-home request selector production classes did not exist yet.
- GREEN phase: implemented pure onboarding state transitions, default-home request selection, Android default-home gateway, onboarding Compose screen, and `MainActivity` route host.
- `.\gradlew.bat :app:testDebugUnitTest --no-daemon` passed after implementation.
- `.\gradlew.bat :app:assembleDebugAndroidTest --no-daemon` passed, confirming focused Compose instrumentation tests compile.
- `adb devices` showed no attached devices; `connectedDebugAndroidTest` was not run because no emulator/device was available.
- Final validation passed: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon`.

### Completion Notes List

- Ultimate context engine analysis completed - comprehensive developer guide created.
- Replaced the starter placeholder with first-run onboarding UI hosted from `MainActivity`.
- Added testable onboarding state/reducer logic for welcome, default-launcher request, preview fallback, retry, and continue-preview flows.
- Added a default-home platform gateway that prefers `RoleManager.ROLE_HOME` when available and falls back to Android Home/default-app settings intents.
- Addressed the review findings by removing settings preflight visibility checks, making the request/result path saveable across rotation, and routing preview into a separate placeholder surface.
- Kept Story 1.3 scope narrow: no permissions, package visibility queries, DataStore persistence, Room schema, App List, Tile Grid, or Live Tile behavior were introduced.
- Added unit coverage for onboarding transitions and default-home request selection, extended manifest regression coverage, and added focused Compose UI instrumentation coverage for onboarding labels/actions.
- Verified debug build, debug unit tests, debug androidTest assembly, and debug lint through the committed Gradle wrapper.

### File List

- app/src/main/java/com/windowsphonelauncher/MainActivity.kt
- app/src/main/java/com/windowsphonelauncher/onboarding/DefaultHomeGateway.kt
- app/src/main/java/com/windowsphonelauncher/onboarding/DefaultHomeRequestSelector.kt
- app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingScreen.kt
- app/src/main/java/com/windowsphonelauncher/onboarding/OnboardingState.kt
- app/src/test/java/com/windowsphonelauncher/PackageMarkerTest.kt
- app/src/test/java/com/windowsphonelauncher/onboarding/DefaultHomeRequestSelectorTest.kt
- app/src/test/java/com/windowsphonelauncher/onboarding/OnboardingReducerTest.kt
- app/src/androidTest/java/com/windowsphonelauncher/onboarding/OnboardingScreenTest.kt
- _bmad-output/implementation-artifacts/1-3-first-run-welcome-and-default-launcher-flow.md
- _bmad-output/implementation-artifacts/sprint-status.yaml

### Change Log

- 2026-06-04: Created Story 1.3 context for first-run welcome, default-launcher request, and preview fallback flow.
- 2026-06-05: Implemented first-run onboarding/default-launcher flow and moved story to review.
- 2026-06-05: Resolved review findings, validated the build/test/lint set, and marked the story done.
