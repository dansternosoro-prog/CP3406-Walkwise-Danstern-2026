# WalkWise — MobileTech Utility App

WalkWise is an Android weather utility developed for **CP3406 Assessment 1: Utility App**, using the provided MobileTech starter template.

It compares weather readings with my walking preferences to help me decide whether conditions fit my chosen limits.

## Getting Started

### How to Run

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Gradle to finish syncing.
4. Select an emulator or physical Android device.
5. Run the app. API 26+ is recommended.

An internet connection is required to retrieve weather data.

## Features

- Weather data from Open-Meteo.
- Current temperature and wind speed.
- Today's maximum UV index and maximum rain probability.
- Walking outlook based on temperature, UV, and rain preferences.
- Adjustable temperature and UV limits.
- A switch to allow light rain.
- Preferences saved across app restarts using SharedPreferences.
- A refresh button, loading feedback, and last updated time.
- Error messages when weather cannot be retrieved.
- Approximate device location requested through “Use my location.”
- Dark styling with lime accents and navigation between WalkWise and Settings.

Wind is displayed for information and does not affect the walking outlook.

## Location and Permissions

The app uses:

- **INTERNET** to retrieve weather data.
- **ACCESS_COARSE_LOCATION** to request an approximate location.

Location permission is requested when I tap “Use my location.”
Background location access is not requested.

Brisbane is the default weather location when the app starts.
After a successful location request, refreshing retrieves weather
for the selected coordinates.

If location permission is denied or location is unavailable,
the app keeps the previously selected weather location.
The selected location is not saved across app restarts.

## Composables

### UtilityApp()

- Uses Scaffold to organise the main layout.
- Provides bottom navigation between WalkWise and Settings.
- Connects the screens to WalkWiseViewModel.
- Handles the location permission request.

### UtilityScreen()

- Displays the walking outlook and weather cards.
- Provides weather refresh and location buttons.
- Shows loading, error, and last updated information.

### SettingsScreen()

- Provides temperature and UV sliders.
- Includes the light rain preference switch.
- Updates the walking preferences through the ViewModel.

## Key Concepts Covered

| Week | Concept | Used In |
| ---- | ------- | ------- |
| 1 | Kotlin + Android Studio | MainActivity.kt and supporting Kotlin files |
| 2 | Jetpack Compose Layouts | UtilityApp(), UtilityScreen(), SettingsScreen() |
| 3 | Material Design 3 | Theme, cards, buttons, sliders, and navigation |
| 4 | ViewModel | WalkWiseViewModel.kt |
| 5 | Retrofit | WeatherApiService.kt, WeatherRepository.kt, AppContainer.kt |

Additional concepts include SharedPreferences, coroutines,
runtime permissions, and approximate location access.

## Main Files

| File | Purpose |
| ---- | ------- |
| MainActivity.kt | Activity startup, screens, navigation, and permission handling |
| WalkWiseViewmodel.kt | Weather state, saved preferences, and location requests |
| WeatherApiService.kt | Retrofit weather endpoint |
| WeatherRepository.kt | Requests weather for supplied coordinates |
| WeatherModels.kt | Weather response data classes |
| AppContainer.kt | Creates the API service and repository |
| ui/theme/ | App colours, typography, and theme |
| AndroidManifest.xml | App configuration and permissions |

## Manual Testing

I completed the following checks during development:

| Check | Result |
| ----- | ------ |
| Build and launch the app | Passed |
| Request weather using approximate location | Passed |
| Change temperature, UV, and rain preferences | Passed |
| Check that the walking outlook responds to preferences | Passed |
| Restart the app and check saved preferences | Passed |

## Screenshots

### Weather and walking outlook

![WalkWise weather screen](<Live weather screenshot 2.png>)

### Walking preferences

![WalkWise settings screen](<setting screenshot 2.png>)

## Limitations

- UV and rain values are daily maximum forecasts, rather than current readings.
- Location availability depends on the device's location services.
- The selected location resets to Brisbane when the app restarts.
- The outlook compares weather with personal preferences; it does not guarantee safe walking conditions.

## Template and Educational Use

This project extends the MobileTech Utility App starter template
provided for CP3406.

The original MainActivity startup block has been retained.

Weather source: https://open-meteo.com/