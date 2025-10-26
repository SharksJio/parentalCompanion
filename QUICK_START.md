# Quick Start Guide

Get up and running with Parental Companion in 10 minutes!

## Prerequisites

✅ Android Studio Arctic Fox or later  
✅ JDK 17  
✅ Android SDK (API 24+)  
✅ Firebase account (free tier is fine)

## Step 1: Clone the Repository

```bash
git clone https://github.com/SharksJio/parentalCompanion.git
cd parentalCompanion
```

## Step 2: Firebase Setup (REQUIRED)

**📖 For comprehensive Firebase setup instructions, see [FIREBASE_SETUP.md](FIREBASE_SETUP.md)**

This is a quick overview. If you encounter any issues, refer to the detailed guide above.

### 2.1 Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click "Add Project"
3. Name it (e.g., "Parental Companion")
4. Disable Google Analytics (optional)
5. Click "Create Project"

### 2.2 Add Android Apps

**For Parent App:**
1. Click "Add App" → Android
2. Package name: `com.parentalcompanion.parent`
3. App nickname: "Parent App"
4. Download `google-services.json`
5. Save to `parent-app/google-services.json` (replace existing)

**For Child App:**
1. Click "Add App" → Android again
2. Package name: `com.parentalcompanion.child`
3. App nickname: "Child App"
4. Download `google-services.json`
5. Save to `child-app/google-services.json` (replace existing)

### 2.3 Enable Realtime Database

1. In Firebase Console, go to "Realtime Database"
2. Click "Create Database"
3. Choose location (closer to your users)
4. Start in "Test Mode" (for development)
5. Click "Enable"

### 2.4 Set Security Rules (Important!)

Go to Rules tab and paste:

```json
{
  "rules": {
    "devices": {
      "$deviceId": {
        ".read": true,
        ".write": true
      }
    },
    "screenTime": {
      "$deviceId": {
        ".read": true,
        ".write": true
      }
    },
    "appControl": {
      "$deviceId": {
        ".read": true,
        ".write": true
      }
    },
    "contacts": {
      "$deviceId": {
        ".read": true,
        ".write": true
      }
    },
    "locations": {
      "$deviceId": {
        ".read": true,
        ".write": true
      }
    },
    "geofences": {
      "$deviceId": {
        ".read": true,
        ".write": true
      }
    }
  }
}
```

**Note:** These are open rules for development only! Implement proper authentication and authorization for production.

## Step 3: Build the Apps

### 3.1 Open in Android Studio

1. Launch Android Studio
2. File → Open
3. Select the `parentalCompanion` folder
4. Wait for Gradle sync to complete

### 3.2 Build Parent App

```bash
./gradlew :parent-app:assembleDebug
```

Output: `parent-app/build/outputs/apk/debug/parent-app-debug.apk`

### 3.3 Build Child App

```bash
./gradlew :child-app:assembleDebug
```

Output: `child-app/build/outputs/apk/debug/child-app-debug.apk`

## Step 4: Install Apps

### Option A: Using Android Studio

1. Connect your Android device(s)
2. Select "parent-app" from run configuration
3. Click Run (green play button)
4. Repeat for "child-app"

### Option B: Using ADB

