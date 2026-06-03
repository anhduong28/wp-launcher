# Technical Feasibility Review: WindowsPhone Launcher UX Draft

Date: 2026-06-02

Reviewed files:

- `DESIGN.md`
- `EXPERIENCE.md`

Context used:

- `../../prds/prd-WindowsPhone Launcher-2026-06-02/prd.md`
- `../../research/technical-android-launcher-live-tile-constraints-research-2026-06-02.md`

## Verdict

**Feasible with required revisions before implementation planning.**

The UX draft is directionally compatible with the PRD and technical research: native Android, Compose-first, local-first, optional permissions, custom Live Tiles, manual Weather setup, and a tightly scoped `1x1` / `2x2` / `4x2` Start Screen. The main problem is that several technically fragile areas are stated as smooth product behavior without enough implementation guardrails. The draft should be tightened before architecture and story work so the team does not accidentally overcommit on Android permission behavior, launcher/package constraints, Compose grid mechanics, animation cadence, and free weather provider assumptions.

## Findings

### 1. Notification access is treated like a normal onboarding permission

Severity: High

`EXPERIENCE.md` says onboarding offers "Notification, Calendar, and Location permissions" after the default launcher step. Calendar and location can use normal runtime permission flows, but notification listener access is a special system settings grant. It cannot be requested through the same runtime permission dialog model as `READ_CALENDAR` or location.

Why this matters: SMS and Phone count behavior depends primarily on `NotificationListenerService` according to the research. If UX treats notification access like a regular permission, implementation will either fail the flow or need a late redesign.

Required revision: Split the onboarding permission model into:

- runtime permissions: Calendar, optional approximate location
- settings-routed access: Notification access
- non-MVP/high-risk permissions: direct SMS and Call Log access avoided unless policy review approves

Also define the return-from-settings state: enabled, not enabled, skipped, and revoked later.

### 2. Default launcher setup needs Android role/default-home fallback states

Severity: High

The UX flow says "Set as default launcher" and then proceeds to Live Tile permissions. It does not define what happens when Android does not show the chooser, the user cancels, another launcher remains default, OEM settings behave differently, or the user returns without completing the default-home choice.

Why this matters: The research calls out the need to declare a home activity with `ACTION_MAIN` + `CATEGORY_HOME`, while still depending on user selection. This is a platform-mediated flow, not an in-app toggle.

Required revision: Add explicit states for:

- app is not default launcher
- user declined/canceled default launcher flow
- user selected WindowsPhone Launcher
- user later changes default launcher elsewhere
- Home/Back behavior when the app is opened as a regular app instead of as the active home

The Start Screen can still be shown in preview mode, but the UX should not imply default-home selection is guaranteed.

### 3. Package visibility and app discovery constraints are under-specified

Severity: High

The App List and pinning flows assume complete installed-app discovery. The research recommends `LauncherApps.getActivityList()` and warns that `QUERY_ALL_PACKAGES` is restricted by Google Play policy.

Why this matters: The launcher can list launchable activities, but "installed apps" is broader than "launchable apps visible to a launcher." Work profiles, disabled apps, apps without launcher activities, package visibility declarations, and multi-user/profile handling can create mismatches.

Required revision: Define the UX language and empty/error states around "launchable apps" rather than all installed packages. Add cases for:

- app removed after being pinned
- app disabled or unavailable
- app label/icon changes
- work profile apps if supported or explicitly deferred
- stale pinned Tile whose launch target no longer resolves

### 4. Compose feasibility is acknowledged but not converted into UX constraints

Severity: High

`EXPERIENCE.md` says there is a performance prototype gate for the Tile Grid, drag/reorder, drag resize, and Live Tile flip animation. That is good, but the interaction specs still assume free drag, snap, reflow, and flip animation all land in MVP.

Why this matters: The research is only medium confidence for Compose-first Tile Grid feasibility and explicitly allows a custom View fallback. Compose lazy grids do not provide this entire Windows Phone-like mixed-size, draggable, resizable, collision-aware grid behavior out of the box.

