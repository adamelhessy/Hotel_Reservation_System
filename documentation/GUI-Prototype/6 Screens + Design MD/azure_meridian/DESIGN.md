# Design System Specification: The Digital Concierge

## 1. Overview & Creative North Star
The creative North Star for this design system is **"The Digital Concierge."** 

Luxury hospitality is defined not by clutter, but by quiet confidence, intentionality, and seamless service. This design system moves away from the "industrial" feel of standard management software, opting instead for a **High-End Editorial** experience. We achieve this through a "Quiet Luxury" aesthetic: using expansive white space, sophisticated tonal layering, and a high-contrast typographic hierarchy that feels like a premium travel magazine. 

By breaking the traditional rigid grid with intentional asymmetry—such as overlapping card elements and varied content densities—we create a system that feels bespoke rather than templated.

---

## 2. Colors & Tonal Architecture
The palette is rooted in deep, authoritative Navies and organic Teals, balanced by a warm, paper-like background.

### The Color Tokens
*   **Primary (Brand & Depth):** `primary` (#00291c) and `secondary` (#436084). Use these for the foundational sidebar and high-level branding to anchor the interface.
*   **Action (Teal):** `on-primary-container` (#3fb68b). This is our signature "Action Teal." It must be used sparingly for primary CTAs and confirmation states to maintain its visual impact.
*   **Status Tints:** 
    *   **Amber:** `tertiary_container` (#543000) for pending luxury.
    *   **Red:** `error` (#ba1a1a) for cancellations.
*   **Neutral Palette:** Use `surface` (#fcf9f1) for the main stage and `surface-container` variants for layered depth.

### The "No-Line" Rule
To achieve a premium finish, **1px solid borders are prohibited for sectioning.** We do not define boundaries with lines; we define them with light. Use background color shifts (e.g., a `surface-container-low` card sitting on a `background` surface) to denote separate areas.

### Signature Textures & Glass
*   **Glassmorphism:** For floating modals or "quick view" reservation panels, use a semi-transparent `surface-container-lowest` with a 20px backdrop-blur. 
*   **Gradient Soul:** Primary CTAs should utilize a subtle linear gradient from `primary` to `primary_container` (Top-to-Bottom) to add a 3D "jewel" quality that flat colors lack.

---

## 3. Typography
We utilize **Inter** to bridge the gap between technical precision and editorial elegance.

*   **Editorial Headlines:** `display-sm` (2.25rem) and `headline-md` (1.75rem). Use these for page titles to create an authoritative "hero" moment at the top of every view.
*   **The Power of Labels:** `label-md` (0.75rem) in `on-surface-variant`. These should be all-caps with a 0.05em letter spacing to evoke the feel of a luxury watch face.
*   **Body Narrative:** `body-md` (0.875rem) at `regular` weight for all reservation details, ensuring high legibility against the light gray background.

---

## 4. Elevation & Depth
Depth in this system is achieved through **Tonal Layering** rather than structural shadows.

### The Layering Principle
Think of the UI as stacked sheets of fine stationery.
1.  **Base:** `surface` (#fcf9f1) - The "desk" everything sits on.
2.  **Sectioning:** `surface-container-low` (#f6f4eb) - Large background areas for grouped content.
3.  **Actionable Cards:** `surface-container-lowest` (#ffffff) - These "pop" forward naturally because they are the brightest element.

### Ambient Shadows
If a card must float (e.g., a guest profile hover), use an **Ambient Shadow**:
*   **Color:** `on-surface` at 6% opacity.
*   **Blur:** 24px.
*   **Spread:** -4px (to keep the shadow tight and sophisticated).

### The "Ghost Border" Fallback
If a border is required for accessibility on interactive inputs, use a **Ghost Border**: `outline-variant` (#c3c6cf) at 30% opacity. Never use 100% opaque borders.

---

## 5. Components

### Sidebar (The Anchor)
*   **Width:** 220px. 
*   **Visual:** Solid `primary` (#00291c). 
*   **Active State:** No bulky backgrounds. An active item is indicated by a 4px wide `on-primary-container` (Teal) left-aligned border and a slight shift to `surface-bright` text color.

### Buttons (The Statement)
*   **Primary:** Solid Teal (`on-primary-container`) with white text. 12px (`DEFAULT`) radius. 
*   **Secondary:** Ghost style. Transparent background, Navy (`secondary`) text, and a 1px Ghost Border.
*   **Tertiary/Danger:** `error` text on a transparent background—no box.

### Status Pills (The Indicators)
*   **Style:** Pill-shaped (`full` roundedness).
*   **Implementation:** Use the `container` color for the background and the `on-container` color for text (e.g., Amber Pending: `tertiary_container` background with `on_tertiary_fixed_variant` text).

### Cards & Lists
*   **The "Invisible List":** Forbid the use of divider lines in lists. Instead, use a 16px vertical gap between items. 
*   **Reservation Cards:** Use `surface-container-lowest` with a `DEFAULT` (12px) radius. Content should be padded by 24px to give guest data "room to breathe."

### Input Fields
*   **Design:** Flat style. Background is `surface-container-high` with a bottom-only Ghost Border. On focus, the bottom border animates to Teal.

---

## 6. Do's and Don'ts

### Do:
*   **Do** use asymmetrical layouts (e.g., a wide 2/3 column for the room map and a narrow 1/3 column for the booking summary).
*   **Do** embrace white space. If a screen feels "full," it is no longer luxury.
*   **Do** use `title-lg` for card values (e.g., "$450.00") to make them feel impactful.

### Don't:
*   **Don't** use 1px solid black or high-contrast grey borders.
*   **Don't** use standard "Drop Shadows" (0, 0, 5, #000). They look dated and cheap.
*   **Don't** crowd icons. Every icon should have a minimum of 8px "safe zone" padding.
*   **Don't** use pure black (#000000) for text. Always use `on-surface` (#1c1c17) to maintain a soft, premium feel.