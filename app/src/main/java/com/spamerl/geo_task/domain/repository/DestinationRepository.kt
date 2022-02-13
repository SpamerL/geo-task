package com.spamerl.geo_task.domain.repository

import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import kotlinx.coroutines.flow.Flow

interface DestinationRepository {
    fun getDirection(origin: String, destination: String): Flow<DirectionsAPIResponse>
}
