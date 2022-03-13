package com.spamerl.geo_task.data.repository

import com.spamerl.geo_task.data.LocationDataSource
import com.spamerl.geo_task.domain.repository.LocationRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationManager: LocationDataSource
) : LocationRepository {
    val receivingLocationUpdates: StateFlow<Boolean> = locationManager.receivingLocationUpdates

    override fun getUserLocation() = locationManager.locationFlow()
}
