---
baseline_commit: 5fca102
---

# Story 2.1: Render Persisted Start Screen Tile Grid

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a launcher user,
I want the Start Screen to render my pinned Tiles in a 4-column grid,
so that I can see a Windows Phone-style home layout.

## Acceptance Criteria

1. Given the Start Screen shell exists, when the Start Screen loads, then it renders a 4-column Tile Grid with `4dp` gaps and `8dp` screen edge spacing.
2. Supported Tile sizes are represented as `1x1`, `2x2`, and `4x2` spans.
3. Tiles never overlap visually.
4. The grid uses the classic black background from Epic 1.
5. Initial placeholder/default Tiles are clearly modeled as persisted Tile records or seed data needed for this story.
6. The debug build, debug unit tests, debug instrumentation APK build, and debug lint pass after the change. Run connected instrumentation tests only if a device/emulator is available.

## Tasks / Subtasks

- [x] Replace the Story 1.4 placeholder grid with a real Start Screen tile-grid renderer (AC: 1-4)
  - [x] Keep `StartScreenShellScreen` in `app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenShellScreen.kt` as the public route used by `MainActivity`.
  - [x] Remove visible placeholder copy such as `Start Screen shell` and `Tile Grid placeholder`; the Start Screen should be the first visible surface, not a labeled demo panel.
  - [x] Preserve `WindowInsets.safeContent`, vertical scrolling, full-screen black background, and the `StartScreenShellTestTags.Root` test tag from Story 1.4.
  - [x] Use exactly 4 grid columns, `4.dp` tile gaps, and `8.dp` screen-edge padding.
- [x] Add tile models that can later be backed by Room without redesign (AC: 2, 5)
  - [x] Add a `tilecore` model for tile identity, display label, size, color, and ordered grid placement.
  - [x] Represent MVP sizes explicitly, for example `TileSize.OneByOne`, `TileSize.TwoByTwo`, and `TileSize.FourByTwo`, with span metadata derived from the enum/model rather than scattered magic numbers.
  - [x] Add seed/default tile records for this story. Seed records are acceptable here because Story 2.2 owns real Room persistence.
  - [x] Do not persist tile layout through DataStore. DataStore remains for lightweight preferences/onboarding only.
- [x] Add a deterministic layout helper for visual placement (AC: 1-3)
  - [x] Convert ordered tile records into row/column spans without visual overlap.
  - [x] Keep placement deterministic: row-major, compact top-to-bottom and left-to-right.
  - [x] If a seed record would overflow a row, wrap to the next valid row. If an invalid span appears, fail fast in tests or coerce through a single validated model boundary.
  - [x] Do not implement drag, resize, reflow-on-edit, pin/unpin, app launch, App List, or Room persistence in this story.
- [x] Render Windows Phone-style tile surfaces (AC: 1-4)
  - [x] Use square corners and solid tile colors only; no cards, rounded Material surfaces, gradients, shadows, or translucent wallpaper-style tiles.
  - [x] Use the approved default palette where practical: cyan `#00BCF2`, blue `#0078D7`, green `#00A300`, red `#E51400`, purple `#800080`, orange `#F7630C`, disabled `#3A3A3A`.
  - [x] Render labels inside tile boundaries with readable contrast. For this story, a simple known-safe foreground mapping is acceptable; full contrast engine belongs to Epic 4.
  - [x] Keep text stable within tile bounds and avoid labels resizing or shifting the grid.
- [x] Add focused tests for layout and route behavior (AC: 1-6)
  - [x] Add unit tests for tile-size span mapping and deterministic non-overlap placement.
  - [x] Update/replace `StartScreenShellScreenTest` so it asserts the real grid is shown and old placeholder copy is gone.
  - [x] Add Compose assertions for at least one `1x1`, one `2x2`, and one `4x2` seeded tile through test tags or semantics.
  - [x] Keep existing app route tests proving `OnboardingStep.Preview` reaches `StartScreenShellScreen`.
  - [x] Keep manifest regression tests proving no `<uses-permission>`, `<queries>`, or notification listener service is introduced by this story.
