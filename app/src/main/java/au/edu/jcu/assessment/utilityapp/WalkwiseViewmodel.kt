package au.edu.jcu.assessment.utilityapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

class WalkWiseViewModel(
    private val weatherRepository: WeatherRepository,
    private val preferences: SharedPreferences,
    private val applicationContext: Context
) : ViewModel() {

    var maximumTemperature by mutableStateOf(
        preferences.getFloat("maximum_temperature", 30f)
    )
        private set

    var maximumUvIndex by mutableStateOf(
        preferences.getFloat("maximum_uv_index", 8f)
    )
        private set

    var allowLightRain by mutableStateOf(
        preferences.getBoolean("allow_light_rain", true)
    )
        private set

    var currentTemperature by mutableStateOf(0.0)
        private set

    var currentUvIndex by mutableStateOf(0.0)
        private set

    var rainChance by mutableStateOf(0)
        private set

    var windSpeed by mutableStateOf(0.0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLocating by mutableStateOf(false)
        private set

    var hasWeatherData by mutableStateOf(false)
        private set

    var locationLabel by mutableStateOf("Brisbane (default)")
        private set

    var locationMessage by mutableStateOf<String?>(null)
        private set

    var lastUpdated by mutableStateOf<String?>(null)
        private set

    private var selectedLatitude = -27.4698
    private var selectedLongitude = 153.0251

    init {
        refreshWeather()
    }

    fun updateMaximumTemperature(value: Float) {
        maximumTemperature = value

        preferences.edit()
            .putFloat("maximum_temperature", value)
            .apply()
    }

    fun updateMaximumUvIndex(value: Float) {
        maximumUvIndex = value

        preferences.edit()
            .putFloat("maximum_uv_index", value)
            .apply()
    }

    fun updateAllowLightRain(value: Boolean) {
        allowLightRain = value

        preferences.edit()
            .putBoolean("allow_light_rain", value)
            .apply()
    }

    fun refreshWeather() {
        if (isLoading || isLocating) return

        isLoading = true

        viewModelScope.launch {
            loadWeather(
                selectedLatitude,
                selectedLongitude,
                locationLabel
            )
        }
    }

    fun locationPermissionDenied() {
        locationMessage =
            "Location permission was not granted. " +
                    "Weather remains set to $locationLabel. " +
                    "You can allow location in Android Settings > " +
                    "Apps > WalkWise > Permissions."
    }

    fun useMyLocation() {
        if (isLoading || isLocating) return

        if (
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionDenied()
            return
        }

        isLocating = true
        locationMessage = null

        viewModelScope.launch {
            try {
                val manager = applicationContext.getSystemService(
                    Context.LOCATION_SERVICE
                ) as LocationManager

                if (
                    !manager.isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER
                    )
                ) {
                    locationMessage =
                        "Approximate location is unavailable. " +
                                "Turn on Location in Android Settings " +
                                "and try again. Weather remains set to " +
                                "$locationLabel."

                    return@launch
                }

                val location = withTimeoutOrNull(15_000L) {
                    currentApproximateLocation(manager)
                }

                if (location == null) {
                    locationMessage =
                        "Could not find your location. " +
                                "Try again shortly. Weather remains " +
                                "set to $locationLabel."

                    return@launch
                }

                isLoading = true

                loadWeather(
                    location.latitude,
                    location.longitude,
                    "Your approximate location"
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(
                    "WalkWiseLocation",
                    "Location request failed",
                    exception
                )

                locationMessage =
                    "Unable to access location. " +
                            "Weather remains set to $locationLabel."
            } finally {
                isLocating = false
            }
        }
    }

    // Permission is checked in useMyLocation before this is called.
    @SuppressLint("MissingPermission")
    private suspend fun currentApproximateLocation(
        manager: LocationManager
    ): Location? = suspendCancellableCoroutine { continuation ->

        val cancellationSignal = CancellationSignal()

        continuation.invokeOnCancellation {
            cancellationSignal.cancel()
        }

        LocationManagerCompat.getCurrentLocation(
            manager,
            LocationManager.NETWORK_PROVIDER,
            cancellationSignal,
            ContextCompat.getMainExecutor(applicationContext)
        ) { location ->
            if (continuation.isActive) {
                continuation.resume(location)
            }
        }
    }

    private suspend fun loadWeather(
        latitude: Double,
        longitude: Double,
        requestedLabel: String
    ) {
        errorMessage = null

        try {
            val weather = weatherRepository.getWeather(
                latitude,
                longitude
            )

            val uv = weather.daily.uvIndex.firstOrNull()
                ?: throw IllegalStateException(
                    "Daily UV data missing"
                )

            val rain = weather.daily.rainChance.firstOrNull()
                ?: throw IllegalStateException(
                    "Daily rain data missing"
                )

            currentTemperature = weather.current.temperature
            windSpeed = weather.current.windSpeed
            currentUvIndex = uv
            rainChance = rain

            selectedLatitude = latitude
            selectedLongitude = longitude
            locationLabel = requestedLabel
            hasWeatherData = true

            lastUpdated = SimpleDateFormat(
                "h:mm a · d MMM",
                Locale.UK
            ).format(Date())

            locationMessage = null
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Log.e(
                "WalkWiseWeather",
                "Weather request failed",
                exception
            )

            errorMessage = if (hasWeatherData) {
                "Unable to update weather. Showing the last " +
                        "loaded readings for $locationLabel."
            } else {
                "Unable to load weather. " +
                        "Check your connection and try again."
            }
        } finally {
            isLoading = false
        }
    }
}

class WalkWiseViewModelFactory(
    private val weatherRepository: WeatherRepository,
    context: Context
) : ViewModelProvider.Factory {

    private val applicationContext = context.applicationContext

    private val preferences =
        applicationContext.getSharedPreferences(
            "walkwise_preferences",
            Context.MODE_PRIVATE
        )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (
            modelClass.isAssignableFrom(
                WalkWiseViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return WalkWiseViewModel(
                weatherRepository,
                preferences,
                applicationContext
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}