# Screen Time Implementation Guide

This document provides a detailed overview of the screen time tracking and enforcement features implemented in the Parental Companion app.

## Overview

The screen time feature consists of two levels of control:

1. **Device-Level Screen Time**: Total daily screen time limit for the entire device
2. **Per-App Screen Time**: Individual time limits for specific applications

Both features work together to provide comprehensive parental control over device usage.

## Architecture

### Parent App

The parent app provides the interface for setting screen time limits:

#### Components

1. **ScreenTimeFragment**: Displays and manages device-level screen time
   - Shows current usage and remaining time
   - Allows setting daily device time limit (in minutes)
   - Updates Firebase in real-time

2. **AppControlFragment**: Manages per-app time limits
   - Displays all installed apps on child device
   - Shows current usage time for each app
   - Allows setting individual time limits per app
   - Provides toggle to block/unblock apps

3. **Models**:
   - `ScreenTimeLimit`: Contains deviceId, dailyLimitMinutes, usedMinutesToday, lastUpdated
   - `AppControl`: Contains packageName, appName, isBlocked, dailyTimeLimit, usedTimeToday

### Child App

The child app monitors and enforces screen time limits:

#### Components

1. **MonitoringService**: Foreground service that continuously monitors device usage
   - Runs periodic checks every 2 seconds for blocked apps
   - Updates usage statistics every minute
   - Enforces both device-level and per-app time limits

2. **ChildRepository**: Handles Firebase communication
   - Observes screen time limits from Firebase
   - Observes app control settings
   - Updates usage times to Firebase
   - Provides real-time data streams using Kotlin Flow

3. **Enforcement Logic**:
   - Device-level: Locks device when total usage exceeds daily limit
   - Per-app: Redirects to home screen when app-specific limit is exceeded
   - Uses Android's UsageStatsManager API for accurate time tracking

## Firebase Data Structure

```json
{
  "screenTime": {
    "{deviceId}": {
      "deviceId": "test_device_id",
      "dailyLimitMinutes": 180,
      "usedMinutesToday": 45,
      "lastUpdated": 1698765432000
    }
  },
  "appControl": {
    "{deviceId}": {
      "com.instagram.android": {
        "packageName": "com.instagram.android",
        "appName": "Instagram",
        "isBlocked": false,
        "dailyTimeLimit": 60,
        "usedTimeToday": 25
      },
      "com.youtube.android": {
        "packageName": "com.youtube.android",
        "appName": "YouTube",
        "isBlocked": false,
        "dailyTimeLimit": 120,
        "usedTimeToday": 80
      }
    }
  }
}
```

## How It Works

### Device-Level Screen Time

1. **Parent sets limit**: Parent opens Screen Time fragment and sets daily limit (e.g., 180 minutes)
2. **Firebase update**: Limit is written to `screenTime/{deviceId}/dailyLimitMinutes`
3. **Child observes**: MonitoringService observes changes via Flow
4. **Usage tracking**: Every minute, service calculates total screen time using UsageStatsManager
5. **Firebase update**: Current usage is written to `screenTime/{deviceId}/usedMinutesToday`
6. **Parent sees updates**: Parent app observes usage in real-time
7. **Enforcement**: When usage >= limit, device shows lock screen

### Per-App Screen Time

1. **Parent sets app limit**: Parent opens App Control fragment, selects an app, sets time limit (e.g., 60 minutes for Instagram)
2. **Firebase update**: Limit is written to `appControl/{deviceId}/{packageName}/dailyTimeLimit`
3. **Child observes**: MonitoringService observes all app controls
4. **Usage tracking**: Every minute, service queries usage for apps with limits
5. **Firebase update**: App usage is written to `appControl/{deviceId}/{packageName}/usedTimeToday`
6. **Active monitoring**: Every 2 seconds, service checks currently running app
7. **Enforcement**: When app usage >= limit, user is redirected to home screen

## Usage Statistics API

The implementation uses Android's `UsageStatsManager` API:

- **Permission required**: `PACKAGE_USAGE_STATS` (must be granted manually in Settings)
- **Query interval**: `INTERVAL_DAILY` for accurate daily statistics
- **Time calculation**: Tracks `totalTimeInForeground` in milliseconds, converted to minutes
- **Reset behavior**: Statistics reset at midnight (device time)

## Key Implementation Details

### Time Tracking Accuracy

