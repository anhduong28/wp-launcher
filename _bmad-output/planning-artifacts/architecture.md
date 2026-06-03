---
stepsCompleted: [1, 2, 3, 4, 5]
lastStep: 5
inputDocuments:
  - "_bmad-output/planning-artifacts/prds/prd-WindowsPhone Launcher-2026-06-02/prd.md"
  - "_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md"
  - "_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md"
  - "_bmad-output/planning-artifacts/research/technical-android-launcher-live-tile-constraints-research-2026-06-02.md"
workflowType: 'architecture'
project_name: 'WindowsPhone Launcher'
user_name: 'Edward'
date: '2026-06-02'
---

# Architecture Decision Document

_This document builds collaboratively through step-by-step discovery. Sections are appended as we work through each architectural decision together._

## Project Context Analysis

### Requirements Overview

**Functional Requirements:**

The PRD defines 25 functional requirements grouped into 6 feature areas: Live Tile System, Tile Grid and Tile Sizing, Start Screen Editing, Tile Visual Style, App List, and Launcher Settings.

Architecturally, this means the app is not a simple themed launcher. It needs:

- A launcher/home-screen shell that can become the default Android home app.
- A 4-column Start Screen grid with mixed tile sizes (`1x1`, `2x2`, `4x2`).
- A deterministic layout engine that supports pin, unpin, move, resize, and reflow.
- A separate App List surface for browsing launchable apps, search, and pinning.
- A Live Tile state model with optional data sources for Weather, Clock, SMS, Phone, and Calendar.
- An edit-mode interaction model with drag, resize, and accessibility alternatives.
- A settings surface for color mode, accent color, background, weather location, and Live Tile animation.

**Non-Functional Requirements:**

The product is constrained by several non-functional requirements that directly shape the architecture:

- Smooth daily-use launcher behavior, with 7-day primary launcher tolerance and no heaviness/lag.
- Accessibility requirements: 48dp hit areas, TalkBack labels/actions, predictable focus order, and readable contrast.
- Privacy-first behavior: counts only for SMS/Phone/Calendar, no previews, no direct SMS/Call Log access in MVP.
- Local-first design: no backend for MVP, local persistence for layout/settings/state.
- Android/Play policy sensitivity: package visibility, notification access, calendar permission, location, and weather provider terms.
- Performance-sensitive live motion: flip cadence, visible-only animation, and prototype validation for the tile grid.
- Weather provider dependency: Open-Meteo is the default provider, but public-release terms must be verified before shipping.

**Scale & Complexity:**

This is a high-complexity mobile Android project.

- Primary domain: Android mobile launcher
- Complexity level: high
- Estimated architectural components: 8-10 core components

Likely major components:

- Launcher shell / default-home activity
- Start Screen tile grid
- Tile layout and persistence engine
- App List and search
- Edit Mode interaction layer
- Live Tile adapters
- Weather provider adapter
- Onboarding and settings
- Permission/state gating layer
- Accessibility and testing/profiling support

### Technical Constraints & Dependencies

- Android must treat the app as a launcher via `ACTION_MAIN` + `CATEGORY_HOME`.
- App discovery should prefer `LauncherApps`; avoid broad package visibility unless declared and justified.
- Notification access is a system-settings flow, not a normal runtime permission.
- Calendar uses runtime permission gating.
- Auto-location is optional; manual city/location is the default Weather flow.
- Direct SMS and Call Log permissions are avoided in MVP.
- Open-Meteo is the default weather provider, but terms/attribution/privacy/commercial-use must be verified before public release.
- Compose is the preferred UI stack, but the Start Screen Tile Grid needs a performance prototype gate and fallback path to a custom View/custom layout if needed.
- Room, DataStore, and WorkManager are the local persistence/background foundations.
- No backend is required for MVP.

### Cross-Cutting Concerns Identified

- Permission-gated UX and graceful degradation
- Accessibility and reduced-motion handling
- Layout determinism for mixed-size tile reflow/reorder/resize
- Performance and battery impact from live tiles and weather refresh
- Privacy and content minimization
- Google Play policy compliance
- Weather provider attribution/terms
- Local persistence and state restoration
- Predictable Android navigation and default-launcher behavior
- Contrast-safe tile colors and readable text/icons

## Starter Template Evaluation

### Primary Technology Domain

Android mobile application, specifically a launcher/home-screen app.

### Starter Options Considered

- Android Studio `Empty Activity` with Compose support.
- Third-party launcher starter or boilerplate.

### Selected Starter: Android Studio `Empty Activity` with Compose support

**Rationale for Selection:**

This project needs a native Android shell with a minimal base so the launcher behavior, tile grid, and permission flows can be built without inheriting unrelated starter assumptions. Android's official Compose setup uses the `Empty Activity` template, which creates the manifest, Gradle wiring, and Compose baseline needed for a clean Kotlin-first app. That is the right foundation for a custom launcher.

