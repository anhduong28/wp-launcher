---
stepsCompleted: [1, 2, 3, 4]
inputDocuments:
  - "_bmad-output/planning-artifacts/prds/prd-WindowsPhone Launcher-2026-06-02/prd.md"
  - "_bmad-output/planning-artifacts/architecture.md"
  - "_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md"
  - "_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md"
workflowType: 'epics-and-stories'
project_name: 'WindowsPhone Launcher'
user_name: 'Edward'
date: '2026-06-03'
lastStep: 4
status: 'complete'
completedAt: '2026-06-04'
---

# WindowsPhone Launcher - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for WindowsPhone Launcher, decomposing the requirements from the PRD, UX Design if it exists, and Architecture requirements into implementable stories.

## Requirements Inventory

### Functional Requirements

FR1: Show notification-derived Live Tile status inside supported app Tiles, such as unread counts or short status text, while keeping SMS and Phone content count-only/no-preview in MVP.

FR2: Provide first-class core Live Tiles for Clock, Weather, SMS, Phone, and Calendar, prioritized Weather, Clock, SMS, Phone, then Calendar.

FR3: Animate Live Tile content inside fixed Tile boundaries with MVP flip or face-switching behavior while preserving launcher responsiveness.

FR4: Support MVP Tile sizes `1x1`, `2x2`, and `4x2` in a mixed-size Start Screen Tile Grid.

FR5: Allow users to resize supported Tiles between available MVP sizes without overlap and without losing app association.

FR6: Launch the associated installed app when a user taps an app Tile, and open the relevant launcher-owned surface for first-class launcher Tiles.

FR7: Enter Start Screen edit mode when the user long presses a Tile.

FR8: Allow users to pin an installed app to the Start Screen.

FR9: Allow users to unpin a Tile from the Start Screen without uninstalling the associated app.

FR10: Allow users to move and rearrange Tiles, persist the updated layout, and prevent overlapping Tiles.

FR11: Render Start Screen Tiles with vivid varied colors by default while preserving readable icon/text contrast.

FR12: Allow users to enable unified accent color mode where supported Tiles use one selected accent color.

FR13: Use a classic black Start Screen background by default.

FR14: Allow users to set an optional image background behind the Tile Grid while preserving Tile readability.

FR15: Provide a vertical alphabetical App List of installed launchable apps with icon and app name.

FR16: Open the App List through a right-to-left horizontal swipe from the Start Screen without interfering with vertical Start Screen scrolling.

FR17: Return from the App List to the Start Screen through a left-to-right horizontal swipe without interfering with vertical App List scrolling.

FR18: Return from the App List to the Start Screen when Android Back is pressed after keyboard handling.

FR19: Provide App List search that filters installed apps by name as the user types and restores the full list when cleared.

FR20: Allow users to pin an app from the App List to the Start Screen.

FR21: Provide a color mode setting for default multi-color Tile styling versus unified accent color mode.

FR22: Provide an accent color picker for unified accent color mode.

FR23: Provide a background setting for classic black background versus selected image background.

FR24: Allow users to configure Weather Tile location through manual city/location entry by default and optional auto-location opt-in.

FR25: Provide a Live Tile animation toggle where disabled animation preserves current single-face information where available.

### NonFunctional Requirements

NFR1: The launcher must be smooth and light enough for daily primary launcher use, supporting the 7-day primary launcher tolerance success metric.

NFR2: Tile animations must preserve responsiveness during scroll, tap, edit, and app launch interactions.

NFR3: The Start Screen Tile Grid, drag/reorder, drag resize, and Live Tile flip animation must pass a performance prototype gate; fallback to custom View/custom layout or deterministic edit controls if needed.

NFR4: The launcher must remain usable when optional permissions or external data sources are unavailable, skipped, revoked, stale, or failing.

NFR5: The app must be local-first for MVP, with no backend, no authentication, no cloud sync, and local persistence for layout/settings/state.

NFR6: Sensitive data must be minimized: SMS/Phone/Calendar Tiles are count-only in MVP, no previews, no caller/message details, and no direct SMS/Call Log permissions without explicit architecture revision.

NFR7: Android and Google Play policy-sensitive areas must be treated explicitly: package visibility, notification access, Calendar permission, location permission, and weather provider terms.

NFR8: Weather provider terms, attribution, quota, privacy, and public free-app compatibility must be verified before public release.

NFR9: Accessibility must provide at least 48dp interactive hit areas, TalkBack labels/actions, predictable focus order, readable contrast, and non-drag alternatives for edit actions.

NFR10: Reduced-motion behavior must stop Live Tile flip motion when Android system animation reduction applies, while preserving the in-app animation toggle.

NFR11: Text/icon contrast must meet readability expectations, including contrast-safe foreground changes for light Tile colors or user-selected accents.

NFR12: MVP is Android phone portrait-first; landscape and tablet are deferred, while status/navigation bars and Android gesture navigation must be accounted for.

NFR13: Live Tile work must be battery-conscious: animate only visible/supported Tiles, avoid high-frequency polling, use caching, and use WorkManager for low-priority weather refresh.

NFR14: App List and launcher behavior must use Android launcher expectations and avoid destabilizing Home/Back behavior.

NFR15: User-facing permission copy must be plain, low-pressure, and avoid coercive language.

### Additional Requirements

