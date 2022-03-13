package com.spamerl.geo_task.di

import com.spamerl.geo_task.data.LocationDataSource
import com.spamerl.geo_task.data.repository.DirectionsRepositoryImpl
import com.spamerl.geo_task.data.repository.LocationRepositoryImpl
import com.spamerl.geo_task.data.service.DirectionsAPI
import com.spamerl.geo_task.domain.repository.DestinationRepository
import com.spamerl.geo_task.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesRepository(api: DirectionsAPI): DestinationRepository =
        DirectionsRepositoryImpl(api)

    @Provides
    @Singleton
    fun providesLocationRepository(locationManager: LocationDataSource): LocationRepository =
        LocationRepositoryImpl(locationManager)
}