**Initialization Command:**

```text
Android Studio -> New Project -> Empty Activity -> Kotlin -> Compose enabled
```

**Architectural Decisions Provided by Starter:**

**Language & Runtime:**
Kotlin-first Android app with the standard Android application runtime.

**Styling Solution:**
Jetpack Compose is enabled from the start, giving us a modern UI layer for the Start Screen, App List, onboarding, and settings.

**Build Tooling:**
Android Studio project structure with Gradle files and the default Android manifest generated.

**Testing Framework:**
Starter does not force a test stack, which is preferable here because launcher behavior will need custom instrumentation and performance coverage later.

**Code Organization:**
Minimal app shell that we can extend into launcher-specific modules without fighting prebuilt navigation or unrelated feature scaffolding.

**Development Experience:**
Fast feedback loop for Compose UI work, simple project bootstrap, and a clean base for launcher-specific manifest declarations such as `CATEGORY_HOME`.

**Note:** Project initialization using this template should be the first implementation story.

## Core Architectural Decisions

### Decision Priority Analysis

**Critical Decisions (Block Implementation):**

- Starter template: Android Studio `Empty Activity` with Compose support.
- Data architecture: Room for structured launcher state, DataStore for small settings.
- Cache strategy: retain last known good live tile state so tiles do not go blank on transient failures.

**Important Decisions (Shape Architecture):**

- Security posture: no auth, no backend, local-only storage.
- Live tile degradation: stale snapshot is better than an empty tile.

**Deferred Decisions (Post-MVP):**

- Encryption-at-rest beyond platform defaults.
- Account/auth flows.
- Cloud sync or backup.

### Data Architecture

- **Room** stores structured launcher data: pinned tiles, tile layout, tile sizes, ordering, app metadata cache, and cached live tile snapshots.
- **DataStore** stores small preferences and flags: accent color, color mode, background mode, live tile animation toggle, weather setup state, and onboarding completion.
- The split is based on data shape, not feature ownership.
- Room is the source of truth for grid structure and tile state.
- DataStore is the source of truth for preferences and lightweight configuration.
- Live tile data keeps a last known good cache so a weather, calendar, or notification source failure does not empty the tile immediately.

### Authentication & Security

- No user authentication in MVP.
- No backend in MVP.
- No app-level encryption layer is planned unless a future feature stores data beyond current scope.
- Sensitive permission-gated functionality should degrade gracefully when revoked instead of blocking the launcher.
- SMS/Phone/Calendar data should remain minimal and local, with no preview text in the MVP scope.

### API & Communication Patterns

- This is a local-first Android app, so there is no external API layer in MVP.
- Internal communication follows Android's recommended unidirectional data flow.
- Repositories are the data producers.
- Screen-level `ViewModel`s own UI state for Start Screen, App List, Settings, and Weather setup.
- Compose consumes immutable `StateFlow`-backed UI state and sends user events back to ViewModels through explicit actions.
- `SharedFlow` or one-shot event channels are reserved for transient actions such as navigation prompts, permission requests, and toast/snackbar-style feedback.
- `suspend` functions are used for mutations and one-off reads; flows are used for continuously updating data like tile state, app lists, and cached live snapshots.
- `ViewModel`s stay lifecycle-aware but Android-agnostic; they should not hold direct `Activity` or `Fragment` references.

### Frontend Architecture

- Jetpack Compose is the default UI layer for the launcher.
- The launcher is organized around screen-level composables and screen-level ViewModels.
- Major surfaces are Start Screen, App List, Edit Mode overlays, Settings, Weather Setup, and first-run onboarding.
- The Start Screen Tile Grid may fall back to a custom View/custom layout implementation if Compose cannot meet performance or interaction targets for drag, snap, and resize.
- The grid interaction model is intentionally local and deterministic; layout reflow should be driven by a single layout engine rather than ad hoc per-screen logic.
- Navigation is intentionally minimal and launcher-like rather than app-like. The core experience is a home shell with a few owned surfaces, not a deep multi-stack app router.

### Infrastructure & Deployment

- No backend infrastructure is required for MVP.
- Deployment target is a standard Android application distributed through Google Play.
- Build tooling stays local in the Android project; there is no cloud configuration layer to manage for MVP.
- Environment-specific configuration should be kept minimal and local to build variants if needed.
- Operational observability beyond local logging and Android platform reporting is deferred until the app has real usage data.
- The app should be designed to work offline for the launcher shell and degrade gracefully when live sources are unavailable.

### Decision Impact Analysis

**Implementation Sequence:**

1. Initialize the Android Studio Compose project.
2. Build local data layer with Room and DataStore.
3. Implement launcher shell and Start Screen tile grid.
4. Add App List and pin/unpin flow through `LauncherApps`.
5. Add Live Tile adapters and permission-gated data sources.
6. Add onboarding, weather setup, and settings surfaces.
7. Harden performance, accessibility, and release monitoring.

