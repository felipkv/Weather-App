package br.com.flp.app

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val city: String = "Sao Paulo,br"
    val api: String = "e15ff8279f7c587e99d8ca51b0493623"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WeatherTask().execute()
    }
    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask(): AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loading_bar).visibility = View.VISIBLE
            findViewById<ConstraintLayout>(R.id.main_container).visibility = View.GONE
            findViewById<TextView>(R.id.error_txt).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$api")
                    .readText(Charsets.UTF_8)
            }
            catch(e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
                        .format(Date(updatedAt * 1000))
                val temp = (main.getInt("temp")-273).toString()+"°C"
                val tempMin = (main.getInt("temp_min")-273).toString() + "°C"
                val tempMax = (main.getInt("temp_max")-273).toString() + "°C"
                val feelsLike = "Feels like " + (main.getInt("feels_like")-273).toString() + "°C"
                val pressure = main.getString("pressure") + " hPa"
                val humidity = main.getString("humidity") + " %"
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = (wind.getDouble("speed")*3.6)
                val windSpeedFormat = "%.2f".format(windSpeed) + " km/h"
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temperature).text = temp
                findViewById<TextView>(R.id.temp_min_max).text = "${tempMin} / ${tempMax}"
                findViewById<TextView>(R.id.feels_like).text = feelsLike
                findViewById<TextView>(R.id.box_sunrise_hour).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.box_sunset_hour).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.box_wind_hour).text = windSpeedFormat
                findViewById<TextView>(R.id.box_pressure_hour).text = pressure
                findViewById<TextView>(R.id.box_humidity_hour).text = humidity

                findViewById<ProgressBar>(R.id.loading_bar).visibility = View.GONE
                findViewById<ConstraintLayout>(R.id.main_container).visibility = View.VISIBLE

            }
            catch(e: Exception) {
                findViewById<ProgressBar>(R.id.loading_bar).visibility = View.GONE
                findViewById<TextView>(R.id.error_txt).visibility = View.VISIBLE
            }
        }
    }
}