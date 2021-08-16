package ru.geekbrains.androidwithkotlin.model

import ru.geekbrains.androidwithkotlin.model.data.Weather

sealed class AppState {
    data class Success(val weatherData: List<Weather>) : AppState()
    class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}
