
package com.example.queueview.data.nerwork

import com.example.queueview.data.model.NominatimResult
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimService {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
//        @Query("limit") limit: Int = 10,
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("viewbox") viewbox: String? = null,
        @Query("bounded") bounded: Int = 1

    ): List<NominatimResult>
}
