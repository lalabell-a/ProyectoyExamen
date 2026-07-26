# Upgrade Android SDK version to 37

The project build is failing because `androidx.core:core-ktx:1.19.0` requires compiling against Android API level 37, while the project is currently configured for API level 36.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/gradle/libs.versions.toml)
- Update `android-compileSdk` from `36` to `37`.
- Update `android-targetSdk` from `36` to `37` to maintain consistency and ensure the app can target the latest APIs.

## Verification Plan

### Automated Tests
- Run `./gradlew :app-repartidor:checkDebugAarMetadata` to verify that the AAR metadata check passes.
- Perform a full build: `./gradlew assembleDebug`.

### Manual Verification
- Sync the project in Android Studio to ensure all dependencies are resolved correctly with the new SDK version.
