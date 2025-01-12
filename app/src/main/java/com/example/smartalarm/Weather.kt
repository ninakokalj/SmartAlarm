package com.example.smartalarm

import com.google.gson.annotations.SerializedName

data class WeatherResponse (
    @SerializedName("current_weather") val currentWeather: CurrentWeather?
)

data class CurrentWeather (
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("weathercode") val weathercode: Int

)