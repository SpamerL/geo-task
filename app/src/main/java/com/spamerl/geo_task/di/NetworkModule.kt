package com.spamerl.geo_task.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.spamerl.geo_task.GeoTask
import com.spamerl.geo_task.data.LocationDataSource
import com.spamerl.geo_task.data.service.DirectionsAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://maps.googleapis.com/"

    @Provides
    @Singleton
    fun providesOkHttpClient(@ApplicationContext context: Context): OkHttpClient =
        OkHttpClient.Builder()
            .build()

    @Provides
    @Singleton
    fun providesRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())

    @Provides
    @Singleton
    fun providesDirectionsApi(retrofit: Retrofit.Builder): DirectionsAPI =
        retrofit.baseUrl(BASE_URL).build().create(DirectionsAPI::class.java)

    @Provides
    @Singleton
    fun providesPlaceClient(@ApplicationContext context: Context): PlacesClient =
        Places.createClient(context)

    @Provides
    @Singleton
    fun providesAutocompleteToken(): AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

    @Provides
    @Singleton
    fun providesLocationDataSource(
        @ApplicationContext context: Context
    ): LocationDataSource = LocationDataSource(context, (context.applicationContext as GeoTask).applicationScope)
}
