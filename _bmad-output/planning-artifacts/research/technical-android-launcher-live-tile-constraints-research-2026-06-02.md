---
stepsCompleted: [1, 2, 3, 4, 5, 6]
inputDocuments:
  - "_bmad-output/planning-artifacts/prds/prd-WindowsPhone Launcher-2026-06-02/prd.md"
workflowType: 'research'
lastStep: 1
research_type: 'technical'
research_topic: 'Android launcher constraints and Live Tile implementation for WindowsPhone Launcher'
research_goals: 'Research Android launcher constraints, free weather provider options, and permissions/data sources for SMS, Phone, Calendar, notifications, Weather, and Live Tile behavior; prepare findings for UX and architecture planning.'
user_name: 'Edward'
date: '2026-06-02'
web_research_enabled: true
source_verification: true
---

# Research Report: technical

**Date:** 2026-06-02
**Author:** Edward
**Research Type:** technical

---

## Research Overview

This research supports WindowsPhone Launcher, a free public Android launcher inspired by Windows Phone 8.1 Live Tiles. The research focused on Android launcher constraints, free weather provider options, and permission/data-source choices for SMS, Phone, Calendar, notifications, Weather, and Live Tile behavior.

The strongest technical direction is a native Kotlin Android app, local-first with no backend for MVP, using `LauncherApps` for app discovery/launch, custom Live Tiles instead of Android widgets, Room/DataStore for local state, WorkManager for low-priority weather refresh, and permission-gated adapters for optional data sources. The highest-risk areas are Google Play policy around SMS/Call Log/package visibility, weather provider terms, and performance of the Tile Grid plus flip animation.

The final synthesis below summarizes the strategic conclusions and implementation roadmap. The detailed source-backed analysis remains in the intermediate sections.

---

## Technical Research Scope Confirmation

**Research Topic:** Android launcher constraints and Live Tile implementation for WindowsPhone Launcher

**Research Goals:** Research Android launcher constraints, free weather provider options, and permissions/data sources for SMS, Phone, Calendar, notifications, Weather, and Live Tile behavior; prepare findings for UX and architecture planning.

**Technical Research Scope:**

- Architecture Analysis - Android launcher architecture, home intent behavior, app discovery, data-source architecture, and Live Tile update model.
- Implementation Approaches - launcher surfaces, gesture handling, tile grid behavior, flip animation approach, permissions, and graceful fallback behavior.
- Technology Stack - Android APIs, Kotlin/Jetpack options, notification/calendar/call/SMS access, location/weather provider integration, and persistence.
- Integration Patterns - Android platform providers, notification listener service, weather APIs, permission flows, and launcher-owned core tiles.
- Performance Considerations - smooth Start Screen scrolling, animation budgets, background update constraints, battery/privacy impact, and daily-launcher reliability.

**Research Methodology:**

- Current web data with rigorous source verification.
- Multi-source validation for critical technical claims.
- Confidence level framework for uncertain information.
- Comprehensive technical coverage with architecture-specific insights.

**Scope Confirmed:** 2026-06-02

---

<!-- Content will be appended sequentially through research workflow steps -->

## Technology Stack Analysis

### Programming Languages

**Recommendation: Kotlin-first native Android.** WindowsPhone Launcher should be implemented as a native Android app in Kotlin. The product depends on Android launcher behavior, platform content providers, notification listener APIs, runtime permissions, background scheduling, and performance-sensitive UI. Kotlin gives direct access to these APIs with first-class Android tooling and avoids the impedance mismatch of cross-platform shells for launcher-specific behavior.

_Popular Languages:_ Kotlin is the practical default for new native Android work; Java remains viable for lower-level Android APIs and examples, but Kotlin should own application code.

_Emerging Languages:_ Multiplatform UI stacks are not a good fit for MVP because the product is tightly coupled to Android launcher and permission behavior.

_Language Evolution:_ Prefer Kotlin coroutines and Flow for tile data streams, with careful lifecycle scoping so Live Tile updates do not keep work alive unnecessarily.

_Performance Characteristics:_ Kotlin/native Android is appropriate for launcher responsiveness; performance risk will come more from UI composition, animation, background refresh, and data-source polling than from the language itself.

_Sources:_ Android Intent home behavior documentation notes `ACTION_MAIN` with `CATEGORY_HOME` for launching the home screen: https://developer.android.com/reference/android/content/Intent.html. Android `LauncherApps` exposes launcher activities across apps/users: https://developer.android.com/reference/android/content/pm/LauncherApps.

### Development Frameworks and Libraries

**Recommended UI stack: Jetpack Compose with a performance gate, or hybrid Compose + custom layout if drag/reorder becomes fragile.** Compose is suitable for a highly custom tile UI, gesture handling, settings, App List, and animated tile content. Official Compose Lazy Grid APIs support scrollable grids, and Compose Animation is current and actively maintained. However, a launcher is a daily-use surface; the MVP must verify release-build scrolling, resize/reorder behavior, and flip animation on real devices before committing fully.

_Major Frameworks:_

- **Jetpack Compose UI** for Start Screen, App List, settings, and Live Tile presentation.
- **Compose Animation** for tile flip/face-switch animation.
- **Compose Lazy layouts** for the App List and possibly Tile Grid if the grid can be represented cleanly.
- **Android View system/custom ViewGroup fallback** if Compose Lazy Grid cannot deliver stable tile drag/reorder and animation performance.

_Micro-frameworks / focused Android APIs:_

- `LauncherApps` for installed launchable activities and app-change callbacks.
- `NotificationListenerService` for notification-derived counts where the user grants notification access.
- Calendar Provider for today's event count.
- CallLog/SMS platform providers only if Play policy and permission strategy are acceptable; otherwise prefer notification-derived counts.
- WorkManager for deferred weather refresh and other non-immediate background updates.

_Evolution Trends:_ Compose is current, but official docs still warn that careless animation/state in lazy layouts can cause performance problems; state should be hoisted outside lazy items, and animation work must be profiled.

_Ecosystem Maturity:_ Compose and Jetpack libraries are mature enough for MVP, but launcher-grade drag/reorder and high-frequency tile animation should be treated as risk areas requiring prototypes.

_Sources:_ Compose Animation release docs: https://developer.android.com/jetpack/androidx/releases/compose-animation. Compose Lazy lists/grids docs: https://developer.android.com/develop/ui/compose/lists. Compose animation quick guide warning about animation performance and lazy-layout state: https://developer.android.com/develop/ui/compose/animation/quick-guide. `NotificationListenerService`: https://developer.android.com/reference/android/service/notification/NotificationListenerService.

### Database and Storage Technologies

