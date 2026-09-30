package au.edu.jcu.assessment.utilityapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class WalkWiseViewModel : ViewModel() {
    var maximumTemperature by mutableStateOf(30f)
        private set

    var maximumUvIndex by mutableStateOf(8f)
        private set

    var allowLightRain by mutableStateOf(true)
        private set

    fun updateMaximumTemperature(value: Float) {
        maximumTemperature = value
    }

    fun updateMaximumUvIndex(value: Float) {
        maximumUvIndex = value
    }

    fun updateAllowLightRain(value: Boolean) {
        allowLightRain = value
    }
}