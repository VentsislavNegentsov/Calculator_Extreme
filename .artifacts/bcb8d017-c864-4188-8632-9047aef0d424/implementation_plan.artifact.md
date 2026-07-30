# Implementation Plan - Multi-Theme Retro Mode

I will expand the "Retro" button to cycle through 7 different themes (Normal + 6 fluorescent retro styles), allowing you to choose the best look for your calculator.

## User Review Required

> [!IMPORTANT]
> - The **Retro** button will now cycle through 7 states: **Normal**, **Green**, **Amber**, **Milky Cyan**, **Plasma**, **Electric Blue**, and **Neon Pink**.
> - Tapping the "Retro" button will advance to the next theme.
> - All retro themes will use a deep black background with their respective glowing fluorescent color palettes.

## Proposed Changes

### Theme Management

#### [MODIFY] [MainActivity.kt](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/java/com/example/myapplication/MainActivity.kt)
- Define a `RetroTheme` data class to store primary, special, and operator colors.
- Create a list of 6 retro themes plus the "Normal" state.
- Change `isRetroMode: Boolean` to `themeIndex: Int` throughout the app.

### UI Integration

#### [MODIFY] Header & Mode Bar
- Update the "RETRO" button to display its current state (e.g., "RETRO 1", "RETRO 2" or just highlight based on `themeIndex > 0`).
- Implement the cycling logic: `onToggleRetro = { themeIndex = (themeIndex + 1) % 7 }`.

#### [MODIFY] Components
- **Surface & Card**: Background and border colors will now be derived from the selected theme in the list.
- **CalculatorButton**: Update the conditional styling to use the colors from the active `RetroTheme` index.
- **BaseInfo**: Update text colors to match the active theme.

## Verification Plan

### Automated Tests
- Run `:app:assembleDebug` to ensure no syntax errors.

### Manual Verification
- Tap the **RETRO** button 7 times and verify:
    1. It cycles back to the **Normal** theme.
    2. Each of the 6 retro themes (Green, Amber, Cyan, etc.) has distinct, glowing fluorescent colors.
    3. Calculations and base switching still work correctly in every theme.