**Recommended storage split: DataStore for settings; Room for tile layout and cached tile state.**

_Relational Databases:_ Room is appropriate for structured launcher state: pinned Tiles, Tile positions, Tile sizes (`1x1`, `2x2`, `4x2`), Tile color assignments, App List metadata cache, and last-known Live Tile values. Room provides an abstraction layer over SQLite and is the right choice once partial updates and relational consistency matter.

_Key-value / typed object storage:_ Jetpack DataStore is appropriate for preferences and small typed configuration: color mode, selected Accent Color, background mode, image background URI, weather location mode, manually entered city/location, animation toggle, and first-run flags.

_In-Memory State:_ Use in-memory state holders for currently visible Tile models, derived from Room/DataStore/provider streams. Avoid using database writes as an animation mechanism.

_Data Warehousing:_ Not applicable. A free public launcher MVP should not collect analytics by default unless explicitly added later with clear privacy policy and consent.

_Sources:_ DataStore docs distinguish key-value/typed object storage and recommend Room for large/complex datasets or partial updates: https://developer.android.com/datastore. Room docs describe Room as an abstraction over SQLite: https://developer.android.com/training/data-storage/room.

### Development Tools and Platforms

_IDE and Editors:_ Android Studio is the expected IDE for Kotlin, Compose, emulator/device profiling, layout inspection, and Android manifest/permission development.

_Version Control:_ Git is sufficient. Keep PRD requirement IDs in commit/issue references once implementation starts.

_Build Systems:_ Gradle with Android Gradle Plugin. The project should target modern Android SDK requirements and validate behavior on Android versions likely to enforce current notification, location, package visibility, and background work rules.

_Testing Frameworks:_

- Unit tests for Tile model sizing, placement, sorting, and weather mapping.
- Instrumented tests for App List discovery, permission gates, settings persistence, and launch intents.
- Macrobenchmark/profiling for Start Screen scroll, tile flip animation, and edit-mode interactions.
- Manual device testing for launcher default behavior, notification listener access, SMS/Phone/Calendar permission prompts, and Google Play policy-sensitive flows.

_Platform / policy constraints:_

- Package visibility is sensitive on modern Android. A launcher-like app has a strong product reason to discover launchable apps, but broad package visibility still has Play policy implications.
- Google Play treats `QUERY_ALL_PACKAGES` as restricted and requires justification/declaration when used.
- `LauncherApps.getActivityList()` is the Android API most aligned with launcher use cases.

_Sources:_ Google Play `QUERY_ALL_PACKAGES` policy: https://support.google.com/googleplay/android-developer/answer/10158779. Android `LauncherApps` reference: https://developer.android.com/reference/android/content/pm/LauncherApps. Android package visibility auto-visibility docs: https://developer.android.com/training/package-visibility/automatic.

### Cloud Infrastructure and Deployment

**Recommended deployment model: no app backend for MVP.** A free public launcher should avoid operating a server unless needed. Weather can be fetched client-side from a free provider, settings/layout remain local, and core tile counts derive from local Android data sources or notification access.

_Major Cloud Providers:_ Not required for MVP. Introducing AWS/Azure/GCP would add cost, privacy surface, and operational work without helping the Start Screen MVP.

_Container Technologies:_ Not applicable for MVP unless later adding a backend weather proxy or sync service.

_Serverless Platforms:_ Optional future mitigation if the chosen weather provider requires key protection or caching. Not recommended for MVP unless provider terms make direct client calls unacceptable.

_CDN and Edge Computing:_ Not applicable for MVP.

_Weather provider options:_

- **Open-Meteo:** Strong MVP candidate for manual-location Weather Tile because it has no API key requirement and broad forecast/current-weather support. Risk: its free API is for non-commercial use and has published rate limits; a free public app without ads/subscriptions appears closer to non-commercial, but legal/product review should confirm if public app distribution is acceptable.
- **MET Norway Locationforecast:** Strong technical candidate with free public weather data, but requires careful User-Agent identification and terms/attribution compliance.
- **OpenWeather:** Familiar provider, but API-key management and free-tier limitations make it less attractive for a no-backend MVP.

_Sources:_ Open-Meteo terms and limits: https://open-meteo.com/en/terms. Open-Meteo feature/API page: https://open-meteo.com/. MET Locationforecast endpoint and User-Agent requirement: https://api.met.no/weatherapi/locationforecast/2.0/. MET docs: https://docs.api.met.no/doc/locationforecast/Locationforecast.html.

### Technology Adoption Trends

_Migration Patterns:_ Native Android apps continue to move toward Kotlin + Jetpack libraries. For this product, adopting modern Android APIs matters more than following broad cross-platform trends.

_Emerging Technologies:_ Compose can accelerate custom UI work, but the MVP should validate tile grid performance early. If Compose drag/reorder or tile flip animation becomes unstable, fall back to a custom View-based Start Screen while keeping Compose for settings/App List.

_Legacy Technology:_ XML/View-based UI remains a credible fallback for the Start Screen if Compose cannot meet launcher-smoothness goals. It should not be rejected on ideology; the product success metric is smooth daily launcher use.

_Community / policy trend:_ Android and Google Play increasingly restrict sensitive data access: package visibility, SMS/Call Log permissions, notification content, background work, and location access. The MVP should bias toward explicit opt-in, count-only tiles, local-only state, and notification-derived counts where possible.

_Sources:_ Google Play SMS/Call Log policy: https://support.google.com/googleplay/android-developer/answer/10208820. Calendar Provider permission docs: https://developer.android.com/identity/providers/calendar-provider. CallLog API reference: https://developer.android.com/reference/android/provider/CallLog.Calls. Telephony SMS provider: https://developer.android.com/reference/android/provider/Telephony.Sms. Runtime permission docs: https://developer.android.com/training/permissions/requesting. Location permission docs: https://developer.android.com/develop/sensors-and-location/location/permissions/runtime. Foreground services overview: https://developer.android.com/develop/background-work/services/fgs. WorkManager work request docs: https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work.

### Technology Stack Recommendation

**MVP stack:**

- Kotlin native Android.
- Jetpack Compose for UI, with a prototype gate for Tile Grid drag/reorder and flip animation.
- DataStore for settings.
- Room for Tile layout, Tile state cache, and app metadata cache.
- LauncherApps for installed launchable apps.
- NotificationListenerService as the preferred source for notification-derived badge/count behavior.
- Calendar Provider for Calendar count only after `READ_CALENDAR` opt-in.
- Weather via Open-Meteo or MET Norway after provider terms review; manual location first, approximate auto-location optional.
- WorkManager for periodic weather refresh and low-priority background refresh, avoiding always-on services.

