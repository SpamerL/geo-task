package com.spamerl.geo_task.domain.usecase

import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import com.spamerl.geo_task.domain.repository.DestinationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class DirectionUseCase @Inject constructor(
    private val repo: DestinationRepository
) {
    fun execute(origin: String, destination: String): Flow<DirectionsAPIResponse> = repo.getDirection(origin, destination)
}