- Usage is calculated from midnight (00:00) to current time
- Uses `Calendar.getInstance()` to get start of day
- Handles timezone changes correctly
- Updates every 60 seconds to balance accuracy and battery usage

### Enforcement Methods

1. **Device Lock**: Shows full-screen `LockScreenActivity` when device limit exceeded
2. **App Redirect**: Sends user to home screen when app limit exceeded
3. **Blocked Apps**: Always redirect to home screen, regardless of time

### Performance Considerations

- Foreground service ensures monitoring continues
- Battery optimization exemption required
- Minimal battery impact (2-second and 60-second intervals)
- Efficient Firebase queries using value listeners

## Parent App UI

### Screen Time Fragment

```
┌─────────────────────────────────────┐
│ Screen Time Control                 │
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ Usage Today                     │ │
│ │ 3h 45min                        │ │
│ │ Time Remaining: 0h 15min        │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ Daily Limit                     │ │
│ │ [240 minutes] [Set Limit]       │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

### App Control Fragment

```
┌─────────────────────────────────────┐
│ App Usage Control                   │
├─────────────────────────────────────┤
│ [Allowed Apps] [Blocked Apps]       │
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ Instagram            [Blocked]▣ │ │
│ │ com.instagram.android           │ │
│ │ 2h 30m used                     │ │
│ │ Limit: 3h 0m                    │ │
│ │ [180 minutes] [Set]             │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ YouTube              [Blocked]□ │ │
│ │ com.youtube.android             │ │
│ │ 1h 20m used                     │ │
│ │ Limit: 2h 0m                    │ │
│ │ [120 minutes] [Set]             │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

## Testing Guidelines

### Testing Device-Level Screen Time

1. Install child app on test device
2. Grant PACKAGE_USAGE_STATS permission in Settings
3. Install parent app on parent device
4. Open Screen Time in parent app
5. Set daily limit (e.g., 1 minute for quick testing)
6. Use child device normally
7. Verify lock screen appears when limit exceeded

### Testing Per-App Screen Time

1. Install child app on test device
2. Grant required permissions
3. Install parent app on parent device
4. Open App Control in parent app
5. Select an app and set time limit (e.g., 1 minute)
6. Open that app on child device
7. Use the app for test duration
8. Verify redirect to home screen when limit exceeded

### Verifying Usage Tracking

1. Check Firebase console
2. Navigate to `screenTime/{deviceId}/usedMinutesToday`
3. Verify value updates every minute
4. Check `appControl/{deviceId}/{packageName}/usedTimeToday`
5. Verify per-app usage updates

## Troubleshooting

### Common Issues

1. **Usage not tracking**
   - Verify PACKAGE_USAGE_STATS permission is granted
   - Check UsageStatsManager is available on device
   - Ensure child app service is running

2. **Limits not enforced**
   - Verify Firebase connection is active
   - Check that MonitoringService is running
   - Verify screen overlay permission for lock screen

3. **Inaccurate time tracking**
   - Android's UsageStatsManager may have slight delays
   - Usage updates every 60 seconds, so some lag is expected
   - Device sleep time is not counted

## Future Enhancements

- [ ] Weekly/monthly time limit options
- [ ] Scheduled screen time (e.g., 2 hours between 3-5 PM)
- [ ] Break reminders
- [ ] Usage analytics and reports
- [ ] App category limits (e.g., "Social Media")
- [ ] Reward system for staying under limits
- [ ] Export usage reports
- [ ] Multiple profiles per child

## Security Considerations

- All usage data is transmitted over encrypted Firebase connection
- No personally identifiable information beyond app names
- Usage statistics stored only in Firebase (not on device)
- Parent authentication required to modify limits (future enhancement)
- Child cannot disable monitoring without device admin access

## Performance Impact

- **CPU**: Minimal (periodic checks only)
- **Memory**: ~10-20 MB for service
- **Battery**: ~1-2% per day
- **Network**: Minimal (only limit changes and periodic updates)

## API Requirements

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 14)
- **Key APIs**:
  - UsageStatsManager (API 21+)
  - JobScheduler (API 21+)
  - Foreground Services (API 26+)

## References

- [Android UsageStatsManager Documentation](https://developer.android.com/reference/android/app/usage/UsageStatsManager)
- [Firebase Realtime Database](https://firebase.google.com/docs/database)
- [Android Foreground Services](https://developer.android.com/guide/components/foreground-services)
