---
title: "WindowsPhone Launcher PRD"
status: "draft"
created: "2026-06-02"
updated: "2026-06-02"
---

# PRD: WindowsPhone Launcher

*Working title - confirm.*

## 0. Document Purpose

This PRD defines the MVP requirements for WindowsPhone Launcher, a free public Android launcher inspired by Windows Phone 8.1 Live Tiles. It is written for product, UX, architecture, and implementation planning. Requirements are grouped by feature with stable functional requirement IDs, and unresolved items are tracked in Open Questions.

## 1. Vision

WindowsPhone Launcher is an Android launcher inspired by the smooth, lightweight, colorful feel of Windows Phone 8.1. Its core promise is not simply to make Android icons square; it is to make the home screen feel alive through animated Live Tiles that show useful, glanceable information inside tile boundaries.

The launcher should give former Windows Phone users a familiar emotional experience while still running as a practical Android launcher. Users should be able to unlock their phone and immediately see time, weather, calendar counts, message counts, and frequently used apps in a dynamic Tile Grid that feels fast, clean, and nostalgic. The app is intended as a free public launcher, so the MVP must be usable as a daily home screen rather than only a visual demo.

## 2. Target User

### 2.1 Jobs To Be Done

- When I unlock my Android phone, I want a home screen that feels like Windows Phone 8.1 so I can regain the smooth, colorful, tile-based experience I miss.
- When I glance at the home screen, I want key updates such as time, weather, calendar, and message counts to appear inside app tiles so I do not need to open multiple apps for basic status checks.
- When I arrange my launcher, I want tile sizes and placement to be customizable so my home screen reflects my priorities.

### 2.2 Key User Journeys

- **UJ-1. A former Windows Phone user starts their day from a living tile grid.**
  - **Persona + context:** A user who previously used Windows Phone 8.1 now uses Android but misses the smooth, lightweight, colorful Live Tile home screen.
  - **Entry state:** The user unlocks the Android phone and lands on the launcher home screen.
  - **Path:** The user sees a grid of square and rectangular app tiles in multiple sizes. Each tile represents an installed application. The SMS tile shows its app icon plus unread message count. Other tiles can show time, weather, calendar, and recent status information. Tile contents animate inside their fixed square or rectangular boundaries.
  - **Climax:** The user recognizes that the screen is not a static icon grid; it feels alive, useful, and reminiscent of Windows Phone 8.1.
  - **Resolution:** The user can quickly understand important updates and feels the launcher is worth using because it is smooth, light, dynamic, and nostalgic.

## 3. Glossary

- **Launcher** - The Android home screen replacement that manages the user's app entry points, home layout, and launcher-level interactions.
- **Tile** - A square or rectangular launcher item representing one installed application.
- **Live Tile** - A Tile whose content updates dynamically inside the Tile boundary, combining the app icon with relevant status information, counts, or animation.
- **Tile Grid** - The home screen layout made of Tiles with different sizes.
- **Tile Size** - The configurable dimensions of a Tile in the Tile Grid.
- **Start Screen** - The primary launcher home screen containing the Tile Grid.
- **App List** - The Windows Phone-style vertical alphabetical list of installed apps.
- **Accent Color** - A user-selected color used by unified accent color mode.

## 4. Features

The MVP centers on six feature groups: the Live Tile system, Tile Grid and Tile sizing, Start Screen editing, visual style, App List, and minimal launcher settings.

### 4.1 Live Tile System

**Description:** Live Tiles are the defining feature of WindowsPhone Launcher. A Live Tile remains visually bounded as a square or rectangular Tile, but its internal content can update or animate to show meaningful app or system information. The desired product direction includes three levels of Live Tile capability: notification-derived counts/text, first-class launcher-owned Tiles for core information, and Windows Phone 8.1-like animation patterns.

**Functional Requirements:**

#### FR-1: Notification-derived Live Tile status

The launcher can show notification-derived information inside supported app Tiles, such as unread counts or short status text. Realizes UJ-1.

**Consequences (testable):**
- An SMS or messaging Tile can display an unread count when relevant notification data is available.
- A Tile remains visually stable in the grid while its internal content changes.
- SMS and Phone Tiles do not show message previews, caller names, or other sensitive content in MVP.

#### FR-2: First-class core Live Tiles

The launcher can provide first-class Live Tiles for core glanceable information such as Clock, Weather, Calendar, SMS, and Phone. Realizes UJ-1.

**Consequences (testable):**
- Core Live Tile implementation priority is Weather, Clock, SMS, Phone, then Calendar.
- The Clock Tile shows a digital clock and updates time without requiring the user to open an app.
- The Weather Tile can show current weather status using a free weather data source when configured.
- The Weather Tile supports manual location entry by default and optional auto-location as an opt-in behavior.
- The SMS Tile shows unread message count only in MVP.
- The Phone Tile shows missed call count only in MVP.
- The Calendar Tile shows today's event count only in MVP when calendar access is available.

