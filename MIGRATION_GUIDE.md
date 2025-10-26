# Migration Guide: Upgrading to Latest Gradle & Android Studio

This guide helps you migrate your local development environment to use the upgraded project configuration.

## Before You Start

### Backup Your Work
```bash
# Commit any local changes
git add .
git commit -m "Backup before upgrade"

# Create a backup branch (optional)
git branch backup-before-upgrade
```

## Step-by-Step Migration

### 1. Update Android Studio

**Required Version:** Android Studio Narwhal (2025.1.1) or later

#### Download and Install
1. Download Android Studio Narwhal from: https://developer.android.com/studio
2. Install the new version
3. Launch Android Studio and let it update any SDK components

### 2. Update Android SDK

1. Open Android Studio → Settings/Preferences → Appearance & Behavior → System Settings → Android SDK
2. Install the following SDK components:
   - **Android 15.0 (API 35)** - SDK Platform
   - **Android SDK Build-Tools 35.0.0** or later
   - **Android SDK Platform-Tools** (latest)
   - **Android SDK Tools** (latest)

### 3. Verify Java/JDK Version

Ensure you have JDK 17 installed:

```bash
# Check Java version
java -version
# Should show: openjdk version "17.x.x" or similar
```

If you need to install JDK 17:
- **Windows/Mac:** Download from https://adoptium.net/
- **Linux:** `sudo apt install openjdk-17-jdk` (Ubuntu/Debian)

### 4. Pull the Latest Changes

```bash
# Pull the upgraded code
git pull origin main

# Or if on a feature branch
git checkout copilot/upgrade-gradle-android-narwhal
git pull
```

### 5. Clean and Sync Project

#### In Android Studio:
1. **Clean Project:**
   - Menu: Build → Clean Project
   - Wait for completion

2. **Invalidate Caches (if needed):**
   - Menu: File → Invalidate Caches / Restart
   - Select "Invalidate and Restart"
   - Wait for Android Studio to restart

3. **Sync Gradle:**
   - Click the "Sync Now" banner if it appears
   - Or: File → Sync Project with Gradle Files
   - Wait for sync to complete (may take a few minutes)

#### Via Command Line:
```bash
# Navigate to project directory
cd /path/to/parentalCompanion

# Clean build directories
./gradlew clean

# Verify gradle wrapper
./gradlew --version
# Should show: Gradle 8.11

# Test build (optional)
./gradlew assembleDebug
```

### 6. Resolve Any Issues

#### Issue: "SDK location not found"
**Solution:** Create or update `local.properties`:
```properties
sdk.dir=/path/to/your/Android/sdk
```

#### Issue: "Failed to resolve: com.android.tools.build:gradle:8.5.2"
**Solution:** 
1. Check internet connection
2. Clear Gradle cache: `rm -rf ~/.gradle/caches/`
3. Sync again

#### Issue: "Minimum supported Gradle version is X.X"
**Solution:** The gradle wrapper is already updated to 8.11, but if you see this:
1. Delete `.gradle` directory in project root
2. Run: `./gradlew wrapper`
3. Sync again

#### Issue: Deprecated API warnings
**Solution:** These are informational. The code still works but may need updates in the future. You can:
1. Note them for future updates
2. Or update the specific APIs now (check Android Studio's suggestions)

### 7. Verify Everything Works

#### Build Both Apps:
```bash
# Build parent app
./gradlew :parent-app:assembleDebug

# Build child app
./gradlew :child-app:assembleDebug
```

#### Run Tests:
```bash
# Run all tests
./gradlew test

# Or run specific module tests
./gradlew :parent-app:test
./gradlew :child-app:test
```

#### Run Apps:
1. Connect a device or start an emulator
2. In Android Studio, select run configuration (parent-app or child-app)
3. Click Run (▶️)

## What Changed?

### Build Configuration
- Gradle: 8.0 → 8.11
- Android Gradle Plugin: 8.1.2 → 8.5.2
- Kotlin: 1.9.10 → 1.9.25
- Target SDK: 34 → 35 (Android 15)
- All libraries updated to latest stable versions

### New Features Available
- Configuration cache for faster builds (enabled by default)
- Android 15 API features
- Latest AndroidX library features
- Improved build performance

### Breaking Changes
**None expected** - All updates are backward compatible with minSdk 24.

## Performance Improvements

With the new configuration, you should notice:
- **Faster builds** thanks to configuration cache
- **Better IDE performance** with Android Studio Narwhal
- **Improved sync times** with Gradle 8.11

## Troubleshooting

### Clear All Caches
If you encounter persistent issues:

```bash
# Clean project
./gradlew clean

# Delete .gradle directory
rm -rf .gradle

# Delete build directories
find . -type d -name "build" -exec rm -rf {} +

# Clear Gradle cache (user level)
rm -rf ~/.gradle/caches/

# Clear Android Studio cache
rm -rf ~/.android/build-cache/

# Sync again
./gradlew --refresh-dependencies
```

### Reset to Previous Version
If you need to revert:

```bash
# Checkout previous commit
git log --oneline
git checkout <commit-hash-before-upgrade>

# Or use backup branch if created
git checkout backup-before-upgrade
```

## Need Help?

1. Check the [UPGRADE_SUMMARY.md](UPGRADE_SUMMARY.md) for detailed changes
2. Review Android Studio's error messages - they often have helpful suggestions
3. Check the [Android Gradle Plugin Release Notes](https://developer.android.com/studio/releases/gradle-plugin)
4. Open an issue on GitHub with error details

## Post-Migration Checklist

- [ ] Android Studio Narwhal installed
- [ ] Android SDK 35 installed
- [ ] JDK 17 verified
- [ ] Project syncs without errors
- [ ] Both apps build successfully
- [ ] Tests pass
- [ ] Apps run on emulator/device
- [ ] Configuration cache working (check build output)

## Next Steps

Once migration is complete:
1. Update any deprecated API usages Android Studio highlights
2. Consider enabling additional Gradle optimizations in `gradle.properties`
3. Review and update CI/CD pipelines if you have them
4. Update documentation for your team

---

**Estimated Migration Time:** 15-30 minutes (depending on download speeds and system performance)

**Difficulty Level:** Easy to Moderate

**Risk Level:** Low (all changes are backward compatible)
