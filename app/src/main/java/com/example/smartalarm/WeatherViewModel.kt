package com.example.smartalarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableStateFlow<CurrentWeather?>(null)
    val weatherData: StateFlow<CurrentWeather?> = _weatherData

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val response = WeatherService.api.getWeather(latitude, longitude)
                _weatherData.value = response.currentWeather
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
