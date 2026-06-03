---
name: WindowsPhone Launcher
description: Android launcher visual system inspired by Windows Phone 8.1 Live Tiles, modernized for readable daily use.
status: draft
sources:
  - ../../prds/prd-WindowsPhone Launcher-2026-06-02/prd.md
  - ../../research/technical-android-launcher-live-tile-constraints-research-2026-06-02.md
updated: 2026-06-02
colors:
  background-classic: '#000000'
  background-overlay: '#111111'
  ink-primary: '#FFFFFF'
  ink-on-light-tile: '#000000'
  ink-secondary: '#D8D8D8'
  ink-muted: '#9A9A9A'
  tile-cyan: '#00BCF2'
  tile-blue: '#0078D7'
  tile-green: '#00A300'
  tile-red: '#E51400'
  tile-purple: '#800080'
  tile-orange: '#F7630C'
  tile-focus: '#FFFFFF'
  tile-focus-dark: '#000000'
  tile-disabled: '#3A3A3A'
typography:
  tile-label:
    note: 'Android Compose body label, slightly heavier than Windows Phone original'
    fontSize: 12sp
    fontWeight: 500
    lineHeight: 14sp
  tile-info:
    note: 'Android Compose title/number style for Weather and Clock Tiles'
    fontSize: 22sp
    fontWeight: 500
    lineHeight: 26sp
  app-list-name:
    note: 'Android Compose body text, readable at phone portrait sizes'
    fontSize: 16sp
    fontWeight: 500
    lineHeight: 22sp
  alphabet-header:
    note: 'Small section marker with low visual weight'
    fontSize: 13sp
    fontWeight: 500
    lineHeight: 18sp
rounded:
  tile: 0px
  control: 0px
  input: 0px
spacing:
  tile-gap: 4dp
  screen-edge: 8dp
  app-list-row-gap: 8dp
  app-list-section-gap: 16dp
  settings-row-gap: 12dp
  min-touch-target: 48dp
components:
  tile-grid:
    columns: 4
    gap: '{spacing.tile-gap}'
    edge: '{spacing.screen-edge}'
  live-tile:
    background: '{colors.tile-blue}'
    foreground: '{colors.ink-primary}'
    radius: '{rounded.tile}'
    gap: '{spacing.tile-gap}'
  non-animated-live-tile:
    background: '{colors.tile-blue}'
    foreground: '{colors.ink-primary}'
    radius: '{rounded.tile}'
  static-app-tile:
    background: '{colors.tile-disabled}'
    foreground: '{colors.ink-primary}'
    radius: '{rounded.tile}'
  weather-tile:
    hierarchy: info-first
  clock-tile:
    hierarchy: info-first
  sms-tile:
    hierarchy: icon-first-count-secondary
  phone-tile:
    hierarchy: icon-first-count-secondary
  calendar-tile:
    hierarchy: icon-first-count-secondary
  app-list-row:
    foreground: '{colors.ink-primary}'
    secondary: '{colors.ink-secondary}'
    minHeight: '{spacing.min-touch-target}'
  search-field:
    background: '{colors.background-overlay}'
    foreground: '{colors.ink-primary}'
    radius: '{rounded.input}'
    minHeight: '{spacing.min-touch-target}'
  edit-control:
    visibleSize: 24dp
    hitTarget: '{spacing.min-touch-target}'
  settings-row:
    minHeight: '{spacing.min-touch-target}'
---

## Brand & Style

WindowsPhone Launcher should feel like a clean continuation of the Windows Phone 8.1 Start Screen rather than a generic Android theme. The visual signature is sharp, dense, colorful Tiles on a black canvas, with movement living inside each Tile boundary.

The system is nostalgic but not fragile. Typography is more readable than the original Windows Phone style: labels are clearer and slightly heavier, while the layout remains minimal and quiet. The product should not drift into Material card surfaces, rounded widgets, gradients, shadows, or decorative chrome.

## Colors

The default canvas is **Classic Black** (`{colors.background-classic}`). Image backgrounds are allowed, but Tiles remain solid color; the image only appears behind the Tile Grid and in gaps.

