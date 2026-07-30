# Walkthrough - Package Renaming to com.calculator_extreme

I have renamed the app's package and application ID to `com.calculator_extreme` to meet the Google Play Store's requirements.

## Changes Made

### Build Configuration

#### [app/build.gradle.kts](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/build.gradle.kts)
- **Updated Namespace**: Changed `namespace` to `com.calculator_extreme`.
- **Updated Application ID**: Changed `applicationId` to `com.calculator_extreme`.

### Source Code & Directory Structure

- **Moved Source Files**: Relocated `MainActivity.kt` and the `ui/theme` files from `com/example/myapplication` to `com/calculator_extreme`.
- **Updated Package Declarations**: Updated the `package` statement in all Kotlin files to reflect the new structure.
- **Updated Tests**: Moved and updated `ExampleInstrumentedTest.kt` and `ExampleUnitTest.kt`.

### Resources & Manifest

#### [AndroidManifest.xml](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/AndroidManifest.xml)
- Updated theme references to match the renamed styles.

#### [themes.xml](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/res/values/themes.xml)
- Renamed `Theme.MyApplication` to `Theme.CalculatorExtreme`.

## Verification Results

### Automated Tests
- Ran `:app:assembleDebug` - **Passed**. The app now compiles correctly with the new identity.

### Manual Action Required
> [!CAUTION]
> You MUST **re-generate your Signed Bundle (.aab)** using the updated project before uploading to the Play Store. The old .aab file still contains the old package name.