Required revision: Add a feasibility gate outcome table:

- pass: Compose Start Screen ships as drafted
- partial pass: Compose for settings/App List, custom layout or View-based Start Screen
- fail: reduce MVP edit behavior to resize buttons plus deterministic placement, defer free drag

This makes the UX draft implementable without pretending the riskiest surface is already solved.

### 5. Tile placement/reflow rules are too vague for implementation

Severity: High

The draft says dragged Tiles "snap" into a 4-column grid and affected Tiles "reflow and push downward." That is not enough for a deterministic layout engine or for testable acceptance criteria.

Why this matters: Mixed `1x1`, `2x2`, and `4x2` layout creates collision and compaction cases. Without formal rules, UX, implementation, and tests can disagree about where Tiles should land.

Required revision: Define layout rules for:

- insertion target calculation from drag position
- collision resolution order
- whether gaps are allowed or compacted
- whether reflow preserves relative order of unaffected Tiles
- behavior at row boundaries and screen edges
- resize expansion direction
- failed resize when no placement is possible
- persistence timing during drag versus on release

The PRD requires no overlap and persistent layout; the UX should provide the deterministic behavior needed to satisfy that.

### 6. Drag resize via corner control is risky for touch and accessibility

Severity: Medium-High

The draft uses "drag Tile corner to resize" and notes accessible alternatives are still open. Corner-drag resize on small `1x1` Tiles is a precision-heavy gesture, especially with tight `4dp` gaps and Android gesture navigation.

Why this matters: The UX must work as a daily launcher, not only as a visual demo. Android accessibility and touch ergonomics will push against tiny corner controls.

Required revision: Keep the corner affordance if desired, but define a non-precision fallback for MVP, such as a resize cycle button or menu action exposed in edit mode and TalkBack actions. Also define minimum touch target handling, even if the visible icon remains small.

### 7. Animation cadence lacks lifecycle and performance rules

Severity: High

The UX specifies Live Tile flips every 8-12 seconds with phase offsets. It does not define when animation pauses, how visible-Tile-only animation is enforced, how scroll/drag interactions affect animation, or whether system animation scale/reduced motion is honored.

Why this matters: The research warns against full-grid recomposition and recommends animating only visible Tiles, reducing/disabling animation based on system settings or user toggle, and profiling frame timing. `EXPERIENCE.md` currently says MVP does not automatically follow system reduced/remove animation settings, which conflicts with the research recommendation and creates accessibility risk.

Required revision: Add animation lifecycle rules:

- animate only while Start Screen is visible and app is foreground
- animate only visible Tiles
- pause or defer flips during drag/reorder and aggressive scroll
- align Clock updates to minute boundaries without full-grid recomposition
- honor the in-app toggle
- decide whether system animator scale/reduced motion is a release requirement, not only an accessibility note

### 8. Live Tile "animation off" incorrectly disables live information

Severity: Medium

`EXPERIENCE.md` says "Live Tile animation off" makes Tiles static and "flip and live information updates are disabled." The PRD says that when animation is disabled, supported Live Tiles remain visually stable while still showing current information where available.

Why this matters: Animation and data freshness are separate capabilities. Turning off motion should not necessarily remove weather, time, or counts.

Required revision: Split settings into:

- motion enabled/disabled
- live data available/unavailable by permission/data source

With animation off, Tiles should show the current single-face state without flip/face switching.

### 9. Weather setup assumes a provider before product/legal constraints are closed

Severity: High

The Weather flow says the Tile fetches weather from "the configured free provider." The PRD still has open questions for provider choice, quota, attribution, privacy, commercial-use constraints, and exact manual location format. The research notes Open-Meteo, MET Norway, and OpenWeather each have tradeoffs, and that "free" may not mean suitable for a public distributed app.

