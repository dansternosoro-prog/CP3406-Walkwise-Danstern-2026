package au.edu.jcu.assessment.utilityapp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppContainer {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    val weatherRepository: WeatherRepository =
        WeatherRepository(apiService)
}