- Initialize the implementation from Android Studio `Empty Activity` with Kotlin and Compose enabled; this is the required Epic 1 Story 1 starter step.
- Select and record exact stable dependency versions during project scaffold because no Android source project exists yet.
- Configure the app as an Android launcher/home app through `ACTION_MAIN` and `CATEGORY_HOME` manifest behavior.
- Prefer `LauncherApps` for app discovery and launch; avoid broad package visibility unless justified, declared, and reviewed.
- Use Jetpack Compose as the default UI layer, with a custom View/custom layout fallback for Start Screen Tile Grid if prototype performance fails.
- Use Room for structured launcher data: pinned Tiles, layout, sizes, ordering, app metadata cache, and cached Live Tile snapshots.
- Use DataStore for small preferences and flags: accent color, color mode, background mode, animation toggle, weather setup state, onboarding completion.
- Use WorkManager for low-priority weather refresh and avoid always-on foreground services.
- Keep repositories as data producers, ViewModels as screen-state owners, Compose as state consumer/action emitter, and no direct database access from composables.
- Use immutable UI state, `StateFlow` for state, one-shot effects through `SharedFlow` or equivalent event channel, `suspend` functions for mutations and one-off reads.
- Keep ViewModels Android-agnostic and free of direct Activity/Fragment references.
- Use feature-first package organization: `startscreen`, `applist`, `editmode`, `settings`, `weather`, `onboarding`, `permissions`, `tilecore`, `datacore`.
- Keep permission gating isolated behind explicit states such as `NeedsPermission`, `Unavailable`, `Stale`, and `Error`.
- Preserve last known good Live Tile data so transient failures do not blank Tiles.
- Keep external provider payloads mapped immediately into internal models.
- Use epoch millis for persisted timestamps and convert for UI at the edge.
- Add ADRs during implementation for package visibility, weather provider, and Compose/custom View grid decision.
- Add benchmark/performance coverage after the first Start Screen prototype exists.

### UX Design Requirements

UX-DR1: Implement a classic black default canvas (`#000000`) with solid-color Tiles and image backgrounds appearing only behind the Tile Grid/in gaps.

UX-DR2: Implement the Windows Phone classic Tile palette: cyan `#00BCF2`, blue `#0078D7`, green `#00A300`, red `#E51400`, purple `#800080`, orange `#F7630C`, plus disabled Tile `#3A3A3A`.

UX-DR3: Implement contrast-safe foreground handling: white foreground by default, dark foreground/overlay when cyan, green, orange, or user-selected accent fails contrast.

UX-DR4: Implement typography tokens for Tile labels (`12sp`, weight `500`, line height `14sp`), info-first Tiles (`22sp`, weight `500`, line height `26sp`), App List names (`16sp`, weight `500`, line height `22sp`), and alphabet headers (`13sp`, weight `500`, line height `18sp`).

UX-DR5: Implement sharp-cornered geometry for Tiles, controls, and inputs with `0px` radius; avoid rounded Material card surfaces in MVP.

UX-DR6: Implement a 4-column Start Screen Tile Grid with `4dp` Tile gap and `8dp` screen edge spacing.

UX-DR7: Keep the Start Screen minimal: no header, no persistent search bar, no settings shortcut above the grid.

UX-DR8: Implement Tile Grid sizes where `1x1` spans 1 column, `2x2` spans 2 columns, and `4x2` spans all 4 columns.

UX-DR9: Implement Live Tile, Non-Animated Live Tile, Static App Tile, Weather Tile, Clock Tile, SMS Tile, Phone Tile, Calendar Tile, Settings Tile, App List Row, App List Search, Edit Mode Tile, Edit Control, Onboarding Step, Weather Setup, and Settings Row component patterns.

UX-DR10: Weather Tile must be info-first, default to `4x2`, show `Set location` before setup, and route tap to Weather Setup.

UX-DR11: Clock Tile must be info-first and digital.

UX-DR12: SMS, Phone, and Calendar Tiles must be icon-first with count secondary and no previews.

UX-DR13: Edit Mode Tile must show active/lifted/selected treatment and visible corner controls for unpin and resize, backed by 48dp non-overlapping hit targets.

UX-DR14: App List must use a vertical Windows Phone-style list with app icon/name rows, alphabet headers, and a fixed search field at top.

UX-DR15: Settings must be reachable through a launcher-owned App List entry and optional pinned Settings Tile, not a global header button on the Start Screen.

UX-DR16: Onboarding must include Welcome, Set as Default Launcher, and optional Live Tile permissions steps.

UX-DR17: If default launcher setup is canceled, onboarding must offer preview mode plus `Try again` and `Continue preview` paths.

UX-DR18: Notification access and Calendar access are optional in onboarding/settings; location is deferred to Weather Setup.

UX-DR19: Permission skipped/revoked states must degrade Tiles to ordinary/static states without inline nag badges.

UX-DR20: Weather Setup must support manual city/location search first, selectable results for ambiguous cities, and optional auto-location as secondary.

UX-DR21: Open-Meteo attribution or terms notices should appear in Weather Setup/Settings unless legally required on Start Screen.

UX-DR22: Weather loading/error/stale states must remain inside the Tile boundary and prefer last known value when available.

UX-DR23: Implement right-to-left swipe from Start Screen to App List and left-to-right swipe back, while avoiding conflicts with vertical scrolling.

UX-DR24: Android Back from App List must close the keyboard first when open, then return to Start Screen.

UX-DR25: In Edit Mode, dragging releases to a snapped 4-column grid position; grid reflow must preserve unaffected relative order where possible.

UX-DR26: Tile resize must expand from current top-left anchor unless off-grid, then use nearest valid anchor.

UX-DR27: Layout persistence must occur on release, not continuously during drag.

UX-DR28: Invalid drop/resize must restore the last valid position/size and announce failure reason when accessibility services are active.

UX-DR29: TalkBack labels must identify Tile name, role, size, grid position, visible live count/info, and available actions.

UX-DR30: Edit Mode accessibility actions must support move before/after/up/down, resize to `1x1`/`2x2`/`4x2`, unpin, and exit Edit Mode.

UX-DR31: Start Screen focus traversal must follow visual row-major grid order and update after reflow.

UX-DR32: App List search focus order must be search field then list results.

UX-DR33: Live Tile flip cadence must be 8-12 seconds per Tile with phase offsets and only run while Start Screen is visible, foregrounded, Tile visible, and user not editing/dragging.

UX-DR34: App target missing state must keep the pinned Tile visible but unavailable, and tap should offer Tile removal if target no longer resolves.

UX-DR35: Background image missing state must fall back to classic black background.

UX-DR36: Microcopy must be plain and low-pressure, e.g. `Set location`, `Enable notification access for counts.`, `Skip for now`, `Use as default launcher`.

### FR Coverage Map

FR1: Epic 5 - Notification-derived Live Tile counts/status and privacy-safe handling.

FR2: Epic 5 - First-class core Live Tiles for Clock, Weather, SMS, Phone, and Calendar.

