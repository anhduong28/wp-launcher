---
baseline_commit: 890098c6fc8b6d200b9ead6380d86138b78c83a1
---

# Story 1.1: Scaffold Kotlin Compose Launcher Project

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a developer,
I want the Android Kotlin Compose project scaffolded with recorded dependency versions,
so that future launcher features have a stable implementation foundation.

## Acceptance Criteria

1. Given the repository has no Android source project, when the project scaffold is created, then it contains an Android application module using Kotlin and Jetpack Compose.
2. Gradle, Kotlin, Android Gradle Plugin, Compose, and core AndroidX dependency versions are recorded in build configuration or a version catalog.
3. The package structure includes `core`, `datacore`, `tilecore`, `startscreen`, `applist`, `editmode`, `settings`, `weather`, `onboarding`, and `permissions` packages or placeholders.
4. The app builds successfully in debug mode.

## Tasks / Subtasks

- [x] Scaffold the Android app project from the approved starter template (AC: 1)
  - [x] Create a native Android application project using Android Studio `Empty Activity`, Kotlin, and Compose enabled.
  - [x] Use Gradle Kotlin DSL for root and app module build files.
  - [x] Set the application namespace/package to `com.windowsphonelauncher` unless the project owner explicitly chooses a different ID before implementation.
- [x] Record build and library versions in one stable place (AC: 2)
  - [x] Prefer `gradle/libs.versions.toml` if the generated template supports a version catalog.
  - [x] Record exact versions for Gradle wrapper, Android Gradle Plugin, Kotlin, Compose BOM or Compose dependencies, and core AndroidX dependencies used by the app module.
  - [x] If Room, DataStore, or WorkManager versions are predeclared to satisfy architecture readiness, keep them in the version catalog only; do not wire unused dependencies into app code before their stories need them.
- [x] Establish the approved package placeholders (AC: 3)
  - [x] Create `app/src/main/java/com/windowsphonelauncher/MainActivity.kt` from the Compose starter.
  - [x] Add `WindowsPhoneLauncherApp.kt` only if an `Application` class is useful for clean future initialization; otherwise leave app-level initialization for the first story that needs it.
  - [x] Create package directories or minimal placeholder files for `core`, `datacore`, `tilecore`, `startscreen`, `applist`, `editmode`, `settings`, `weather`, `onboarding`, and `permissions`.
- [x] Keep Story 1.1 strictly scoped to the scaffold (AC: 1-4)
  - [x] Do not add Android home launcher intent behavior; Story 1.2 owns `CATEGORY_HOME` and launcher-selection declarations.
  - [x] Do not add sensitive permissions, broad package visibility, `LauncherApps` queries, notification listener declarations, calendar permissions, location permissions, weather networking, Room schema, DataStore keys, WorkManager jobs, tile layout logic, or live tile behavior.
  - [x] Template-provided normal app launch behavior is acceptable for development, but launcher-home behavior must remain out of scope.
- [x] Verify debug build (AC: 4)
  - [x] Run `./gradlew :app:assembleDebug` from the project root, or the equivalent Gradle debug build in Android Studio.
  - [x] Record the build command and result in the Dev Agent Record completion notes.

### Review Findings

- [x] [Review][Patch] Wrapper metadata exists without runnable Gradle wrapper files [gradle/wrapper/gradle-wrapper.properties:3] — fixed by generating and committing `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar`; README now uses the wrapper as the primary build command.
- [x] [Review][Patch] Compile SDK pins Android 36.1 minor API unnecessarily [app/build.gradle.kts:9] — fixed by changing the module to `compileSdk = 36`.
- [x] [Review][Patch] Backup rules opt future shared preferences into backup/transfer too early [app/src/main/AndroidManifest.xml:6] — fixed by setting `android:allowBackup="false"` and switching backup/data extraction rules to exclude shared preferences by default.
- [x] [Review][Patch] Empty `Application` class added without demonstrated scaffold value [app/src/main/java/com/windowsphonelauncher/WindowsPhoneLauncherApp.kt:5] — fixed by deleting `WindowsPhoneLauncherApp.kt` and removing the manifest `android:name`.
- [x] [Review][Patch] Expected `mipmap-*` launcher resource directories are missing [app/src/main/AndroidManifest.xml:10] — fixed by adding `mipmap-anydpi` launcher icon resources and pointing manifest icon attributes to `@mipmap`.

## Dev Notes

### Current Repository State

- The repository currently contains planning artifacts and no Android source project or Gradle build files. Story 1.1 is expected to create the initial Android project files rather than update existing app code.
- No previous implementation story exists for Epic 1, so there are no prior story learnings, code patterns, or review corrections to inherit.

### Architecture Guardrails

