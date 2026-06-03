# Accessibility Review - WindowsPhone Launcher UX Draft

Reviewed files:

- `DESIGN.md`
- `EXPERIENCE.md`

Review lens: drag resize/reorder, small corner controls, touch targets, TalkBack, focus order, reduced motion, contrast, and permission/onboarding accessibility.

## Verdict

Not accessibility-ready. The draft recognizes the right risk areas, but several are left as notes or soft guidance instead of enforceable UX requirements. Edit Mode is the highest-risk area: drag-only reordering, corner resize handles, and small controls will exclude touch, motor, switch-access, and TalkBack users unless alternative actions are designed now.

## Findings

1. **Blocker - Drag reorder and resize are specified as pointer-first with no complete accessible alternative.** `EXPERIENCE.md:93-95` and `EXPERIENCE.md:156-161` require free drag and corner drag, while `EXPERIENCE.md:116` only says alternatives are needed "where platform accessibility requires alternatives." This is not sufficient for a launcher. Edit Mode needs explicit non-drag actions exposed through TalkBack custom actions and keyboard/switch-access paths: move up/down/left/right, move before/after, resize to `1x1`, `2x2`, `4x2`, confirm/cancel, and announce the resulting grid position.

2. **Blocker - Small corner controls are incompatible with touch target requirements as written.** `DESIGN.md:112`, `EXPERIENCE.md:73`, and `EXPERIENCE.md:157` call for small corner controls, while `EXPERIENCE.md:113` weakens the requirement with "where possible." The design should require at least 48dp minimum touch targets, independent of visual size. If the visible icon must stay small for Windows Phone authenticity, the hit area still needs to be expanded and non-overlapping.

3. **High - Resize handles on Tile corners are likely to conflict with launch, drag, scroll, and unpin gestures.** The draft does not define gesture priority, hit slop, cancellation, or what happens when a user starts near a corner but intended to scroll or reorder. This is especially risky on `1x1` tiles in a 4-column grid with 4dp gaps (`DESIGN.md:41`, `DESIGN.md:92-98`). The UX needs explicit gesture conflict rules and visible/focusable edit controls that do not depend on precision corners.

4. **High - TalkBack behavior is under-specified for Live Tiles and Edit Mode.** `EXPERIENCE.md:114` says labels must include Tile name, role, and action, but it does not define examples, state announcements, or action menus. Live Tiles with counts, stale weather, setup prompts, permission-denied static states, selected edit state, current size, and grid position all need deterministic announcements. Counts should be announced only when visible, but visible count changes should also avoid disruptive repeated announcements during normal launcher use.

5. **High - Reduced motion support is explicitly deferred from the Android system setting.** `EXPERIENCE.md:117` says MVP does not automatically follow Android reduced/remove animation settings. That is an accessibility failure for a motion-heavy launcher with Live Tile flips every 8-12 seconds (`EXPERIENCE.md:63`, `EXPERIENCE.md:99`) and edit-mode lift via scale/outline/motion (`DESIGN.md:102`). The launcher should honor system Animator duration scale/remove animations by default, with the in-app toggle as an additional control.

6. **High - Several declared white-on-tile color combinations fail WCAG contrast for normal text.** `DESIGN.md:80` says text and monochrome icons should use white on Tile surfaces, and `EXPERIENCE.md:118` says text must remain readable. However, the declared palette fails normal-text contrast on cyan (`#00BCF2`, 2.22:1), green (`#00A300`, 3.37:1), and orange (`#F7630C`, 3.11:1). Blue is borderline at 4.50:1 and should not be relied on with anti-aliasing or smaller text. Either those colors need dark foreground text, darker accessible variants, large-text-only constraints, or no text/counts on failing colors.

7. **High - Color is doing too much work without non-color affordances.** Tile colors, stale/error state, focus, disabled state, and permission-derived states are not required to have non-color indicators. `DESIGN.md:21` sets focus to white, which can disappear against white text/icons and may be ambiguous on high-contrast tiles. Error/stale states are described as "subtle" in `EXPERIENCE.md:83`, which is a red flag for low-vision users. Require shape, text, icon, outline, or semantic state changes in addition to color.

8. **Medium - Focus order is only defined for App List search, not for the launcher as a whole.** `EXPERIENCE.md:115` covers search field -> list results, but Start Screen, Edit Mode, onboarding, Weather Setup, Settings, and permission steps lack focus order requirements. Tile traversal should be row-major according to visual grid placement, not data insertion order, and reflow after resize/reorder must update focus predictably without dumping focus to the top.

