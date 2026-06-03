---
name: WindowsPhone Launcher
status: draft
sources:
  - ../../prds/prd-WindowsPhone Launcher-2026-06-02/prd.md
  - ../../research/technical-android-launcher-live-tile-constraints-research-2026-06-02.md
updated: 2026-06-02
---

# WindowsPhone Launcher - Experience Spine

## Foundation

MVP is Android phone portrait-first. Landscape and tablet are deferred. The UI system is native Android / Jetpack Compose, with a performance prototype gate for the Start Screen Tile Grid, drag/reorder, drag resize, and Live Tile flip animation.

`DESIGN.md` is the visual identity reference. This document owns information architecture, behavior, states, interactions, accessibility, and key flows.

Key-screen reference: `mockups/key-screens.html`. Spines win on conflict with the mockup.

## Information Architecture

| Surface | Reached from | Purpose |
|---|---|---|
| Onboarding - Welcome | First launch | Establish promise: WP-style living Start Screen on Android. |
| Onboarding - Set as default launcher | Welcome | Route user into Android default launcher selection. |
| Onboarding - Live Tile permissions | Default launcher step | Offer optional Notification access and Calendar access. Location is deferred to Weather Setup. |
| Start Screen | Home / onboarding completion | Primary launcher surface with 4-column Tile Grid. |
| App List | Right-to-left swipe from Start Screen | Browse launchable apps in vertical alphabetized list. |
| Edit Mode | Long press Tile | Rearrange, resize, and unpin Tiles. |
| Weather Setup | Weather Tile `Set location` or Settings | Configure manual city/location search using Open-Meteo as default provider, with optional auto-location. |
| Launcher Settings | Settings Tile or Settings App List entry | Configure color mode, accent color, background, weather location, and Live Tile animation. |

Navigation rules:

- Start Screen -> App List: right-to-left horizontal swipe.
- App List -> Start Screen: left-to-right horizontal swipe.
- Android Back from App List returns to Start Screen after keyboard is closed.
- Settings is always present as a launcher-owned App List entry and may also be pinned as a Tile.

## Voice and Tone

Microcopy should be plain and low-pressure. Permission copy must explain value without coercion.

| Do | Don't |
|---|---|
| `Set location` | `Weather unavailable!` |
| `Enable notification access for counts.` | `Required for Live Tiles` |
| `Skip for now` | `No thanks, I don't want features` |
| `Use as default launcher` | `Activate full experience now!` |

## Component Patterns

| Component | Use | Behavioral rules |
|---|---|---|
| Tile Grid | Start Screen | 4-column vertical grid. Supports `1x1`, `2x2`, `4x2`. Tight spacing. |
| Live Tile | Start Screen | Can flip every 8-12 seconds with phase offsets. Only visible/supported Tiles animate. |
| Non-Animated Live Tile | Start Screen | Used when motion is off but data is available. Shows current single-face live data without flip. |
| Static App Tile | Start Screen | Used for plain app Tiles or when data source/permission is unavailable. Shows app icon only, no live info. |
| Weather Tile | Start Screen | Info-first. Before setup, shows `Set location`; tap opens Weather Setup. |
| Clock Tile | Start Screen | Info-first digital clock. |
| SMS Tile | Start Screen | Icon-first. Shows best-effort notification-derived count only when available. |
| Phone Tile | Start Screen | Icon-first. Shows best-effort notification-derived missed-call count only when available. |
| Calendar Tile | Start Screen | Count-only for today's events when Calendar permission is granted. |
| Settings Tile | Start Screen/App List | Opens Launcher Settings. Can be pinned like a launcher-owned app. |
| App List Row | App List | Icon + app name. Tap launches app. Accessible row action can pin app. |
| App List Search | App List | Fixed search field at top. Typing filters list immediately. |
| Edit Controls | Edit Mode | Small visual corner controls for unpin and resize, backed by 48dp hit areas and accessible actions. |

## State Patterns