FR3: Epic 5 - Live Tile animation behavior, with Epic 6 validation.

FR4: Epic 2 - MVP Tile sizes in the Start Screen Tile Grid.

FR5: Epic 2 - Tile resizing behavior.

FR6: Epic 1, Epic 2, Epic 3 - launcher-owned shell, Tile launch, and app launch behavior.

FR7: Epic 2 - Enter Start Screen edit mode.

FR8: Epic 2, Epic 3 - pin apps through Start Screen/App List workflows.

FR9: Epic 2 - Unpin Tiles from Start Screen.

FR10: Epic 2 - Rearrange Tiles and persist layout.

FR11: Epic 4 - Default multi-color Tile styling.

FR12: Epic 4 - Unified accent color mode.

FR13: Epic 1, Epic 4 - black Start Screen baseline and full theme behavior.

FR14: Epic 4 - Optional image background behind Tiles.

FR15: Epic 3 - Alphabetical App List.

FR16: Epic 3 - Swipe from Start Screen to App List.

FR17: Epic 3 - Reverse swipe from App List to Start Screen.

FR18: Epic 3 - Android Back from App List.

FR19: Epic 3 - App List search.

FR20: Epic 3 - Pin from App List.

FR21: Epic 4 - Color mode setting.

FR22: Epic 4 - Accent color picker.

FR23: Epic 4 - Background setting.

FR24: Epic 5 - Weather location setting.

FR25: Epic 4, Epic 5 - animation preference plus Live Tile behavior.

## Epic List

### Epic 1: Launcher Foundation & First Start

Users can install and open WindowsPhone Launcher, set it as the Android default launcher or continue in preview mode, and land on a usable Windows Phone-style Start Screen shell that establishes the project foundation for all later launcher features.

**FRs covered:** FR6 partial, FR13 partial.

**Implementation notes:** Scaffold Android Studio `Empty Activity` with Kotlin and Compose; select and record Gradle/Kotlin/AndroidX dependency versions; create launcher manifest declarations for home behavior; establish feature-first package structure; implement initial onboarding with Welcome, Set as Default Launcher, and preview fallback; create initial Start Screen route with classic black background. Include Settings/App List placeholders only if needed for navigation stability; full behavior belongs to later epics.

### Epic 2: Start Screen Tile Grid & Editing

Users can build and maintain a personal Start Screen with mixed Tile sizes, pin/unpin behavior, resizing, moving, reflow, persistence, and accessible edit alternatives.

**FRs covered:** FR4, FR5, FR6, FR7, FR8, FR9, FR10.

**Implementation notes:** This is the core launcher usability epic. It includes the Tile Grid layout engine, Tile persistence, edit mode, resize/move/reflow rules, accessible non-drag alternatives, and the performance prototype gate because these are inseparable from editing quality.

### Epic 3: App List, Search & Pinning

Users can browse installed apps in a Windows Phone-style alphabetical App List, search apps, launch apps, pin apps to Start Screen, and navigate between Start Screen/App List through gestures and Android Back.

**FRs covered:** FR15, FR16, FR17, FR18, FR19, FR20, FR8 partial, FR6 partial.

**Implementation notes:** Builds on Epic 2's pinning model but delivers a complete independent app access workflow through `LauncherApps`, App List search, gesture navigation, and Android Back handling.

### Epic 4: Visual Style, Theme Settings & Motion Control

Users get the recognizable Windows Phone 8.1 visual identity and can adjust color mode, accent color, background, and Live Tile animation preference without harming readability or accessibility.

**FRs covered:** FR11, FR12, FR13, FR14, FR21, FR22, FR23, FR25.

**Implementation notes:** Groups design system, settings controls, contrast rules, black/image background handling, and animation toggle because they modify the same visual system and should remain consistent.

### Epic 5: Core Live Tiles & Permission-Gated Data

Users can see useful glanceable Live Tile information for Clock, Weather, SMS, Phone, and Calendar, with privacy-safe permission handling and stale/fallback states.

**FRs covered:** FR1, FR2, FR3, FR24, FR25 partial.

**Implementation notes:** Includes Clock, Weather setup/provider adapter/cache, notification-derived SMS/Phone counts, Calendar count, permission flows, count-only handling, stale/error states, and Live Tile flip behavior.

### Epic 6: Launcher Hardening, Accessibility & Release Readiness

Users can rely on the launcher as a daily home screen because performance, accessibility, policy-sensitive behavior, error states, and release readiness are validated.

**FRs covered:** Cross-cutting validation for FR1-FR25.

**Implementation notes:** Captures Macrobenchmark/performance gates, low/mid-range device validation, TalkBack audit, reduced-motion verification, Play policy/package visibility/weather terms checks, regression coverage, and final MVP hardening.

## Epic 1: Launcher Foundation & First Start

Users can install and open WindowsPhone Launcher, set it as the Android default launcher or continue in preview mode, and land on a usable Windows Phone-style Start Screen shell that establishes the project foundation for all later launcher features.

### Story 1.1: Scaffold Kotlin Compose Launcher Project

As a developer,
I want the Android Kotlin Compose project scaffolded with recorded dependency versions,
So that future launcher features have a stable implementation foundation.

**Acceptance Criteria:**

**Given** the repository has no Android source project
**When** the project scaffold is created
**Then** it contains an Android application module using Kotlin and Jetpack Compose
**And** Gradle, Kotlin, Android Gradle Plugin, Compose, and core AndroidX dependency versions are recorded in build configuration or version catalog
**And** the package structure includes `core`, `datacore`, `tilecore`, `startscreen`, `applist`, `editmode`, `settings`, `weather`, `onboarding`, and `permissions` packages or placeholders
**And** the app builds successfully in debug mode

### Story 1.2: Configure Android Launcher Entry Point

As an Android user,
I want WindowsPhone Launcher to be recognizable by Android as a home launcher,
So that I can choose it as my default launcher.

**Acceptance Criteria:**

**Given** the Android app scaffold exists
**When** the app manifest is configured
**Then** the main launcher activity declares the correct home/launcher intent behavior for Android launcher selection
**And** the app can be opened normally during development
**And** launcher declarations are isolated in `AndroidManifest.xml` for review
**And** no broad package visibility or sensitive permissions are introduced in this story