**Parent App (on parent's device):**
```bash
adb -s <parent_device_id> install parent-app/build/outputs/apk/debug/parent-app-debug.apk
```

**Child App (on child's device):**
```bash
adb -s <child_device_id> install child-app/build/outputs/apk/debug/child-app-debug.apk
```

## Step 5: Initial Setup

### On Child Device:

1. Open "Child Monitor" app
2. Tap "Grant Permissions"
3. Grant the following permissions:
   - ✅ Location (Fine)
   - ✅ Contacts (if prompted)
   - ✅ Notifications (Android 13+)
4. Enable "Usage Stats Permission"
   - This opens Settings
   - Find "Child Monitor"
   - Enable permission
5. Enable "Display Over Other Apps"
   - This opens Settings
   - Enable permission
6. Enable "Device Admin" (optional but recommended)
   - Settings → Security → Device Administrators
   - Enable "Child Monitor"

The app should now show "Monitoring Active"

### On Parent Device:

1. Open "Parental Companion" app
2. No special permissions needed initially
3. Location permission will be requested when using tracking features

## Step 6: Test Basic Functionality

### Test 1: Device Status

**Expected:** Parent app should show "Online" status

**On Parent App:**
- Navigate to Dashboard
- Check device status card

### Test 2: Device Lock

**On Parent App:**
1. Navigate to Device Lock
2. Tap "Lock Device"

**On Child Device:**
- Should show full-screen lock overlay
- Cannot exit with back button

**To Unlock:**
- Go back to parent app
- Tap "Unlock Device"

### Test 3: Location Sharing

**On Parent App:**
1. Grant location permission when prompted
2. Navigate to Location
3. Tap "Request Location"

**Expected:**
- Child device sends location
- Parent app shows location on map (or coordinates)

## Troubleshooting

### Problem: "Apps not communicating"

**Solutions:**
- ✅ Check Firebase Realtime Database is enabled
- ✅ Verify both apps are using correct `google-services.json`
- ✅ Check internet connection on both devices
- ✅ Ensure Firebase security rules are set
- ✅ Check Firebase Console for errors

### Problem: "Build failed"

**Solutions:**
- ✅ Run `./gradlew clean`
- ✅ File → Invalidate Caches / Restart in Android Studio
- ✅ Check JDK version is 17
- ✅ Update Android SDK tools

### Problem: "Child app not showing 'Monitoring Active'"

**Solutions:**
- ✅ Grant all required permissions
- ✅ Check if service is running (Settings → Apps → Child Monitor → Battery → Unrestricted)
- ✅ Restart the app
- ✅ Check Logcat for errors

### Problem: "Location not updating"

**Solutions:**
- ✅ Grant location permission on child device
- ✅ Enable GPS on child device
- ✅ Remove battery optimization for child app
- ✅ Check if Location Service is running

### Problem: "Lock screen not appearing"

**Solutions:**
- ✅ Grant "Display Over Other Apps" permission
- ✅ Ensure Monitoring Service is running
- ✅ Check Firebase connection
- ✅ Verify lock status in Firebase Console

## Development Tips

### View Firebase Data

1. Go to Firebase Console
2. Click "Realtime Database"
3. See real-time data updates
4. Manually edit values to test

### Debug Logs

**View logs:**
```bash
# Child app
adb logcat | grep ChildApplication

# Parent app
adb logcat | grep ParentApplication
```

### Test Device Pairing

Currently using hardcoded device ID `test_device_id`. To test multiple devices:

1. Edit `ParentRepository.kt` and `ChildRepository.kt`
2. Change device ID initialization
3. Use unique IDs for each child device

## Common Use Cases

### Use Case 1: Set Screen Time Limit

1. Parent app → Screen Time
2. Enter daily limit (e.g., 120 minutes)
3. Tap Save
4. Child device will enforce limit

### Use Case 2: Block an App

1. Parent app → App Control
2. Find app in list
3. Toggle block switch
4. Child device will block app access

### Use Case 3: Create Safe Zone

1. Parent app → Location
2. Tap "Add Geofence"
3. Enter name (e.g., "School")
4. Set location and radius
5. Enable entry/exit notifications

## Next Steps

✅ **Basic Setup Complete!**

Now you can:
- Explore all features
- Customize UI
- Add authentication
- Implement additional features
- Prepare for production

## Resources

- 📖 [Full Documentation](README.md)
- 🏗️ [Architecture Guide](ARCHITECTURE.md)
- 💻 [Implementation Details](IMPLEMENTATION.md)
- 🤝 [Contributing Guide](CONTRIBUTING.md)

## Getting Help

- 📧 Create an issue on GitHub
- 💬 Check existing issues for solutions
- 📚 Review documentation thoroughly

## Production Checklist

Before releasing to production:

- [ ] Implement Firebase Authentication
- [ ] Set proper Firebase Security Rules
- [ ] Add user registration/login
- [ ] Implement device pairing mechanism
- [ ] Complete all UI screens
- [ ] Add comprehensive error handling
- [ ] Implement crash reporting
- [ ] Add analytics
- [ ] Create privacy policy
- [ ] Add terms of service
- [ ] Test on multiple devices
- [ ] Test on different Android versions
- [ ] Optimize battery usage
- [ ] Add app icons
- [ ] Add splash screen
- [ ] Sign APK with release key
- [ ] Test in release mode
- [ ] Prepare app store listing

---

**Congratulations!** 🎉 You now have a working parental control system!

Happy coding! 👨‍💻👩‍💻