#### FR-3: Live Tile animation behavior

The launcher can animate Live Tile content inside the Tile boundary in a way that evokes Windows Phone 8.1 while remaining smooth and lightweight on Android. Realizes UJ-1.

**Consequences (testable):**
- Tile animations do not resize or shift neighboring Tiles.
- Tile animations preserve launcher responsiveness during scroll, tap, and app launch interactions.
- MVP animation style uses a Windows Phone-inspired flip or face-switching behavior.
- Slide, fade, and content rotation animation styles are not required for MVP.

### 4.2 Tile Grid and Tile Sizing

**Description:** The Start Screen is built from a grid of resizable Tiles. MVP supports three Tile sizes: `1x1`, `2x2`, and `4x2`. These sizes provide enough variety to recreate the recognizable Windows Phone 8.1 rhythm while keeping layout behavior manageable for the first release.

**Functional Requirements:**

#### FR-4: MVP Tile sizes

The launcher supports `1x1`, `2x2`, and `4x2` Tile sizes in MVP. Realizes UJ-1.

**Consequences (testable):**
- A user can place app Tiles using `1x1`, `2x2`, or `4x2` sizes.
- The Tile Grid can contain mixed Tile sizes on the same home screen.
- `4x4` Tile size is not required for MVP.

#### FR-5: Tile resizing

The user can resize supported Tiles between available MVP sizes when the Tile's content supports the target size.

**Consequences (testable):**
- Resizing a Tile preserves the Tile's app association.
- Resizing a Tile updates the layout without overlapping neighboring Tiles.
- If a Tile does not support a target size, the launcher prevents or clearly omits that size option.

### 4.3 Start Screen Editing

**Description:** Users can manage the Start Screen through familiar launcher interactions. MVP supports pinning apps to the Start Screen, unpinning apps, resizing Tiles, rearranging Tiles, launching apps by tapping Tiles, and entering edit mode through long press.

**Functional Requirements:**

#### FR-6: Launch app from Tile

The user can tap a Tile to open its associated application. Realizes UJ-1.

**Consequences (testable):**
- Tapping an app Tile launches the correct installed application.
- Tapping a first-class launcher Tile opens the corresponding launcher-owned detail or configuration experience when applicable.

#### FR-7: Enter Start Screen edit mode

The user can long press a Tile to enter Start Screen edit mode.

**Consequences (testable):**
- Long pressing a Tile exposes edit actions for that Tile.
- Edit mode visually distinguishes editable state from normal launcher browsing.

#### FR-8: Pin app to Start Screen

The user can pin an installed app to the Start Screen.

**Consequences (testable):**
- A pinned app appears as a Tile on the Start Screen.
- The Tile is associated with the correct app icon and launch target.

#### FR-9: Unpin app from Start Screen

The user can remove a Tile from the Start Screen without uninstalling the associated app.

**Consequences (testable):**
- Unpinning removes the Tile from the Start Screen.
- The associated app remains installed and available elsewhere in the launcher.

#### FR-10: Rearrange Tiles

The user can move Tiles to rearrange the Start Screen layout.

**Consequences (testable):**
- Moving a Tile updates its position in the Tile Grid.
- Rearrangement does not create overlapping Tiles.
- Tile layout persists after leaving edit mode.

### 4.4 Tile Visual Style

**Description:** The launcher should evoke the colorful Windows Phone 8.1 Start Screen while giving users control over visual consistency. MVP defaults to vivid multi-color Tiles, with an option to use one primary accent color across Tiles.

**Functional Requirements:**

#### FR-11: Default multi-color Tile styling

The launcher can render Start Screen Tiles with vivid, varied colors by default.

**Consequences (testable):**
- A new or default Start Screen can show multiple Tile colors.
- Tile color choices feel colorful and lively rather than monochrome.
- Tile colors preserve readable icon and text contrast.

#### FR-12: Unified accent color mode

The user can choose a mode where Tiles use one primary accent color.

**Consequences (testable):**
- The user can enable a unified accent color mode.
- Tiles update to use the selected accent color mode.
- The mode remains readable and visually consistent across supported Tile sizes.

#### FR-13: Classic black Start Screen background

The launcher uses a classic black Start Screen background by default.

**Consequences (testable):**
- A fresh Start Screen uses a black background behind Tiles.
- Tile colors, icons, and Live Tile content remain readable against the black background.
- The default background evokes the classic Windows Phone visual style.

#### FR-14: Optional image background behind Tiles

The user can set an image background behind the Tile Grid.

**Consequences (testable):**
- The user can choose an image to display behind Tiles.
- The image appears behind the Tile Grid without replacing Tile content.
- Tile readability remains protected when an image background is enabled. [ASSUMPTION: MVP may use a simple overlay or opacity treatment to preserve contrast.]