Why this matters: Weather is one of the highest-priority Live Tiles, but provider terms can force UX changes: attribution display, rate limiting, city lookup behavior, geocoding source, API key handling, User-Agent requirements, or backend proxy deferral.

Required revision: Add provider-agnostic UX states and requirements:

- manual city search may require geocoding and disambiguation
- attribution/source label may be required in Weather setup or settings
- stale weather and refresh throttling are expected
- offline/no-network state uses cache
- provider legal/terms review is a release gate

### 10. Manual weather location format is unresolved but appears in the main flow

Severity: Medium

The UX flow says the user "enters a city/location manually" but does not define whether this is free text, city plus country, coordinates, provider search results, or a selected resolved place.

Why this matters: Manual weather without location permission still needs a reliable way to resolve user input to provider coordinates or a provider-specific location identifier. This can affect UI complexity and error states.

Required revision: Define an MVP input model. Recommended: free-text city query plus result selection, storing resolved label and coordinates locally. If that is too large for MVP, constrain copy to "city and country" and define failure/disambiguation states.

### 11. No-backend/local-first posture is mostly aligned but external weather calls need explicit privacy copy

Severity: Medium

The draft is local-first in spirit, but Weather requires network calls to a third-party provider. The UX does not state what location data is sent, how manual versus auto-location affects privacy, or how no-backend differs from no-network.

Why this matters: "No backend" does not mean "no data leaves the device." Weather requests can expose location or query data to the provider.

Required revision: Add concise Weather setup/settings disclosure:

- manual city or approximate coordinates are sent to the weather provider
- launcher layout/app inventory/notification data are not sent to a backend in MVP
- auto-location is optional and approximate-first

### 12. Calendar permission flow needs revocation and count-only edge states

Severity: Medium

The draft handles Calendar permission as optional but does not define post-grant revocation, calendar account absence, work calendar restrictions, all-day events, or stale count behavior.

Why this matters: Calendar Provider access can fail or return no visible calendars even with permission. The research recommends adapter state machines and permission-gated access.

Required revision: Define Calendar Tile states:

- no permission
- permission granted, no calendars/events
- count available
- stale/error
- permission revoked

Keep the MVP count-only model explicit: no titles, attendees, locations, or previews.

### 13. SMS/Phone counts should be described as notification-derived and best-effort

Severity: Medium-High

The UX says SMS and Phone show counts when notification-derived count is available, which is good, but the product flow may still imply unread/missed counts are reliable system truth.

Why this matters: Notification-derived counts vary by OEM, app, notification settings, conversation grouping, notification clearing, and whether the user uses the default SMS/Phone app. The research flags this as an integration risk.

Required revision: Mark SMS/Phone counts as best-effort notification-derived counts in implementation notes and fallback states. Avoid copy that promises exact unread SMS or exact missed-call counts across devices.

### 14. Settings Tile as the only settings entry can strand users

Severity: Medium

The draft rejects a global header settings button and makes Settings a launcher-owned Tile/app entry that can be pinned. It says Settings can be pinned, but does not define what happens if the Settings Tile is unpinned or absent from the visible Start Screen.

Why this matters: Settings controls weather, background, color, and animation. If Settings is only reachable through a Tile that can be removed, users may lose obvious access.

Required revision: Ensure Settings is always present in App List as a launcher-owned entry and add a recovery path. If Settings Tile can be unpinned, the App List entry must remain discoverable and searchable.

### 15. Image background behind solid Tiles still needs Android storage/access handling

Severity: Medium

`DESIGN.md` allows image backgrounds behind solid Tiles. The UX does not define image picker, URI permission persistence, missing image behavior, or memory/performance constraints.

Why this matters: Android photo picker/document URI behavior can require persisted access or cache handling. Large images can also affect launcher startup and scroll performance.

Required revision: Define background image as an optional settings flow using Android Photo Picker or equivalent, with local cached/scaled rendering, missing-permission fallback to black, and no impact on Tile readability.

