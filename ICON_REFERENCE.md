# Icon Reference Guide

This document provides a visual reference for all icons included in the Parental Companion apps.

## Parent App Icons

### Feature Icons

```
┌─────────────────────────────────────────────────────────────────┐
│                     PARENT APP FEATURE ICONS                     │
└─────────────────────────────────────────────────────────────────┘

📊 ic_dashboard.xml
   ┌───┬───┐
   │ ▪ │ ▪ │   Dashboard/Grid view
   ├───┼───┤   Used for: Main dashboard navigation
   │ ▪ │ ▪ │
   └───┴───┘

⏰ ic_screen_time.xml
      ⟲
    ┌───┐      Clock/Time management
    │⋅⋅⋅│      Used for: Screen time limits feature
    └───┘

⊞ ic_app_control.xml
   ▪▪▪▪▪▪
   ▪▪▪▪▪▪      Application grid
   ▪▪▪▪▪▪      Used for: App blocking/management

👤 ic_contacts.xml
    ╭─╮
    │●│        Person/Contact icon
    ╰─╯        Used for: Contact management feature
    ╱│╲

📍 ic_location.xml
     ▲
    ╱│╲        Map pin/Location marker
   ╱ │ ╲       Used for: Location tracking feature
  └──┴──┘

🔒 ic_device_lock.xml
   ┌─┐
   │●│         Padlock
   ╠═╣         Used for: Remote device lock feature
   ╚═╝

⊕ ic_geofence.xml
     ⊙          Location with boundary circle
   ╱   ╲        Used for: Geofencing/safe zones
  │  📍  │
   ╲   ╱

➕ ic_add.xml
     │
   ──┼──        Plus/Add symbol
     │          Used for: Adding new items

✓ ic_check.xml
      ╱
    ╱          Checkmark
   ╱           Used for: Confirmations, success states

✕ ic_close.xml
   ╲ ╱
    ╳          X/Close symbol
   ╱ ╲         Used for: Cancel, close, delete actions

⚠ ic_warning.xml
     ▲
   ╱ ! ╲       Warning triangle
  └─────┘      Used for: Warnings, alerts, errors

⬤ ic_notification.xml
    ●           Circle/Dot
                Used for: Notification indicators
```

### Launcher Icon

```
┌──────────────────────────────────────────────┐
│         PARENT APP LAUNCHER ICON              │
└──────────────────────────────────────────────┘

🟢 Green Theme (#3DDC84)
   ╭──────────╮
   │          │
   │   👥     │    Parent/Guardian icon
   │   ||     │    Represents monitoring role
   │          │
   ╰──────────╯

Files:
- ic_launcher.xml (adaptive)
- ic_launcher_round.xml (round adaptive)
- ic_launcher_foreground.xml (foreground layer)
```

## Child App Icons

### Feature Icons

```
┌─────────────────────────────────────────────────────────────────┐
│                     CHILD APP FEATURE ICONS                      │
└─────────────────────────────────────────────────────────────────┘

🔒 ic_lock.xml
   ┌─┐
   │●│         Padlock
   ╠═╣         Used for: Lock screen overlay
   ╚═╝

🛡️ ic_monitoring.xml
     ⚡
   ╱   ╲       Shield with checkmark
  │  ✓  │      Used for: Monitoring service notification
   ╲   ╱

📍 ic_location.xml
     ▲
    ╱│╲        Map pin
   ╱ │ ╲       Used for: Location service
  └──┴──┘

✓ ic_check.xml
      ╱
    ╱          Checkmark
   ╱           Used for: Confirmations

⚠ ic_warning.xml
     ▲
   ╱ ! ╲       Warning triangle
  └─────┘      Used for: Warnings and alerts

⬤ ic_notification.xml
    ●           Circle/Dot
                Used for: Service notification
```

### Launcher Icon

```
┌──────────────────────────────────────────────┐
│         CHILD APP LAUNCHER ICON               │
└──────────────────────────────────────────────┘

🔵 Blue Theme (#2196F3)
   ╭──────────╮
   │          │
   │   📍     │    Location/Tracking icon
   │   ⊙      │    Represents monitored device
   │          │
   ╰──────────╯

Files:
- ic_launcher.xml (adaptive)
- ic_launcher_round.xml (round adaptive)
- ic_launcher_foreground.xml (foreground layer)
```

## Icon Specifications

### Technical Details

| Property | Value |
|----------|-------|
| Format | Vector Drawable (XML) |
| Size | 24dp × 24dp (feature icons) |
| | 108dp × 108dp (launcher icon canvas) |
| Viewport | 24 × 24 (feature icons) |
| | 108 × 108 (launcher icons) |
| Color | @android:color/white (tintable) |
| API Level | 21+ (Vector drawables) |
| | 26+ (Adaptive icons) |

### File Structure

```
app/src/main/res/
├── drawable/
│   ├── ic_*.xml              # Feature icons (vector)
│   └── ic_launcher_foreground.xml
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml       # Adaptive icon
│   └── ic_launcher_round.xml # Round adaptive icon
├── mipmap-mdpi/              # (empty - vector only)
├── mipmap-hdpi/              # (empty - vector only)
├── mipmap-xhdpi/             # (empty - vector only)
├── mipmap-xxhdpi/            # (empty - vector only)
└── mipmap-xxxhdpi/           # (empty - vector only)
```

