# Child App Command Handler Implementation Summary

## Overview
This document summarizes the changes made to the child app to handle all commands executed by the parent app through Firebase Realtime Database.

## Problem Statement
The child app was missing handlers for several parent app commands. Previously, only device lock commands were being observed and enforced. This implementation completes the child app by adding handlers for all parent commands.

## Parent App Commands
The parent app can send the following commands to the child device:

1. **Device Lock/Unlock** - Lock or unlock the child device remotely
2. **Screen Time Limits** - Set daily screen time limits in minutes
3. **App Blocking** - Block specific apps from running
4. **Contact Control** - Allow/block specific phone contacts
5. **Location Requests** - Request immediate location update
6. **Geofence Management** - Add/remove geofences for monitoring

## Implementation Details

### 1. Data Models (Added)
Created three new data model classes to match the parent app:

- **AppControl.kt** - Represents app control settings
  - packageName, appName, isBlocked, dailyTimeLimit, usedTimeToday
  
- **ScreenTimeLimit.kt** - Represents screen time configuration
  - deviceId, dailyLimitMinutes, usedMinutesToday, lastUpdated
  
- **ContactControl.kt** - Represents contact control settings
  - contactId, contactName, phoneNumber, isAllowed

### 2. Repository Updates
Enhanced `ChildRepository.kt` with:

- **observeScreenTime()** - Observe full screen time data object (not just the limit)
  - Returns Flow<ScreenTimeLimit?> for complete screen time information

### 3. MonitoringService Enhancements
Significantly enhanced `MonitoringService.kt` to:

#### Screen Time Enforcement
- Track total daily screen time using UsageStatsManager
- Update Firebase with current usage every minute
- Automatically lock device when daily limit is exceeded
- Calculate screen time from start of day

#### App Blocking Enforcement
- Monitor running apps every 2 seconds using UsageStatsManager
- Detect when a blocked app is launched
- Automatically redirect to home screen when blocked app is detected
- Maintain real-time list of blocked apps from Firebase

#### Geofence Monitoring
- Observe geofence updates from Firebase
- Log geofence changes for monitoring

### 4. Contact Filtering (New)
Created two broadcast receivers for contact filtering:

#### CallReceiver.kt
- Intercepts incoming phone calls
- Checks caller number against allowed contacts list
- Blocks calls from non-allowed contacts
- Uses reflection to access telephony service for call blocking

#### SmsReceiver.kt
- Intercepts incoming SMS messages
- Checks sender number against allowed contacts list
- Blocks SMS from non-allowed contacts
- Aborts broadcast to prevent message from reaching inbox
- Priority set to 999 for early interception

### 5. Geofence Monitoring (New)
Created `GeofenceMonitor.kt` service helper:

- Tracks geofence enter/exit states
- Calculates distance from current location to geofence centers
- Detects state transitions (inside ↔ outside)
- Sends notifications when entering/exiting geofences
- Respects notifyOnEnter and notifyOnExit flags
- Creates dedicated notification channel for geofence alerts

### 6. LocationService Updates
Enhanced `LocationService.kt` to:

- Initialize GeofenceMonitor on service creation
- Check geofences whenever location updates
- Observe geofence list changes from Firebase
- Trigger geofence checks on location updates

### 7. AndroidManifest Updates
Updated manifest with:

#### New Permissions
- CALL_PHONE - For call management
- READ_CALL_LOG - For call history access
- ANSWER_PHONE_CALLS - For answering calls
- RECEIVE_SMS - For receiving SMS messages
- READ_SMS - For reading SMS messages

#### Registered Receivers
- CallReceiver - Listens to PHONE_STATE_CHANGED
- SmsReceiver - Listens to SMS_RECEIVED with priority 999

## Technical Implementation Details

### Screen Time Tracking
- Uses UsageStatsManager.INTERVAL_DAILY
- Sums totalTimeInForeground across all apps
- Updates Firebase every 60 seconds
- Calculates start of day for daily reset

### App Blocking
- Uses UsageStatsManager.INTERVAL_BEST
- Checks last 5 seconds of app usage
- Identifies most recently used app
- Launches home screen intent to exit blocked app

### Contact Filtering
- Normalizes phone numbers for comparison
- Handles partial matches (end-based matching)
- Removes non-numeric characters for comparison
- Operates in background via BroadcastReceivers

### Geofence Detection
- Calculates distance using Location.distanceBetween()
- Maintains state map to detect transitions
- Checks geofences on every location update
- Only processes active geofences

## Testing Considerations

To test these implementations:

1. **Screen Time**: 
   - Set a low limit (e.g., 1 minute) in parent app
   - Use child device for 1+ minute
   - Verify lock screen appears

2. **App Blocking**:
   - Block an app in parent app
   - Try to launch blocked app on child device
   - Verify redirect to home screen

3. **Contact Filtering**:
   - Set allowed contacts in parent app
   - Call/SMS child device from non-allowed number
   - Verify call/SMS is blocked

4. **Geofences**:
   - Add geofence in parent app
   - Move child device in/out of geofence
   - Verify notifications appear

## Security Notes

1. **Call Blocking**: Uses reflection to access internal telephony APIs. This may not work on all Android versions/devices.

2. **SMS Blocking**: High priority receiver may conflict with other SMS apps.

3. **UsageStats Access**: Requires user to grant PACKAGE_USAGE_STATS permission in Settings.

4. **Contact Filtering**: Currently loads allowed contacts on every call/SMS. Consider caching for better performance.

## Files Modified

1. `child-app/src/main/AndroidManifest.xml`
2. `child-app/src/main/java/com/parentalcompanion/child/data/repository/ChildRepository.kt`
3. `child-app/src/main/java/com/parentalcompanion/child/service/MonitoringService.kt`
4. `child-app/src/main/java/com/parentalcompanion/child/service/LocationService.kt`

## Files Created

1. `child-app/src/main/java/com/parentalcompanion/child/data/model/AppControl.kt`
2. `child-app/src/main/java/com/parentalcompanion/child/data/model/ScreenTimeLimit.kt`
3. `child-app/src/main/java/com/parentalcompanion/child/data/model/ContactControl.kt`
4. `child-app/src/main/java/com/parentalcompanion/child/receiver/CallReceiver.kt`
5. `child-app/src/main/java/com/parentalcompanion/child/receiver/SmsReceiver.kt`
6. `child-app/src/main/java/com/parentalcompanion/child/service/GeofenceMonitor.kt`

## Summary

All parent app commands are now properly handled by the child app:

✅ Device Lock - Observed and enforced  
✅ Screen Time Limits - Tracked, updated, and enforced  
✅ App Blocking - Monitored and enforced  
✅ Contact Filtering - Calls and SMS filtered  
✅ Location Requests - Handled (already implemented)  
✅ Geofence Monitoring - Detected and notifications sent  

The child app now provides complete enforcement of all parental controls configured through the parent app.