- Use the selected starter: Android Studio `Empty Activity` with Compose support. The architecture explicitly makes this the first implementation priority.
- Build a Kotlin-first native Android app. Jetpack Compose is the default UI layer, but do not implement Start Screen UI beyond the generated starter shell in this story.
- Keep the MVP local-first. No backend, auth, cloud config, or environment service is needed.
- Preserve future boundaries from the beginning:
  - UI composables emit actions only.
  - ViewModels own screen state when a story introduces them.
  - Repositories own data access when a story introduces them.
  - `tilecore` owns layout rules and live tile contracts later.
  - `datacore` owns persistence later.
  - UI must never read Room or DataStore directly.
- Do not introduce direct SMS or Call Log access in MVP. Do not add permission-sensitive declarations in this scaffold story.

### Latest Technical Notes

- Official Android guidance for new Compose apps uses Android Studio project templates and `Empty Activity`; Kotlin is the language for Compose projects and minimum API should be API 21 or higher unless the project owner chooses otherwise.
- For current Compose setup, official Android guidance uses the Compose Compiler Gradle plugin with Kotlin 2.0+ and recommends a Compose BOM for Compose library version alignment.
- Before finalizing implementation, verify the exact stable AGP, Kotlin, Gradle, and Compose BOM versions from official Android/Kotlin tooling visible in the local Android Studio or official docs. Do not mix incompatible Kotlin and Compose compiler setup.
- Android Gradle Plugin 9.x is a major release with behavior changes; if using AGP 9.x, prefer Android Studio's generated compatible defaults or Upgrade Assistant guidance rather than manually guessing a dependency matrix.

### File Structure Requirements

Expected scaffold shape:

