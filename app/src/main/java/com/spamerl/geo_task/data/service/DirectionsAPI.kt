package com.spamerl.geo_task.data.service

import com.spamerl.geo_task.BuildConfig
import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsAPI {
    companion object {
        const val apikey = BuildConfig.API_KEY
    }

    @GET("maps/api/directions/json")
    suspend fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key")key: String = apikey
    ): DirectionsAPIResponse
}