| State | Surface | Treatment |
|---|---|---|
| First launch | Onboarding | Welcome -> set default launcher -> optional Live Tile permissions. |
| Default launcher canceled | Onboarding | Explain preview mode. Offer `Try again` and `Continue preview`. |
| Default launcher later revoked | Start Screen / app open | Show Start Screen preview and route to make default again. |
| Permission skipped | SMS/Phone/Calendar Tiles | Show normal app Tile/icon with no count. No inline `Enable` badge. |
| Notification access not enabled | SMS/Phone Tiles | Show static app Tile with no count; Settings can reopen Android notification listener settings. |
| Notification access revoked | SMS/Phone Tiles | Remove counts on next refresh and show ordinary app Tile state. |
| Calendar permission revoked | Calendar Tile | Remove count and show ordinary Calendar Tile without preview. |
| Weather not configured | Weather Tile | Show `Set location`; tap opens Weather Setup. |
| Weather city ambiguous | Weather Setup | Show selectable Open-Meteo geocoding/location results; user chooses one resolved location. |
| Open-Meteo requires attribution or terms notice | Weather Setup / Settings | Show provider/source attribution outside Start Screen unless legally required. |
| Weather loading | Weather Tile | Keep Tile stable; show loading state only inside Tile boundary. |
| Weather stale/error | Weather Tile | Prefer last known value with visible stale/error state; do not block launcher. |
| Live Tile animation off | Start Screen | Supported Tiles stop flip/face switching and render latest current single-face information where data/permission exists. |
| Edit Mode active | Start Screen | Long-pressed Tile becomes active/lifted. Corner controls appear. |
| Edit Mode accessible actions | Start Screen | Selected Tile exposes actions: move before/after/up/down, resize to `1x1`/`2x2`/`4x2`, unpin, exit Edit Mode. |
| Invalid drop/resize | Edit Mode | Tile returns to last valid grid position/size and announces failure reason when accessibility services are active. |
| App target missing | Start Screen | Pinned Tile remains but shows unavailable state; tap offers remove Tile if target no longer resolves. |
| App List loading | App List | Keep search disabled until launchable apps are loaded. |
| Search empty | App List | Show `No apps found.` |
| Pin success | App List / Start Screen | Confirm app was pinned; focus moves predictably. |
| Background image missing | Start Screen | Fall back to classic black background. |

## Interaction Primitives

- Tap Tile to launch associated app or open launcher-owned surface.
- Long press Tile to enter Edit Mode.
- Drag Tile freely in Edit Mode; release snaps it into the 4-column grid.
- Drag Tile corner to resize; supported sizes are `1x1`, `2x2`, `4x2`.
- If resize needs more space, the grid reflows and pushes affected Tiles downward.
- Non-drag alternatives must exist for move, resize, and unpin through accessibility actions and keyboard/switch-compatible command paths.
- Right-to-left swipe from Start Screen opens App List.
- Left-to-right swipe from App List returns to Start Screen.
- App List vertical scroll must not conflict with horizontal navigation swipe.
- In App List search, Back first closes the keyboard when open; Back from App List with no active keyboard returns to Start Screen.
- Live Tile flip cadence is 8-12 seconds per Tile, phase-offset.
- Live Tile flips run only while Start Screen is visible, app is foreground, the Tile is visible, and the user is not actively dragging/reordering.

## Tile Grid Layout Rules

- Grid insertion target is calculated from the Tile center at release.
- Reflow preserves the relative order of unaffected Tiles where possible.
- Tiles never overlap.
- Gaps are compacted top-to-bottom and left-to-right after release.
- Resize expands from the Tile's current top-left anchor unless doing so would leave the Tile off-grid; then the nearest valid anchor is used.
- Layout persists on release, not continuously during drag.
- Prototype gate outcomes:
  - **Pass:** Compose Start Screen ships as drafted.
  - **Partial pass:** Compose remains for App List/Settings; custom layout or View-based Start Screen handles Tile Grid.
  - **Fail:** free drag/corner resize is deferred and MVP uses deterministic edit controls.

## Platform Permission Model

| Capability | UX entry | Platform route | MVP behavior if skipped |
|---|---|---|---|
| Notification-derived SMS/Phone counts | Onboarding and Settings | Android notification listener settings | SMS/Phone Tiles show no count. |
| Calendar count | Onboarding and Settings | Runtime Calendar permission | Calendar Tile shows no count. |
| Weather manual location | Weather Setup | City/location search + Open-Meteo lookup | Weather Tile remains `Set location`. |
| Weather auto-location | Weather Setup | Optional approximate location runtime permission | Manual city still works. |
| Direct SMS/Call Log | None in MVP | Avoided | Not used. |

## Accessibility Floor

- Every interactive target must provide at least a 48dp hit area, even when the visible icon is smaller.
- TalkBack labels must identify Tile name, role, size, grid position, live count/info when visible, and available actions.
- Count changes should not repeatedly interrupt normal launcher use; announce changes when focus is on the Tile or when the user explicitly queries it.
- App List search focus order is search field -> list results.
- Start Screen focus traversal follows visual row-major grid order and updates after reflow.
- Edit Mode controls must be reachable without precise drag gestures.
- Android system remove-animation / animator-duration accessibility settings should stop Live Tile flip motion. The in-app animation toggle remains the explicit product control.
- Text must remain readable over Tile colors using `DESIGN.md` contrast choices.
- Onboarding permissions must have headings, grouped explanations, `Skip for now`, and focus restoration after returning from Android system settings.

