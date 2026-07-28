# Implementation Plan - Launcher Icon Cleanup

The user wants the project to use ONLY the icons from the `ic_launcher` set shown in the screenshot. This means removing the "round icon" variant and ensuring the manifest only points to `@mipmap/ic_launcher`.

## User Review Required

> [!IMPORTANT]
> I will be removing the `android:roundIcon` attribute from the `AndroidManifest.xml` and deleting all `ic_launcher_round` files from the project. This will ensure that only the main `ic_launcher` set is used.
>
> I will keep the `ic_launcher_foreground` and `ic_launcher_background` files for now, as they are required by the `ic_launcher.xml` adaptive icon definition. If you want the icon to be strictly non-adaptive (legacy only), please let me know.

## Proposed Changes

### Android Manifest

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/vents/AndroidStudioProjects/Calculator_Extreme/app/src/main/AndroidManifest.xml)
- Remove `android:roundIcon="@mipmap/ic_launcher_round"`.

### Resources

#### [DELETE] `ic_launcher_round` files
- Delete `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`
- Delete `app/src/main/res/mipmap-hdpi/ic_launcher_round.webp`
- Delete `app/src/main/res/mipmap-mdpi/ic_launcher_round.webp`
- Delete `app/src/main/res/mipmap-xhdpi/ic_launcher_round.webp`
- Delete `app/src/main/res/mipmap-xxhdpi/ic_launcher_round.webp`
- Delete `app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp`

## Verification Plan

### Manual Verification
- Deploy the app to a device and verify the launcher icon appears correctly.
- Check the "Android" project view in Android Studio to confirm only `ic_launcher (6)` appears under `mipmap`.