### Story 1.3: First-Run Welcome and Default Launcher Flow

As a first-time user,
I want a simple onboarding path to set the app as my default launcher or continue in preview mode,
So that I can start using the launcher without being blocked.

**Acceptance Criteria:**

**Given** the user opens the app for the first time
**When** onboarding starts
**Then** the user sees a plain welcome step describing the WP-style living Start Screen promise
**And** the user can choose `Use as default launcher` to enter Android's default launcher selection flow
**And** if the user cancels the Android launcher selection flow, the app shows a preview-mode explanation
**And** the preview-mode state offers `Try again` and `Continue preview` paths
**And** copy is low-pressure and does not imply optional permissions are required

### Story 1.4: Initial Start Screen Shell

As a user,
I want to land on a simple Windows Phone-style Start Screen shell after onboarding,
So that I can see the launcher's core visual direction before full Tile behavior exists.

**Acceptance Criteria:**

**Given** onboarding is completed or preview mode is selected
**When** the app navigates to the Start Screen
**Then** the Start Screen displays a classic black background
**And** the screen is phone portrait-first and accounts for system bars/gesture navigation
**And** no header, persistent search bar, or settings shortcut appears above the grid area
**And** any temporary placeholder content is clearly internal to the shell and does not imply completed Tile Grid behavior
**And** the shell remains usable if optional permissions have not been granted

### Story 1.5: Persist First-Run and Preview State

As a returning user,
I want the launcher to remember whether onboarding was completed or preview mode was selected,
So that I do not repeat first-run setup unnecessarily.

**Acceptance Criteria:**

**Given** the user completes onboarding or selects preview mode
**When** the app is closed and reopened
**Then** the app routes to the correct Start Screen shell state instead of restarting onboarding
**And** the state is stored locally using the architecture-approved lightweight settings approach
**And** no Room schema or Tile layout persistence is introduced before it is needed by later epics
**And** default-launcher cancellation remains recoverable through the preview flow

## Epic 2: Start Screen Tile Grid & Editing

Users can build and maintain a personal Start Screen with mixed Tile sizes, pin/unpin behavior, resizing, moving, reflow, persistence, and accessible edit alternatives.

### Story 2.1: Render Persisted Start Screen Tile Grid

As a launcher user,
I want the Start Screen to render my pinned Tiles in a 4-column grid,
So that I can see a Windows Phone-style home layout.

**Acceptance Criteria:**

**Given** the Start Screen shell exists
**When** the Start Screen loads
**Then** it renders a 4-column Tile Grid with `4dp` gaps and `8dp` screen edge spacing
**And** supported Tile sizes are represented as `1x1`, `2x2`, and `4x2` spans
**And** Tiles never overlap visually
**And** the grid uses the classic black background from Epic 1
**And** initial placeholder/default Tiles are clearly modeled as persisted Tile records or seed data needed for this story

### Story 2.2: Store Tile Layout State

As a launcher user,
I want my Tile layout to be saved locally,
So that my Start Screen remains the same after reopening the launcher.

**Acceptance Criteria:**

**Given** Tiles are shown on the Start Screen
**When** the layout state is saved
**Then** Tile identity, size, order/position, and launch target metadata are stored locally using Room
**And** Room stores only structured launcher data needed for Tile layout in this story
**And** DataStore remains reserved for lightweight settings
**And** reopening the app restores the saved Tile layout
**And** timestamp fields use epoch millis if persisted

### Story 2.3: Launch App or Launcher-Owned Tile from Start Screen

As a launcher user,
I want to tap a Tile to open its target,
So that the Start Screen works as a launcher entry point.

**Acceptance Criteria:**

**Given** a Tile has an installed app target
**When** the user taps the Tile
**Then** the correct installed app is launched through the architecture-approved launch mechanism
**And** if the Tile is launcher-owned, tapping opens the corresponding launcher-owned surface or placeholder route
**And** if the target cannot be resolved, the Tile remains visible in an unavailable state
**And** unresolved target handling does not crash the launcher

### Story 2.4: Enter and Exit Start Screen Edit Mode

As a launcher user,
I want to long press a Tile to enter edit mode,
So that I can manage my Start Screen layout.

**Acceptance Criteria:**

**Given** the Start Screen displays at least one Tile
**When** the user long presses a Tile
**Then** Edit Mode becomes active
**And** the selected Tile receives active/lifted/selected treatment
**And** visible corner controls are shown for edit actions where available
**And** each visible edit control has a non-overlapping 48dp hit target
**And** the user can exit Edit Mode without changing the layout

### Story 2.5: Pin and Unpin Tiles on the Start Screen

As a launcher user,
I want to add and remove Tiles from the Start Screen,
So that the layout reflects my priorities.

**Acceptance Criteria:**

**Given** Edit Mode or an internal pin action is available
**When** a Tile is pinned to the Start Screen
**Then** the Tile appears in the first valid available grid position
**And** the Tile has a valid default size
**And** the Tile is associated with the correct target metadata
**And** when the user unpins a Tile, it is removed from the Start Screen without uninstalling the app
**And** the updated layout persists after leaving Edit Mode

### Story 2.6: Move Tiles with Deterministic Reflow

As a launcher user,
I want to move Tiles around the Start Screen,
So that I can arrange my layout manually.

**Acceptance Criteria:**

**Given** Edit Mode is active
**When** the user drags a Tile and releases it
**Then** the insertion target is calculated from the Tile center at release
**And** the Tile snaps to the 4-column grid
**And** affected Tiles reflow without overlap
**And** unaffected Tiles preserve relative order where possible
**And** gaps are compacted top-to-bottom and left-to-right
**And** the final layout persists on release

### Story 2.7: Resize Tiles Between MVP Sizes

As a launcher user,
I want to resize supported Tiles,
So that important Tiles can occupy more space on my Start Screen.

**Acceptance Criteria:**

