# Drawable Resources

This document describes the drawable resources included in the Parental Companion apps.

## App Icons (Launcher Icons)

Both apps include launcher icons using Android's Adaptive Icon format (API 26+):

### Parent App
- **Icon**: Green-themed with parent/monitoring symbol
- **Background Color**: `#3DDC84` (Material Green)
- **Location**: `parent-app/src/main/res/`
  - `mipmap-anydpi-v26/ic_launcher.xml`
  - `mipmap-anydpi-v26/ic_launcher_round.xml`
  - `drawable/ic_launcher_foreground.xml`

### Child App
- **Icon**: Blue-themed with location/tracking symbol
- **Background Color**: `#2196F3` (Material Blue)
- **Location**: `child-app/src/main/res/`
  - `mipmap-anydpi-v26/ic_launcher.xml`
  - `mipmap-anydpi-v26/ic_launcher_round.xml`
  - `drawable/ic_launcher_foreground.xml`

## Parent App Feature Icons

All icons are vector drawables (24dp × 24dp) located in `parent-app/src/main/res/drawable/`:

| Icon File | Feature | Description |
|-----------|---------|-------------|
| `ic_dashboard.xml` | Dashboard | Grid/dashboard layout icon |
| `ic_screen_time.xml` | Screen Time Control | Clock/timer icon |
| `ic_app_control.xml` | App Management | App grid/application icon |
| `ic_contacts.xml` | Contact Management | Person/profile icon |
| `ic_location.xml` | Location Tracking | Map pin/location marker |
| `ic_device_lock.xml` | Device Lock | Padlock icon |
| `ic_geofence.xml` | Geofencing | Map with boundary circle |
| `ic_notification.xml` | Notifications | Notification dot/circle |
| `ic_add.xml` | Add Action | Plus/add symbol |
| `ic_check.xml` | Confirmation | Checkmark |
| `ic_close.xml` | Close/Cancel | X/cross symbol |
| `ic_warning.xml` | Warning/Alert | Warning triangle/exclamation |

## Child App Icons

All icons are vector drawables (24dp × 24dp) located in `child-app/src/main/res/drawable/`:

| Icon File | Feature | Description |
|-----------|---------|-------------|
| `ic_lock.xml` | Device Lock | Padlock icon |
| `ic_monitoring.xml` | Monitoring Service | Shield icon |
| `ic_location.xml` | Location Service | Map pin icon |
| `ic_notification.xml` | Service Notification | Notification dot |
| `ic_warning.xml` | Warnings | Warning symbol |
| `ic_check.xml` | Confirmation | Checkmark |

## Usage Examples

### In XML Layouts

```xml
<ImageView
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:src="@drawable/ic_location"
    android:contentDescription="@string/location" />
```

### In Code (Kotlin)

```kotlin
imageView.setImageResource(R.drawable.ic_screen_time)
```

### In Menu Items

```xml
<item
    android:id="@+id/action_add"
    android:icon="@drawable/ic_add"
    android:title="@string/add" />
```

### With Tint

```xml
<ImageView
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:src="@drawable/ic_location"
    android:tint="@color/primary" />
```

## Icon Design Guidelines

All icons follow Material Design principles:

- **Size**: 24dp × 24dp (default)
- **Format**: Vector drawable (XML)
- **Color**: Uses `@android:color/white` by default (can be tinted)
- **Style**: Material Design icons
- **Compatibility**: API 21+

## Customizing Icons

To customize an icon:

1. Open the icon XML file
2. Modify the `android:pathData` attribute
3. Update colors in the `android:fillColor` attribute
4. Rebuild the app

Example:
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FF0000"  <!-- Change color -->
        android:pathData="M12,2L2,7v10c0,5.5,3.8,10.7,9,12c5.2-1.3,9-6.5,9-12V7L12,2z"/>
</vector>
```

## Adding New Icons

To add a new icon:

1. Create a new XML file in `res/drawable/`
2. Use the vector drawable format
3. Follow the naming convention: `ic_[feature_name].xml`
4. Use 24dp × 24dp dimensions
5. Test on different screen densities

Example template:
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/white"
        android:pathData="[YOUR_PATH_DATA]"/>
</vector>
```

## Icon Sources

Icons are based on Material Design icons:
- [Material Design Icons](https://material.io/resources/icons/)
- [Material Icons Guide](https://material.io/design/iconography/system-icons.html)

## Best Practices

1. **Always provide content descriptions** for accessibility
2. **Use vector drawables** instead of PNG for scalability
3. **Maintain consistent icon style** across the app
4. **Test icons on different screen sizes** and densities
5. **Use appropriate tints** to match your app theme
6. **Keep file sizes small** by simplifying paths when possible

## Accessibility

Ensure all icons have proper content descriptions:

```xml
<ImageView
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:src="@drawable/ic_location"
    android:contentDescription="@string/location_icon_description" />
```

Add string resources:
```xml
<string name="location_icon_description">Location tracking icon</string>
```

## Troubleshooting

### Icon not showing
- Verify the file is in the correct drawable directory
- Check for XML syntax errors
- Clean and rebuild the project

### Icon appears blurry
- Ensure you're using vector drawables, not rasterized images
- Check the viewportWidth and viewportHeight match the icon design

### Icon color issues
- Use `android:tint` attribute in XML or `setColorFilter()` in code
- Ensure `fillColor` is set to a visible color

## Future Enhancements

Potential icon additions:
- [ ] Settings icon
- [ ] Help/support icon
- [ ] History/timeline icon
- [ ] Statistics/analytics icon
- [ ] Emergency/SOS icon
- [ ] Notification preferences icon
- [ ] Profile/account icon
- [ ] Logout icon

---

For questions about drawable resources, see:
- [Android Vector Drawables](https://developer.android.com/guide/topics/graphics/vector-drawable-resources)
- [Material Design Icons](https://material.io/resources/icons/)
