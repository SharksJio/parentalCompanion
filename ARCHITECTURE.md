# Architecture Documentation

## System Architecture Overview

The Parental Companion system consists of two Android applications that communicate via Firebase Realtime Database to enable parental monitoring and control.

```
┌─────────────────────────────────────────────────────────────────┐
│                      Firebase Realtime Database                   │
│                                                                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ Devices  │  │ Screen   │  │   App    │  │ Contacts │        │
│  │          │  │  Time    │  │ Controls │  │          │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
│                                                                   │
│  ┌──────────┐  ┌──────────┐                                     │
│  │Locations │  │Geofences │                                     │
│  └──────────┘  └──────────┘                                     │
└─────────────────────────────────────────────────────────────────┘
           ▲                                    ▲
           │                                    │
           │ Firebase SDK                       │ Firebase SDK
           │                                    │
           ▼                                    ▼
┌─────────────────────┐              ┌─────────────────────┐
│    Parent App       │              │     Child App       │
│  (Parent Device)    │              │  (Child Device)     │
│                     │              │                     │
│  - Dashboard        │              │  - Monitoring       │
│  - Screen Time      │              │    Service          │
│  - App Control      │              │  - Location         │
│  - Contacts         │              │    Service          │
│  - Location Track   │              │  - Lock Screen      │
│  - Geofencing       │              │  - Enforcement      │
│  - Device Lock      │              │                     │
└─────────────────────┘              └─────────────────────┘
```

## MVVM Architecture Pattern

Both apps follow the MVVM (Model-View-ViewModel) architecture:

```
┌───────────────────────────────────────────────────────────┐
│                         VIEW LAYER                         │
│                                                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐               │
│  │ Activity │  │ Fragment │  │  Layout  │               │
│  │          │  │          │  │   XML    │               │
│  └──────────┘  └──────────┘  └──────────┘               │
│       │              │              │                     │
│       └──────────────┴──────────────┘                     │
│                      │                                    │
│            Observes / Commands                            │
│                      │                                    │
└──────────────────────┼────────────────────────────────────┘
                       │
                       ▼
┌───────────────────────────────────────────────────────────┐
│                     VIEWMODEL LAYER                        │
│                                                            │
│  ┌────────────────────────────────────────────────────┐  │
│  │            ViewModel                                │  │
│  │                                                     │  │
│  │  • Manages UI State (StateFlow/LiveData)          │  │
│  │  • Handles UI Logic                               │  │
│  │  • No Android Framework Dependencies              │  │
│  │  • Survives Configuration Changes                 │  │
│  └────────────────────────────────────────────────────┘  │
│                      │                                    │
│              Requests Data                                │
│                      │                                    │
└──────────────────────┼────────────────────────────────────┘
                       │
                       ▼
┌───────────────────────────────────────────────────────────┐
│                      MODEL LAYER                           │
│                                                            │
│  ┌────────────────────────────────────────────────────┐  │
│  │              Repository                             │  │
│  │                                                     │  │
│  │  • Abstracts Data Sources                          │  │
│  │  • Coordinates Between Local and Remote            │  │
│  │  • Provides Clean API to ViewModel                 │  │
│  └────────────────────────────────────────────────────┘  │
│         │                              │                  │
│         ▼                              ▼                  │
│  ┌──────────────┐            ┌──────────────┐           │
│  │   Firebase   │            │  Room DB     │           │
│  │   Realtime   │            │  (Future)    │           │
│  │   Database   │            │              │           │
│  └──────────────┘            └──────────────┘           │
└───────────────────────────────────────────────────────────┘
```

## Parent App Architecture

```
ParentApplication
    │
    ├── UI Layer
    │   ├── MainActivity
    │   │   └── Navigation Host
    │   │
    │   ├── Dashboard
    │   │   ├── DashboardFragment
    │   │   └── DashboardViewModel
    │   │
    │   ├── Screen Time
    │   │   ├── ScreenTimeFragment
    │   │   └── ScreenTimeViewModel
    │   │
    │   ├── App Control
    │   │   ├── AppControlFragment
    │   │   └── AppControlViewModel
    │   │
    │   ├── Contacts
    │   │   ├── ContactsFragment
    │   │   └── ContactsViewModel
    │   │
    │   ├── Location
    │   │   ├── LocationFragment
    │   │   └── LocationViewModel
    │   │
    │   └── Device Lock
    │       ├── DeviceLockFragment
    │       └── DeviceLockViewModel
    │
    ├── Data Layer
    │   ├── Repository
    │   │   └── ParentRepository
    │   │       ├── observeChildDevice()
    │   │       ├── updateDeviceLockStatus()
    │   │       ├── observeScreenTime()
    │   │       ├── setScreenTimeLimit()
    │   │       ├── observeAppControls()
    │   │       ├── updateAppControl()
    │   │       ├── observeContacts()
    │   │       ├── updateContact()
    │   │       ├── observeLocation()
    │   │       ├── requestLocationUpdate()
    │   │       ├── observeGeofences()
    │   │       ├── addGeofence()
    │   │       └── deleteGeofence()
    │   │
    │   └── Models
    │       ├── ChildDevice
    │       ├── ScreenTimeLimit
    │       ├── AppControl
    │       ├── ContactControl
    │       ├── LocationData
    │       └── Geofence
    │
    └── Service Layer
        └── ParentFirebaseMessagingService
            └── Receives notifications from child device
```

