# WindowsPhone Launcher

Native Android launcher project scaffolded with Kotlin and Jetpack Compose.

## Build

Use Android Studio or the committed Gradle wrapper with Android SDK support:

```powershell
.\gradlew.bat :app:assembleDebug
```

On macOS or Linux:

```bash
./gradlew :app:assembleDebug
```

## Recorded Versions

Dependency versions are centralized in `gradle/libs.versions.toml`.

Current scaffold records:

- Gradle wrapper distribution: `8.13`
- Android Gradle Plugin: `8.13.2`
- Kotlin and Compose Compiler Gradle plugin: `2.3.21`
- Compose BOM: `2026.05.00`
- AndroidX Core KTX: `1.17.0`
- AndroidX Lifecycle Runtime KTX: `2.10.0`
- AndroidX Activity Compose: `1.12.0`
