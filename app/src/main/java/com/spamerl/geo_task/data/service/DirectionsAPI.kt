package com.spamerl.geo_task.data.service

import com.spamerl.geo_task.BuildConfig
import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsAPI {

    @GET("maps/api/directions/json")
    suspend fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key")key: String = BuildConfig.API_KEY
    ): DirectionsAPIResponse
}
