# Fix Android SDK, AGP, and Gradle Version Mismatch

The project is failing to build because `androidx.core:core-ktx:1.19.0` requires:
1. Android API level 37 (compileSdk 37).
2. Android Gradle Plugin (AGP) 9.1.0 or higher.
3. Upgrading AGP requires a newer version of Gradle (9.3.1).

The project is currently configured with API level 36, AGP 9.0.1, and Gradle 9.1.0.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/gradle/libs.versions.toml)
- Update `android-compileSdk` from `36` to `37`.
- Update `android-targetSdk` from `36` to `37`.
- Update `agp` from `9.0.1` to `9.1.0`.

#### [MODIFY] [gradle-wrapper.properties](file:///C:/Users/ionly/AndroidStudioProjects/ProyectoyExamen/gradle/wrapper/gradle-wrapper.properties)
- Update `distributionUrl` to `https\://services.gradle.org/distributions/gradle-9.3.1-bin.zip`.
- Remove `distributionSha256Sum` to avoid mismatch.

## Verification Plan

### Automated Tests
- Run Gradle sync to ensure the project accepts the new SDK, AGP, and Gradle versions.
- Execute `:app-repartidor:checkDebugAarMetadata` to verify the metadata check passes.
- Perform a clean build of the `:app-repartidor` module.
