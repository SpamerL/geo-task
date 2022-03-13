package com.spamerl.geo_task.domain.usecase

import android.location.Location
import com.spamerl.geo_task.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    fun execute(): Flow<Location> = repository.getUserLocation()
}
