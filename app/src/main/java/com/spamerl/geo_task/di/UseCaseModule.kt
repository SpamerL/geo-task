package com.spamerl.geo_task.di

import com.spamerl.geo_task.domain.repository.DestinationRepository
import com.spamerl.geo_task.domain.repository.LocationRepository
import com.spamerl.geo_task.domain.usecase.DirectionUseCase
import com.spamerl.geo_task.domain.usecase.LocationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun providesDirectionUseCase(repository: DestinationRepository) = DirectionUseCase(repository)

    @Provides
    @ViewModelScoped
    fun providesLocationUseCase(repository: LocationRepository) = LocationUseCase(repository)
}