**Given** Edit Mode is active
**When** the user resizes a supported Tile
**Then** the available target sizes are limited to `1x1`, `2x2`, and `4x2`
**And** unsupported target sizes are omitted or prevented
**And** resizing preserves the Tile's app or launcher-owned association
**And** resize expands from the current top-left anchor unless doing so would leave the Tile off-grid
**And** if more space is needed, affected Tiles reflow downward without overlap
**And** the final layout persists on release

### Story 2.8: Accessible Tile Editing Actions

As a launcher user using accessibility tools,
I want non-drag actions for moving, resizing, and unpinning Tiles,
So that I can edit the Start Screen without precise gestures.

**Acceptance Criteria:**

**Given** a Tile is focused in Edit Mode
**When** accessibility actions are requested
**Then** the Tile exposes actions for move before/after/up/down, resize to `1x1`/`2x2`/`4x2`, unpin, and exit Edit Mode where applicable
**And** TalkBack labels identify Tile name, role, size, grid position, visible live count/info when available, and available actions
**And** Start Screen focus traversal follows visual row-major grid order
**And** focus order updates after Tile reflow
**And** invalid actions announce a short failure reason when accessibility services are active

### Story 2.9: Validate Tile Grid Performance Prototype Gate

As a launcher user,
I want Tile Grid editing and scrolling to remain responsive,
So that the launcher feels usable as a daily home screen.

**Acceptance Criteria:**

**Given** the Tile Grid supports rendering, move, resize, and Edit Mode
**When** the grid is tested in a release-like build with realistic Tile counts
**Then** scrolling, drag/reorder, resize, and app launch interactions preserve visible responsiveness
**And** results are documented as pass, partial pass, or fail
**And** if the result is partial pass, Compose remains acceptable for non-grid surfaces while Start Screen fallback is documented
**And** if the result is fail, deterministic edit controls or custom View/custom layout fallback is documented before later epics depend on free drag behavior

## Epic 3: App List, Search & Pinning

Users can browse installed apps in a Windows Phone-style alphabetical App List, search apps, launch apps, pin apps to Start Screen, and navigate between Start Screen/App List through gestures and Android Back.

### Story 3.1: Discover Launchable Apps for App List

As a launcher user,
I want the launcher to discover installed launchable apps,
So that I can browse and open apps from the App List.

**Acceptance Criteria:**

**Given** the app has launcher foundation and Start Screen navigation
**When** the App List data source loads
**Then** it retrieves launchable app entries using `LauncherApps` or the architecture-approved launcher app source
**And** each app entry includes display name, icon, package/activity identity, and launch target metadata
**And** broad package visibility is not introduced unless explicitly justified and documented
**And** app discovery errors are represented as typed states instead of crashing the launcher

### Story 3.2: Render Alphabetical App List

As a launcher user,
I want installed apps shown in a vertical alphabetical list,
So that I can browse apps in the Windows Phone style.

**Acceptance Criteria:**

**Given** launchable app entries are available
**When** the App List screen opens
**Then** apps are sorted alphabetically by display name
**And** each row shows app icon and app name
**And** alphabet headers appear for visual grouping
**And** each app row has at least a 48dp interactive height
**And** the App List is vertical and does not use an Android grid drawer pattern

### Story 3.3: Open App List from Start Screen Swipe

As a launcher user,
I want to swipe from the Start Screen to the App List,
So that navigation feels like Windows Phone.

**Acceptance Criteria:**

**Given** the user is on the Start Screen
**When** the user swipes right-to-left horizontally
**Then** the launcher transitions to the App List
**And** the gesture does not interfere with vertical Start Screen scrolling
**And** the transition does not destabilize Tile Grid state
**And** if Edit Mode is active, the app handles or blocks the gesture consistently without losing layout changes

### Story 3.4: Return to Start Screen from App List

As a launcher user,
I want to return from the App List to the Start Screen with a reverse swipe or Android Back,
So that navigation remains predictable.

**Acceptance Criteria:**

**Given** the user is on the App List
**When** the user swipes left-to-right
**Then** the launcher returns to the Start Screen
**And** the gesture does not interfere with vertical App List scrolling
**And** when Android Back is pressed with no keyboard open, the launcher returns to the Start Screen
**And** Android Back does not exit, close, or destabilize the launcher from the App List

### Story 3.5: Search Apps from App List

As a launcher user,
I want to search installed apps from the App List,
So that I can quickly find an app.

**Acceptance Criteria:**

**Given** the App List contains launchable apps
**When** the user types in the fixed search field
**Then** results filter immediately by app name
**And** clearing the query restores the full alphabetical list
**And** when no results match, the App List shows `No apps found.`
**And** focus order is search field then list results
**And** Android Back closes the keyboard first when it is open

### Story 3.6: Launch App from App List

As a launcher user,
I want to tap an app row in the App List,
So that I can open installed apps without pinning them first.

**Acceptance Criteria:**

**Given** an app row has a valid launch target
**When** the user taps the row
**Then** the correct installed app launches
**And** launch failures are represented as an actionable unavailable/error state
**And** the launcher does not crash if the target disappears before launch
**And** app launch behavior uses the same launch boundary as Start Screen Tiles

### Story 3.7: Pin App from App List to Start Screen

As a launcher user,
I want to pin an app from the App List to the Start Screen,
So that frequently used apps become Tiles.

**Acceptance Criteria:**

**Given** the user is viewing an app row in the App List
**When** the user invokes the pin action
**Then** a Tile is created on the Start Screen using the correct app identity and launch target
**And** the Tile uses a valid default size
**And** the Tile is inserted into the first valid available grid position
**And** pin success is confirmed without interruptive permission prompts
**And** focus moves predictably after pinning

### Story 3.8: Expose App List Accessibility Actions

As a launcher user using accessibility tools,
I want App List rows and search to be accessible,
So that I can find, launch, and pin apps without visual-only interactions.

**Acceptance Criteria:**

**Given** the App List is displayed
**When** accessibility services inspect the screen
**Then** the search field has a clear label and appears before results in focus order
**And** each app row announces app name and role
**And** each app row exposes actions to launch and pin where applicable
**And** alphabet headers do not trap focus unnecessarily
**And** pin success or launch failure feedback is available to screen reader users

## Epic 4: Visual Style, Theme Settings & Motion Control