### 16. App List horizontal swipe can conflict with vertical scroll/search gestures

Severity: Medium

The draft says vertical scroll must not conflict with horizontal navigation swipe, but does not specify gesture thresholds or how search focus/keyboard changes the swipe behavior.

Why this matters: Compose nested scroll and pointer input interactions can be fragile. App List has vertical scrolling, fixed search, horizontal page transition, keyboard focus, and launcher Back behavior.

Required revision: Define gesture arbitration:

- horizontal swipe threshold and start area, if any
- vertical scroll wins when vertical delta dominates
- keyboard Back first closes keyboard, then returns to Start Screen, if that is the intended Android behavior
- horizontal swipe disabled or constrained while search text field is actively editing, if needed

## Feasibility Notes By Requested Lens

### Android / Compose

Feasible if Compose is treated as a prototype-gated implementation choice for the Start Screen. Compose is appropriate for settings, onboarding, Weather setup, and App List. The mixed-size Tile Grid with edit-mode drag/reorder/resize/reflow is the riskiest Compose surface and should have a custom layout or View fallback path.

### Permission Flows

Feasible if permission UX distinguishes runtime permissions from settings-mediated access. Notification access must route to system settings. Calendar and location use runtime flows. SMS/Call Log permissions should stay out of the Play Store MVP unless a policy review explicitly accepts them.

### Package / Launcher Constraints

Feasible if App List scope is "launchable apps" through `LauncherApps`, not arbitrary package inventory. Default launcher selection must be modeled as a system-mediated, cancelable flow.

### Tile Grid / Drag / Resize / Reflow

Feasible but under-specified. The UX needs deterministic layout, collision, resize, compaction, and persistence rules before implementation stories are written.

### Animation Cadence

Feasible if animation is visible-only, lifecycle-aware, paused during expensive interactions, and separated from live data refresh. The current "animation off disables live information" behavior conflicts with the PRD.

### Weather Setup

Feasible with manual location first, local cache, stale/error states, WorkManager refresh, and provider terms review. Provider choice and location input format remain blocking design inputs for implementation detail.

### No-Backend / Local-First

Feasible. The UX should clarify that app layout, counts, settings, and notification-derived state stay local, while weather queries necessarily contact a third-party provider. No user accounts, sync, analytics, or remote config are needed for MVP.

## Recommended Draft Changes Before Architecture

1. Add a platform permission matrix covering Notification access, Calendar, Location, network/weather, SMS/Call Log non-use, and package visibility.
2. Add deterministic Tile Grid rules for placement, resize, collision, reflow, compaction, and persistence.
3. Add a Compose Start Screen feasibility gate with fallback outcomes.
4. Correct the animation toggle so motion can be disabled while live data remains visible.
5. Add weather provider dependency notes: location resolution, attribution, cache/stale behavior, rate limiting, and terms review.
6. Add default launcher failure/cancel/revoked states.
7. Add stale/unavailable states for pinned app targets and launcher-owned Settings access recovery.

## Implementation Gate Checklist

- Launcher shell can be selected as default home and behaves correctly when not default.
- App List uses `LauncherApps` and launches selected activities without `QUERY_ALL_PACKAGES` by default.
- Tile layout engine passes unit tests for `1x1`, `2x2`, `4x2`, collisions, resize, reflow, and persistence.
- Start Screen scroll, flip animation, and edit gestures pass low/mid-range device profiling.
- Notification access flow is tested as settings-mediated opt-in, skip, disable, and revoke.
- Calendar count uses `READ_CALENDAR` only after opt-in and stores no event details.
- SMS/Phone count implementation uses notification-derived best-effort state, not direct SMS/Call Log permissions.
- Weather provider terms are approved and Weather setup handles manual location, cache, stale/error, and no-network states.
- Animation-off mode keeps live data visible without flip/face-switch motion.
- Permission-denied mode still supports Start Screen, App List, pin, resize, arrange, and launch.