Default Tile colors use a Windows Phone classic palette: cyan, blue, green, red, purple, and orange. Tiles default to vivid multi-color assignment. Unified accent color mode uses one selected Tile color across supported Tiles.

Text and monochrome icons prefer `{colors.ink-primary}` on Tile surfaces, but contrast wins. Cyan, green, orange, or any user-selected accent that fails contrast must use `{colors.ink-on-light-tile}`, a darker overlay, or restrict text to large display roles only. Minimum contrast target for normal Tile labels/counts is 4.5:1.

Avoid: beige themes, gradients, translucent Tiles over wallpaper, pastel-only palettes, and excessive color variants.

## Typography

Typography follows Android readability rather than exact Windows Phone thinness. Labels use `{typography.tile-label}` and should be legible at phone portrait sizes. Weather and Clock Tiles use `{typography.tile-info}` and are information-first.

App List names use `{typography.app-list-name}` with enough weight for scanning. Alphabet headers use `{typography.alphabet-header}` and should support fast visual grouping without becoming navigation chrome.

## Layout & Spacing

The Start Screen uses a 4-column vertical Tile Grid:

- `1x1` spans 1 column.
- `2x2` spans 2 columns.
- `4x2` spans all 4 columns.

Spacing is tight via `{spacing.tile-gap}`. The Start Screen has no header, no persistent search bar, and no settings shortcut above the grid. Tiles begin near the top of the screen with only minimal safe-edge breathing room.

## Elevation & Depth

The visual system avoids shadows and card elevation. Edit Mode may make the active Tile feel lifted through scale, outline, or motion, but not through Material-style shadow depth.

## Shapes

Tiles are sharp-cornered with `{rounded.tile}`. Controls and inputs also stay square in MVP. Rounded Android surfaces are intentionally avoided because square geometry is part of the product identity.

## Components

Key-screen reference: `mockups/key-screens.html`. Spines win on conflict with the mockup.

- **Tile Grid** - Four columns, tight spacing, sharp edges, no header chrome.
- **Live Tile** - Solid Tile color, sharp corners, contrast-safe monochrome icon when feasible, app icon fallback when monochrome conversion is poor.
- **Non-Animated Live Tile** - Same content hierarchy as Live Tile, but no flip/face switching. Used when motion is disabled while data remains available.
- **Static App Tile** - Same Tile shape and color system, no live count/info because the data source is unavailable, permission is missing, or the Tile is a plain app Tile.
- **Weather Tile** - Info-first `4x2` default. Setup state uses `Set location`. Open-Meteo provider attribution must not appear on Start Screen unless legally required.
- **Clock Tile** - Info-first digital clock.
- **SMS Tile / Phone Tile / Calendar Tile** - Icon-first with count as secondary. No previews.
- **Edit Mode Tile** - Active Tile feels lifted/selected. Small visual corner controls appear for unpin and resize, but each control has a `{spacing.min-touch-target}` invisible hit area.
- **Edit Control** - Visual icon can be small for WP authenticity; hit target must remain at least `{spacing.min-touch-target}` and non-overlapping.
- **App List Row** - Icon + app name, vertical Windows Phone-style list with alphabet headers.
- **App List Search** - Fixed search field at the top of App List, using `{components.search-field}`.
- **Settings Tile** - Launcher-owned Tile that can be pinned to the Start Screen; no global header settings button.
- **Onboarding Step** - Full-screen simple step with one primary action and one skip/secondary action where applicable.
- **Weather Setup** - Manual city/location search first, resolved through Open-Meteo by default. Optional auto-location action secondary.
- **Settings Row** - Square, dense row with clear label/value/toggle. No cards.

## Do's and Don'ts

| Do | Don't |
|---|---|
| Use sharp square Tiles | Round Tile corners |
| Keep Tile spacing tight | Turn the Start Screen into card UI |
| Use classic WP colors | Replace the palette with one-note gradients |
| Switch to dark foreground or overlay when contrast fails | Put white text on cyan/green/orange when it fails contrast |
| Keep wallpaper behind solid Tiles | Make Tiles translucent over image backgrounds |
| Use readable Android-weight typography | Recreate ultra-thin labels that hurt readability |
