# Firebase Setup Guide for Parental Companion

This comprehensive guide will walk you through setting up Firebase for the Parental Companion project. Firebase is essential for the app's real-time communication between parent and child devices.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Creating a Firebase Project](#creating-a-firebase-project)
3. [Adding Android Apps to Firebase](#adding-android-apps-to-firebase)
4. [Enabling Firebase Services](#enabling-firebase-services)
5. [Configuring Security Rules](#configuring-security-rules)
6. [Testing Your Setup](#testing-your-setup)
7. [Production Considerations](#production-considerations)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before you begin, ensure you have:

- ✅ A Google account (Gmail)
- ✅ Internet connection
- ✅ Android Studio installed
- ✅ The Parental Companion project cloned

**Estimated Time:** 15-20 minutes

---

## Creating a Firebase Project

### Step 1: Access Firebase Console

1. Open your web browser and navigate to the [Firebase Console](https://console.firebase.google.com)
2. Sign in with your Google account
3. You'll see the Firebase welcome screen

### Step 2: Create New Project

1. Click the **"Add project"** or **"Create a project"** button
2. Enter a project name:
   - Suggested: `Parental-Companion` or `ParentalControl`
   - Note: The project ID will be auto-generated (e.g., `parental-companion-abc12`)
3. Click **"Continue"**

### Step 3: Google Analytics (Optional)

1. You'll be asked about Google Analytics
   - **For Development:** You can disable this by toggling it OFF
   - **For Production:** Enable it to track app usage and crashes
2. If enabling Analytics:
   - Select your Analytics account or create a new one
   - Accept the terms and conditions
3. Click **"Create project"**

### Step 4: Wait for Project Creation

- Firebase will take 30-60 seconds to set up your project
- You'll see a progress indicator
- Click **"Continue"** when the setup completes

---

## Adding Android Apps to Firebase

You need to add **two separate Android apps** to your Firebase project - one for the parent app and one for the child app.

### Adding the Parent App

#### Step 1: Start Adding App

1. In the Firebase Console, you should see the project overview
2. Click the **Android icon** (robot) to add an Android app
3. Alternatively, click the gear icon ⚙️ next to "Project Overview" → "Project settings" → Scroll to "Your apps" → "Add app" → Select Android

#### Step 2: Register Parent App

Fill in the registration form:

1. **Android package name** (Required):
   ```
   com.parentalcompanion.parent
   ```
   ⚠️ This must match exactly! Check your `parent-app/build.gradle.kts` if unsure.

2. **App nickname** (Optional but recommended):
   ```
   Parent App
   ```

3. **Debug signing certificate SHA-1** (Optional for now):
   - Can be added later
   - Required for Google Sign-In and Dynamic Links
   - Skip for now unless you need these features

4. Click **"Register app"**

#### Step 3: Download Configuration File

1. Firebase will generate a `google-services.json` file
2. Click **"Download google-services.json"**
3. Save it to your computer (remember the location!)

#### Step 4: Add Configuration to Project

1. Locate the downloaded `google-services.json` file
2. Copy it to your project at:
   ```
   parent-app/google-services.json
   ```
3. **Important:** Replace the existing placeholder file
4. In Android Studio, you should see the file in the Project view under `parent-app/`

#### Step 5: Complete Setup

1. The Firebase Console will show Gradle dependencies
2. **Skip this step** - the project already has these dependencies configured!
3. Click **"Next"**
4. Click **"Continue to console"**

### Adding the Child App

Repeat the same process for the child app:

#### Step 1: Add Another Android App

1. From the Firebase Console project overview
2. Click **"Add app"** → Select **Android**

#### Step 2: Register Child App

1. **Android package name**:
   ```
   com.parentalcompanion.child
   ```

2. **App nickname**:
   ```
   Child App
   ```

3. Click **"Register app"**

#### Step 3: Download and Install

1. Download the `google-services.json` for the child app
2. Copy it to:
   ```
   child-app/google-services.json
   ```
3. Replace the existing placeholder file

#### Step 4: Complete Setup

1. Skip the Gradle dependencies step
2. Click **"Continue to console"**

### Verify Both Apps Are Added

In the Firebase Console:
1. Click the gear icon ⚙️ → "Project settings"
2. Scroll to "Your apps" section
3. You should see both apps listed:
   - `com.parentalcompanion.parent` (Parent App)
   - `com.parentalcompanion.child` (Child App)

---

## Enabling Firebase Services

The Parental Companion project uses two main Firebase services:

### 1. Firebase Realtime Database

This is the **most critical service** - it enables real-time communication between parent and child devices.

#### Enable Realtime Database

1. In the Firebase Console, locate the left sidebar
2. Click on **"Build"** to expand the menu
3. Click on **"Realtime Database"**
4. Click the **"Create Database"** button

#### Choose Database Location

1. Select a location closest to your users:
   - **United States**: `us-central1`
   - **Europe**: `europe-west1`
   - **Asia**: `asia-southeast1`
   
   💡 **Tip:** Choose the region where most of your users will be located for best performance.

2. Click **"Next"**

#### Set Security Rules

1. You'll see two options:
   - **Locked mode**: Denies all reads and writes (very secure but won't work for development)
   - **Test mode**: Allows all reads and writes (ONLY for development)

2. For initial development, select **"Start in test mode"**
3. Click **"Enable"**

⚠️ **IMPORTANT:** Test mode rules expire in 30 days. We'll set proper rules in the next section.

#### Verify Database is Ready

- You should see an empty database with a URL like:
  ```
  https://parental-companion-abc12-default-rtdb.firebaseio.com/
  ```
- The database structure is empty (null) - this is correct!

### 2. Firebase Cloud Messaging (FCM)

FCM enables push notifications between devices. Good news: **FCM is automatically enabled** when you add Android apps!

#### Verify FCM is Enabled

1. Go to **"Project settings"** (gear icon)
2. Navigate to the **"Cloud Messaging"** tab
3. You should see:
   - Server key
   - Sender ID
   - FCM registration tokens (will appear after devices connect)

No additional setup is required for FCM!

---

## Configuring Security Rules

Firebase Security Rules control who can read and write data. The default test mode rules are insecure and should be updated.

### Understanding the Data Structure

The app uses this database structure:

```
firebase-root/
├── devices/
│   └── {deviceId}/
├── screenTime/
│   └── {deviceId}/
├── appControl/
│   └── {deviceId}/
├── contacts/
│   └── {deviceId}/
├── locations/
│   └── {deviceId}/
└── geofences/
    └── {deviceId}/
```

### Setting Up Development Rules

For development and testing, use these rules:

1. In Firebase Console, go to **"Realtime Database"**
2. Click the **"Rules"** tab
3. Replace the existing rules with:

```json
{
  "rules": {
    "devices": {
      "$deviceId": {
        ".read": true,
        ".write": true,
        ".validate": "newData.hasChildren(['deviceName', 'childName', 'isOnline'])"
      }
    },
    "screenTime": {
      "$deviceId": {
        ".read": true,
        ".write": true,
        ".validate": "newData.hasChildren(['dailyLimitMinutes'])"
      }
    },
    "appControl": {
      "$deviceId": {
        "$packageName": {
          ".read": true,
          ".write": true
        }
      }
    },
    "contacts": {
      "$deviceId": {
        "$contactId": {
          ".read": true,
          ".write": true
        }
      }
    },
    "locations": {
      "$deviceId": {
        "current": {
          ".read": true,
          ".write": true,
          ".validate": "newData.hasChildren(['latitude', 'longitude', 'timestamp'])"
        }
      }
    },
    "geofences": {
      "$deviceId": {
        "$geofenceId": {
          ".read": true,
          ".write": true
        }
      }
    }
  }
}
```

4. Click **"Publish"**
5. Confirm the changes

### What These Rules Do

- ✅ Allow read and write access to all device data (for development)
- ✅ Validate that required fields are present
- ✅ Organize data by `deviceId` for multi-device support
- ⚠️ **WARNING:** These rules are permissive and suitable for development only

### Production Security Rules

For production, implement authentication-based rules:

```json
{
  "rules": {
    "devices": {
      "$deviceId": {
        ".read": "auth != null && (auth.uid == data.child('parentId').val() || auth.uid == data.child('childId').val())",
        ".write": "auth != null && auth.uid == data.child('parentId').val()",
        ".validate": "newData.hasChildren(['deviceName', 'childName', 'parentId', 'isOnline'])"
      }
    },
    "screenTime": {
      "$deviceId": {
        ".read": "auth != null",
        ".write": "auth != null && root.child('devices/' + $deviceId + '/parentId').val() == auth.uid"
      }
    },
    "appControl": {
      "$deviceId": {
        ".read": "auth != null",
        ".write": "auth != null && root.child('devices/' + $deviceId + '/parentId').val() == auth.uid"
      }
    },
    "contacts": {
      "$deviceId": {
        ".read": "auth != null",
        ".write": "auth != null && root.child('devices/' + $deviceId + '/parentId').val() == auth.uid"
      }
    },
    "locations": {
      "$deviceId": {
        ".read": "auth != null && root.child('devices/' + $deviceId + '/parentId').val() == auth.uid",
        ".write": "auth != null"
      }
    },
    "geofences": {
      "$deviceId": {
        ".read": "auth != null",
        ".write": "auth != null && root.child('devices/' + $deviceId + '/parentId').val() == auth.uid"
      }
    }
  }
}
```

**Key Features:**
- Requires authentication (`auth != null`)
- Parents can only access their own child devices
- Child devices can only write to their own data
- Validates parent-child relationships

⚠️ **Note:** Production rules require Firebase Authentication to be implemented first!

---

## Testing Your Setup

Let's verify everything is configured correctly.

### 1. Verify Configuration Files

Check that `google-services.json` files are in the correct locations:

```bash
# From your project root
ls -la parent-app/google-services.json
ls -la child-app/google-services.json
```

Both files should exist and be different (different package names).

### 2. Check `google-services.json` Content

Open each file and verify:

**Parent App** (`parent-app/google-services.json`):
```json
{
  "project_info": {
    "project_id": "your-project-id"
  },
  "client": [{
    "client_info": {
      "android_client_info": {
        "package_name": "com.parentalcompanion.parent"
      }
    }
  }]
}
```

**Child App** (`child-app/google-services.json`):
```json
{
  "project_info": {
    "project_id": "your-project-id"
  },
  "client": [{
    "client_info": {
      "android_client_info": {
        "package_name": "com.parentalcompanion.child"
      }
    }
  }]
}
```

### 3. Build the Apps

Build both apps to ensure Firebase is properly integrated:

```bash
# From project root
./gradlew clean
./gradlew :parent-app:assembleDebug
./gradlew :child-app:assembleDebug
```

✅ If both build successfully, Firebase is integrated correctly!

### 4. Test Database Connection

1. Install both apps on devices (or emulators)
2. Open the Child app first
   - Grant all required permissions
   - The app should update the device status in Firebase
3. Open Firebase Console → Realtime Database
4. You should see data appearing under `devices/test_device_id/`
5. Open the Parent app
6. Navigate to the Dashboard
7. You should see the child device status as "Online"

### 5. Test Real-Time Sync

1. With both apps running
2. In Firebase Console, manually change a value:
   - Navigate to `devices/test_device_id/isOnline`
   - Change it to `false`
3. The Parent app should immediately show "Offline"
4. Change it back to `true`
5. The Parent app should show "Online" again

✅ If you see real-time updates, your setup is complete!

---

## Production Considerations

Before deploying to production:

### 1. Enable Firebase Authentication

```kotlin
// Add to both apps' build.gradle.kts
dependencies {
    implementation("com.google.firebase:firebase-auth-ktx")
}
```

Implement sign-up and login flows in both apps.

### 2. Update Security Rules

Switch from development rules to production rules (see section above).

### 3. Set Up Firebase Crashlytics

```kotlin
// Add to both apps
dependencies {
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
}
```

### 4. Enable Database Backup

1. Go to Firebase Console → Realtime Database
2. Click the three dots (⋮) → "Export JSON"
3. Set up automated backups using Firebase CLI or Cloud Functions

### 5. Monitor Usage

1. Go to Firebase Console → Usage and billing
2. Set up budget alerts
3. Monitor:
   - Database reads/writes
   - Data stored
   - Bandwidth usage

### 6. Configure Rate Limiting

Add rate limiting to prevent abuse:

```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null && 
               (!data.child('lastWrite').exists() || 
                data.child('lastWrite').val() < now - 1000)"
  }
}
```

---

## Troubleshooting

### Issue: "Apps not communicating"

**Symptoms:**
- Parent app shows "Offline"
- Child app appears to be running
- No data in Firebase Console

**Solutions:**

1. **Check Internet Connection**
   ```bash
   # On Android device/emulator
   adb shell ping -c 3 google.com
   ```

2. **Verify google-services.json**
   - Ensure files are in correct locations
   - Verify package names match
   - Rebuild the apps after updating

3. **Check Firebase Console**
   - Ensure Realtime Database is enabled
   - Verify security rules allow writes
   - Check the "Usage" tab for any errors

4. **Enable Firebase Debug Logging**
   ```kotlin
   // Add to both Application classes
   FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)
   ```

5. **Check Logcat**
   ```bash
   adb logcat | grep Firebase
   ```

### Issue: "Build failed with Firebase plugin error"

**Error Message:**
```
Could not find google-services.json
```

**Solutions:**

1. Ensure `google-services.json` files exist in:
   - `parent-app/google-services.json`
   - `child-app/google-services.json`

2. Clean and rebuild:
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

3. Sync Gradle files in Android Studio:
   - File → Sync Project with Gradle Files

### Issue: "Permission denied" in Database

**Symptoms:**
- Error: "PERMISSION_DENIED: Permission denied"
- No data written to Firebase

**Solutions:**

1. **Check Security Rules**
   - Firebase Console → Realtime Database → Rules
   - Verify rules allow read/write access

2. **For Development, Use Open Rules:**
   ```json
   {
     "rules": {
       ".read": true,
       ".write": true
     }
   }
   ```
   ⚠️ Remember to restrict before production!

3. **If Using Auth, Verify Token:**
   ```kotlin
   val user = FirebaseAuth.getInstance().currentUser
   Log.d("Firebase", "User: ${user?.uid}")
   ```

### Issue: "Database connection slow"

**Symptoms:**
- Delays in data sync
- Parent app shows stale data

**Solutions:**

1. **Enable Disk Persistence**
   ```kotlin
   FirebaseDatabase.getInstance().setPersistenceEnabled(true)
   ```

2. **Optimize Queries**
   ```kotlin
   // Instead of loading entire node
   databaseRef.child("devices").child(deviceId).addValueEventListener(...)
   
   // Use limitToLast for recent data
   databaseRef.child("locations").limitToLast(1).addValueEventListener(...)
   ```

3. **Check Network Quality**
   - Use WiFi instead of mobile data for testing
   - Check Firebase Status: https://status.firebase.google.com

### Issue: "Too many requests" error

**Error:**
```
Error: QUOTA_EXCEEDED
```

**Solutions:**

1. **Check Firebase Console → Usage**
   - You may have hit free tier limits
   - Upgrade to Blaze (pay-as-you-go) plan

2. **Optimize Database Reads**
   - Use `addListenerForSingleValueEvent()` instead of `addValueEventListener()` when possible
   - Implement pagination
   - Cache data locally

3. **Review Security Rules**
   - Ensure they're not overly permissive
   - Add validation to prevent invalid writes

### Getting More Help

1. **Firebase Documentation**
   - [Realtime Database Guide](https://firebase.google.com/docs/database)
   - [Security Rules](https://firebase.google.com/docs/database/security)

2. **Firebase Support**
   - [Firebase Support](https://firebase.google.com/support)
   - [Stack Overflow - firebase](https://stackoverflow.com/questions/tagged/firebase)

3. **Project Issues**
   - [GitHub Issues](https://github.com/SharksJio/parentalCompanion/issues)

---

## Summary Checklist

Use this checklist to verify your Firebase setup:

- [ ] Created Firebase project
- [ ] Added Parent app with package `com.parentalcompanion.parent`
- [ ] Downloaded and placed `parent-app/google-services.json`
- [ ] Added Child app with package `com.parentalcompanion.child`
- [ ] Downloaded and placed `child-app/google-services.json`
- [ ] Enabled Firebase Realtime Database
- [ ] Set up Security Rules (development or production)
- [ ] Verified FCM is enabled
- [ ] Built both apps successfully
- [ ] Tested database connection
- [ ] Verified real-time synchronization works
- [ ] (Production only) Implemented Firebase Authentication
- [ ] (Production only) Updated to production security rules
- [ ] (Production only) Set up monitoring and alerts

---

## Next Steps

Now that Firebase is set up:

1. **Test All Features**
   - Device lock/unlock
   - Screen time limits
   - App blocking
   - Location tracking
   - Geofencing

2. **Implement Authentication**
   - See `IMPLEMENTATION.md` for authentication guide
   - Update security rules after implementing auth

3. **Customize for Your Needs**
   - Modify database structure if needed
   - Add additional features
   - Customize UI

4. **Prepare for Production**
   - Review all TODO comments in code
   - Implement proper error handling
   - Add analytics
   - Test thoroughly on multiple devices

---

**Congratulations!** 🎉 Your Firebase setup is complete, and you're ready to build an amazing parental control system!

For more information, see:
- [README.md](README.md) - Project overview
- [QUICK_START.md](QUICK_START.md) - Quick setup guide
- [IMPLEMENTATION.md](IMPLEMENTATION.md) - Implementation details
- [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture overview
