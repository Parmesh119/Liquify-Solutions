import com.fasterxml.jackson.annotation.JsonProperty

data class API(
    val city: City,
    val list: List<WeatherData>
)

data class WeatherData(
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    @JsonProperty("feels_like")
    val feelsLike: Double
)

data class Weather(
    val description: String
)

data class City(
    val name: String
)
