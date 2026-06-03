# Spine Pair Review - WindowsPhone Launcher

## Overall verdict

The UX spine pair is directionally solid and gives architecture/dev a recognizable Windows Phone-style product contract, but it is not yet clean enough for downstream implementation without follow-up interpretation. The largest gaps are component contract completeness, state coverage across non-Start-Screen surfaces, and key-flow coverage for several PRD requirements that are currently specified only as tables or isolated rules.

## 1. Flow coverage - thin

Checked PRD UJ-1 and FR-1 through FR-25 against EXPERIENCE.md Key Flows. The draft has four named-protagonist flows with numbered steps and climax beats, and Flow 1 covers the main UJ-1 promise well.

### Findings

- **high** Several PRD requirements are not exercised by a Key Flow, especially unpinning, settings configuration, color mode/accent/background changes, animation toggle, and Android Back behavior from App List. These are load-bearing MVP requirements in PRD FR-9, FR-18, and FR-21 through FR-25, but Key Flows cover only first run, find/pin, edit/resize, and weather setup (EXPERIENCE.md:132-172; PRD.md FR list at 146, 238, 270-304). *Fix:* Add one settings flow that covers color mode, accent color, image/black background, weather location, and animation toggle; add explicit steps for Android Back and unpin either in Flow 2/3 or separate compact flows.
- **medium** Failure paths are uneven. Flow 1 covers skipped permissions and Flow 4 covers weather fetch failure, but there is no failure/edge path for default-launcher setup cancellation, unsupported resize target, failed pin placement, empty App List/search result beyond the state table, or permission access being revoked after onboarding (EXPERIENCE.md:136-172). *Fix:* Add one failure-path paragraph or branch per affected flow where the state table already defines the outcome.
- **low** Requirement names and IDs are not carried into the flows, which makes traceability harder for story slicing. *Fix:* Add inline tags such as "Covers FR-8, FR-20" below each flow title or in a short coverage matrix.

## 2. Token completeness - adequate

Checked DESIGN.md YAML tokens and prose references. Color tokens include hex values, shape/spacing tokens are defined, and prose references such as `{colors.background-classic}`, `{spacing.tile-gap}`, and `{components.search-field}` resolve.

### Findings

- **medium** Contrast requirements are not stated for load-bearing text/icon combinations, and some declared default tile colors are risky with white text/icons. DESIGN.md says text and monochrome icons should use white on Tile surfaces, including cyan, green, and orange (DESIGN.md:15-20, 80), but it does not define contrast targets or fallback treatment. *Fix:* State the minimum contrast target for tile labels/counts and define per-color treatment where white is insufficient, such as darker overlay, alternate ink token, larger-only usage, or color exclusion for text-heavy tiles.
- **medium** Typography tokens define weight and notes but no size, line height, truncation, or density constraints for the most constrained surfaces, especially `1x1` tiles and App List rows (DESIGN.md:23-35, 84-88). *Fix:* Add semantic Compose style references or token fields for tile label, tile info, app list row text, and alphabet headers.
- **low** Focus/disabled visual tokens exist but are not described in prose or mapped to components/states (DESIGN.md:21-22). *Fix:* Either specify how focus and disabled/unavailable tiles use those tokens, or remove the unused tokens until needed.

## 3. Component coverage - broken

Checked every component-like name in DESIGN.md and EXPERIENCE.md for both visual and behavioral coverage. The behavioral table is stronger than the visual contract, but naming is not aligned across the two spines.

### Findings

- **high** Multiple EXPERIENCE.md components have no matching DESIGN.md component spec: Tile Grid, Weather Tile, Clock Tile, SMS Tile, Phone Tile, Calendar Tile, App List Search, and Edit Controls (EXPERIENCE.md:62-73; DESIGN.md:46-58, 108-116). Downstream builders will have to infer visual structure, sizing, icon/count hierarchy, edit affordance placement, and tile-specific content rules. *Fix:* Add visual component entries or a component table in DESIGN.md for each named behavioral component, even if several inherit from Live Tile.
- **high** Component names diverge between spines: DESIGN.md has "Search Field" while EXPERIENCE.md has "App List Search"; DESIGN.md has "Edit Mode Tile" while EXPERIENCE.md has "Edit Controls"; DESIGN.md has "Weather Setup Tile State" while EXPERIENCE.md uses "Weather Tile" and state rows (DESIGN.md:112-115; EXPERIENCE.md:65, 72-73, 81). *Fix:* Normalize names and use aliases only when explicitly declared.
- **medium** IA surfaces for Onboarding, Weather Setup, and Launcher Settings have no component-level contract beyond purpose and some microcopy/settings mentions (EXPERIENCE.md:30-37, 46-56). *Fix:* Add component patterns for onboarding step layout/actions, weather setup fields, settings rows/controls, and permission toggles.

## 4. State coverage - thin

