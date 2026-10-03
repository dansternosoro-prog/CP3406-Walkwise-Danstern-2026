package au.edu.jcu.assessment.utilityapp

class WeatherRepository(
    private val apiService: WeatherApiService
) {
    suspend fun getWeather(
        latitude: Double = -27.4698,
        longitude: Double = 153.0251
    ): WeatherResponse {
        return apiService.getWeather(
            latitude = latitude,
            longitude = longitude
        )
    }
}