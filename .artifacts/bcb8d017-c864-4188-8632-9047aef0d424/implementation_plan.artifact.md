# Implementation Plan - Handle Bottom Navigation Bar Insets

The user wants to adjust the layout to avoid the 3-button navigation bar on their Redmi Note 14s, moving the calculator content up and leaving a "plank line" (blank space or solid bar) at the bottom.

## User Review Required

> [!IMPORTANT]
> I will be enabling **Edge-to-Edge** mode. This allows the app to draw behind the system bars, and we will then use padding to ensure the UI stays above the navigation buttons.
>
> I will assume "plank line" refers to the space occupied by the navigation bar. If you want a specific color or a visible divider line there, please let me know.

## Proposed Changes

### App Component

#### [MODIFY] [MainActivity.kt](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/java/com/example/myapplication/MainActivity.kt)
- Import `androidx.activity.enableEdgeToEdge`.
- Call `enableEdgeToEdge()` in `onCreate`.
- Update the root `Column` in `AdvancedCalculatorScreen` to use `Modifier.safeDrawingPadding()`. This will automatically push the content above the status bar and the bottom navigation buttons.
- If a "plank line" (visible bar) is desired, the `Surface` background will fill that area by default.

## Verification Plan

### Manual Verification
- Deploy the app to a device with 3-button navigation enabled.
- Verify that the bottom-most buttons of the calculator are no longer obscured by the system navigation buttons.
- Confirm that the top of the app (header) is not overlapping with the status bar (clock/icons).
