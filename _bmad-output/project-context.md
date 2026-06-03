---
project_name: 'WindowsPhone Launcher'
user_name: 'Edward'
date: '2026-06-02'
sections_completed: ['discovery']
existing_patterns_found: 0
source_documents:
  - '_bmad-output/planning-artifacts/prds/prd-WindowsPhone Launcher-2026-06-02/prd.md'
  - '_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/DESIGN.md'
  - '_bmad-output/planning-artifacts/ux-designs/ux-WindowsPhone Launcher-2026-06-02/EXPERIENCE.md'
  - '_bmad-output/planning-artifacts/research/technical-android-launcher-live-tile-constraints-research-2026-06-02.md'
---

# Project Context for AI Agents

_This file contains critical rules and patterns that AI agents must follow when implementing code in this project. Focus on unobvious details that agents might otherwise miss._

---

## Technology Stack & Versions

Discovery state: no Android source project or build files exist yet in the repository. No exact Gradle, Kotlin, Android Gradle Plugin, Compose, Room, DataStore, or WorkManager versions have been selected in code.

Planned stack from research and UX decisions:

- Native Android application.
- Kotlin-first codebase.
- Jetpack Compose-first UI, with a custom View/custom layout fallback for the Start Screen Tile Grid if prototype performance fails.
- Room for Tile layout and cached runtime state.
- DataStore for settings and small preferences.
- WorkManager for low-priority weather refresh.
- `LauncherApps` for launchable app discovery and app launching.
- `NotificationListenerService` for best-effort SMS/Phone count Live Tiles.
- Calendar Provider for today's event count after opt-in.
- Open-Meteo as default weather provider.

## Critical Implementation Rules

Discovery placeholder. Detailed implementation rules will be generated in the next workflow step after confirmation.