Users get the recognizable Windows Phone 8.1 visual identity and can adjust color mode, accent color, background, and Live Tile animation preference without harming readability or accessibility.

### Story 4.1: Implement Windows Phone Visual Tokens

As a launcher user,
I want the launcher to use a recognizable Windows Phone-style visual system,
So that the Start Screen feels sharp, colorful, and familiar.

**Acceptance Criteria:**

**Given** the app has Start Screen and App List surfaces
**When** visual tokens are implemented
**Then** the design system includes classic black `#000000`, background overlay `#111111`, primary white text, dark text for light Tiles, muted/supporting text, WP-style Tile colors, focus colors, and disabled Tile color
**And** typography tokens are implemented for Tile labels, info Tiles, App List names, and alphabet headers
**And** spacing tokens include `4dp` Tile gap, `8dp` screen edge, App List row gap, settings row gap, and 48dp minimum touch target
**And** shape tokens keep Tiles, controls, and inputs square with `0px` radius
**And** these tokens are used through shared design primitives rather than duplicated constants

### Story 4.2: Apply Default Multi-Color Tile Styling

As a launcher user,
I want default Tiles to be vivid and varied,
So that the Start Screen feels alive rather than monochrome.

**Acceptance Criteria:**

**Given** Tiles render on the Start Screen
**When** default visual styling is applied
**Then** Tiles use a vivid multi-color assignment from the approved WP-style palette
**And** color assignment remains stable across app restarts unless the Tile is explicitly changed by future behavior
**And** Tile labels/icons remain readable on every assigned color
**And** Tiles remain solid color, not gradients or translucent wallpaper overlays
**And** the Start Screen retains its classic black background by default

### Story 4.3: Enforce Contrast-Safe Tile Foregrounds

As a launcher user,
I want Tile text and icons to remain readable on all Tile colors,
So that visual style does not reduce usability.

**Acceptance Criteria:**

**Given** a Tile uses a light or user-selected color
**When** foreground content is rendered
**Then** the launcher chooses white, dark, or overlay treatment based on contrast needs
**And** normal Tile labels/counts meet the target readability threshold
**And** cyan, green, orange, and user-selected accents that fail white contrast use dark foreground or overlay treatment
**And** contrast logic is shared across Start Screen, Settings previews, and future Live Tile surfaces
**And** no Tile uses hardcoded foreground color if it bypasses contrast rules

### Story 4.4: Build Launcher Settings Surface

As a launcher user,
I want a focused launcher settings screen,
So that I can adjust only the MVP options that affect the Start Screen and Live Tiles.

**Acceptance Criteria:**

**Given** the launcher has a settings route or launcher-owned Settings entry
**When** the user opens Launcher Settings
**Then** settings include color mode, accent color, background, weather location entry point, and Live Tile animation toggle
**And** settings rows are square, dense, and clear with no card-based layout
**And** every setting row has at least a 48dp hit target
**And** Settings is reachable through the launcher-owned App List entry and optional Settings Tile path
**And** no global settings shortcut is added above the Start Screen grid

### Story 4.5: Implement Color Mode and Accent Color Settings

As a launcher user,
I want to switch between multi-color Tiles and unified accent color mode,
So that I can choose between lively and consistent Tile styling.

**Acceptance Criteria:**

**Given** the user opens Launcher Settings
**When** the user selects color mode
**Then** they can choose default multi-color mode or unified accent color mode
**And** when unified accent color mode is enabled, supported Tiles use the selected accent color
**And** the user can select an accent color from approved or valid choices
**And** selected mode and accent color persist locally through DataStore
**And** contrast-safe foreground handling applies to the selected accent color

### Story 4.6: Implement Background Setting

As a launcher user,
I want to choose between classic black and an image background,
So that I can personalize the Start Screen without losing Tile readability.

**Acceptance Criteria:**

**Given** the user opens Launcher Settings
**When** the user changes background mode
**Then** they can select classic black background or image background
**And** selecting image background lets the user choose an image source supported by Android
**And** the image appears behind the Tile Grid and in gaps without replacing solid Tile content
**And** returning to black background removes the image from the Start Screen
**And** if the selected image becomes unavailable, the launcher falls back to classic black background
**And** background choice persists locally through DataStore

### Story 4.7: Implement Live Tile Animation Preference

As a launcher user,
I want to turn Live Tile animation on or off,
So that I can control motion on my Start Screen.

**Acceptance Criteria:**

**Given** the user opens Launcher Settings
**When** the user toggles Live Tile animation
**Then** the preference persists locally through DataStore
**And** when animation is disabled, supported future Live Tiles must render current single-face information where data exists
**And** when animation is enabled, supported future Live Tiles may use flip or face-switching behavior
**And** Android system reduced-motion/animator-duration accessibility settings override flip motion when applicable
**And** this story defines the shared animation preference contract even if full Live Tile data arrives in Epic 5

### Story 4.8: Validate Visual Accessibility and Reduced Motion

As a launcher user,
I want visual styling and motion settings to respect accessibility needs,
So that the launcher stays readable and calm for daily use.

**Acceptance Criteria:**

**Given** visual tokens, theme settings, and animation preference are implemented
**When** the Start Screen, App List, and Settings are reviewed
**Then** text and icons remain readable over Tile colors and background choices
**And** minimum 48dp touch targets are preserved in settings and edit controls touched by this epic
**And** reduced-motion behavior disables flip-style motion paths
**And** TalkBack labels for settings controls expose current value and action
**And** any accessibility gaps are documented before Epic 5 builds on the visual/live tile system

## Epic 5: Core Live Tiles & Permission-Gated Data

Users can see useful glanceable Live Tile information for Clock, Weather, SMS, Phone, and Calendar, with privacy-safe permission handling and stale/fallback states.

### Story 5.1: Define Live Tile State and Adapter Contracts

As a launcher user,
I want Live Tiles to behave consistently across data sources,
So that each Tile can show useful information without breaking the Start Screen.

**Acceptance Criteria:**