## Child App Architecture

```
ChildApplication
    │
    ├── UI Layer
    │   ├── MainActivity
    │   │   ├── Permission Management
    │   │   └── Service Initialization
    │   │
    │   ├── MainViewModel
    │   │
    │   └── LockScreenActivity
    │       └── Full-screen lock overlay
    │
    ├── Service Layer
    │   ├── MonitoringService (Foreground)
    │   │   ├── Observes lock status
    │   │   ├── Observes screen time limits
    │   │   ├── Observes app controls
    │   │   ├── Observes contact restrictions
    │   │   └── Enforces restrictions
    │   │
    │   ├── LocationService (Foreground)
    │   │   ├── Periodic location updates
    │   │   ├── Responds to location requests
    │   │   └── Monitors geofences
    │   │
    │   └── ChildFirebaseMessagingService
    │       └── Receives commands from parent
    │
    ├── Data Layer
    │   ├── Repository
    │   │   └── ChildRepository
    │   │       ├── updateDeviceStatus()
    │   │       ├── observeLockStatus()
    │   │       ├── observeScreenTimeLimit()
    │   │       ├── updateScreenTimeUsage()
    │   │       ├── observeAppControls()
    │   │       ├── observeAllowedContacts()
    │   │       ├── updateLocation()
    │   │       ├── observeLocationRequest()
    │   │       └── observeGeofences()
    │   │
    │   └── Models
    │       └── GeofenceData
    │
    └── Receiver Layer
        └── DeviceAdminReceiver
            └── Handles device admin events
```

## Data Flow Examples

### Example 1: Parent Locks Child Device

```
1. Parent App
   └─> User taps "Lock Device" in DeviceLockFragment
       └─> DeviceLockViewModel.lockDevice(deviceId)
           └─> ParentRepository.updateDeviceLockStatus(deviceId, true)
               └─> Firebase: devices/{deviceId}/isLocked = true

2. Firebase Realtime Database
   └─> Updates devices/{deviceId}/isLocked to true

3. Child App
   └─> MonitoringService observing lock status
       └─> ChildRepository.observeLockStatus(deviceId) receives true
           └─> MonitoringService.showLockScreen()
               └─> Launches LockScreenActivity
                   └─> Device is locked with full-screen overlay
```

### Example 2: Parent Requests Location

```
1. Parent App
   └─> User taps "Request Location" in LocationFragment
       └─> LocationViewModel.requestLocationUpdate(deviceId)
           └─> ParentRepository.requestLocationUpdate(deviceId)
               └─> Firebase: devices/{deviceId}/requestLocation = true

2. Child App
   └─> LocationService observing location requests
       └─> ChildRepository.observeLocationRequest(deviceId) receives true
           └─> LocationService.getCurrentLocation()
               └─> Gets device location
                   └─> ChildRepository.updateLocation(lat, lng, accuracy)
                       └─> Firebase: locations/{deviceId}/current = {data}

3. Parent App
   └─> LocationViewModel observing location
       └─> ParentRepository.observeLocation(deviceId)
           └─> Receives updated location
               └─> LocationFragment displays on map
```

### Example 3: Screen Time Enforcement

```
1. Parent App
   └─> User sets daily limit to 120 minutes
       └─> ScreenTimeViewModel.setDailyLimit(deviceId, 120)
           └─> ParentRepository.setScreenTimeLimit(deviceId, 120)
               └─> Firebase: screenTime/{deviceId}/dailyLimitMinutes = 120

2. Child App
   └─> MonitoringService observing screen time
       └─> ChildRepository.observeScreenTimeLimit(deviceId)
           └─> Receives limit of 120 minutes
               └─> Monitors screen usage
                   └─> When usage reaches 120 minutes:
                       └─> Launches LockScreenActivity
                           └─> Shows "Screen time exceeded" message
```

## Communication Pattern

### Observer Pattern (Firebase Realtime Database)

