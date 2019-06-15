@file:Suppress("UNREACHABLE_CODE")

package com.reezkyillma.projectandroid.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hp.weatherapplication.WeatherData.ApixuWeatherApiService
import com.example.hp.weathermodule.Model.ModelMakanan
import com.example.hp.weathermodule.Model.ModelMinuman
import com.reezkyillma.projectandroid.Adapter.RecHomeAdapter

import com.reezkyillma.projectandroid.R
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.rec_view_makanan.*
import kotlinx.android.synthetic.main.rec_view_minuman.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class WeatherFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        initialize()
    }
    fun initialize(){
        val listMakanan: List<ModelMakanan> = listOf(
                ModelMakanan("Makanan 1", "Deskripsi Makanan , makanan ini adalah sdfdsfs","urlVideo","https://cdn.moneysmart.id/wp-content/uploads/2018/12/08181221/Makanan-ini-paling-pas-disantap-saat-musim-hujan-turun-700x497.jpg"),
                ModelMakanan("Makanan 2", "Deskripsi Makanan , makanan ini adalah sdfdsfs","urlVideo","https://cdn.moneysmart.id/wp-content/uploads/2018/12/08181221/Makanan-ini-paling-pas-disantap-saat-musim-hujan-turun-700x497.jpg"),
                ModelMakanan("Makanan 3", "Deskripsi Makanan , makanan ini adalah sdfdsfs","urlVideo","https://cdn.moneysmart.id/wp-content/uploads/2018/12/08181221/Makanan-ini-paling-pas-disantap-saat-musim-hujan-turun-700x497.jpg")
        )

        val listMinuman: List<ModelMakanan> = listOf(
                ModelMakanan("Minuman 1", "Deskripsi Minuman , minuman ini adalah sdfdsfs","urlVideo","https://cdn.moneysmart.id/wp-content/uploads/2018/12/08181221/Makanan-ini-paling-pas-disantap-saat-musim-hujan-turun-700x497.jpg"),
                ModelMakanan("Minuman 2", "Deskripsi Minuman , minuman ini adalah sdfdsfs","urlVideo","https://cdn.moneysmart.id/wp-content/uploads/2018/12/08181221/Makanan-ini-paling-pas-disantap-saat-musim-hujan-turun-700x497.jpg"),
                ModelMakanan("Minuman 3", "Deskripsi Minuman , minuman ini adalah sdfdsfs","urlVideo","https://cdn.moneysmart.id/wp-content/uploads/2018/12/08181221/Makanan-ini-paling-pas-disantap-saat-musim-hujan-turun-700x497.jpg")
        )

        val apiService = ApixuWeatherApiService()


        val launch = GlobalScope.launch(Dispatchers.Main) {
            val currentWeatherResponse = apiService.getCurrentWeather("Cirebon")
                    .await()

            textView.text = (currentWeatherResponse.currentWeatherEntry.condition.text)
            lokasi.text = (currentWeatherResponse.location.name)
            temperatur.text = ((currentWeatherResponse.currentWeatherEntry.tempC.toString()).substring(0, 2))
            println("CONDITION = " + currentWeatherResponse.currentWeatherEntry.condition.toString())
            println("LOCATION  = " + currentWeatherResponse.location.name.toString())
            println("TEMPERATURE = " + (currentWeatherResponse.currentWeatherEntry.tempC.toString()).substring(0, 2))
        }

        fun onClick() {
            Toast.makeText(context,"item clicked", Toast.LENGTH_SHORT)
        }
        val FoodAdapter = RecHomeAdapter(listMakanan)

        food_rec_view.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        minuman_rec_view.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        food_rec_view.adapter = RecHomeAdapter(listMakanan)
        minuman_rec_view.adapter = RecHomeAdapter(listMinuman)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false)
        initialize()

    }


}