**High-confidence decisions:**

- Native Android is required.
- No backend is needed for MVP.
- Settings/layout should be local-first.
- Sensitive tiles should remain count-only.

**Research gaps moving to Step 3:**

- Whether SMS/Phone counts should avoid direct SMS/CallLog permissions entirely and rely on notifications to reduce Google Play risk.
- Exact package visibility strategy: `LauncherApps` alone, manifest `<queries>`, or `QUERY_ALL_PACKAGES` with declaration.
- Weather provider final choice and attribution/commercial compatibility.
- Background update cadence that preserves battery and avoids foreground service misuse.

## Integration Patterns Analysis

### API Design Patterns

WindowsPhone Launcher is primarily a **client-side Android platform integration app**, not a networked microservice product. Its integration design should use platform APIs as bounded adapters feeding a local Tile state model.

_RESTful APIs:_ Weather is the only MVP network integration. Use simple HTTPS GET APIs returning JSON, with a provider adapter that maps provider-specific fields into a launcher-owned `WeatherTileState`. Open-Meteo and MET Norway both expose HTTP weather endpoints suitable for this pattern. The app should cache the last successful weather result locally and render stale-but-labelled weather if refresh fails.

_GraphQL APIs:_ Not applicable for MVP. No known weather or Android platform need justifies GraphQL.

_RPC and gRPC:_ Not applicable for MVP because no app backend is planned.

_Webhook Patterns:_ Not applicable. Android platform events are received through system callbacks/services/providers rather than external webhooks.

_Android platform adapters:_ Create explicit adapter boundaries:

- `LauncherAppSource`: installed launchable apps, labels, icons, package/activity identity.
- `NotificationCountSource`: notification-derived counts for app Tiles.
- `WeatherSource`: free weather provider integration.
- `CalendarCountSource`: today's calendar event count after permission.
- `PhoneCountSource`: missed call count, preferably notification-derived for MVP.
- `SmsCountSource`: unread SMS/message count, preferably notification-derived for MVP.

_Source:_ Android `LauncherApps`: https://developer.android.com/reference/android/content/pm/LauncherApps. Android `NotificationListenerService`: https://developer.android.com/reference/android/service/notification/NotificationListenerService. Open-Meteo docs/terms: https://open-meteo.com/ and https://open-meteo.com/en/terms. MET Locationforecast: https://api.met.no/weatherapi/locationforecast/2.0/.

### Communication Protocols

_HTTP/HTTPS Protocols:_ Weather should use HTTPS with conservative refresh cadence and local caching. Manual city entry requires geocoding; use the same provider's geocoding API where possible to avoid mixing terms and attribution obligations.

_WebSocket Protocols:_ Not applicable. Weather and tile counts do not need persistent sockets.

_Message Queue Protocols:_ Not applicable for MVP. In-app state can use Kotlin Flow/StateFlow rather than external messaging.

_gRPC and Protocol Buffers:_ Not applicable unless a backend is introduced later.

_Android callback/event protocols:_ The key "protocols" are Android system callbacks and user-granted access:

- `NotificationListenerService` receives notification posted/removed callbacks after the user enables notification access. This is the strongest MVP integration for SMS/Phone counts because it avoids direct SMS/Call Log reads, but it depends on notification availability and OEM/app behavior.
- Calendar Provider queries require `READ_CALENDAR` and should be run only after explicit opt-in.
- `LauncherApps` provides launcher activities and package-change integration aligned with launcher use cases.
- WorkManager handles deferrable background refresh, especially weather.

_Source:_ Notification listener docs: https://developer.android.com/reference/android/service/notification/NotificationListenerService. Calendar Provider docs: https://developer.android.com/identity/providers/calendar-provider. WorkManager docs: https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work.

### Data Formats and Standards

_JSON:_ Weather providers return JSON. Normalize provider JSON into a small internal model: location label, temperature, weather code/condition, timestamp, attribution/source, and stale flag.

_XML:_ Not needed for MVP app logic. Android manifests and some provider metadata are XML, but runtime data should be Kotlin models.

_Protobuf and MessagePack:_ Optional for DataStore typed storage if using Proto DataStore. Not necessary unless settings schema needs stronger typing.

_SQLite schema:_ Room should store Tile layout and cached Tile state using stable IDs. Avoid storing sensitive notification text or personal previews because MVP only needs counts.

_Custom data formats:_ Define launcher-owned tile models instead of passing provider-specific data into UI:

- `TileDefinition`: tile ID, type, app/component identity, size, position, color mode override if any.
- `TileRuntimeState`: count, label, icon reference, last update time, stale/error status.
- `LiveTileFace`: front/back face content for flip animation.

_Source:_ Room docs: https://developer.android.com/training/data-storage/room. DataStore docs: https://developer.android.com/datastore.

### System Interoperability Approaches

_Point-to-Point Integration:_ MVP should use point-to-point adapters to Android platform APIs and the weather provider. This is appropriate because the app is local-first and has no backend.

_API Gateway Patterns:_ Not needed for MVP. A backend/weather proxy could be introduced later if provider terms, API keys, or caching needs require it.

_Service Mesh / Enterprise Service Bus:_ Not applicable.

_Android launcher interoperability:_

- Home role: Declare a launcher/home activity with `ACTION_MAIN` + `CATEGORY_HOME` so Android can offer the app as a home screen. The app still depends on the user choosing it as default launcher.
- App discovery: Prefer `LauncherApps.getActivityList()` for launchable apps. If using PackageManager visibility, use targeted `<queries>` first. Treat `QUERY_ALL_PACKAGES` as a policy-sensitive fallback, not a default assumption.
- App launch: Use `LauncherApps.startMainActivity()` or launch intents for selected launchable activities.
- Shortcuts: App shortcuts are useful later but not required for MVP; integrating them expands scope beyond "pin app".
- Widgets: `AppWidgetHost` exists for home screens that embed widgets, but Android widgets are explicitly out of MVP because they would compete with the custom Live Tile system and add complexity.

_Source:_ Android Intent `CATEGORY_HOME`: https://developer.android.com/reference/android/content/Intent.html. `LauncherApps`: https://developer.android.com/reference/android/content/pm/LauncherApps. Google Play `QUERY_ALL_PACKAGES` policy: https://support.google.com/googleplay/android-developer/answer/10158779. `AppWidgetHost`: https://developer.android.com/reference/android/appwidget/AppWidgetHost. Widget host guide: https://developer.android.com/guide/topics/appwidgets/host.

### Microservices Integration Patterns

_API Gateway Pattern:_ Not applicable for MVP. If a backend is later introduced, a small weather proxy is the only plausible gateway-like component.