### 4.5 App List

**Description:** MVP uses a Windows Phone-style App List rather than a traditional Android icon grid drawer. The App List presents installed apps in a vertical alphabetical list with search. This keeps the launcher experience aligned with Windows Phone 8.1 while giving users a practical way to find and pin apps.

**Functional Requirements:**

#### FR-15: Alphabetical App List

The launcher provides a vertical list of installed apps sorted alphabetically.

**Consequences (testable):**
- Installed apps appear in a vertical list.
- Apps are sorted alphabetically by display name.
- Each app row shows enough identity for recognition, including app icon and app name.

#### FR-16: Open App List with horizontal swipe

The user can open the App List by swiping from right to left on the Start Screen.

**Consequences (testable):**
- A right-to-left horizontal swipe from the Start Screen transitions to the App List.
- The gesture evokes the Windows Phone Start Screen to App List navigation model.
- The transition does not interfere with vertical Start Screen scrolling.

#### FR-17: Return to Start Screen with reverse horizontal swipe

The user can return from the App List to the Start Screen by swiping from left to right.

**Consequences (testable):**
- A left-to-right horizontal swipe from the App List transitions back to the Start Screen.
- The return gesture feels like the inverse of opening the App List.
- The transition does not interfere with vertical App List scrolling.

#### FR-18: Return to Start Screen with Android Back

When the user is in the App List, Android Back returns to the Start Screen.

**Consequences (testable):**
- Pressing Android Back from the App List transitions to the Start Screen.
- Android Back does not exit, close, or destabilize the launcher while the App List is active.
- The behavior supports Android user expectations without replacing the Windows Phone-style horizontal gesture.

#### FR-19: App List search

The user can search installed apps from the App List.

**Consequences (testable):**
- Typing a query filters the App List by app name.
- Search results update without requiring the user to submit the query.
- Clearing the query restores the full alphabetical App List.

#### FR-20: Pin from App List

The user can pin an app from the App List to the Start Screen.

**Consequences (testable):**
- A pin action from the App List creates a Tile on the Start Screen.
- The pinned Tile launches the same app selected from the App List.

### 4.6 Launcher Settings

**Description:** MVP includes a minimal settings surface for the preferences required by the Start Screen and core Live Tile experience. Settings should stay focused on the choices already necessary for v1 rather than becoming a broad launcher control center.

**Functional Requirements:**

#### FR-21: Color mode setting

The user can choose between default multi-color Tile styling and unified accent color mode.

**Consequences (testable):**
- The setting exposes both multi-color and unified accent modes.
- Changing the setting updates Start Screen Tile styling.

#### FR-22: Accent color picker

The user can choose the accent color used by unified accent color mode.

**Consequences (testable):**
- The user can select an accent color.
- The selected accent color is applied to supported Tiles when unified accent color mode is enabled.

#### FR-23: Background setting

The user can choose between the default black background and an image background.

**Consequences (testable):**
- The setting exposes black background and image background options.
- Selecting image background lets the user choose an image.
- Returning to black background removes the image background from the Start Screen.

#### FR-24: Weather location setting

The user can configure Weather Tile location with manual city/location entry and optional auto-location.

**Consequences (testable):**
- Manual city/location entry is available without granting location permission.
- Auto-location requires explicit user opt-in.
- Weather Tile uses the configured location source.

#### FR-25: Live Tile animation toggle

The user can turn Live Tile animation on or off.

**Consequences (testable):**
- When animation is enabled, supported Live Tiles can use the MVP flip or face-switching behavior.
- When animation is disabled, supported Live Tiles remain visually stable while still showing current information where available.

## 5. Non-Goals

- Windows Phone 8.1 system-level clone or operating system skin.
- Paid monetization, ads, or subscription mechanics in MVP.
- Full third-party Live Tile platform for arbitrary apps.
- Exact parity with all historical Windows Phone 8.1 tile sizes, animations, and app-specific behaviors.
- Android widget replacement platform.
- Advanced launcher customization suite beyond the settings needed for the MVP Start Screen and core Live Tiles.

## 6. MVP Scope

### 6.1 In Scope

