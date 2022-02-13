package com.spamerl.geo_task.presentation.ui.path

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.ktx.api.net.awaitFindAutocompletePredictions
import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import com.spamerl.geo_task.domain.usecase.DirectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PathViewModel @Inject constructor(
    private val useCase: DirectionUseCase,
    private val placesClient: PlacesClient
) : ViewModel() {
    private val _events = MutableStateFlow<PlacesSearchEvent>(PlacesSearchEventLoading)
    val events: StateFlow<PlacesSearchEvent> get() = _events

    private val _origin = MutableStateFlow(LatLng(0.0, 0.0))
    val origin: StateFlow<LatLng> get() = _origin

    private val _destination = MutableStateFlow(LatLng(0.0, 0.0))
    val destination: StateFlow<LatLng> get() = _destination

    private val _path = MutableStateFlow(DirectionsAPIResponse())
    val path: StateFlow<DirectionsAPIResponse> get() = _path

    var isOriginChanged: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isDestinationChanged: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var job: Job? = null

    fun getDirection(origin: LatLng, destination: LatLng) {
        val from: String = origin.latitude.toString() + "," + origin.longitude.toString()
        val to: String = destination.latitude.toString() + "," + destination.longitude.toString()
        useCase.execute(from, to)
            .onEach { _path.value = it }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun getLngLatFromID(placeId: String, source: String) {
        val placeField = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeField)

        if (source == "Origin") {
            placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
                _origin.value = response.place.latLng!!
                Timber.i(origin.value.toString())
            }
        } else {
            placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
                _destination.value = response.place.latLng!!
                Timber.i(destination.value.toString())
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onSearchQueryChanged(query: String) {
        var originChanged = false
        var destinationChanged = false
        if (isOriginChanged.value) {
            originChanged = true
        }
        if (isDestinationChanged.value) {
            destinationChanged = true
        }
        job?.cancel()

        val handler = CoroutineExceptionHandler { _, throwable ->
            _events.value = PlacesSearchEventError(throwable)
        }

        job = viewModelScope.launch(handler) {
            delay(200)

            val response = placesClient
                .awaitFindAutocompletePredictions {
                    this.query = query
                }

            _events.value = PlacesSearchEventFound(response.autocompletePredictions, originChanged, destinationChanged)
        }
    }
}