_Service Discovery:_ Not applicable.

_Circuit Breaker Pattern:_ Apply a local equivalent for weather: rate-limit refreshes, back off after repeated failures, use cached weather, and do not block Start Screen rendering on network.

_Saga Pattern:_ Not applicable.

_Local modularity pattern:_ Use a modular monolith inside the Android app. Keep feature modules/adapters independent enough that risky permissions can be disabled without breaking Start Screen:

- Launcher core works without notification, calendar, phone, SMS, or location permissions.
- Weather works with manual location and network only.
- Auto-location is optional.
- SMS/Phone/Calendar Tiles degrade to disabled/count-unavailable states without permissions.

_Source:_ Android runtime permission docs: https://developer.android.com/training/permissions/requesting. Android location permission docs: https://developer.android.com/develop/sensors-and-location/location/permissions/runtime.

### Event-Driven Integration

_Publish-Subscribe Patterns:_ Use in-app state streams: platform adapters publish `TileRuntimeState` updates to a repository; UI subscribes and renders. This should be local-only, not a broker.

_Event Sourcing:_ Not needed. Persist current layout and last-known tile state, not every event.

_Message Broker Patterns:_ Not applicable.

_CQRS Patterns:_ A light separation is useful: commands mutate layout/settings, queries render read-only Tile state. Do not formalize CQRS beyond clean repositories.

_Android event sources:_

- Notifications: event-like callback source for app counts.
- Package changes: `LauncherApps` callbacks can refresh App List metadata.
- Calendar: polling/query on permission and scheduled refresh is enough; no need for high-frequency observers in MVP.
- Weather: scheduled refresh via WorkManager and manual refresh on settings changes.
- Clock: local timer aligned to minute boundaries while Start Screen is visible.

_Source:_ `NotificationListenerService`: https://developer.android.com/reference/android/service/notification/NotificationListenerService. `LauncherApps`: https://developer.android.com/reference/android/content/pm/LauncherApps. WorkManager docs: https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work.

### Integration Security Patterns

_OAuth 2.0 and JWT:_ Not applicable for MVP because there is no user account/backend.

_API Key Management:_ Prefer no-key weather providers. Open-Meteo requires no API key but has non-commercial/free-use terms and rate limits. MET Norway also avoids app-specific secret handling but requires User-Agent identification and terms compliance. OpenWeather requires API keys and is less attractive without a backend.

_Mutual TLS:_ Not applicable.

_Data Encryption:_ Rely on Android app sandbox and HTTPS. Do not store notification text, SMS previews, caller previews, or calendar details in MVP. Counts and layout are low sensitivity but still user data.

_Prominent consent and disclosure:_

- Notification access must be opt-in and explained as powering count-only Live Tiles.
- Calendar access must be opt-in and explained as today's event count only.
- Location must default to manual city entry; auto-location should request approximate location where possible.
- SMS/Call Log direct permissions should be avoided for Google Play MVP unless later research proves launcher count use is acceptable and necessary.

_Policy-sensitive permissions:_

- Google Play restricts SMS and Call Log permission groups. For a public free launcher, direct `READ_SMS` and `READ_CALL_LOG` are high rejection risk unless the app qualifies under permitted uses. The safer MVP pattern is notification-derived counts.
- `QUERY_ALL_PACKAGES` is also restricted; use launcher-specific APIs and scoped visibility where possible.

_Source:_ Google Play SMS/Call Log policy: https://support.google.com/googleplay/android-developer/answer/10208820. Google Play `QUERY_ALL_PACKAGES` policy: https://support.google.com/googleplay/android-developer/answer/10158779. Android runtime permission docs: https://developer.android.com/training/permissions/requesting. Location permission docs: https://developer.android.com/develop/sensors-and-location/location/permissions/runtime.

### Integration Recommendations

**Recommended MVP integration decisions:**

1. Use `LauncherApps` as the primary app discovery/launch integration.
2. Avoid Android widgets in MVP; build custom Live Tiles instead.
3. Use notification listener access for SMS/Phone count behavior first.
4. Avoid direct `READ_SMS` and `READ_CALL_LOG` in the Play Store MVP unless later policy review explicitly approves them.
5. Use `READ_CALENDAR` only for Calendar count, behind opt-in.
6. Use manual weather location by default; optional approximate auto-location only after opt-in.
7. Use no-key weather provider integration if provider terms permit public free app distribution.
8. Use WorkManager for periodic weather refresh; avoid foreground services for low-priority background updates.
9. Treat all Live Tile data sources as optional adapters; Start Screen and App List must work without sensitive permissions.

**Integration risks:**

- Notification-derived counts may not perfectly match unread SMS/missed calls on all apps/OEMs.
- Google Play policy may reject direct SMS/Call Log permissions for a launcher.
- Weather provider "free" may not mean unrestricted for public distributed apps.
- Package visibility policy may require clear Play listing disclosure if broad visibility is requested.
- Compose tile grid reorder/animation remains a performance integration risk with the UI layer.

**Confidence:** High for Android API/policy constraints from official sources; medium for final weather provider choice until terms/legal compatibility is reviewed.

## Architectural Patterns and Design

### System Architecture Patterns

**Recommended pattern: local-first modular Android app with clean architecture boundaries.** WindowsPhone Launcher should be a single native Android application with clear UI, domain, and data layers. The system should not be a microservice or backend-dependent product in MVP.

_Architecture shape:_

- **UI layer:** Start Screen, App List, edit mode, settings, and permission explanation screens.
- **Domain layer:** Tile layout rules, tile resizing rules, tile state aggregation, permission-gated capability decisions, and Live Tile face generation.
- **Data layer:** Room/DataStore persistence, Android platform adapters, weather API adapter, and cached tile runtime state.

_Module boundaries:_

- `app`: Android entry point, manifest, dependency wiring.
- `feature-start-screen`: Tile Grid UI, edit mode, gestures, tile rendering.
- `feature-app-list`: alphabetical App List, search, pin action.
- `feature-settings`: color/background/weather/animation settings.
- `core-tile`: Tile models, sizes, layout rules, Live Tile state abstractions.
- `core-data`: Room, DataStore, repositories.
- `platform-launcher`: `LauncherApps`, app icons/labels, app launch.
- `platform-notifications`: notification listener and notification-derived counts.
- `platform-calendar`: Calendar Provider adapter.
- `platform-location`: optional approximate location adapter.
- `platform-weather`: weather provider adapter.
- `core-permissions`: permission state, explanation copy hooks, capability gates.

_Trade-off:_ This is more structure than a throwaway prototype, but it is justified because the launcher must degrade gracefully when permissions are denied and because UX/architecture/implementation will evolve through BMad artifacts.