## Usage Examples

### In Layout XML

```xml
<!-- Feature Icon -->
<ImageView
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:src="@drawable/ic_location"
    android:tint="?attr/colorPrimary" />

<!-- With background -->
<ImageView
    android:layout_width="48dp"
    android:layout_height="48dp"
    android:padding="12dp"
    android:background="?attr/selectableItemBackgroundBorderless"
    android:src="@drawable/ic_dashboard" />
```

### In Kotlin Code

```kotlin
// Set icon on ImageView
imageView.setImageResource(R.drawable.ic_screen_time)

// Apply tint programmatically
imageView.setColorFilter(
    ContextCompat.getColor(context, R.color.primary),
    PorterDuff.Mode.SRC_IN
)

// Use in notification
val notification = NotificationCompat.Builder(context, CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_notification)
    .build()
```

### In Menu XML

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/action_add"
        android:icon="@drawable/ic_add"
        android:title="@string/add" />
</menu>
```

## Color Customization

All icons use white as the default fill color and can be tinted:

```xml
<!-- Static tint -->
<ImageView
    android:src="@drawable/ic_location"
    android:tint="@color/primary" />

<!-- Theme attribute tint -->
<ImageView
    android:src="@drawable/ic_warning"
    android:tint="?attr/colorError" />
```

## Accessibility

Always provide content descriptions:

```xml
<ImageView
    android:src="@drawable/ic_location"
    android:contentDescription="@string/location_tracking" />
```

Example string resources:

```xml
<string name="location_tracking">Location tracking</string>
<string name="device_lock">Device lock control</string>
<string name="screen_time">Screen time management</string>
```

## Icon States

Icons can represent different states using tints:

```kotlin
// Normal state
iconView.setColorFilter(ContextCompat.getColor(context, R.color.primary))

// Active/Selected state
iconView.setColorFilter(ContextCompat.getColor(context, R.color.accent))

// Disabled state
iconView.setColorFilter(ContextCompat.getColor(context, R.color.disabled))
iconView.alpha = 0.38f

// Error state
iconView.setColorFilter(ContextCompat.getColor(context, R.color.error))
```

## Material Design Guidelines

These icons follow Material Design principles:

1. **Touch Target**: Minimum 48dp × 48dp (use padding for smaller icons)
2. **Icon Size**: 24dp × 24dp for standard UI icons
3. **Optical Alignment**: Icons are optically centered within their bounds
4. **Consistency**: All icons follow the same visual style
5. **Accessibility**: High contrast, clear shapes, meaningful descriptions

## Icon Recommendations by Feature

| Feature | Parent App Icon | Child App Icon |
|---------|----------------|---------------|
| Dashboard | `ic_dashboard` | - |
| Screen Time | `ic_screen_time` | - |
| App Control | `ic_app_control` | - |
| Contacts | `ic_contacts` | - |
| Location | `ic_location` | `ic_location` |
| Device Lock | `ic_device_lock` | `ic_lock` |
| Geofencing | `ic_geofence` | - |
| Monitoring | - | `ic_monitoring` |
| Notifications | `ic_notification` | `ic_notification` |
| Actions | `ic_add`, `ic_check`, `ic_close` | `ic_check` |
| Warnings | `ic_warning` | `ic_warning` |

## Testing Icons

### Visual Testing

1. Test on different screen sizes (phone, tablet)
2. Test on different Android versions (API 21+)
3. Verify icons in both light and dark themes
4. Check icon clarity at different densities

### Automated Testing

```kotlin
@Test
fun testIconsExist() {
    val icons = listOf(
        R.drawable.ic_dashboard,
        R.drawable.ic_screen_time,
        R.drawable.ic_location
        // ... add all icons
    )
    
    icons.forEach { iconRes ->
        val drawable = ContextCompat.getDrawable(context, iconRes)
        assertNotNull("Icon should exist", drawable)
    }
}
```

## Future Icon Additions

Suggested icons for future features:

- [ ] `ic_settings` - Settings/preferences
- [ ] `ic_help` - Help and support
- [ ] `ic_history` - Activity history/timeline
- [ ] `ic_stats` - Statistics and analytics
- [ ] `ic_emergency` - Emergency/SOS button
- [ ] `ic_profile` - User profile/account
- [ ] `ic_logout` - Sign out
- [ ] `ic_edit` - Edit mode
- [ ] `ic_delete` - Delete action
- [ ] `ic_share` - Share functionality

## Resources

- [Material Icons](https://material.io/resources/icons/)
- [Material Design Iconography](https://material.io/design/iconography/system-icons.html)
- [Android Vector Drawables](https://developer.android.com/guide/topics/graphics/vector-drawable-resources)
- [Adaptive Icons](https://developer.android.com/guide/practices/ui_guidelines/icon_design_adaptive)

---

**Note**: This is a text-based visual representation. For actual rendered icons, build and run the apps or use Android Studio's drawable preview.

For questions or to request new icons, please open an issue on GitHub.