Walked IA surfaces from EXPERIENCE.md: Onboarding, Start Screen, App List, Edit Mode, Weather Setup, and Launcher Settings. The draft covers major tile degradation states but leaves several surface-level states unstated.

### Findings

- **high** Launcher Settings and Weather Setup lack form states: initial, focus, validation error, provider/network failure, permission denied for auto-location, save success/cancel, and stale cached weather source selection (EXPERIENCE.md:36-37, 75-87, 164-172). *Fix:* Add state rows for Weather Setup and Launcher Settings, including exact fallback destinations.
- **medium** App List states are incomplete. Search empty is covered, but initial loading/app discovery, no installed launchable apps, package list changing, search focus/clear, and pin action success/failure are not specified (EXPERIENCE.md:34, 71-72, 86, 144-152). *Fix:* Add state rows for App List loading, no apps, filtered results, and pin feedback.
- **medium** Edit Mode has only the active state. It does not define cancel/exit, invalid drop, unsupported resize target, target-size omission, collision/reflow preview, or accessible reorder/resize alternatives (EXPERIENCE.md:85, 91-95, 113-117). *Fix:* Add edit-mode state rows and tie them to FR-5/FR-10 consequences.
- **medium** Start Screen has no explicit cold-load/restoring-layout state, empty/unpinned state, app removed/uninstalled state, or background-image readability state despite PRD FR-14 (EXPERIENCE.md:33, 75-87; DESIGN.md:69, 126). *Fix:* Add Start Screen state rows for restored layout, empty grid, missing app target, and image background readability handling.

## 5. Visual reference coverage - adequate

Checked `mockups/`, `wireframes/`, and `imports/`. No visual reference files exist beyond placeholder `.gitkeep` files in `.working/` and `imports/`, so there are no orphaned mockups or unlinked visual files.

### Findings

- **medium** The draft explicitly leaves key-screen visual mocks open for Start Screen, App List, Edit Mode, Onboarding, Weather Setup, and Settings (EXPERIENCE.md:179). That is acceptable for draft status, but implementation of the Start Screen and edit affordances will need visual anchors because the product depends on nostalgia, density, and motion posture. *Fix:* Produce at least Start Screen, App List, and Edit Mode mocks before build handoff; then link them inline and state that spines win on conflict.

## 6. Bloat & overspecification - strong

The pair is compact and mostly decision-oriented. DESIGN.md avoids restating the PRD at length, and EXPERIENCE.md uses tables where appropriate.

### Findings

- **low** A few lines restate product promise rather than contract, especially in Brand & Style and Foundation, but they are short and useful for tone (DESIGN.md:63-65; EXPERIENCE.md:18-24). *Fix:* No urgent change; keep them unless the document grows.

## 7. Inheritance discipline - adequate

Checked source references, naming inheritance, requirement naming, and token references. Frontmatter sources appear structurally correct relative to the UX workspace, and most product language stays aligned with the PRD and technical research.

### Findings

- **medium** EXPERIENCE.md resolves several PRD open questions by decision, but does not mark those decisions against the original OQ IDs. Examples: Settings entry is decided as a Settings Tile despite PRD OQ-10, notification-derived SMS/Phone counts address OQ-1, and default launcher Back behavior from App List is decided while Start Screen Back remains open in PRD OQ-7 (EXPERIENCE.md:32, 41-44, 67-68; PRD.md:390-395). *Fix:* Add a short "Resolved PRD Questions" note or annotate the decision log/spines with OQ IDs.
- **medium** Static App Tile behavior conflicts with PRD FR-25 wording. EXPERIENCE.md says animation off disables both flip and live information updates, while PRD says tiles remain visually stable while still showing current information where available (EXPERIENCE.md:64, 84; PRD.md:304). *Fix:* Decide whether the toggle disables only animation or all live updates, then update the UX contract and PRD alignment.
- **low** "WindowsPhone Launcher" naming is consistent, but "Tile", "Live Tile", "Static App Tile", and tile-specific variants should be treated as a formal glossary across spines before implementation. *Fix:* Add a short glossary or normalize component taxonomy in both files.

## 8. Shape fit - strong

DESIGN.md uses the expected canonical section order, and EXPERIENCE.md includes the required defaults plus applicable Inspiration/Anti-patterns and Responsive/Platform sections. The documents are appropriately separated: visual decisions live mainly in DESIGN.md and behavior/state decisions mainly in EXPERIENCE.md.

### Findings

- **low** Open UX review notes remain inside Accessibility Floor, which is fine for draft status but should not survive final handoff without triage (EXPERIENCE.md:116-117). *Fix:* Resolve, remove, or move those notes into Open UX Items with owners/decisions before finalizing.

## Mechanical notes

- Source paths in both frontmatters appear to resolve from the UX workspace to the PRD and technical research.
- No mockups, wireframes, or imports are currently present to link.
- All explicit `{...}` token references in the prose appear to resolve to DESIGN.md frontmatter tokens.
- Main severity count: 0 critical, 4 high, 11 medium, 5 low.