9. **Medium - App List pinning is ambiguous for accessibility.** `EXPERIENCE.md:71` says "Long press or row action can pin app." Long press alone is not enough. The row action needs to be specified as an accessible trailing action or TalkBack custom action with label such as "Pin to Start," and the result should announce whether the app was pinned and where the new Tile was placed.

10. **Medium - Onboarding permission accessibility lacks structural requirements.** `EXPERIENCE.md:32`, `EXPERIENCE.md:48`, and `EXPERIENCE.md:139` establish optional permissions and non-coercive copy, but they do not require accessible grouping, clear headings, consequences of skipping, focus restoration after Android system permission screens, or a no-permission completion path. The flow should work with TalkBack from first launch through returning from system settings, including clear "Skip for now" semantics and no trapped focus.

11. **Medium - Weather setup relies on a short Tile prompt that may be ambiguous to assistive tech users.** `DESIGN.md:113`, `EXPERIENCE.md:65`, and `EXPERIENCE.md:166` use `Set location` as the Tile state. The visible copy is acceptable, but the accessibility label should be more explicit, for example "Weather, location not set, double tap to set location." Manual city entry also needs labeled fields, error text associated to the field, and accessible loading/error announcements.

12. **Medium - Tight spacing and minimal safe-edge breathing room may harm reachability and accidental activation.** The Start Screen has 4dp tile gaps and 8dp screen edges (`DESIGN.md:41-42`, `DESIGN.md:98`). This can be visually authentic, but the draft needs explicit guarantees that each interactive Tile remains at least 48dp in both dimensions, that edge controls avoid gesture-navigation exclusion zones, and that edit handles near screen edges remain reachable.

13. **Medium - Text scaling and truncation are not specified.** The draft says labels are readable (`DESIGN.md:86-88`), but it does not define behavior at large Android font sizes, display size changes, or long app names. Tile labels, App List rows, alphabet headers, Weather values, and permission copy need rules for wrapping, truncation, minimum line height, and whether Live Tile content can be simplified under large text.

14. **Low - Landscape and tablet deferral is acceptable for MVP, but accessibility must not be portrait-only in assumptions.** `EXPERIENCE.md:14` and `EXPERIENCE.md:128-130` defer landscape/tablet. That is fine for product scope, but accessibility requirements should still account for Android display size, one-handed reach, external keyboard/switch access, and system navigation modes on supported portrait phones.

15. **Low - The draft lacks measurable accessibility acceptance criteria.** The Accessibility Floor (`EXPERIENCE.md:109-118`) is directionally useful but not testable enough. Add explicit checks for touch target size, contrast ratios, TalkBack labels/actions, focus order, system reduced motion behavior, large text, onboarding permission path, and edit-mode non-drag completion.

## Required Changes Before Implementation

- Define an accessible Edit Mode command model: reorder, resize, unpin, confirm, and cancel must all work without drag gestures.
- Require 48dp minimum touch targets for Tiles and edit controls, even when visible corner icons are smaller.
- Replace "where possible" accessibility language with hard MVP requirements or documented exceptions.
- Honor Android system reduced/remove animation settings for Live Tile flips and edit-mode motion.
- Resolve the tile color contrast failures with foreground switching, accessible color variants, or content restrictions per color.
- Specify TalkBack labels and custom actions for Tile states, Live Tile counts, Weather setup/error/stale states, App List pinning, and Edit Mode.
- Define focus order for Start Screen, App List, Edit Mode, onboarding, Weather Setup, and Settings.
- Add permission/onboarding accessibility requirements for headings, grouping, skip path, focus restoration after system flows, and clear consequences.

## Suggested Acceptance Criteria

- A TalkBack user can pin an app, move it in the grid, resize it through all supported sizes, unpin it, and exit Edit Mode without using drag.
- Every interactive target has a minimum 48dp hit area and visible focus indication.
- Start Screen focus traversal follows visual grid order before and after reflow.
- Live Tile animation stops or becomes static when Android reduced/remove animation settings are enabled.
- All text/count foreground and Tile background combinations meet at least 4.5:1 for normal text, or the UI uses a compliant alternate foreground/background.
- Onboarding can be completed with all Live Tile permissions skipped, and TalkBack announces the purpose and consequence of each permission option.
