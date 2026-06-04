---
baseline_commit: 7507402b6e12b5b049ee58878ebf55fafbfb94cd
---

# Story 1.2: Configure Android Launcher Entry Point

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As an Android user,
I want WindowsPhone Launcher to be recognizable by Android as a home launcher,
so that I can choose it as my default launcher.

## Acceptance Criteria

1. Given the Android app scaffold exists, when the app manifest is configured, then the main launcher activity declares the correct home/launcher intent behavior for Android launcher selection.
2. The app can still be opened normally during development from the regular app launcher.
3. Launcher declarations are isolated in `AndroidManifest.xml` for review.
4. No broad package visibility declarations or sensitive permissions are introduced in this story.
5. The debug build, debug unit tests, and debug lint pass after the manifest change.

## Tasks / Subtasks

- [x] Configure `MainActivity` manifest intent filters for home-launcher behavior (AC: 1, 2, 3)
  - [x] Preserve the existing normal development entry filter with `android.intent.action.MAIN` and `android.intent.category.LAUNCHER`.
  - [x] Add a separate home intent filter on `MainActivity` with `android.intent.action.MAIN`, `android.intent.category.HOME`, and `android.intent.category.DEFAULT`.
  - [x] Keep all launcher/home declarations inside `app/src/main/AndroidManifest.xml`; do not move behavior into code or generated manifest snippets.
- [x] Preserve Story 1.2 privacy and policy scope (AC: 4)
  - [x] Do not add `<queries>`, `QUERY_ALL_PACKAGES`, package visibility declarations, `LauncherApps` code, notification listener declarations, Calendar/location/SMS/Phone permissions, or direct SMS/Call Log access.
  - [x] Keep `android:allowBackup="false"` and existing backup/data extraction exclusions from Story 1.1 unchanged.
  - [x] Do not implement onboarding, default-launcher selection UI, preview mode, Start Screen shell behavior, App List, app discovery, app launching, or tile behavior; those are later stories.
- [x] Add targeted validation for launcher manifest declarations (AC: 1-4)
  - [x] Add or update a local unit test that parses `app/src/main/AndroidManifest.xml` and verifies exactly one `HOME` category exists on `MainActivity`.
  - [x] Verify the same manifest has no `<uses-permission>` entries and no `<queries>` element.
  - [x] Verify the existing `LAUNCHER` development entry remains present.
- [x] Run build and quality checks (AC: 5)
  - [x] Run `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon` from the project root.
  - [x] Record the command and result in the Dev Agent Record completion notes.

### Review Findings

- [x] [Review][Patch] Home entry point lacks task-instance guard [app/src/main/AndroidManifest.xml]
- [x] [Review][Patch] Unit test does not verify exactly one `HOME` category exists [app/src/test/java/com/windowsphonelauncher/PackageMarkerTest.kt]

## Dev Notes

### Current Repository State

- Story 1.1 completed the Kotlin/Compose Android scaffold and is committed as `7507402 Done epic 1.1`.
- The app currently has a single `MainActivity` with a normal development launcher intent filter only:
  - `android.intent.action.MAIN`
  - `android.intent.category.LAUNCHER`
- `MainActivity` currently renders only a minimal Compose placeholder. Do not expand UI behavior in this story.
- `WindowsPhoneLauncherApp.kt` was deliberately removed during Story 1.1 review because it was an empty `Application` class. Do not reintroduce it unless a later story has a real initialization need.
- Story 1.1 review also established:
  - full Gradle wrapper is committed and README uses it as the primary build path;
  - `compileSdk = 36`;
  - `android:allowBackup="false"`;
  - backup/data extraction rules exclude shared preferences by default;
  - launcher icons live in `mipmap-anydpi`.

### Architecture Guardrails

- Android must treat the app as a launcher via `ACTION_MAIN` + `CATEGORY_HOME` manifest behavior. Keep this declaration explicit and easy to review.
- The app must still be openable normally during development, so preserve the `CATEGORY_LAUNCHER` entry point.
- Add `CATEGORY_DEFAULT` to the home intent filter. Android's manifest category docs state implicit intents require `CATEGORY_DEFAULT`; Android's Intent reference describes `ACTION_MAIN` with `CATEGORY_HOME` as the home screen launch path and defines `CATEGORY_HOME` as the home activity shown when the device boots.
- App discovery and package visibility are not part of this story. Architecture says App List/app launching should later use `LauncherApps` and avoid broad package visibility unless justified and reviewed.
- The default-launcher selection flow, cancel/revoked states, preview mode, and `Use as default launcher` microcopy belong to Story 1.3. This story only makes Android recognize the app as a launcher candidate.
- No sensitive permissions may be introduced. MVP avoids direct SMS and Call Log permissions without explicit architecture revision.

### Files to Update

- `app/src/main/AndroidManifest.xml`
  - Add a second `intent-filter` under `.MainActivity` for home behavior.
  - Preserve `android:exported="true"` because `MainActivity` has intent filters.
  - Keep application-level backup/icon/theme attributes unchanged except where manifest formatting requires no behavioral change.
