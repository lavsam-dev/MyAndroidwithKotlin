package ru.geekbrains.androidwithkotlin.view

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import ru.geekbrains.androidwithkotlin.BuildConfig
import ru.geekbrains.androidwithkotlin.R
import ru.geekbrains.androidwithkotlin.databinding.DetailsFragmentBinding
import ru.geekbrains.androidwithkotlin.model.data.Weather
import ru.geekbrains.androidwithkotlin.model.dto.WeatherDTO
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

private const val YOUR_API_KEY = "475de630fc0048e30e409a068afd8132"//BuildConfig.WEATHER_API_KEY

class DetailsFragment : Fragment() {

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherBundle: Weather

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable<Weather>(BUNDLE_EXTRA) ?: Weather()
        binding.main.hide()
        binding.loadingLayout.show()
        loadWeather()
    }

    @RequiresApi(Build.VERSION_CODES.N)
//    private fun loadWeather() {
//        try {
//            val uri =
//                URL("https://api.themoviedb.org/3/movie/680?api_key=dc14a9f3b0182612c3b5a4e43393fb15")
//            val handler = Handler(Looper.myLooper()!!)
//            Thread(Runnable {
//                lateinit var urlConnection: HttpsURLConnection
//                try {
//                    urlConnection = uri.openConnection() as HttpsURLConnection
//                    urlConnection.requestMethod = "GET"
//                    urlConnection.readTimeout = 10000
//                    val bufferedReader =
//                        BufferedReader(InputStreamReader(urlConnection.inputStream))
//
//                    // преобразование ответа от сервера (JSON) в модель данных (WeatherDTO)
//                    val weatherDTO: WeatherDTO =
//                        Gson().fromJson(getLines(bufferedReader), WeatherDTO::class.java)
//                    handler.post { displayWeather(weatherDTO) }
//                } catch (e: Exception) {
//                    Log.e("WEATHER", "Fail connection", e)
//                    e.printStackTrace()
//                    //Обработка ошибки
//                } finally {
//                    urlConnection.disconnect()
//                }
//            }).start()
//        } catch (e: MalformedURLException) {
//            Log.e("WEATHER", "Fail URI", e)
//            e.printStackTrace()
//            //Обработка ошибки
//        }
//    }
//    это погода, она работает
    private fun loadWeather() {
        try {
            val uri =
//                URL("https://api.weather.yandex.ru/v2/informers?lat=${weatherBundle.city.lat}&lon=${weatherBundle.city.lon}")
                URL("https://api.openweathermap.org/data/2.5/weather?q=${weatherBundle.city.city}&appid=$YOUR_API_KEY&units=metric&lang=ru")
            val handler = Handler(Looper.myLooper()!!)
            Thread(Runnable {
                lateinit var urlConnection: HttpsURLConnection
                try {
                    urlConnection = uri.openConnection() as HttpsURLConnection
                    urlConnection.requestMethod = "GET"
//                    urlConnection.addRequestProperty(
//                        "X-Yandex-API-Key",
//                        YOUR_API_KEY
//                    )
                    urlConnection.readTimeout = 10000
                    val bufferedReader =
                        BufferedReader(InputStreamReader(urlConnection.inputStream))

                    // преобразование ответа от сервера (JSON) в модель данных (WeatherDTO)
                    val weatherDTO: WeatherDTO =
                        Gson().fromJson(getLines(bufferedReader), WeatherDTO::class.java)
                    handler.post { displayWeather(weatherDTO) }
                } catch (e: Exception) {
                    Log.e("WEATHER", "Fail connection", e)
                    e.printStackTrace()
                    //Обработка ошибки
                } finally {
                    urlConnection.disconnect()
                }
            }).start()
        } catch (e: MalformedURLException) {
            Log.e("WEATHER", "Fail URI", e)
            e.printStackTrace()
            //Обработка ошибки
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }


    private fun displayWeather(weatherDTO: WeatherDTO) {
        with(binding) {
            main.show()
            loadingLayout.hide()
            weatherBundle.city.also{ city ->
                cityName.text = city.city
                cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    city.lat.toString(),
                    city.lon.toString()
                )
            }
            
            weatherDTO.main?.let { main ->
                temperatureValue.text = main.temp.toString()
                feelsLikeValue.text = main.feels_like.toString()
                weatherCondition.text = main.pressure.toString()
            }
            weatherDTO.wind?.let { wind ->
                windSpeed.text = wind.speed.toString()
            }
            weatherDTO.weather?.get(0)?.let { desc ->
                weatherDescription.text = desc.description
            }
        }
    }

    companion object {
        const val BUNDLE_EXTRA = "weather"

        val arr: ArrayList<Any>? = null

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}