**Cross-Component Dependencies:**

- Room schema changes affect Start Screen, Edit Mode, live tile caching, and restore behavior.
- DataStore preference changes affect visual style, weather setup, and animation policy.
- Repository/ViewModel boundaries affect every screen and permission flow.
- The tile grid layout engine is the main performance risk and influences the Compose-vs-custom-view fallback decision.
- Permission gating directly affects the availability of SMS, Phone, Calendar, and Weather functionality.

## Implementation Patterns & Consistency Rules

### Pattern Categories Defined

**Critical Conflict Points Identified:**

6 areas where AI agents could make different choices and break consistency

### Naming Patterns

**Database Naming Conventions:**

- Tables: lowercase plural snake_case. Example: `tiles`, `tile_snapshots`, `app_entries`, `launcher_settings`
- Columns: lowercase snake_case. Example: `tile_id`, `created_at`, `last_updated_at`
- Primary key: `id`
- Foreign keys: `*_id`
- Indexes: `idx_<table>_<column>`. Example: `idx_tiles_position`, `idx_app_entries_package_name`

**Code Naming Conventions:**

- Kotlin classes, composables, and ViewModels: PascalCase. Example: `StartScreenViewModel`, `TileGrid`, `WeatherSetupScreen`
- Functions, properties, events: camelCase. Example: `loadTiles()`, `onTileMoved()`, `weatherLocation`
- Files: match the primary class/composable name. Example: `StartScreenScreen.kt`, `TileRepository.kt`
- Packages: all lowercase, feature-oriented. Example: `ui.startscreen`, `data.tiles`, `domain.weather`

### Structure Patterns

**Project Organization:**

- Organize by feature, not by technical type alone.
- Every major surface gets its own UI + ViewModel + repository boundary where needed.
- Shared primitives live in `core` or `common` only when reused by multiple surfaces.

Recommended feature layout:

- `startscreen`
- `applist`
- `editmode`
- `settings`
- `weather`
- `onboarding`
- `permissions`
- `tilecore`
- `datacore`

**File Structure Patterns:**

- UI composables stay near the feature they serve.
- Repositories stay with the data source they wrap.
- Mappers live beside the boundary they translate across.
- Tests follow the same feature structure as production code.
- Static assets are grouped by launcher surface, not dumped into one generic folder.

### Format Patterns

**State Formats:**

- Screen state must be immutable.
- Use explicit sealed states for launcher surfaces. Example: `Loading`, `Ready`, `Empty`, `Error`, `NeedsPermission`, `Stale`, `Unavailable`

**Data Exchange Formats:**

- Internal contracts use Kotlin data classes.
- External provider payloads are mapped into internal models immediately.
- Timestamp storage uses epoch millis in persistence, converted for UI at the edge.
- UI-facing text stays localized and is never persisted as the source of truth for logic.

### Communication Patterns

**Event System Patterns:**

- UI sends explicit action events upward. Example: `onTileMoved`, `onTileResized`, `onPinRequested`
- ViewModels expose state downward via `StateFlow`.
- One-shot effects use `SharedFlow` or an equivalent event channel.
- No feature should mutate shared state directly from UI code.

**State Management Patterns:**

- UDF is mandatory.
- Repository produces data.
- ViewModel owns screen state.
- Compose reads state and sends actions only.
- No direct database access from composables.

### Process Patterns

**Error Handling Patterns:**

- Errors must be typed, not stringly-typed.
- User-facing errors should be short and actionable.
- Technical details belong in logs, not on the Start Screen.
- Permission loss must degrade to a usable launcher state, not a blocking screen.

**Loading State Patterns:**

- Do not use global blocking loaders for the home screen.
- Prefer local skeletons, stale cached tile state, or placeholder content.
- App List and Settings may show local loading, but the launcher shell must remain usable.

### Enforcement Guidelines

**All AI Agents MUST:**

- Use the feature-based package structure.
- Keep UI, ViewModel, repository, and model boundaries explicit.
- Use immutable UI state and explicit actions.
- Preserve Room/DataStore separation.
- Route permission failures into `NeedsPermission` or `Unavailable`, not crashes.

**Pattern Enforcement:**

- Verify new code against these rules during implementation and review.
- Document violations in the relevant story or architecture note.
- Update patterns only through an explicit architecture revision, not ad hoc in implementation.

### Pattern Examples

**Good Examples:**

- `TileRepository` returns `Flow<List<TileState>>`
- `StartScreenViewModel` exposes `StateFlow<StartScreenUiState>`
- `TileEntity`, `TileDao`, `TileDatabase`
- `onWeatherLocationSelected()`
- `NeedsPermission` state for missing notification access

**Anti-Patterns:**

- UI directly reading from Room DAOs
- Mutable shared state spread across composables
- Hardcoded strings used as logic keys
- Empty tiles when cached live data already exists
- Treating permission revocation as an app-wide failure