_Source:_ Android app architecture guide: https://developer.android.com/topic/architecture. Android modularization guide: https://developer.android.com/topic/modularization.

### Design Principles and Best Practices

**Principle 1: Start Screen works without sensitive permissions.** The app should launch, list apps, pin tiles, resize tiles, and open apps without notification/calendar/location/SMS/call-log permissions. Sensitive integrations enrich tiles but cannot be prerequisites for the launcher.

**Principle 2: Permission-gated capabilities, not permission-gated screens.** Each Live Tile should expose a state machine:

- `Unavailable`: platform/API not supported or provider not configured.
- `NeedsPermission`: user has not granted required permission.
- `Loading`: adapter is resolving current state.
- `Ready`: count/weather/time is available.
- `Stale`: last known value exists but refresh failed or expired.
- `Error`: configuration/provider failure.

**Principle 3: Count-only privacy model.** MVP should not persist or render SMS previews, caller names, calendar titles, attendees, or notification text. It should store counts and coarse state only.

**Principle 4: Stable UI state model.** Compose UI should render immutable UI models from repositories. Fast-changing time/animation state should be isolated to visible tiles so the entire Tile Grid does not recompose every minute or animation frame.

**Principle 5: Explicit product fallback.** Every data source needs a fallback visible to UX:

- Notification access denied -> generic app Tile with no count.
- Calendar denied -> Calendar Tile shows permission-needed state or count unavailable.
- Weather provider unavailable -> last known weather with stale marker or setup-needed state.
- Auto-location denied -> manual city still works.

_Source:_ Android architecture guide: https://developer.android.com/topic/architecture. Runtime permissions: https://developer.android.com/training/permissions/requesting. Compose lifecycle/stability guidance: https://developer.android.com/develop/ui/compose/lifecycle.

### Scalability and Performance Patterns

For this product, "scalability" means **scaling across installed app count, tile count, device performance, and long-term daily usage**, not server load.

_Tile Grid performance:_

- Use lazy composition only where it does not break drag/reorder semantics.
- Keep Tile UI models stable and small.
- Avoid triggering full-grid recomposition for clock ticks or tile face flips.
- Run flip animation only for visible tiles.
- Disable or reduce animation when system animation scale/accessibility settings imply reduced motion, or when user toggles Live Tile animation off.
- Prototype Tile Grid with realistic tile counts early on low/mid-range Android devices.

_Background refresh:_

- Weather refresh should be scheduled conservatively with WorkManager and cached locally.
- Clock updates should be foreground/visible UI work, not background work.
- Notification counts should react to notification callbacks when access is enabled.
- Calendar count can refresh on app foreground, permission grant, and periodic low-frequency schedule.

_Battery strategy:_

- No always-on foreground service for MVP.
- No high-frequency weather polling.
- No continuous location tracking.
- Auto-location should resolve approximate location on demand or infrequently, then weather uses stored coordinates/location.

_Source:_ Compose performance docs: https://developer.android.com/jetpack/compose/performance. Compose animation quick guide: https://developer.android.com/develop/ui/compose/animation/quick-guide. WorkManager docs: https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work. Foreground service guidance: https://developer.android.com/develop/background-work/services/fgs.

### Integration and Communication Patterns

Use **adapter + repository** boundaries around every external system:

- `LauncherAppsAdapter` -> `AppRepository`
- `NotificationListenerAdapter` -> `NotificationCountRepository`
- `WeatherApiClient` + `GeocodingClient` -> `WeatherRepository`
- `CalendarProviderAdapter` -> `CalendarCountRepository`
- `LocationAdapter` -> `LocationRepository`

The UI should depend on repositories or use cases, not directly on Android providers or weather responses.

_Tile aggregation pattern:_

1. `TileLayoutRepository` emits ordered Tile definitions.
2. Each Tile definition resolves its runtime source.
3. Runtime repositories emit optional Tile state.
4. `TileStateAggregator` merges layout + runtime state + settings.
5. UI renders `TileUiModel`.
6. Live Tile animation derives front/back `LiveTileFace` from `TileUiModel`.

_Permission-gated adapter pattern:_

Each adapter must check capability before access. For example, `CalendarCountRepository` should expose `NeedsPermission` without querying Calendar Provider if `READ_CALENDAR` is not granted.

_Source:_ Android `LauncherApps`: https://developer.android.com/reference/android/content/pm/LauncherApps. `NotificationListenerService`: https://developer.android.com/reference/android/service/notification/NotificationListenerService. Calendar Provider: https://developer.android.com/identity/providers/calendar-provider.

### Security Architecture Patterns

**Security posture: minimal sensitive data, local-only, opt-in.**

_Data minimization:_

- Store Tile layout, settings, app component IDs, counts, timestamps, and weather state.
- Do not store notification text, SMS body, caller identity, calendar titles, attendees, or locations from calendar events.
- Do not send launcher layout, app inventory, or notification-derived data to any backend in MVP.

_Permission minimization:_

- Required for core launcher: home/launcher intent handling and app discovery/launch APIs.
- Optional: notification access, calendar read, location, internet/weather.
- Avoid direct SMS/Call Log permissions in MVP unless Play policy review says otherwise.

_User disclosure:_

- Explain notification access as count-only Live Tiles.
- Explain calendar access as today's event count only.
- Explain location as optional auto weather location, with manual city as default.

_Storage security:_

- Use internal storage/Room/DataStore under Android app sandbox.
- Avoid exporting content providers or services unless required.
- Notification listener service must be declared according to Android requirements and should expose only the minimum behavior.

_Source:_ Android security checklist: https://developer.android.com/guide/practices/security. Google Play SMS/Call Log policy: https://support.google.com/googleplay/android-developer/answer/10208820. Google Play package visibility policy: https://support.google.com/googleplay/android-developer/answer/10158779.

### Data Architecture Patterns

**Core data model:**

- `TileDefinition(id, type, componentName?, userHandle?, size, position, colorOverride?, createdAt, updatedAt)`
- `TileSize(widthUnits, heightUnits)` constrained to `1x1`, `2x2`, `4x2`.
- `TileRuntimeState(tileId, stateType, count?, textLabel?, iconKey?, updatedAt, staleAfter, errorCode?)`
- `LauncherSettings(colorMode, accentColor, backgroundMode, backgroundImageUri?, weatherLocationMode, manualLocation?, autoLocationEnabled, liveTileAnimationEnabled)`
- `WeatherState(locationLabel, temperature, conditionCode, provider, attribution, observedAt, fetchedAt, isStale)`

**Persistence split:**

