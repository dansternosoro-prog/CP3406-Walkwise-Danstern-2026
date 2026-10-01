package au.edu.jcu.assessment.utilityapp
class WeatherRepository(
    private val apiService: WeatherApiService
) {
    suspend fun getWeather(): WeatherResponse {
        return apiService.getWeather()
    }
}