- [x] Run validation (AC: 6)
  - [x] Run `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon`.
  - [x] If a device/emulator is attached, run `.\gradlew.bat :app:connectedDebugAndroidTest --no-daemon`.
  - [x] Record commands and results in the Dev Agent Record completion notes.

### Review Findings

- [x] [Review][Patch] Preserve tile order when packing positioned tiles [app/src/main/java/com/windowsphonelauncher/tilecore/StartTileModels.kt:72]
- [x] [Review][Patch] Enforce minimum tile grid width for 48dp cells and narrow safe-inset cases [app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenShellScreen.kt:84]
- [x] [Review][Patch] Validate tile IDs are nonblank and unique before rendering tags [app/src/main/java/com/windowsphonelauncher/tilecore/StartTileModels.kt:32]
- [x] [Review][Patch] Move seed tile data behind a Start Screen state/seed boundary [app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenShellScreen.kt:154]

## Dev Notes

### Current Repository State

- Current git `HEAD` is `5fca102` (`Done item 1-5`). Stories 1.1 through 1.5 are complete.
- Story 1.5 routes returning users who completed onboarding or selected preview directly into `StartScreenShellScreen()` through `WindowsPhoneLauncherAppContent`.
- `MainActivity.kt` still owns the top-level onboarding/preview route. Story 2.1 should keep using that route seam; do not introduce a navigation library.
- `StartScreenShellScreen.kt` currently renders a black full-screen shell with safe-content insets, 8dp padding, vertical scroll, visible placeholder text, and a 3-row x 4-column border-only placeholder grid.
- Existing tests:
  - `StartScreenShellScreenTest` currently expects placeholder copy and must be updated.
  - `WindowsPhoneLauncherAppContentTest.previewStepRoutesToStartScreenShell` should continue passing.
  - `PackageMarkerTest.manifestDeclaresMainActivityAsHomeLauncherWithoutSensitiveScope` guards against accidental permissions/package visibility.

### Story 1.5 Intelligence

- Preserve first-run persistence behavior. Do not change `OnboardingPreferencesRepository` or the first-run DataStore keys for this story.
- `StartScreenShellScreen()` is reached only after persisted first-run completion/preview flow. Do not add alternate startup routing.
- Connected instrumentation was not run for story 1.5 because no device/emulator was attached; continue to build androidTest APKs and record device availability honestly.

### Epic 2 Context

- Epic 2 owns Start Screen grid usability: mixed tile sizes, pin/unpin, resizing, moving, reflow, persistence, accessible edit alternatives, and performance validation.
- Story 2.1 is the first Epic 2 slice. It should establish grid rendering, tile-size models, deterministic non-overlap placement, and seed/default tile records.
- Story 2.2 owns real Room-backed layout persistence. Story 2.1 must not make Story 2.2 redundant by implementing full Room schema/migrations unless the developer finds that a minimal Room setup is unavoidable and records the decision.
- Stories 2.3 through 2.8 own app launching, edit mode, pin/unpin, move/reflow, resize, and accessible edit actions. Do not pre-implement those behaviors here.

### Architecture Requirements

- Room owns tile layout, pinned tile state, app cache, and cached live snapshots. DataStore owns preferences, onboarding flags, animation settings, accent color, and weather setup flags. UI must not read Room or DataStore directly. [Source: `_bmad-output/planning-artifacts/architecture.md#Data Boundaries`]
- UI composables emit actions only. ViewModels own screen state. Repositories own data access. `tilecore` owns layout rules and live tile contracts. [Source: `_bmad-output/planning-artifacts/architecture.md#Component Boundaries`]
- Repositories expose Flow-based data. ViewModels expose StateFlow UI state and SharedFlow one-shot effects. Compose sends explicit action callbacks. [Source: `_bmad-output/planning-artifacts/architecture.md#Integration Points`]
- The Start Screen Tile Grid may remain Compose-first, but Epic 2 includes a performance prototype gate and a fallback path to custom View/custom layout if Compose cannot meet later drag/snap/resize targets. [Source: `_bmad-output/planning-artifacts/architecture.md#Frontend Architecture`]
- Use feature-first packages: `startscreen` for Start Screen UI/state, `tilecore` for shared tile models/layout rules, and `datacore` only when actual persistence boundaries are introduced. [Source: `_bmad-output/planning-artifacts/architecture.md#Project Structure & Boundaries`]