- Room for Tile layout and cached runtime state.
- DataStore for settings and small user preferences.
- In-memory Flow/StateFlow for visible UI aggregation.

**Data lifecycle:**

- Layout persists indefinitely unless user unpins/resets.
- Counts are ephemeral and can be recomputed from notifications/providers.
- Weather cache persists with freshness metadata.
- Permission states should be derived from platform checks, not treated as permanently stored truth.

_Source:_ Room docs: https://developer.android.com/training/data-storage/room. DataStore docs: https://developer.android.com/datastore.

### Deployment and Operations Architecture

**Deployment:** Google Play public free app is the target. This increases the importance of Play policy compliance for package visibility, SMS/Call Log permissions, location, and notification access.

**No backend operations in MVP:** Avoid server operations, user accounts, sync, analytics, and remote config unless explicitly added later. This keeps cost and privacy surface low for a free launcher.

**Operational monitoring without invasive analytics:** For MVP testing, use local logs/debug builds and manual tester feedback. If crash reporting is added later, it must be disclosed and configured to avoid sensitive content.

**Release gates:**

- Play policy review for `QUERY_ALL_PACKAGES` if used.
- Play policy review before declaring any SMS/Call Log permissions.
- Weather provider terms review.
- Low/mid-range device performance test for 7-day daily launcher target.
- Permission-denied UX test for every optional data source.

_Source:_ Google Play restricted permissions policy: https://support.google.com/googleplay/android-developer/answer/10158779 and https://support.google.com/googleplay/android-developer/answer/10208820. Android privacy/permissions: https://developer.android.com/training/permissions/requesting.

### Architectural Decision Summary

**Recommended architecture:** Kotlin native Android, local-first modular monolith, Compose-first UI with measured fallback to custom View for the Tile Grid if performance/reorder requires it.

**Key ADR candidates for architecture phase:**

- ADR-1: Native Android/Kotlin over cross-platform.
- ADR-2: Local-first MVP with no backend.
- ADR-3: LauncherApps for app discovery/launch.
- ADR-4: Notification-derived SMS/Phone counts for Play policy risk reduction.
- ADR-5: Direct Calendar Provider access for count-only Calendar Tile after opt-in.
- ADR-6: Weather provider adapter with manual location default.
- ADR-7: Compose-first UI with Tile Grid performance prototype gate.
- ADR-8: Room + DataStore persistence split.

**Confidence:** High for local-first modular architecture and permission-gated adapters; medium for Compose-first Tile Grid until prototype performance is verified.

## Implementation Approaches and Technology Adoption

### Technology Adoption Strategies

Adopt the stack incrementally through prototype gates rather than committing every risky part at once. The riskiest MVP areas are not Kotlin or Room/DataStore; they are launcher behavior, package visibility, Tile Grid reorder/resize, flip animation smoothness, Play policy-sensitive permissions, and weather provider terms.

**Recommended adoption sequence:**

1. **Launcher shell prototype:** Declare launcher/home activity, verify Android can select WindowsPhone Launcher as home screen, and confirm Back/Home behavior.
2. **App List prototype:** Use `LauncherApps` to list launchable apps alphabetically, search, and launch selected apps.
3. **Tile Grid prototype:** Implement `1x1`, `2x2`, `4x2` layout rules and persistence.
4. **Edit-mode prototype:** Long press, move, resize, pin/unpin, no overlap.
5. **Flip animation prototype:** Prove visible-tile flip animation remains smooth during scroll and app launch.
6. **Weather MVP slice:** Manual city/location first, free provider adapter, cache, stale state.
7. **Notification-count slice:** Notification access opt-in and count-only Tile state.
8. **Calendar-count slice:** `READ_CALENDAR` opt-in and today's count only.
9. **Settings MVP:** Color mode, accent color, background, weather location, animation toggle.

This sequence preserves the product's primary success metric: a smooth daily launcher.

_Source:_ Android app architecture guide: https://developer.android.com/topic/architecture. Compose performance guidance: https://developer.android.com/jetpack/compose/performance. Google Play Android vitals: https://support.google.com/googleplay/android-developer/answer/9844486.

### Development Workflows and Tooling

**Recommended workflow:**

- Android Studio + Kotlin + Gradle Kotlin DSL.
- Version catalog for dependency versions.
- Compose Compiler Gradle plugin for Kotlin 2.x era Compose setup.
- Multi-module build once the prototype stabilizes; avoid over-modularizing before the first working launcher shell.
- ADR files for major technical decisions.
- PRD FR IDs referenced in implementation tickets and tests.

**Build setup direction:**

- Use modern Android Gradle Plugin and Kotlin versions available at project start.
- Enable Compose in modules that render UI.
- Use Room, DataStore, WorkManager, and AndroidX Test libraries.
- Keep a `benchmark` module for Macrobenchmark/Baseline Profile work once Start Screen exists.

**Code quality:**

- Keep platform adapters thin and testable.
- Do not put Android provider logic directly inside Composables.
- Do not let animation state mutate persisted tile layout.
- Use fake repositories for UI tests and preview states.

_Source:_ Android build configuration: https://developer.android.com/build. Compose setup and compiler plugin docs: https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler.

### Testing and Quality Assurance

**Local unit tests:**

- Tile size validation: only `1x1`, `2x2`, `4x2`.
- Layout placement and collision rules.
- Resize rules and fallback when target size unsupported.
- App List sorting and search filtering.
- Weather response mapping and stale/error logic.
- Permission-state to Tile-state mapping.
- Settings persistence mapping.

**Instrumented tests:**

- App List discovery and app launch using Android framework APIs.
- Start Screen interactions: tap, long press, edit mode, pin, unpin, resize, reorder.
- Settings screens and DataStore persistence.
- Permission-denied and permission-granted flows for notification/calendar/location where feasible.
- Android Back behavior from App List to Start Screen.

**Performance tests:**

- Macrobenchmark cold startup into Start Screen.
- Start Screen vertical scroll with realistic tile count.
- App List search responsiveness.
- Flip animation frame timing.
- Edit-mode drag/reorder responsiveness.
- Baseline Profile comparison when available.

**Manual/device tests:**

- Set as default launcher on multiple Android versions/OEMs.
- Notification listener enable/disable.
- Calendar permission grant/deny.
- Location approximate/deny/manual weather fallback.
- Google Play pre-launch report before public release.

_Source:_ Android testing fundamentals: https://developer.android.com/training/testing/fundamentals/what-to-test. Android testing strategies: https://developer.android.com/training/testing/fundamentals/strategies. AndroidJUnitRunner docs: https://developer.android.com/training/testing/junit-runner.html. Macrobenchmark/Baseline Profiles: https://developer.android.com/topic/performance/baselineprofiles/measure-baselineprofile.