```
Parent App                Firebase                Child App
    │                        │                         │
    │  Write Command         │                         │
    ├───────────────────────>│                         │
    │                        │                         │
    │                        │  Observe Changes        │
    │                        │<────────────────────────┤
    │                        │                         │
    │                        │  Notify Change          │
    │                        ├────────────────────────>│
    │                        │                         │
    │                        │  Enforce/Execute        │
    │                        │<────────────────────────┤
    │                        │                         │
    │  Observe Status        │  Update Status          │
    │<───────────────────────┤<────────────────────────┤
    │                        │                         │
    │  Update UI             │                         │
    ▼                        ▼                         ▼
```

## Technology Stack

### Android Framework
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Language: Kotlin 1.9.10
- Build System: Gradle 8.0

### Architecture Components
- ViewModel: Manages UI-related data
- LiveData/Flow: Observable data holder
- Lifecycle: Lifecycle-aware components
- Navigation: Fragment navigation
- View Binding: Type-safe view access
- Data Binding: Declarative UI binding

### Firebase
- Realtime Database: Real-time data sync
- Cloud Messaging: Push notifications
- Authentication: User management (future)

### Google Play Services
- Location API: Location tracking
- Maps API: Map display (parent app)
- Geofencing API: Geofence monitoring

### Background Processing
- Foreground Services: Long-running operations
- Work Manager: Scheduled tasks (future)
- Broadcast Receivers: System events

### Concurrency
- Kotlin Coroutines: Asynchronous programming
- Flow: Reactive data streams
- Lifecycle Scope: Lifecycle-aware coroutines

## Security Architecture

### Data Protection
```
┌─────────────────────────────────────────────┐
│         Application Layer                    │
│  - Minimal data storage                     │
│  - Secure SharedPreferences                 │
│  - No sensitive data in logs                │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│      Transport Layer (HTTPS/TLS)            │
│  - All Firebase communication encrypted     │
│  - Certificate pinning (recommended)        │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│         Firebase Security Rules              │
│  - Authentication required                  │
│  - Device-specific read/write rules         │
│  - Validate data structure                  │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│         Firebase Database                    │
│  - Encrypted at rest                        │
│  - Regular backups                          │
│  - Access logging                           │
└─────────────────────────────────────────────┘
```

### Permission Model

**Parent App:**
- Runtime permissions for location
- Notification permission
- No special permissions required

**Child App:**
- Device Admin: For device lock functionality
- Usage Stats: For app usage monitoring
- Overlay: For lock screen display
- Location: For location sharing
- Contacts: For contact filtering

## Performance Considerations

### Battery Optimization
- Use foreground services with notifications
- Implement efficient location tracking (5-minute intervals)
- Use WorkManager for non-urgent tasks
- Request battery optimization exemption

### Network Optimization
- Observe only necessary Firebase nodes
- Use Firebase query limits
- Implement offline caching (future)
- Batch writes when possible

### Memory Management
- Use ViewModel to survive configuration changes
- Properly manage lifecycle in services
- Cancel coroutines when not needed
- Use weak references where appropriate

## Scalability

### Current Design (Single Child)
- One device ID per child app
- Direct Firebase references
- Simple data structure

### Future Multi-Child Support
```
firebase-root/
├── users/
│   └── {parentUserId}/
│       ├── profile
│       └── childDevices: [deviceId1, deviceId2, ...]
└── devices/
    ├── {deviceId1}/
    └── {deviceId2}/
```

## Testing Strategy

### Unit Tests
- ViewModel logic
- Repository operations
- Data transformations
- Business logic

### Integration Tests
- Repository with Firebase
- Service interactions
- Permission handling

### UI Tests
- Fragment navigation
- User interactions
- Lock screen enforcement

### End-to-End Tests
- Parent-child communication
- Real-time updates
- Feature workflows

## Deployment Architecture

```
Development → Testing → Staging → Production

Each environment:
- Separate Firebase project
- Different google-services.json
- Environment-specific configuration
- Separate analytics and crash reporting
```

## Monitoring and Analytics

### Recommended Tools
- Firebase Analytics: User behavior
- Firebase Crashlytics: Crash reporting
- Firebase Performance: Performance monitoring
- Custom logging: Feature usage tracking

## Future Architecture Enhancements

1. **Dependency Injection**
   - Implement Hilt or Koin
   - Better testability
   - Cleaner code

2. **Offline Support**
   - Room database for local caching
   - Sync when online
   - Conflict resolution

3. **Modularization**
   - Feature modules
   - Shared module
   - Better build times

4. **Multi-Platform**
   - iOS child app
   - Web dashboard
   - Shared backend logic

5. **Advanced Features**
   - Machine learning for usage patterns
   - Intelligent recommendations
   - Automated scheduling