### UX Requirements

- Start Screen uses a 4-column vertical Tile Grid. `1x1` spans 1 column, `2x2` spans 2 columns, and `4x2` spans all 4 columns. [Source: `_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md#Layout & Spacing`]
- Tile gap is `4dp`; screen edge spacing is `8dp`; minimum touch target is `48dp`. [Source: `_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md#spacing`]
- The Start Screen has no header, no persistent search bar, and no settings shortcut above the grid. Tiles begin near the top with minimal safe-edge breathing room. [Source: `_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md#Layout & Spacing`]
- Default canvas is classic black `#000000`. Tiles remain solid color; image backgrounds later appear only behind the Tile Grid/in gaps. [Source: `_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md#Colors`]
- Avoid Material card styling: no rounded tile corners, gradients, shadows, decorative chrome, or translucent tiles. [Source: `_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md#Brand & Style`]

### Suggested Implementation Shape

- `tilecore`:
  - Add a tile-size enum/model with `columnSpan` and `rowSpan`.
  - Add a simple tile record model such as `StartTileRecord(id, label, size, color, order)`.
  - Add a pure layout function that maps ordered records to positioned tile models and validates no overlaps.
- `startscreen`:
  - Replace `ShellPlaceholderGrid` with `StartTileGrid` or equivalent.
  - Render seeded records by default through `StartScreenShellScreen(tiles = defaultStartTiles)` or an internal state holder.
  - Add stable test tags for the grid and individual tiles, for example `start-screen-tile-grid` and `start-screen-tile-<id>`.
- Testing:
  - Prefer pure JVM tests for tile layout math.
  - Use Compose tests only for route/render assertions, not for complex geometry math.
  - Keep pixel-perfect visual assertions out of scope; assert structure, text/semantics/tags, and absence of placeholder copy.

### Scope Boundaries

- Do not add App List, app discovery, `LauncherApps`, package visibility declarations, broad manifest `<queries>`, or app launch behavior in this story.
- Do not add direct SMS, Call Log, Calendar, location, notification listener, or WorkManager behavior.
- Do not introduce full Room schema/migrations unless absolutely necessary. If Room is added, keep it limited to tile layout seed/read shape and document why Story 2.2 remains meaningful.
- Do not add Live Tile animation, weather provider calls, notification-derived counts, Calendar counts, or settings surfaces.
- Do not block Start Screen rendering behind optional permissions, default-home status, or app discovery.

### Latest Technical Notes