### Deployment and Operations Practices

**Deployment target:** Google Play public free app.

**Release strategy:**

- Internal testing track for launcher shell and risky permission flows.
- Closed testing with former Windows Phone users for subjective WP8.1 feel.
- Open testing only after crash/ANR/performance gates pass.
- Public release after Play policy declarations are reviewed.

**Operations without backend:**

- No server operations in MVP.
- No user accounts.
- No remote sync.
- No analytics by default unless explicitly added later with disclosure.
- Monitor Android vitals after Play release: user-perceived crash rate, user-perceived ANR rate, startup, and battery-related metrics.

**Policy gates before release:**

- Package visibility declaration strategy.
- Notification access disclosure.
- Calendar permission disclosure.
- Location permission disclosure.
- Weather provider attribution/terms.
- Avoid SMS/Call Log permissions unless there is a strong approved policy case.

_Source:_ Google Play Android vitals: https://support.google.com/googleplay/android-developer/answer/9844486. Google Play package visibility policy: https://support.google.com/googleplay/android-developer/answer/10158779. Google Play SMS/Call Log policy: https://support.google.com/googleplay/android-developer/answer/10208820.

### Team Organization and Skills

**Minimum skill set:**

- Android/Kotlin engineer comfortable with launcher intents, `LauncherApps`, permissions, services, Room/DataStore, WorkManager, and Compose.
- UX/product designer familiar with Windows Phone 8.1 interaction patterns and Android gesture conventions.
- QA tester with physical Android devices, including low/mid-range devices.
- Policy/release owner for Google Play permissions and weather provider terms.

**Development discipline:**

- Prototype first where platform risk is high.
- Treat UI smoothness as a release-blocking requirement, not polish.
- Keep permission flows visible in UX and test plans.
- Write ADRs for policy-sensitive or architecture-sensitive decisions.

_Source:_ Android modularization and architecture docs: https://developer.android.com/topic/modularization and https://developer.android.com/topic/architecture.

### Cost Optimization and Resource Management

**Cost model:** MVP can be near-zero infrastructure cost if it avoids backend and paid weather providers.

**Weather cost control:**

- Prefer no-key/free weather provider only if terms allow public free app usage.
- Cache weather results.
- Rate-limit refresh.
- Refresh on visible demand/settings changes rather than high-frequency background polling.
- Consider backend/proxy only if provider terms or API-key security require it later.

**Battery/resource control:**

- No always-on foreground service.
- No continuous location.
- No high-frequency network polling.
- Animate only visible tiles.
- Allow animation off toggle.

_Source:_ WorkManager docs: https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work. Foreground services overview: https://developer.android.com/develop/background-work/services/fgs. Open-Meteo terms: https://open-meteo.com/en/terms.

### Risk Assessment and Mitigation

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Compose Tile Grid jank | Breaks core MVP promise | Prototype early; measure on real devices; fallback to custom View for Start Screen |
| SMS/Call Log permission rejection | Blocks Play release | Use notification-derived counts first; avoid direct SMS/Call Log permissions |
| Weather provider terms mismatch | Forces provider change | Review Open-Meteo/MET terms before implementation; keep provider adapter swappable |
| Notification-derived counts inaccurate | Tile counts may be imperfect | Communicate count availability; degrade gracefully; do not promise exact unread database counts |
| Package visibility policy issue | Play review risk | Prefer `LauncherApps`; avoid `QUERY_ALL_PACKAGES` unless justified and declared |
| Battery drain from updates/animations | User abandons launcher | WorkManager, caching, visible-only animation, animation toggle |
| Permission denial breaks Live Tiles | Poor first-run UX | Permission-gated tile states; Start Screen remains useful without sensitive permissions |

_Source:_ Google Play policy sources above; Android vitals and background work docs above.

## Technical Research Recommendations

### Implementation Roadmap

**Phase 1: Launcher foundation**

- Native Android project.
- Home/launcher manifest.
- App List with `LauncherApps`.
- App launching.
- Basic Start Screen with persisted Tile layout.

**Phase 2: Start Screen MVP**

- Tile sizes `1x1`, `2x2`, `4x2`.
- Pin/unpin/resize/reorder.
- Horizontal App List gestures and Android Back behavior.
- Color/background settings.

**Phase 3: Live Tile MVP**

- Digital Clock Tile.
- Weather Tile with manual location and provider adapter.
- Flip animation prototype and performance gate.
- Notification-derived SMS/Phone counts.
- Calendar count after opt-in.

**Phase 4: Hardening**

- Permission-denied states.
- Macrobenchmark/Baseline Profile.
- Low/mid-range device testing.
- Play policy forms and release checklist.

### Technology Stack Recommendations

- Kotlin native Android.
- Compose-first UI with custom View fallback for Tile Grid if needed.
- Room for layout/state cache.
- DataStore for settings.
- WorkManager for weather refresh.
- `LauncherApps` for app discovery/launch.
- `NotificationListenerService` for count-derived Live Tiles.
- Calendar Provider for today's event count.
- Open-Meteo or MET Norway after terms review.

### Skill Development Requirements

- Android launcher/home intent behavior.
- Modern Android package visibility and Play policy.
- Notification listener implementation and user enablement flow.
- Compose performance and stability.
- Drag/reorder UI implementation.
- Android Macrobenchmark and Baseline Profile testing.
- Permission UX and privacy-first copywriting.

### Success Metrics and KPIs

**Technical KPIs:**

- Cold Start Screen display within acceptable launcher expectations on target devices.
- Smooth Start Screen scroll with realistic tile count.
- Flip animation does not cause visible jank on target low/mid-range devices.
- No user-perceived ANR clusters in Play testing.
- No Play policy rejection for permissions/package visibility.
- Weather refresh succeeds and caches fallback state.

**Product-aligned KPIs:**

- Test users can use it as primary launcher for 7 days without heaviness/lag complaints.
- Former Windows Phone users report the Start Screen feels recognizably WP8.1-like.

# WindowsPhone Launcher: Comprehensive Android Launcher and Live Tile Technical Research

## Executive Summary

WindowsPhone Launcher is technically feasible as a free public Android launcher, but it should be treated as a performance-sensitive, policy-sensitive native Android product rather than a simple themed icon grid. The core product promise depends on three hard constraints: Android must accept the app as a launcher, the Start Screen must remain smooth enough for daily use, and Live Tile information must be useful without overreaching into sensitive user data.

