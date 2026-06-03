# Product Coherence Review: WindowsPhone Launcher UX Draft

Date: 2026-06-02  
Reviewer lens: Product Coherence  
Reviewed files:

- `DESIGN.md`
- `EXPERIENCE.md`
- Context PRD: `../../prds/prd-WindowsPhone Launcher-2026-06-02/prd.md`

## Verdict

The UX draft is broadly coherent with the PRD and preserves the core Windows Phone 8.1 promise: sharp dense Tiles, black Start Screen posture, horizontal Start Screen/App List navigation, useful Live Tiles, and privacy-first count-only behavior. It also shows good MVP restraint by explicitly banning `4x4` Tiles, bottom-sheet edit actions, alphabet jump lists, preview content, decorative-only Live Tiles, and header chrome above the Start Screen.

However, there is one product-significant contradiction: the draft makes the Live Tile animation toggle disable live information, while the PRD says disabling animation should keep supported current information visible. There is also a privacy/posture risk in asking for Location during general onboarding instead of deferring auto-location to Weather setup after manual entry has been presented as the default.

Overall status: **Coherent with targeted revisions required before implementation handoff.**

## Findings

### 1. Animation-off behavior currently weakens the Live Tile usefulness promise

Severity: High  
Files: `EXPERIENCE.md:64`, `EXPERIENCE.md:84`, `DESIGN.md:111`  
PRD references: `prd.md:304-310`, `prd.md:367-369`, `prd.md:378-381`

The PRD separates animation from useful current information. FR-25 says that when animation is disabled, supported Live Tiles should remain visually stable while still showing current information where available. The MVP direction also says visual nostalgia alone is insufficient; useful live information is part of the product promise.

The UX draft currently states:

- `Static App Tile` is used when animation is off and shows app icon only, no live info.
- `Live Tile animation off` means flip and live information updates are disabled.
- `DESIGN.md` repeats that static app tiles have no live information when animation is disabled.

This turns a motion preference into a functional disablement of counts, weather, and clock value. That is incoherent with the PRD and could make the launcher feel less useful for users who want a calm or lower-motion home screen.

Recommendation: split "animation off" from "data unavailable." When animation is off, Tiles should stop flipping but still render the latest supported stable face: digital clock, weather value, SMS count, Phone missed-call count, and Calendar count when data/permission exists. Only data/permission-unavailable states should degrade to plain static app Tiles.

### 2. General onboarding asks for Location too early for the privacy-first Weather posture

Severity: Medium  
Files: `EXPERIENCE.md:32`, `EXPERIENCE.md:139`, `EXPERIENCE.md:164-172`  
PRD references: `prd.md:73-80`, `prd.md:295-302`, `prd.md:390-394`

The PRD is explicit that Weather supports manual location by default and optional auto-location as opt-in behavior. The UX draft respects this in the Weather configuration flow, but the onboarding permission step offers Location alongside Notification and Calendar before the user has reached Weather setup.

That risks making the product feel permission-forward instead of privacy-first, especially for a free public launcher. It also blurs the MVP promise that Weather can become useful without granting location permission.

Recommendation: remove Location from the general Live Tile permissions onboarding step, or phrase it as a deferred Weather option rather than a first-run permission ask. The cleaner MVP flow is: onboarding may offer notification/calendar access for counts, Weather Tile shows `Set location`, Weather Setup defaults to manual city/location entry, and auto-location is offered there as an explicit optional convenience.

### 3. Weather usefulness depends on unresolved provider details, but the UX flow presents fetching as settled

Severity: Medium  
Files: `EXPERIENCE.md:169`, `EXPERIENCE.md:176`  
PRD references: `prd.md:76`, `prd.md:388-394`

The draft says the Weather Tile fetches weather from the configured free provider, while the PRD keeps provider choice, quota, attribution, privacy, commercial-use constraints, and exact location input format open. The UX does list provider fields as an open UX item, which is good, but Flow 4 reads like the provider contract is already known.

This matters for product coherence because the app is positioned as public and free. A provider that requires visible attribution, rate-limit messaging, account setup, paid fallback, or location-format constraints can affect Weather setup screens, error states, and the free-app posture.

Recommendation: keep Flow 4 provider-neutral until OQ-8/OQ-9 are resolved. Add an implementation note that Weather setup must not introduce account creation, paid prompts, ad-like attribution, or privacy-hostile defaults in MVP. If attribution is required, it should be handled quietly in Weather Setup or Settings without turning the Start Screen into provider chrome.

### 4. Settings access is product-coherent, but it resolves an open PRD question and should be promoted to a decision

Severity: Low  
Files: `DESIGN.md:116`, `EXPERIENCE.md:37`, `EXPERIENCE.md:44`, `EXPERIENCE.md:70`, `EXPERIENCE.md:140`  
PRD references: `prd.md:264-266`, `prd.md:388-395`

The UX chooses a Settings Tile that can be pinned to the Start Screen and avoids a global header settings button. This is coherent with the Windows Phone Start Screen promise and avoids adding Android-style launcher chrome above the grid.

The only coherence issue is process-level: PRD OQ-10 still asks where users should access Launcher Settings from, while the UX draft has effectively answered it.

Recommendation: mark Settings Tile as the proposed answer to OQ-10, or feed the decision back into the PRD. Keep the no-header rule; it strongly protects the WP8.1 feel and MVP scope.

## Coherence Strengths

- Strong Windows Phone 8.1 visual posture: black canvas, square Tiles, tight spacing, vivid colors, no Material cards, no gradients, no rounded widget surfaces.
- Clear MVP tile-size discipline: `1x1`, `2x2`, and `4x2` only, with `4x4` explicitly banned.
- Live Tile scope is mostly well controlled: flip/face-switching cadence only, visible/supported Tiles animate, no advanced animation menu.
- Privacy-first count-only behavior is explicit: SMS, Phone, and Calendar previews are banned; skipped permissions degrade calmly without nag badges.
- Public-free posture is mostly protected: no ads, upsell, subscription mechanics, or broad launcher-control-center drift appear in the UX draft.
- Daily launcher usability is considered: default launcher onboarding, Android Back from App List, permission-denied usability, App List search, and performance prototype gates are all aligned with the PRD success metrics.

## Scope Drift Check

No major scope drift found. The UX introduces onboarding surfaces, but they are reasonable for a public Android launcher that must become the default home app and explain optional permissions. The fixed App List search field is already required by PRD FR-19 and does not become a Start Screen header. Accessibility notes are not scope drift; they are necessary delivery constraints.

Potential drift to watch:

- Do not let Weather provider constraints expand into account setup, paid provider configuration, broad settings, or Start Screen attribution chrome.
- Do not let edit accessibility alternatives become an advanced layout-management suite; keep them equivalent paths for resize/reorder/unpin.
- Do not expand Settings beyond color mode, accent color, background, weather location, and Live Tile animation toggle.

## Recommended Revision Checklist

- Change animation-off behavior so live information remains visible and stable when data/permission exists.
- Distinguish static fallback states caused by unavailable data/permission from non-animated live states caused by the animation toggle.
- Defer Location permission to Weather Setup, with manual city/location as the default path.
- Keep Weather flow provider-neutral until free-provider constraints and location input format are decided.
- Feed the Settings Tile decision back into the PRD as the proposed resolution for OQ-10.

