package com.spamerl.geo_task.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getUserLocation(): Flow<Location>
}
