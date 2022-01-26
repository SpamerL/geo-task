package com.spamerl.geo_task.data.repository

import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import com.spamerl.geo_task.data.service.DirectionsAPI
import com.spamerl.geo_task.domain.repository.DestinationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DirectionsRepositoryImpl @Inject constructor(
    private val api: DirectionsAPI
) : DestinationRepository {
    override fun getDirection(origin: String, destination: String): Flow<DirectionsAPIResponse> = flow { emit(api.getDirection(origin, destination)) }
}
