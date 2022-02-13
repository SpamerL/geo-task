package com.spamerl.geo_task

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GeoTask : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(this, BuildConfig.API_KEY)
    }
}
