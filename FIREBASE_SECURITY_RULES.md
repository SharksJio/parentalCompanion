# Firebase Realtime Database Security Rules

This document provides the recommended Firebase Realtime Database security rules for the Parental Companion app.

## Overview

The app requires proper security rules to prevent "Permission denied" errors while maintaining data security. The rules below provide a balance between development ease and production security.

## Development Rules (Testing Only)

For development and testing, you can use these permissive rules. **WARNING: Do not use these in production!**

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

## Production Rules (Recommended)

For production deployments, use authentication-based rules that restrict access based on user authentication:

```json
{
  "rules": {
    "devices": {
      "$deviceId": {
        ".read": "auth != null && (auth.uid == data.child('parentId').val() || auth.uid == data.child('childId').val())",
        ".write": "auth != null && (auth.uid == data.child('parentId').val() || auth.uid == $deviceId)",
        ".validate": "newData.hasChildren(['deviceName', 'childName', 'parentId', 'isOnline'])"
      }
    },
    "screenTime": {
      "$deviceId": {
        ".read": "auth != null && (auth.uid == root.child('devices/' + $deviceId + '/parentId').val() || auth.uid == $deviceId)",
        ".write": "auth != null && auth.uid == root.child('devices/' + $deviceId + '/parentId').val()"
      }
    },
    "appControl": {
      "$deviceId": {
        ".read": "auth != null && (auth.uid == root.child('devices/' + $deviceId + '/parentId').val() || auth.uid == $deviceId)",
        ".write": "auth != null && auth.uid == root.child('devices/' + $deviceId + '/parentId').val()"
      }
    },
    "contacts": {
      "$deviceId": {
        ".read": "auth != null && (auth.uid == root.child('devices/' + $deviceId + '/parentId').val() || auth.uid == $deviceId)",
        ".write": "auth != null && auth.uid == root.child('devices/' + $deviceId + '/parentId').val()"
      }
    },
    "locations": {
      "$deviceId": {
        "current": {
          ".read": "auth != null && (auth.uid == root.child('devices/' + $deviceId + '/parentId').val() || auth.uid == $deviceId)",
          ".write": "auth != null && auth.uid == $deviceId",
          ".validate": "newData.hasChildren(['latitude', 'longitude', 'timestamp'])"
        }
      }
    },
    "geofences": {
      "$deviceId": {
        ".read": "auth != null && (auth.uid == root.child('devices/' + $deviceId + '/parentId').val() || auth.uid == $deviceId)",
        ".write": "auth != null && auth.uid == root.child('devices/' + $deviceId + '/parentId').val()",
        "$geofenceId": {
          ".validate": "newData.hasChildren(['id', 'name', 'latitude', 'longitude', 'radiusMeters'])"
        }
      }
    }
  }
}
```

## Understanding the Production Rules

### Access Control

1. **Parent Access**: Parents can read and write data for their child devices
   - Determined by checking `parentId` in the device record
   - Parents can modify screen time, app controls, contacts, and geofences

2. **Child Device Access**: Child devices can:
   - Read their own device configuration
   - Write their current location
   - Read geofences set by parents
   - Cannot modify screen time limits, app controls, or contacts

3. **Authentication Required**: All operations require Firebase Authentication
   - Use `auth.uid` to identify the authenticated user
   - Anonymous access is denied

### Data Validation

Rules validate that required fields are present when writing data:
- Devices must have: `deviceName`, `childName`, `parentId`, `isOnline`
- Screen time must have: `dailyLimitMinutes`
- Locations must have: `latitude`, `longitude`, `timestamp`
- Geofences must have: `id`, `name`, `latitude`, `longitude`, `radiusMeters`

## Applying the Rules

1. Open the [Firebase Console](https://console.firebase.google.com)
2. Select your project
3. Navigate to **Realtime Database** in the left sidebar
4. Click the **Rules** tab
5. Copy and paste the appropriate rules (development or production)
6. Click **Publish**
7. Confirm the changes

## Testing the Rules

After applying the rules, test them:

1. **Test Read Access**:
   - Try to access data with and without authentication
   - Verify that only authorized users can read data

2. **Test Write Access**:
   - Try to write data as a parent user
   - Try to write data as a child device
   - Verify that permissions are enforced correctly

3. **Monitor for Permission Errors**:
   - Check Android Logcat for any "Permission denied" errors
   - If errors occur, verify:
     - User is authenticated
     - `parentId` is correctly set in device records
     - Rules match your data structure

## Troubleshooting Permission Errors

If you encounter "Permission denied" errors:

1. **Verify Authentication**:
   ```kotlin
   val currentUser = FirebaseAuth.getInstance().currentUser
   Log.d("Auth", "Current user: ${currentUser?.uid}")
   ```

2. **Check Device Record**:
   - Ensure the device record has a `parentId` field
   - Verify the `parentId` matches the authenticated user's UID

3. **Review Database Structure**:
   - Ensure paths match the rules (e.g., `/geofences/{deviceId}/{geofenceId}`)
   - Check that required fields are present

4. **Use Firebase Console**:
   - View data in the Firebase Console
   - Use the Rules Playground to test specific operations

5. **Check Application Logs**:
   - The app now logs detailed error messages for permission errors
   - Look for logs with tag "ParentRepository" or "LocationViewModel"

## Error Handling in the App

The app now handles Firebase permission errors gracefully:

- **ParentRepository**: Logs permission errors with context (device ID, operation)
- **LocationViewModel**: Catches errors and exposes them via `errorMessage` StateFlow
- **UI Layer**: Should observe `errorMessage` and display user-friendly error messages

Example UI error handling:
```kotlin
viewModel.errorMessage.collect { error ->
    error?.let {
        // Show error to user (e.g., Toast, Snackbar, Dialog)
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        viewModel.clearError()
    }
}
```

## Security Best Practices

1. **Never Use Development Rules in Production**
   - Development rules allow unrestricted access
   - Always implement authentication-based rules for production

2. **Validate Data Structure**
   - Use `.validate` rules to enforce data schema
   - Prevent malformed data from being written

3. **Minimize Permissions**
   - Only grant the minimum permissions needed
   - Separate read and write permissions

4. **Regular Security Audits**
   - Review rules periodically
   - Test with different user roles

5. **Monitor Access Patterns**
   - Use Firebase Analytics to track data access
   - Alert on unusual patterns

## Additional Resources

- [Firebase Security Rules Documentation](https://firebase.google.com/docs/database/security)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Testing Security Rules](https://firebase.google.com/docs/database/security/test-rules)
