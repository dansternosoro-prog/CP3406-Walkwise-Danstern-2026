package au.edu.jcu.assessment.utilityapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WalkWiseViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    var maximumTemperature by mutableStateOf(30f)
        private set

    var maximumUvIndex by mutableStateOf(8f)
        private set

    var allowLightRain by mutableStateOf(true)
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
    }

    fun updateMaximumUvIndex(value: Float) {
        maximumUvIndex = value
    }

    fun updateAllowLightRain(value: Boolean) {
        allowLightRain = value
    }

    fun refreshWeather() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val weather = weatherRepository.getWeather()

                currentTemperature = weather.current.temperature
                windSpeed = weather.current.windSpeed
                currentUvIndex =
                    weather.daily.uvIndex.firstOrNull() ?: currentUvIndex
                rainChance =
                    weather.daily.rainChance.firstOrNull() ?: rainChance
            } catch (exception: kotlinx.coroutines.CancellationException) {
                throw exception
            } catch (exception: Exception) {
                android.util.Log.e(
                    "WalkWiseWeather",
                    "Weather request failed",
                    exception
                )
                errorMessage =
                    "Unable to load weather: ${exception.localizedMessage ?: "Unknown error"}"
            } finally {
                isLoading = false
            }
        }
    }
}

class WalkWiseViewModelFactory(
    private val weatherRepository: WeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkWiseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalkWiseViewModel(weatherRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}