package au.edu.jcu.assessment.utilityapp
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val current: CurrentWeather,
    val daily: DailyWeather
)

data class CurrentWeather(
    @SerializedName("temperature_2m")
    val temperature: Double,

    @SerializedName("wind_speed_10m")
    val windSpeed: Double
)

data class DailyWeather(
    @SerializedName("uv_index_max")
    val uvIndex: List<Double>,

    @SerializedName("precipitation_probability_max")
    val rainChance: List<Int>
)