- `app/src/test/java/com/windowsphonelauncher/PackageMarkerTest.kt`
  - Extend or replace with manifest-structure assertions. Keep the package `com.windowsphonelauncher`.
  - Prefer standard JDK XML parsing APIs already available to unit tests; do not add new dependencies for manifest parsing.

### Expected Manifest Shape

The target activity should have two intent filters:

```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LAUNCHER" />
</intent-filter>
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.HOME" />
    <category android:name="android.intent.category.DEFAULT" />
</intent-filter>
```

Do not add `<queries>` or `<uses-permission>` in this story.

### Testing Requirements

- Required validation:
  - `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon`
- Unit test guidance:
  - Assert `MainActivity` contains both development launch and home filters.
  - Assert the home filter uses `MAIN` + `HOME` + `DEFAULT`.
  - Assert no sensitive permissions or package visibility declarations exist.
- Manual emulator/device validation is useful but not required for Story 1.2 because Story 1.3 owns default-launcher flow UX and cancellation handling.

### Project Structure Notes

- This story updates existing files only. Expected update files:
  - `app/src/main/AndroidManifest.xml`
  - `app/src/test/java/com/windowsphonelauncher/PackageMarkerTest.kt`
- Keep all source under the existing `com.windowsphonelauncher` package.
- Do not add feature implementation files in `onboarding`, `startscreen`, `applist`, or `permissions` for this story.
- If Android Studio or Gradle generates build/cache output, do not commit it.

### Previous Story Intelligence

- Story 1.1 ended cleanly after code review with status `done`.
- Known environment detail: validation should use the committed Gradle wrapper, not system Gradle.
- Avoid repeating Story 1.1 review mistakes:
  - do not add placeholder classes with no concrete responsibility;
  - do not add platform-sensitive manifest declarations unless this story explicitly owns them;
  - keep backup disabled/excluded by default;
  - keep changes narrow and verifiable.

### Latest Technical Notes

- Android API reference identifies `ACTION_MAIN` plus `CATEGORY_HOME` as the home screen launch intent and defines `CATEGORY_HOME` as the home activity shown when the device boots.
- Android manifest category documentation states implicit intent filters need `CATEGORY_DEFAULT`; include it in the home filter for resolver compatibility.
- Android manifest action documentation confirms `ACTION_MAIN` maps to `android.intent.action.MAIN`.

### References

- [Source: _bmad-output/planning-artifacts/epics.md#Story 1.2: Configure Android Launcher Entry Point]
- [Source: _bmad-output/planning-artifacts/epics.md#Epic 1: Launcher Foundation & First Start]
- [Source: _bmad-output/planning-artifacts/architecture.md#Technical Constraints & Dependencies]
- [Source: _bmad-output/planning-artifacts/architecture.md#Project Structure & Boundaries]
- [Source: _bmad-output/planning-artifacts/architecture.md#Integration Points]
- [Source: _bmad-output/implementation-artifacts/1-1-scaffold-kotlin-compose-launcher-project.md#Previous Story Intelligence]
- [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md#Information Architecture]
- [Source: _bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/review-technical-feasibility.md#Default Launcher Onboarding and Preview Mode]
- [Official Android: Intent API reference](https://developer.android.com/reference/kotlin/android/content/Intent.html)
- [Official Android: manifest `<category>` element](https://developer.android.com/guide/topics/manifest/category-element)
- [Official Android: manifest `<action>` element](https://developer.android.com/guide/topics/manifest/action-element)

## Dev Agent Record

### Agent Model Used

Codex GPT-5

### Debug Log References

- `git rev-parse HEAD` captured baseline commit `7507402b6e12b5b049ee58878ebf55fafbfb94cd`.
- RED phase: `.\gradlew.bat :app:testDebugUnitTest --no-daemon` failed as expected after adding manifest assertions because `MainActivity` did not yet declare a `HOME` filter.
- GREEN phase: added a separate `MAIN` + `HOME` + `DEFAULT` intent filter to `MainActivity` in `AndroidManifest.xml`.
- `.\gradlew.bat :app:testDebugUnitTest --no-daemon` passed after the manifest update.
- Final validation passed: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon`.

### Completion Notes List

- Ultimate context engine analysis completed - comprehensive developer guide created.
- Added Android home-launcher manifest behavior while preserving the normal development launcher entry.
- Kept Story 1.2 scope narrow: no permissions, `<queries>`, package visibility declarations, app discovery code, onboarding UI, Start Screen behavior, or tile behavior were introduced.
- Added unit coverage that parses `AndroidManifest.xml` and verifies the launcher/home declarations plus absence of permissions and package visibility declarations.
- Addressed code review findings by adding `singleTask` task-instance guarding to `MainActivity` and tightening the manifest test to count exactly one `HOME` category.
- Verified debug build, debug unit tests, and debug lint through the committed Gradle wrapper.

### File List

- app/src/main/AndroidManifest.xml
- app/src/test/java/com/windowsphonelauncher/PackageMarkerTest.kt
- _bmad-output/implementation-artifacts/1-2-configure-android-launcher-entry-point.md
- _bmad-output/implementation-artifacts/sprint-status.yaml

### Change Log

- 2026-06-04: Created Story 1.2 context for Android home launcher manifest configuration.
- 2026-06-04: Implemented Android home launcher manifest entry point and targeted manifest tests; story moved to review.
