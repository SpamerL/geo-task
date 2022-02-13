package com.spamerl.geo_task.presentation.ui.path

import com.google.android.libraries.places.api.model.AutocompletePrediction

sealed class PlacesSearchEvent

object PlacesSearchEventLoading : PlacesSearchEvent()

data class PlacesSearchEventError(
    val exception: Throwable
) : PlacesSearchEvent()

data class PlacesSearchEventFound(
    val places: List<AutocompletePrediction>,
    var originChanged: Boolean,
    var destinationChanged: Boolean
) : PlacesSearchEvent()