- Authentic Windows Phone 8.1-inspired Start Screen experience as the primary MVP slice.
- Tile Grid home screen with `1x1`, `2x2`, and `4x2` Tiles.
- Tile resizing and arrangement across supported MVP sizes.
- Pin app to Start Screen.
- Unpin app from Start Screen.
- Move and rearrange Tiles.
- Tap Tile to launch app.
- Long press Tile to enter edit mode.
- Windows Phone-style vertical alphabetical App List.
- Right-to-left horizontal swipe from Start Screen to App List.
- Left-to-right horizontal swipe from App List to Start Screen.
- Android Back from App List returns to Start Screen.
- App List search.
- Pin app from App List to Start Screen.
- Minimal launcher settings: color mode, accent color, background, weather location, and Live Tile animation toggle.
- Colorful Tile styling inspired by Windows Phone 8.1.
- Smooth Live Tile animation inside Tile boundaries.
- Default vivid multi-color Tile styling.
- Optional unified accent color mode.
- Classic black Start Screen background by default.
- Optional image background behind Tiles.
- Useful live information for core Tiles, rather than purely decorative demo animation.
- First-class live information direction for Weather, Clock, SMS, Phone, and Calendar, in that priority order. [ASSUMPTION: MVP may ship with a stable subset first if implementing all five at production quality threatens smoothness or delivery.]
- Clock Live Tile uses a digital clock style in MVP.
- SMS and Phone Live Tiles show counts only in MVP.
- Calendar Live Tile shows today's event count only in MVP.

### 6.2 Out of Scope for MVP

- Full parity with every Windows Phone 8.1 Live Tile behavior.
- `4x4` Tile size.
- Deep per-app custom Live Tile behavior for arbitrary third-party apps.
- SMS message previews and Phone caller previews.
- Calendar event title, attendee, or location previews.
- Analog Clock Tile style.
- Multiple advanced animation styles beyond the default flip or face-switching behavior.
- Complex launcher ecosystem features that do not support the Start Screen MVP, such as icon pack marketplace, advanced backup/sync, or extensive automation.
- App uninstall management from the launcher unless needed by Android platform conventions.
- Advanced per-Tile color rules beyond default multi-color and unified accent color modes.
- Advanced background editing beyond choosing a background image and preserving readability.
- Broad launcher settings unrelated to MVP Start Screen and core Live Tile behavior.

### 6.3 MVP Direction

The MVP should combine **Authentic Start Screen** and **Useful Live Info**. The first release must feel recognizably like a smooth Windows Phone 8.1 Start Screen, while also proving that Live Tiles can show real, useful information. Visual nostalgia alone is insufficient; live information is part of the product promise.

## 7. Success Metrics

**Primary**

- **SM-1: Seven-day primary launcher tolerance** - A user can use WindowsPhone Launcher as their main Android launcher for 7 days without reporting that it feels heavy, laggy, or disruptive. Validates FR-3, FR-4, FR-5, FR-6, FR-10, FR-16, FR-17, FR-18.
- **SM-2: Windows Phone 8.1 Start Screen feel** - Users who remember Windows Phone 8.1 subjectively report that the Start Screen feels recognizably smooth, lightweight, colorful, and alive. Validates FR-3, FR-4, FR-11, FR-13, FR-14.

**Secondary**

- **SM-3: Core Live Tile usefulness** - At least the highest-priority available core Live Tiles show useful current information under configured permissions and data sources. Validates FR-1, FR-2.
- **SM-4: App access completeness** - Users can find, pin, resize, arrange, and launch installed apps through the MVP launcher surfaces. Validates FR-6, FR-7, FR-8, FR-9, FR-10, FR-15, FR-19, FR-20.

**Counter-metrics (do not optimize)**

- **SM-C1: Feature count over smoothness** - Do not optimize for the number of Live Tile types or settings if doing so makes the launcher feel heavy or unstable. Counterbalances SM-1 and SM-2.
- **SM-C2: Visual nostalgia over daily usability** - Do not optimize for visual imitation if users cannot comfortably use the launcher as their main home screen. Counterbalances SM-1.

## 8. Open Questions

- OQ-1: Which Android data sources should Live Tiles support in MVP: notification listener, app shortcuts, widgets, calendar provider, weather API, SMS permission, or launcher-owned integrations?
- OQ-6: If MVP must ship with a subset of first-class Live Tiles, how many from the priority order are mandatory for the first release?
- OQ-7: Should Android Back behavior from Start Screen do nothing, open launcher settings, or follow normal Android home behavior?
- OQ-8: MVP default weather provider is Open-Meteo. Before public release, confirm quota, attribution, privacy, and commercial-use constraints against current Open-Meteo terms.
- OQ-9: Manual Weather setup uses city/location search with a resolved selectable result. Exact geocoding UX should follow Open-Meteo integration constraints.
- OQ-10: Where should users access Launcher Settings from in MVP?

## 9. Assumptions Index

- [ASSUMPTION] The primary emotional target is former Windows Phone 8.1 users who want a smooth, lightweight, nostalgic launcher experience on Android.
- [ASSUMPTION] MVP may ship with a stable subset of Clock, Weather, Calendar, SMS, and Phone Live Tiles first if implementing all five at production quality threatens smoothness or delivery.
- [ASSUMPTION] MVP may use a simple overlay or opacity treatment to preserve Tile readability over image backgrounds.
