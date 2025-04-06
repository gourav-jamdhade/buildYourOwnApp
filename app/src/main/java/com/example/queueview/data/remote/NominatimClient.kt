
package com.example.queueview.data.remote

import com.example.queueview.data.nerwork.NominatimService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NominatimClient {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    val instance: NominatimService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimService::class.java)
    }
}

fun createViewBox(lat: Double, lon: Double, offsetKm: Double = 150.0): String {
    // Convert km to approx degrees (1 deg ≈ 111 km)
    val offset = offsetKm / 111.0
    val left = lon - offset
    val right = lon + offset
    val top = lat + offset
    val bottom = lat - offset
    return "$left,$top,$right,$bottom"
}
