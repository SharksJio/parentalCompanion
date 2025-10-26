# Parental Companion

An Android parental control solution with two applications - one for parents and another for children's devices.

## Overview

Parental Companion is a comprehensive parental control system that allows parents to monitor and manage their children's device usage remotely. The system consists of two Android applications built with MVVM architecture in Kotlin.

## Features

### Parent App Features
- **Dashboard**: View real-time status of child device
- **Screen Time Control**: Set daily screen time limits
- **App Usage Management**: Block or allow specific apps
- **Contact Management**: Control which contacts the child can communicate with
- **Location Tracking**: Real-time location tracking of child device
- **Geofencing**: Create safe zones and get notifications when child enters/exits
- **Device Locking**: Remotely lock/unlock child device

### Child App Features
- **Monitoring Service**: Runs as a foreground service to enforce parental controls
- **Screen Time Enforcement**: Automatically locks device when time limit is exceeded
- **App Blocking**: Prevents blocked apps from running
- **Contact Filtering**: Restricts communication to allowed contacts only
- **Location Sharing**: Shares device location with parent
- **Geofencing**: Monitors geofence boundaries and sends alerts
- **Device Lock**: Enforces device lock when requested by parent

## Architecture

Both applications follow the MVVM (Model-View-ViewModel) architecture pattern:

```
app/
├── data/
│   ├── model/          # Data models
│   ├── repository/     # Data repositories
│   ├── local/          # Local data sources (Room DB)
│   └── remote/         # Remote data sources (Firebase)
├── ui/                 # UI layer (Activities, Fragments)
│   ├── dashboard/      # Dashboard feature
│   ├── screentime/     # Screen time feature
│   ├── appcontrol/     # App control feature
│   ├── contacts/       # Contact management feature
│   ├── location/       # Location tracking feature
│   └── devicelock/     # Device lock feature
├── service/            # Background services
└── utils/              # Utility classes
```

## Technology Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: Material Design Components, View Binding, Data Binding
- **Navigation**: Navigation Component
- **Dependency Injection**: Manual DI (can be upgraded to Hilt/Koin)
- **Database**: Room (for local storage)
- **Backend**: Firebase Realtime Database
- **Authentication**: Firebase Auth
- **Messaging**: Firebase Cloud Messaging
- **Location**: Google Play Services Location API
- **Coroutines**: For asynchronous operations
- **LiveData/Flow**: For reactive data streams

## Setup Instructions

### Prerequisites
- Android Studio Narwhal (2025.1.1) or later
- JDK 17
- Gradle 8.11 (configured via wrapper)
- Android SDK with API level 35 (Android 15)
- Minimum deployment target: API level 24 (Android 7.0)
- Firebase project setup

### Firebase Setup

1. Create a Firebase project at [https://console.firebase.google.com](https://console.firebase.google.com)
2. Add two Android apps to your Firebase project:
   - Parent app: `com.parentalcompanion.parent`
   - Child app: `com.parentalcompanion.child`
3. Download `google-services.json` for each app
4. Replace the placeholder `google-services.json` files in:
   - `parent-app/google-services.json`
   - `child-app/google-services.json`
5. Enable Firebase Realtime Database
6. Enable Firebase Cloud Messaging
7. Set up Firebase Authentication (optional)

### Building the Apps

1. Clone the repository:
```bash
git clone https://github.com/SharksJio/parentalCompanion.git
cd parentalCompanion
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build the Parent app:
```bash
./gradlew :parent-app:assembleDebug
```

5. Build the Child app:
```bash
./gradlew :child-app:assembleDebug
```

### Installation

1. Install Parent app on parent's device:
```bash
adb install parent-app/build/outputs/apk/debug/parent-app-debug.apk
```

2. Install Child app on child's device:
```bash
adb install child-app/build/outputs/apk/debug/child-app-debug.apk
```

## Permissions

### Parent App Permissions
- `INTERNET`: For Firebase communication
- `ACCESS_FINE_LOCATION`: For viewing child's location
- `ACCESS_COARSE_LOCATION`: For geofencing
- `POST_NOTIFICATIONS`: For receiving notifications

### Child App Permissions
- `INTERNET`: For Firebase communication
- `ACCESS_FINE_LOCATION`: For location sharing
- `FOREGROUND_SERVICE`: For monitoring service
- `PACKAGE_USAGE_STATS`: For app usage monitoring
- `READ_CONTACTS`: For contact filtering
- `SYSTEM_ALERT_WINDOW`: For lock screen overlay
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: To prevent service termination

## Firebase Database Structure

```
firebase-root/
├── devices/
│   └── {deviceId}/
│       ├── deviceName
│       ├── childName
│       ├── isOnline
│       ├── isLocked
│       └── lastSeen
├── screenTime/
│   └── {deviceId}/
│       ├── dailyLimitMinutes
│       ├── usedMinutesToday
│       └── lastUpdated
├── appControl/
│   └── {deviceId}/
│       └── {packageName}/
│           ├── appName
│           ├── isBlocked
│           └── dailyTimeLimit
├── contacts/
│   └── {deviceId}/
│       └── {contactId}/
│           ├── contactName
│           ├── phoneNumber
│           └── isAllowed
├── locations/
│   └── {deviceId}/
│       └── current/
│           ├── latitude
│           ├── longitude
│           ├── accuracy
│           └── timestamp
└── geofences/
    └── {deviceId}/
        └── {geofenceId}/
            ├── name
            ├── latitude
            ├── longitude
            ├── radiusMeters
            ├── notifyOnEnter
            └── notifyOnExit
```

## Security Considerations

- All Firebase communication is encrypted
- Implement proper Firebase Security Rules
- Use Firebase Authentication for user management
- Consider implementing end-to-end encryption for sensitive data
- Follow Android security best practices
- Request only necessary permissions
- Store sensitive data securely using Android Keystore

## Future Enhancements

- [ ] Web dashboard for parents
- [ ] Multiple child device support
- [ ] Screen content monitoring
- [ ] Website filtering
- [ ] Scheduled restrictions
- [ ] Emergency SOS feature
- [ ] Activity reports and analytics
- [ ] Cross-platform support (iOS)

## Contributing

Contributions are welcome! Please follow these guidelines:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or suggestions, please open an issue on GitHub.

## Disclaimer

This application is designed for parental monitoring purposes. Ensure you comply with local laws and regulations regarding digital monitoring and privacy. Users are responsible for obtaining necessary consent before monitoring devices.