The recommended MVP architecture is a local-first Kotlin Android app with no backend. Use `LauncherApps` for app discovery and launching, custom in-app Live Tiles for the Windows Phone-style experience, DataStore for settings, Room for Tile layout and cached state, WorkManager for conservative weather refresh, and optional permission-gated adapters for notification, calendar, weather, and location. The MVP should avoid Android widgets, direct SMS reads, and direct Call Log reads unless a later policy review explicitly approves them.

The most important strategic decision is to preserve smoothness and privacy over feature depth. For a public free launcher, notification-derived SMS/Phone counts are safer than `READ_SMS` / `READ_CALL_LOG`; manual weather location is safer than requiring location; and count-only Calendar/SMS/Phone tiles are safer than previews. UX should assume permissions can be denied and still leave the Start Screen and App List fully usable.

**Key Technical Findings:**

- Native Kotlin Android is the right platform; cross-platform frameworks add friction for launcher APIs, permissions, and performance.
- `LauncherApps` is the preferred app discovery/launch API for a launcher-like app.
- Compose is viable for MVP UI, but Tile Grid reorder/resize and flip animation require a real-device performance prototype gate.
- Direct SMS and Call Log permissions are high-risk for Google Play review; notification-derived counts are the safer MVP path.
- Weather provider selection is not just technical; Open-Meteo and MET Norway are strong candidates but terms, attribution, and public-app compatibility must be reviewed before release.
- The app should not need a backend for MVP.

**Technical Recommendations:**

- Build the MVP as a local-first modular Android app.
- Keep Start Screen and App List functional without sensitive permissions.
- Use custom Live Tiles, not Android widgets, for the MVP tile system.
- Implement Weather, Clock, SMS, Phone, and Calendar as optional Tile state adapters.
- Make smooth scrolling, flip animation, and 7-day launcher usability release gates.

## Table of Contents

1. Technical Research Scope Confirmation
2. Technology Stack Analysis
3. Integration Patterns Analysis
4. Architectural Patterns and Design
5. Implementation Approaches and Technology Adoption
6. Strategic Synthesis
7. Implementation Roadmap
8. Risk Assessment
9. Source Verification Summary
10. Next Steps

## Strategic Synthesis

### Technical Landscape

Android supports third-party launchers through home intent behavior and launcher-oriented APIs, but modern Android and Google Play policy constrain how broadly an app can inspect installed apps and sensitive user data. This matters directly to WindowsPhone Launcher because the app wants an App List, SMS count, missed call count, Calendar count, and notification-derived Tile updates.

The launcher should therefore be designed around capability gates. App discovery and launch are core capabilities. Notification access, Calendar access, auto-location, and weather network access are optional enrichment capabilities. The Start Screen must never become unusable because a user denies one of those permissions.

### Architecture Direction

The recommended architecture is a modular monolith:

- UI layer renders Start Screen, App List, settings, and permission states.
- Domain layer owns Tile models, layout rules, resize rules, Live Tile face generation, and permission capability decisions.
- Data layer owns Room, DataStore, Android platform adapters, weather provider adapters, and cached runtime Tile state.

This structure keeps UX and platform risk separated. For example, the Weather Tile UI should not care whether the provider is Open-Meteo or MET Norway; it should render `Ready`, `Stale`, `NeedsSetup`, or `Error` state.

### Implementation Direction

The implementation should proceed through prototype gates:

1. Launcher shell and default home behavior.
2. App List via `LauncherApps`.
3. Persisted Tile Grid with `1x1`, `2x2`, `4x2`.
4. Pin, unpin, resize, reorder, and edit mode.
5. Flip animation performance test.
6. Weather Tile with manual location and cached provider result.
7. Notification-derived SMS/Phone counts.
8. Calendar count with opt-in `READ_CALENDAR`.
9. MVP settings and permission explanation flows.

Each gate should be usable before moving to the next. This protects the core goal: a launcher that feels light enough to use every day.

## Risk Assessment

| Risk | Severity | Recommendation |
| --- | --- | --- |
| Tile Grid jank in Compose | High | Prototype early on real low/mid-range devices; fallback to custom View if needed. |
| Google Play rejection for SMS/Call Log permissions | High | Avoid `READ_SMS` and `READ_CALL_LOG` in MVP; use notification-derived counts first. |
| Weather provider terms incompatible with public free app | Medium | Review Open-Meteo and MET Norway terms before implementation; keep provider adapter swappable. |
| Package visibility review issue | Medium | Prefer `LauncherApps`; avoid `QUERY_ALL_PACKAGES` unless necessary and declared. |
| Permission denial makes tiles feel broken | Medium | Design explicit `NeedsPermission`, `Unavailable`, and `Stale` tile states. |
| Battery drain from background refresh/animation | High | Use WorkManager, cache weather, animate visible tiles only, provide animation toggle. |

## Source Verification Summary

Primary source categories used:

- Android Developers: Intent/home behavior, `LauncherApps`, `NotificationListenerService`, Calendar Provider, CallLog/SMS providers, Room, DataStore, WorkManager, foreground services, runtime/location permissions, Compose, Lazy layouts, Compose performance, testing, Macrobenchmark/Baseline Profiles, architecture, modularization, security.
- Google Play policy: `QUERY_ALL_PACKAGES`, SMS/Call Log restrictions, Android vitals.
- Weather provider sources: Open-Meteo terms/API pages, MET Norway Locationforecast documentation.

Confidence levels:

- **High:** Native Android stack, local-first architecture, permission-gated adapters, avoiding always-on foreground service, use of Room/DataStore/WorkManager.
- **High:** SMS/Call Log direct access is policy-sensitive and risky for public Play distribution.
- **Medium:** Final weather provider choice until terms and attribution are reviewed for a public free app.
- **Medium:** Compose-first Tile Grid until reorder/resize/flip animation is benchmarked on real devices.

## Next Steps

1. Run `bmad-ux` to specify the Start Screen, App List, edit mode, Tile states, permission prompts, Weather setup, and settings interaction details.
2. Run `bmad-create-architecture` to turn this research into formal ADRs and implementation architecture.
3. Update the PRD open questions with research conclusions:
   - Prefer `LauncherApps`.
   - Prefer notification-derived SMS/Phone counts.
   - Avoid direct SMS/Call Log permissions in MVP.
   - Keep Weather provider swappable pending terms review.
   - Build permission-denied states into UX.
4. Prototype Tile Grid performance before committing to Compose-only implementation.

---

**Technical Research Completion Date:** 2026-06-02  
**Research Period:** Current public-source technical analysis  
**Source Verification:** Technical claims grounded in current Android, Google Play, and weather-provider documentation  
**Technical Confidence Level:** High for architecture direction; medium for provider and UI-performance decisions pending prototypes/review
