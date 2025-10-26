# Quick Reference: Version Updates

## Build Tools

| Component | Old Version | New Version |
|-----------|-------------|-------------|
| Gradle | 8.0 | **8.11** |
| Android Gradle Plugin | 8.1.2 | **8.5.2** |
| Kotlin | 1.9.10 | **1.9.25** |
| Google Services Plugin | 4.4.0 | **4.4.2** |

## Android SDK

| Setting | Old Value | New Value |
|---------|-----------|-----------|
| compileSdk | 34 | **35** |
| targetSdk | 34 | **35** |
| minSdk | 24 | 24 (unchanged) |

## Core AndroidX Libraries

| Library | Old Version | New Version |
|---------|-------------|-------------|
| core-ktx | 1.12.0 | **1.15.0** |
| appcompat | 1.6.1 | **1.7.0** |
| material | 1.10.0 | **1.12.0** |
| constraintlayout | 2.1.4 | **2.2.0** |

## Lifecycle Components

| Library | Old Version | New Version |
|---------|-------------|-------------|
| lifecycle-* | 2.6.2 | **2.8.7** |

## Navigation

| Library | Old Version | New Version |
|---------|-------------|-------------|
| navigation-* | 2.7.5 | **2.8.5** |

## Database

| Library | Old Version | New Version |
|---------|-------------|-------------|
| room-* | 2.6.0 | **2.6.1** |

## Firebase

| Library | Old Version | New Version |
|---------|-------------|-------------|
| firebase-bom | 32.5.0 | **33.7.0** |

## Coroutines

| Library | Old Version | New Version |
|---------|-------------|-------------|
| kotlinx-coroutines-android | 1.7.3 | **1.9.0** |
| kotlinx-coroutines-play-services | 1.7.3 | **1.9.0** |

## Google Play Services

| Library | Old Version | New Version |
|---------|-------------|-------------|
| play-services-location | 21.0.1 | **21.3.0** |
| play-services-maps | 18.2.0 | **19.0.0** |

## Work Manager

| Library | Old Version | New Version |
|---------|-------------|-------------|
| work-runtime-ktx | 2.8.1 | **2.10.0** |

## Testing Libraries

| Library | Old Version | New Version |
|---------|-------------|-------------|
| junit (test) | 4.13.2 | 4.13.2 (unchanged) |
| junit (androidTest) | 1.1.5 | **1.2.1** |
| espresso-core | 3.5.1 | **3.6.1** |

## New Features Enabled

- ✅ Gradle Configuration Cache
- ✅ Distribution URL Validation
- ✅ Android 15 (API 35) Support

## Compatibility

**Requires:**
- Android Studio Narwhal (2025.1.1) or later
- JDK 17
- Android SDK 35

**Supports:**
- Minimum Android 7.0 (API 24)
- Target Android 15 (API 35)

---

For detailed information, see:
- [UPGRADE_SUMMARY.md](UPGRADE_SUMMARY.md) - Complete changelog
- [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md) - Step-by-step migration instructions
