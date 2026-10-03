package au.edu.jcu.assessment.utilityapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class WalkWiseViewModel(
    private val weatherRepository: WeatherRepository,
    private val preferences: SharedPreferences
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

    var currentTemperature by mutableStateOf(26.0)
        private set

    var currentUvIndex by mutableStateOf(4.2)
        private set

    var rainChance by mutableStateOf(20)
        private set

    var windSpeed by mutableStateOf(12.0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

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
        if (isLoading) return

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val weather = weatherRepository.getWeather()

                currentTemperature = weather.current.temperature
                windSpeed = weather.current.windSpeed
                currentUvIndex =
                    weather.daily.uvIndex.firstOrNull() ?: currentUvIndex
                rainChance =
                    weather.daily.rainChance.firstOrNull() ?: rainChance
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(
                    "WalkWiseWeather",
                    "Weather request failed",
                    exception
                )
                errorMessage =
                    "Unable to load weather. Check your connection and try again."
            } finally {
                isLoading = false
            }
        }
    }
}

class WalkWiseViewModelFactory(
    private val weatherRepository: WeatherRepository,
    context: Context
) : ViewModelProvider.Factory {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            "walkwise_preferences",
            Context.MODE_PRIVATE
        )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkWiseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalkWiseViewModel(
                weatherRepository,
                preferences
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}