**Given** the Tile Grid and visual system exist
**When** Live Tile contracts are implemented
**Then** `tilecore` defines shared Live Tile state models for ready, stale, loading, needs setup, needs permission, unavailable, and error states
**And** Live Tile adapters map source-specific data into internal models immediately
**And** Live Tile UI consumes internal state only, not provider/platform payloads directly
**And** last known good snapshot support is defined for source failures
**And** the contracts preserve count-only/privacy rules for sensitive Tiles

### Story 5.2: Implement Digital Clock Live Tile

As a launcher user,
I want a Clock Tile that shows the current time,
So that I can glance at the Start Screen for time without opening an app.

**Acceptance Criteria:**

**Given** a Clock Tile is pinned or seeded
**When** the Start Screen is visible
**Then** the Clock Tile shows a digital clock using the info-first Tile hierarchy
**And** the time updates while the launcher is foregrounded
**And** the Tile does not shift or resize neighboring Tiles when the time changes
**And** reduced-motion and animation-off states still show current single-face time
**And** no network or sensitive permission is required

### Story 5.3: Implement Weather Setup with Manual Location

As a launcher user,
I want to configure Weather manually by city/location,
So that the Weather Tile works without granting location permission.

**Acceptance Criteria:**

**Given** the Weather Tile shows `Set location`
**When** the user taps the Weather Tile or opens weather location from Settings
**Then** Weather Setup opens with manual city/location entry as the primary path
**And** ambiguous city results are shown as selectable resolved locations
**And** optional auto-location is secondary and requires explicit opt-in
**And** if the user skips setup, the Weather Tile remains in `Set location` state
**And** Weather setup state persists locally through DataStore or approved lightweight settings storage

### Story 5.4: Implement Weather Provider Adapter and Cache

As a launcher user,
I want the Weather Tile to show current weather after setup,
So that I can glance at useful conditions from the Start Screen.

**Acceptance Criteria:**

**Given** a weather location is configured
**When** weather data is fetched
**Then** the provider adapter maps Open-Meteo or selected provider responses into internal weather state
**And** current weather appears in the Weather Tile using the info-first `4x2` hierarchy
**And** successful results are cached as last known good weather state
**And** loading, stale, and error states remain inside the Tile boundary
**And** failed refresh prefers last known good state when available
**And** provider attribution/terms notice appears in Weather Setup or Settings unless legally required on Start Screen

### Story 5.5: Schedule Weather Refresh Responsibly

As a launcher user,
I want weather to refresh without draining battery,
So that Live Tiles remain useful but lightweight.

**Acceptance Criteria:**

**Given** weather setup is complete
**When** background refresh is scheduled
**Then** WorkManager handles low-priority weather refresh
**And** refresh cadence is conservative and documented
**And** no always-on foreground service is introduced
**And** manual foreground refresh or visible-demand refresh respects caching and rate limits
**And** failed or skipped background refresh does not block launcher usability

### Story 5.6: Implement Notification Access Flow for SMS and Phone Counts

As a launcher user,
I want to optionally enable notification-derived counts,
So that SMS and Phone Tiles can show counts without exposing sensitive content.

**Acceptance Criteria:**

**Given** SMS or Phone Tiles are present
**When** the user enters onboarding or Settings permission flow
**Then** the app explains notification access with low-pressure copy
**And** the user is routed to Android notification listener settings when they choose to enable it
**And** if notification access is skipped or revoked, SMS/Phone Tiles show ordinary/static Tile state with no count
**And** no SMS message previews, caller names, or notification text previews are shown in MVP
**And** no direct SMS or Call Log permission is introduced

### Story 5.7: Show Notification-Derived SMS and Phone Counts

As a launcher user,
I want SMS and Phone Tiles to show best-effort counts when notification access is enabled,
So that I can glance at missed activity privately.

**Acceptance Criteria:**

**Given** notification access is enabled
**When** SMS or Phone-related notifications are posted or removed
**Then** the notification adapter updates count-only Tile state for supported SMS/Phone Tiles
**And** counts are best-effort and degrade gracefully if source notifications are unavailable
**And** count changes do not repeatedly interrupt normal launcher use
**And** sensitive notification content is not persisted or displayed
**And** revoking notification access clears counts on next refresh and returns Tiles to static state

### Story 5.8: Implement Calendar Permission and Count Tile

As a launcher user,
I want the Calendar Tile to show today's event count only when I grant permission,
So that I can see schedule volume without exposing event details.

**Acceptance Criteria:**

**Given** a Calendar Tile is present
**When** the user chooses to enable Calendar count
**Then** the app requests the appropriate runtime Calendar permission
**And** if permission is granted, the Calendar Tile shows today's event count only
**And** event titles, attendees, locations, and previews are not shown in MVP
**And** if permission is denied or revoked, the Calendar Tile shows ordinary/static state with no count
**And** Calendar query failures are represented as typed unavailable/error states

### Story 5.9: Implement Live Tile Flip and Face Switching

As a launcher user,
I want supported Live Tiles to animate inside their Tile boundaries,
So that the Start Screen feels alive without moving the layout.

**Acceptance Criteria:**

**Given** Live Tile data exists for a supported Tile
**When** Live Tile animation is enabled and motion is allowed
**Then** supported Tiles may flip or face-switch every 8-12 seconds with phase offsets
**And** animation occurs only inside Tile boundaries
**And** neighboring Tiles do not resize, shift, or reflow because of animation
**And** animation runs only while Start Screen is visible, app is foregrounded, the Tile is visible, and user is not editing/dragging
**And** animation stops when the user disables Live Tile animation or Android reduced-motion settings require it
**And** non-animated state still shows current single-face data where available

### Story 5.10: Validate Live Tile Privacy, Stale State, and Failure Handling

As a launcher user,
I want Live Tiles to stay useful and private when data sources fail or permissions change,
So that the launcher remains trustworthy as my home screen.

**Acceptance Criteria:**

**Given** Live Tile adapters for Weather, Clock, SMS, Phone, and Calendar exist
**When** permissions are skipped/revoked, provider calls fail, app targets disappear, or cached data becomes stale
**Then** each Tile enters the correct `NeedsSetup`, `NeedsPermission`, `Unavailable`, `Stale`, or `Error` state
**And** Start Screen remains usable with no blocking full-screen error
**And** last known good values are retained where approved by privacy rules
**And** sensitive content is never displayed or persisted beyond count-only state
**And** all state transitions are covered by tests or documented validation cases

