package au.edu.jcu.assessment.utilityapp
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double = -27.4698,
        @Query("longitude") longitude: Double = 153.0251,
        @Query("current") current: String =
            "temperature_2m,wind_speed_10m",
        @Query("daily") daily: String =
            "uv_index_max,precipitation_probability_max",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
