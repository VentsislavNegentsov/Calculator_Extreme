# Implementation Plan - Rename Package to com.calculator_extreme

The Google Play Store requires the package name (Application ID) of the uploaded bundle to match the one you registered in the Play Console. Currently, your project is using the default `com.example.myapplication`, but it must be `com.calculator_extreme`.

## User Review Required

> [!IMPORTANT]
> This change will modify the identity of your app. Once published with `com.calculator_extreme`, you cannot change it without creating a new app listing in the Play Store.

## Proposed Changes

### Build Configuration

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/build.gradle.kts)
- Change `namespace` to `"com.calculator_extreme"`.
- Change `applicationId` to `"com.calculator_extreme"`.

### Source Code

#### [MODIFY] [MainActivity.kt](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/java/com/example/myapplication/MainActivity.kt)
- Update the package declaration from `package com.example.myapplication` to `package com.calculator_extreme`.

#### [MOVE] Directory Restructuring
- I will move the source file from the `com/example/myapplication` directory to `com/calculator_extreme` to match the new package name.

### Android Manifest

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/AndroidManifest.xml)
- Ensure the activity reference remains correct (it should stay `.MainActivity` as it's relative to the namespace).

## Verification Plan

### Automated Tests
- Run `:app:assembleDebug` to ensure the app builds successfully with the new package name.
- Verify the generated APK/AAB has the correct `applicationId`.

### Manual Verification
- After I finish, you will need to **re-generate your Signed Bundle (.aab)** and upload it to the Play Store.