- Android Developers list Room 3.0 as alpha-only as of June 3, 2026; it uses new `androidx.room3` artifacts and has breaking package/API changes. Do not use Room 3 alpha for this MVP story without an explicit architecture decision. [Source: https://developer.android.com/jetpack/androidx/releases/room3]
- Android Developers' current Room KMP setup page lists Room `2.8.4` and the Room Gradle plugin/KSP pattern for schema configuration. If Room is introduced in Story 2.2, prefer the stable `androidx.room` 2.x line unless architecture is revised. [Source: https://developer.android.com/kotlin/multiplatform/room]
- Current project versions are AGP `8.13.2`, Kotlin `2.3.21`, Compose BOM `2026.05.00`, DataStore `1.2.1`, compileSdk/targetSdk `36`. Keep new dependencies centralized in `gradle/libs.versions.toml`.

### References

- `_bmad-output/planning-artifacts/epics.md#Story 2.1: Render Persisted Start Screen Tile Grid`
- `_bmad-output/planning-artifacts/epics.md#Epic 2: Start Screen Tile Grid & Editing`
- `_bmad-output/planning-artifacts/prds/prd-WindowsPhone Launcher-2026-06-02/prd.md#4.2 Tile Grid and Tile Sizing`
- `_bmad-output/planning-artifacts/prds/prd-WindowsPhone Launcher-2026-06-02/prd.md#4.3 Start Screen Editing`
- `_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md#Layout & Spacing`
- `_bmad-output/planning-artifacts/architecture.md#Data Boundaries`
- `_bmad-output/planning-artifacts/architecture.md#Component Boundaries`
- `_bmad-output/implementation-artifacts/1-5-persist-first-run-and-preview-state.md`
- `app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenShellScreen.kt`
- `app/src/main/java/com/windowsphonelauncher/MainActivity.kt`
- `app/src/androidTest/java/com/windowsphonelauncher/startscreen/StartScreenShellScreenTest.kt`
- `app/src/androidTest/java/com/windowsphonelauncher/WindowsPhoneLauncherAppContentTest.kt`
- `app/src/test/java/com/windowsphonelauncher/PackageMarkerTest.kt`

## Dev Agent Record

### Agent Model Used

Codex GPT-5

### Debug Log References

- RED phase: `.\gradlew.bat :app:testDebugUnitTest --tests com.windowsphonelauncher.tilecore.StartTileLayoutTest --no-daemon` failed as expected because `TileSize`, `StartTileRecord`, `GridPosition`, and `positionStartTiles` did not exist yet.
- Targeted layout validation: `.\gradlew.bat :app:testDebugUnitTest --tests com.windowsphonelauncher.tilecore.StartTileLayoutTest --no-daemon` passed.
- Android test APK validation: `.\gradlew.bat :app:assembleDebugAndroidTest --no-daemon` passed.
- Full validation: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon` passed.
- Device check: SDK `platform-tools\adb.exe devices` returned no attached devices, so `connectedDebugAndroidTest` was not run.
- Code review patch validation on 2026-06-07: `.\gradlew.bat :app:testDebugUnitTest --tests com.windowsphonelauncher.tilecore.StartTileLayoutTest --no-daemon` passed.
- Code review patch validation on 2026-06-07: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest :app:lintDebug --no-daemon` passed.
- Device check on 2026-06-07: SDK `platform-tools\adb.exe devices` returned no attached devices, so `connectedDebugAndroidTest` was not run.

### Completion Notes List

- Story context created by BMad create-story workflow on 2026-06-07.
- Ultimate context engine analysis completed - comprehensive developer guide created.
- Added `tilecore` tile models for MVP sizes, seed-like records, grid positions, and positioned tiles.
- Added deterministic row-major tile placement that supports mixed `1x1`, `2x2`, and `4x2` spans without overlap.
- Replaced the Story 1.4 placeholder shell copy/grid with a real black Start Screen tile grid using 4 columns, 4dp gaps, 8dp edge padding, safe-content insets, vertical scrolling, square solid-color tiles, and stable test tags.
- Added unit tests for tile span metadata, wrapping, and non-overlap placement.
- Updated Compose Start Screen tests to assert seeded tiles render and placeholder copy is gone.
- Code review fixes preserve visual order during tile packing, validate tile IDs, protect 48dp minimum cells on narrow widths with horizontal scrolling, and move seed tiles behind `StartScreenUiState`.

### File List

- `_bmad-output/implementation-artifacts/2-1-render-persisted-start-screen-tile-grid.md`
- `_bmad-output/implementation-artifacts/sprint-status.yaml`
- `app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenShellScreen.kt`
- `app/src/main/java/com/windowsphonelauncher/startscreen/StartScreenUiState.kt`
- `app/src/main/java/com/windowsphonelauncher/tilecore/StartTileModels.kt`
- `app/src/test/java/com/windowsphonelauncher/tilecore/StartTileLayoutTest.kt`
- `app/src/androidTest/java/com/windowsphonelauncher/startscreen/StartScreenShellScreenTest.kt`

## Change Log

- 2026-06-07: Replaced Start Screen placeholder with a seeded 4-column tile grid and deterministic tilecore layout model.
- 2026-06-07: Added unit and Compose coverage for tile spans, non-overlap placement, seeded tile rendering, and placeholder removal.
- 2026-06-07: Addressed code review findings for tile order, narrow-width sizing, tile ID validation, and seed state ownership.
