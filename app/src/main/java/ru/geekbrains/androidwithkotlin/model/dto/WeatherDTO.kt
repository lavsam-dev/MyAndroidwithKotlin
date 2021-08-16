package ru.geekbrains.androidwithkotlin.model.dto

data class JSONweather(val description: String?)

data class WeatherDTO(val main: MainDTO?, val wind: WindDTO?, val weather: ArrayList<JSONweather>?)