## Inspiration & Anti-patterns

- **Lifted from Windows Phone 8.1:** dense Start Screen, sharp Tiles, vivid colors, horizontal transition from Start Screen to App List, live motion inside Tile boundaries.
- **Modernized for Android:** labels are clearer and slightly heavier; App List has a fixed search field; Android Back from App List returns to Start Screen.
- **Rejected - Android grid drawer:** breaks the Windows Phone mental model for MVP.
- **Rejected - decorative-only Live Tiles:** live information is part of the product promise.
- **Rejected - permission nag badges inside Tiles:** privacy and calm daily use matter more than pushing opt-ins.

## Responsive & Platform

MVP supports Android phone portrait. Landscape and tablet are deferred. Layout must account for status/navigation bars and Android gesture navigation, but the Start Screen itself remains a minimal full-height Tile Grid.

## Key Flows

### Flow 1 - First run into a living Start Screen

1. Minh opens WindowsPhone Launcher after install.
2. Welcome explains the launcher as a WP-style living Start Screen for Android.
3. Minh proceeds to Set as default launcher and follows Android system flow.
4. If Minh cancels, the app offers preview mode and a `Try again` path.
5. Live Tile permissions offer Notification access and Calendar. Notification access routes through Android system settings.
6. Launcher opens Start Screen with Weather `4x2`, Clock `2x2`, SMS `2x2`, Phone `1x1`, Calendar `2x2`, Settings `1x1`.
7. Weather shows `Set location` if not configured; SMS/Phone/Calendar show normal app/icon Tiles without counts if permissions were skipped.
8. **Climax:** Minh recognizes the sharp, colorful, moving Tile Grid as Windows Phone-like while the launcher remains usable even without every permission.

### Flow 2 - Find and pin an app

1. Minh swipes right-to-left from Start Screen.
2. App List appears with fixed search field, alphabet headers, app icons, and app names.
3. Minh searches for an app.
4. Results filter immediately.
5. Minh pins the app to Start Screen.
6. Minh presses Android Back from App List; if the keyboard is closed, the launcher returns to Start Screen.
7. **Climax:** Minh can build the Start Screen around personal priorities without leaving the Windows Phone-style interaction model.

### Flow 3 - Edit the Start Screen

1. Minh long presses a Tile.
2. Edit Mode activates; the Tile feels lifted and corner controls appear.
3. Minh drags the Tile freely.
4. On release, it snaps to the 4-column grid and surrounding Tiles reflow.
5. Minh drags a Tile corner to resize from `1x1` to `2x2`.
6. The grid pushes affected Tiles downward if needed.
7. Minh unpins another Tile through the corner control.
8. With TalkBack active, Minh uses custom actions to resize or move without dragging.
9. **Climax:** The Start Screen becomes personal while preserving a tight, stable WP-style grid.

### Flow 4 - Configure Weather

1. Minh taps the Weather Tile showing `Set location`.
2. Weather Setup opens.
3. Minh enters a city/location manually.
4. If multiple locations match, Minh chooses one resolved result.
5. The Weather Tile fetches weather from Open-Meteo.
6. If Open-Meteo attribution or terms notice is required, Weather Setup or Settings shows it without adding Start Screen chrome.
7. If the fetch succeeds, the Tile becomes info-first with current weather.
8. If it fails, the Tile shows a stable error/stale state inside the Tile boundary.
9. **Climax:** Weather becomes useful without requiring location permission.

### Flow 5 - Change launcher look and motion

1. Minh opens Settings from the Settings Tile or launcher-owned Settings entry in App List.
2. Minh switches from multi-color Tiles to unified accent color.
3. Minh chooses an accent color; Tiles update using contrast-safe foreground rules.
4. Minh changes background from classic black to an image; Tiles remain solid and readable.
5. Minh turns Live Tile animation off.
6. Supported Tiles stop flipping but continue to show current single-face data where available.
7. **Climax:** Minh can calm the Start Screen without losing useful information.

## Resolved PRD Questions

- OQ-1: SMS/Phone counts use notification-derived best-effort counts in MVP; direct SMS/Call Log permissions are avoided.
- OQ-7: Android Back from App List returns to Start Screen after keyboard handling.
- OQ-10: Settings is a launcher-owned App List entry and optional Settings Tile.

## Open UX Items

- Verify current Open-Meteo quota, attribution, privacy, and commercial-use terms before public release.