```text
windowsphone-launcher/
|-- README.md
|-- settings.gradle.kts
|-- build.gradle.kts
|-- gradle.properties
|-- gradle/
|   `-- libs.versions.toml
|-- app/
|   |-- build.gradle.kts
|   |-- proguard-rules.pro
|   `-- src/
|       |-- main/
|       |   |-- AndroidManifest.xml
|       |   |-- java/com/windowsphonelauncher/
|       |   |   |-- MainActivity.kt
|       |   |   |-- WindowsPhoneLauncherApp.kt
|       |   |   |-- core/
|       |   |   |-- datacore/
|       |   |   |-- tilecore/
|       |   |   |-- startscreen/
|       |   |   |-- applist/
|       |   |   |-- editmode/
|       |   |   |-- settings/
|       |   |   |-- weather/
|       |   |   |-- onboarding/
|       |   |   `-- permissions/
|       |   `-- res/
|       |       |-- drawable/
|       |       |-- mipmap-*/
|       |       |-- values/
|       |       `-- xml/
|       |-- test/java/com/windowsphonelauncher/
|       `-- androidTest/java/com/windowsphonelauncher/
`-- docs/
```

If Android Studio generates `kotlin/` instead of `java/` for Kotlin sources, that is acceptable only if all generated source and test roots are internally consistent and the package remains `com.windowsphonelauncher`.

### Naming and Organization

- Kotlin classes, composables, and ViewModels use PascalCase.
- Functions, properties, events, and actions use camelCase.
- Files match the primary class or composable name.
- Packages are lowercase and feature-oriented.
- Shared primitives belong in `core`, `tilecore`, or `datacore` only when a future story actually introduces reused behavior.

### Testing Requirements

- Minimum validation for this story is a successful debug build.
- Unit, instrumentation, Compose UI, launcher behavior, and performance tests are not required until stories introduce behavior that needs them.
- If the Android template creates default tests, keep their package paths aligned with `com.windowsphonelauncher`.

### Project Structure Notes

- This story creates NEW scaffold files. There are no existing app files marked for update.
- Keep Android config at the root and `app/`. Keep app metadata and future launcher declarations in `AndroidManifest.xml`.
- Avoid broad placeholder abstractions. Package placeholders are required, but implementation code should wait for the story that owns each feature.

### References

- [Source: _bmad-output/planning-artifacts/epics.md#Story 1.1: Scaffold Kotlin Compose Launcher Project]
- [Source: _bmad-output/planning-artifacts/epics.md#Epic 1: Launcher Foundation & First Start]
- [Source: _bmad-output/planning-artifacts/architecture.md#Starter Template Evaluation]
- [Source: _bmad-output/planning-artifacts/architecture.md#Implementation Patterns & Consistency Rules]
- [Source: _bmad-output/planning-artifacts/architecture.md#Project Structure & Boundaries]
- [Source: _bmad-output/project-context.md#Technology Stack & Versions]
- [Official Android: Compose quick start](https://developer.android.com/develop/ui/compose/setup)
- [Official Android: Compose dependencies and compiler setup](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler)
- [Official Android: Compose BOM](https://developer.android.com/develop/ui/compose/bom)
- [Official Android: Android Gradle Plugin release notes](https://developer.android.com/studio/releases/gradle-plugin)
- [Official Kotlin: Kotlin releases](https://kotlinlang.org/docs/releases.html)

## Dev Agent Record

### Agent Model Used

Codex GPT-5

### Debug Log References

- `git rev-parse HEAD` captured baseline commit `890098c6fc8b6d200b9ead6380d86138b78c83a1`.
- `.\gradlew.bat :app:assembleDebug` could not run because `gradlew.bat` is not present.
- Environment checks found no `gradle`, no `java`, no `ANDROID_HOME`, and no `ANDROID_SDK_ROOT` exposed in this shell.
- Static validation completed: XML files parse successfully; manifest contains no HOME category, sensitive permissions, or package queries; required feature package directories exist; version records are present.
- `gradle :app:assembleDebug --no-daemon` initially failed with inconsistent Java/Kotlin JVM targets; aligned the app module to Java 17/Kotlin JVM toolchain 17.
- `gradle :app:lintDebug --no-daemon` initially failed because `android:windowLightNavigationBar` was in base `values/` despite minSdk 23; moved it to `values-v27/`.
- Final validation command passed: `gradle clean :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon`.
- Code review found 5 patch findings; all were fixed.
- Final post-review validation command passed: `.\gradlew.bat clean :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --no-daemon`.

### Completion Notes List

- Story context engine analysis completed; comprehensive developer guide created for scaffold implementation.
- Created a Kotlin-first Android application scaffold with Compose enabled through Gradle Kotlin DSL.
- Centralized dependency and plugin versions in `gradle/libs.versions.toml`; recorded Gradle distribution in `gradle/wrapper/gradle-wrapper.properties` and README.
- Added `MainActivity`, package markers for required architecture packages, resources, test scaffolds, and launcher icon resources.
- Preserved Story 1.1 scope: no Android HOME launcher category, sensitive permissions, broad package visibility, Room/DataStore/WorkManager behavior, weather networking, tile layout logic, or live tile behavior was introduced.
- Verified the debug build successfully using local Gradle with `ANDROID_HOME=C:\Users\anhdu\AppData\Local\Android\Sdk`.
- Aligned Java and Kotlin compilation targets to JVM 17 so the scaffold compiles consistently.
- Split API 27-only navigation bar styling into `values-v27` so lint passes while preserving minSdk 23.
- Final validation passed: clean debug build, debug unit tests, and debug lint.
- Addressed code review patches: committed full Gradle wrapper files, changed compile SDK to API 36, disabled backup by default, removed the unused `Application` subclass, and added `mipmap-anydpi` launcher icons.

### File List

- README.md
- settings.gradle.kts
- build.gradle.kts
- gradle.properties
- gradlew
- gradlew.bat
- gradle/libs.versions.toml
- gradle/wrapper/gradle-wrapper.jar
- gradle/wrapper/gradle-wrapper.properties
- app/build.gradle.kts
- app/proguard-rules.pro
- app/src/main/AndroidManifest.xml
- app/src/main/java/com/windowsphonelauncher/MainActivity.kt
- app/src/main/java/com/windowsphonelauncher/applist/PackageMarker.kt
- app/src/main/java/com/windowsphonelauncher/core/PackageMarker.kt
- app/src/main/java/com/windowsphonelauncher/datacore/PackageMarker.kt
- app/src/main/java/com/windowsphonelauncher/editmode/PackageMarker.kt
- app/src/main/java/com/windowsphonelauncher/onboarding/PackageMarker.kt
- app/src/main/java/com/windowsphonelauncher/permissions/PackageMarker.kt
- app/src/main/java/com/windowsphonelauncher/settings/PackageMarker.kt
- app/src/main/java/com/windowsphonelauncher/startscreen/PackageMarker.kt
- app/src/main/java/com/windowsphonelauncher/tilecore/PackageMarker.kt
- app/src/main/java/com/windowsphonelauncher/weather/PackageMarker.kt
- app/src/main/res/drawable/ic_launcher.xml
- app/src/main/res/mipmap-anydpi/ic_launcher.xml
- app/src/main/res/mipmap-anydpi/ic_launcher_round.xml
- app/src/main/res/values/strings.xml
- app/src/main/res/values/styles.xml
- app/src/main/res/values-v27/styles.xml
- app/src/main/res/xml/backup_rules.xml
- app/src/main/res/xml/data_extraction_rules.xml
- app/src/test/java/com/windowsphonelauncher/PackageMarkerTest.kt
- app/src/androidTest/java/com/windowsphonelauncher/ExampleInstrumentedTest.kt

### Change Log

- 2026-06-04: Added initial Android Kotlin Compose scaffold and version catalog.
- 2026-06-04: Completed debug build verification; aligned JVM targets, fixed API-specific styling resource placement, and moved story to review.
- 2026-06-04: Fixed all code review findings and moved story to done.
