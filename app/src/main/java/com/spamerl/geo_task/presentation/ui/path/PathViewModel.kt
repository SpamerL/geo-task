package com.spamerl.geo_task.presentation.ui.path

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import com.spamerl.geo_task.domain.usecase.DirectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PathViewModel @Inject constructor(
    private val useCase: DirectionUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow<PathViewState>(PathViewState.Empty)
    val viewState: StateFlow<PathViewState> = _viewState

    val _origin = MutableStateFlow<String>("")
    val origin: StateFlow<String> = _origin

    val _destination = MutableStateFlow<String>("")
    val destination: StateFlow<String> = _destination

    private val _path = MutableStateFlow<DirectionsAPIResponse>(DirectionsAPIResponse())
    val path: StateFlow<DirectionsAPIResponse> = _path.asStateFlow()

    fun getDirection(origin: String, destination: String) {
        useCase.execute(origin, destination)
            .onEach { _path.value = it }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}
