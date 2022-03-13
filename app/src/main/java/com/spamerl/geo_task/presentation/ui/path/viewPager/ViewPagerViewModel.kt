package com.spamerl.geo_task.presentation.ui.path.viewPager

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.ktx.api.net.awaitFindAutocompletePredictions
import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import com.spamerl.geo_task.domain.model.PlacesSearchEvent
import com.spamerl.geo_task.domain.model.PlacesSearchEventError
import com.spamerl.geo_task.domain.model.PlacesSearchEventFound
import com.spamerl.geo_task.domain.model.PlacesSearchEventLoading
import com.spamerl.geo_task.domain.usecase.DirectionUseCase
import com.spamerl.geo_task.domain.usecase.LocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.cos

@HiltViewModel
class ViewPagerViewModel @Inject constructor(
    private val directionUseCase: DirectionUseCase,
    private val locationUseCase: LocationUseCase,
    private val autocompleteSessionToken: AutocompleteSessionToken,
    private val placesClient: PlacesClient
) : ViewModel() {

    private var _searchButtonState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val searchButtonState: StateFlow<Boolean> get() = _searchButtonState

    private var _originLatLng: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))
    val originLatLng: StateFlow<LatLng> get() = _originLatLng

    private var _destinationLatLng: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))
    val destinationLatLng: StateFlow<LatLng> get() = _destinationLatLng

    private val _userLocationLatLng = MutableStateFlow(LatLng(0.0, 0.0))
    val userLocationLatLng: StateFlow<LatLng> get() = _userLocationLatLng

    private val _events = MutableStateFlow<PlacesSearchEvent>(PlacesSearchEventLoading)
    val events: StateFlow<PlacesSearchEvent> get() = _events

    private val _path: MutableStateFlow<DirectionsAPIResponse> = MutableStateFlow(DirectionsAPIResponse())
    val path: StateFlow<DirectionsAPIResponse> get() = _path

    private var job: Job? = null
    private var locationFlow: Job? = null

    fun searchPath() {
        val from = originLatLng.value.latitude.toString() + "," + originLatLng.value.longitude.toString()
        val to = destinationLatLng.value.latitude.toString() + "," + destinationLatLng.value.longitude.toString()
        directionUseCase.execute(from, to)
            .onEach { _path.value = it }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    init {
        subscribeToLocationUpdate()
    }

    @ExperimentalCoroutinesApi
    fun autocomplete(searchQuery: String) {
        job?.cancel()
        val handler = CoroutineExceptionHandler { _, throwable ->
            _events.value = PlacesSearchEventError(throwable)
        }
        job = viewModelScope.launch(handler) {
            delay(200)
            val response = placesClient.awaitFindAutocompletePredictions {
                this.sessionToken = autocompleteSessionToken
                this.query = searchQuery
                this.locationBias = RectangularBounds.newInstance(
                    bound(userLocationLatLng.value, 2000, 2000),
                    bound(userLocationLatLng.value, -2000, -2000)
                )
            }
            _events.value = PlacesSearchEventFound(response.autocompletePredictions)
            Log.e("Debug autocompletePredictions", _events.value.toString())
        }
    }

    fun getLatLng(placeId: String, origin: Boolean) {
        val placeField = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeField)
        if (origin) {
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    Log.e("debug -- Fetch place", response.place.toString())
                    _originLatLng.value = response.place.latLng!!
                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: ${exception.message}")
                    }
                }
        } else {
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    Log.e("debug -- Fetch place", response.place.toString())
                    _destinationLatLng.value = response.place.latLng!!
                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: ${exception.message}")
                    }
                }
        }
    }
    private fun subscribeToLocationUpdate() {
        locationFlow?.cancel()
        locationFlow = locationUseCase.execute()
            .onEach {
                _userLocationLatLng.value = LatLng(it.latitude, it.longitude)
                Log.e("debug -- location service", it.toString())
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun unsubscribeToLocationUpdates() {
        locationFlow?.cancel()
    }

    fun changeSearchButtonState() {
        _searchButtonState.value = true
    }

    // compute request bound based on user location
    // 6378137 - Earth radius in meters
    // disX, disY - distance in meters
    private fun bound(userLocation: LatLng, disX: Long, disY: Long): LatLng {
        val lat: Double = userLocation.latitude + (180 / Math.PI) * (disX / 6378137)
        val lng: Double = userLocation.longitude + (180 / Math.PI) * (disY / 6378137) / cos(userLocation.latitude)
        return LatLng(lat, lng)
    }

    fun midpoint() : LatLng {
        val midpointLat: Double = (originLatLng.value.latitude + destinationLatLng.value.latitude) / 2
        val midpointLng: Double = (originLatLng.value.longitude + destinationLatLng.value.longitude) / 2
        return LatLng(midpointLat, midpointLng)
    }
}