## Epic 6: Launcher Hardening, Accessibility & Release Readiness

Users can rely on the launcher as a daily home screen because performance, accessibility, policy-sensitive behavior, error states, and release readiness are validated.

### Story 6.1: Add Launcher Performance Benchmarks

As a launcher user,
I want the launcher to stay responsive during daily use,
So that it can function as my primary home screen.

**Acceptance Criteria:**

**Given** the launcher has Start Screen, App List, edit mode, settings, and Live Tiles
**When** performance benchmark coverage is added
**Then** benchmark or profiling coverage measures cold startup into Start Screen, Start Screen vertical scroll, App List search responsiveness, edit-mode drag/reorder/resize, and Live Tile animation frame behavior
**And** benchmarks run against release-like builds where feasible
**And** results are documented with pass/fail notes or follow-up risks
**And** no benchmark requires a backend or external account

### Story 6.2: Validate Low and Mid-Range Device Usability

As a launcher user on everyday Android hardware,
I want the launcher to feel smooth and stable,
So that I can tolerate it as my primary launcher.

**Acceptance Criteria:**

**Given** the MVP feature set is implemented
**When** the launcher is tested on representative low/mid-range Android devices or emulators where physical devices are unavailable
**Then** Start Screen scroll, App List search, app launch, edit mode, Live Tile animation, and settings navigation are validated
**And** any visible jank, ANR risk, or battery concern is documented
**And** feature count is not expanded to mask smoothness problems
**And** release readiness is blocked if primary launcher use feels heavy, laggy, or disruptive

### Story 6.3: Complete Accessibility Audit and Fix Pass

As a launcher user using accessibility tools,
I want the launcher's core workflows to be accessible,
So that I can use it without relying on precise touch or visual-only cues.

**Acceptance Criteria:**

**Given** Start Screen, App List, Edit Mode, Settings, Weather Setup, and Live Tile states exist
**When** accessibility audit is performed
**Then** every interactive target has at least 48dp hit area
**And** TalkBack labels/actions cover Tile name, role, size, grid position, live count/info, app rows, settings values, and edit actions
**And** focus traversal follows visual row-major grid order on Start Screen and search-field-to-results order in App List
**And** non-drag edit actions work for move, resize, unpin, and exit Edit Mode
**And** reduced-motion behavior is verified for Live Tile animation
**And** critical issues are fixed before release readiness is marked complete

### Story 6.4: Validate Permission Denial and Revocation Flows

As a privacy-conscious launcher user,
I want the launcher to remain useful when I deny or revoke optional permissions,
So that I can trust it as my home screen.

**Acceptance Criteria:**

**Given** notification access, Calendar permission, optional location, and Weather setup flows exist
**When** each permission or setup path is skipped, denied, granted, and later revoked where applicable
**Then** SMS, Phone, Calendar, and Weather Tiles degrade to the correct static, setup, needs permission, stale, unavailable, or error states
**And** no permission denial blocks Start Screen or App List use
**And** no inline nag badge appears inside Tiles for skipped permissions
**And** user-facing copy remains plain and low-pressure
**And** all permission-state transitions are covered by automated tests or documented manual validation cases

### Story 6.5: Complete Google Play Policy and Package Visibility Review

As a public app user,
I want the launcher to follow Android and Google Play policy-sensitive behavior,
So that it can be distributed safely and predictably.

**Acceptance Criteria:**

**Given** the MVP manifest and permission usage are implemented
**When** policy review is performed
**Then** launcher/home declarations are documented
**And** package visibility strategy is reviewed, with `LauncherApps` preferred and any broad visibility justified or removed
**And** notification listener access disclosure is reviewed
**And** Calendar and optional location permission disclosures are reviewed
**And** no direct SMS or Call Log permission exists in MVP
**And** any required Google Play declarations or release notes are documented before public release

### Story 6.6: Verify Weather Provider Terms and Attribution

As a launcher user,
I want weather data to be provided responsibly,
So that the Weather Tile can ship without hidden legal or privacy risk.

**Acceptance Criteria:**

**Given** the Weather provider adapter is implemented
**When** provider terms are reviewed before public release
**Then** Open-Meteo or selected provider quota, attribution, privacy, and public free-app/commercial-use compatibility are verified against current terms
**And** required attribution or notices are placed in Weather Setup or Settings unless legally required on Start Screen
**And** if provider terms are incompatible, the provider adapter remains swappable and an alternative provider decision is documented
**And** no API key or backend is introduced unless a new architecture decision approves it

### Story 6.7: Add Regression Coverage for Core Launcher Workflows

As a launcher user,
I want core workflows to keep working as the app evolves,
So that releases do not break daily launcher behavior.

**Acceptance Criteria:**

**Given** MVP workflows are implemented
**When** regression test coverage is added
**Then** coverage includes first-run onboarding, default-launcher preview fallback, Start Screen rendering, Tile pin/unpin/move/resize, App List search/launch/pin, settings persistence, Weather setup, permission denial/revocation, and Live Tile stale/error handling
**And** tests follow the feature-based package structure
**And** fake repositories or controlled test data are used where platform data is unreliable
**And** tests do not require sensitive personal data
**And** failures produce actionable output for implementation agents

### Story 6.8: Prepare MVP Release Readiness Checklist

As a product owner,
I want a release readiness checklist for the launcher MVP,
So that public release decisions are based on explicit criteria.

**Acceptance Criteria:**

**Given** implementation, tests, policy checks, and accessibility checks are complete
**When** release readiness is assessed
**Then** the checklist covers primary launcher tolerance, Windows Phone-style feel, app access completeness, core Live Tile usefulness, crash/ANR risk, battery risk, permission copy, weather terms, package visibility, accessibility, and known limitations
**And** unresolved risks are clearly marked as release blockers or accepted limitations
**And** the checklist references PRD success metrics SM-1 through SM-4 and counter-metrics SM-C1/SM-C2
**And** the checklist confirms no out-of-scope MVP features have displaced smoothness or usability work
