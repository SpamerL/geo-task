package com.spamerl.geo_task.data.model

import com.google.gson.annotations.SerializedName

data class DirectionsAPIResponse(
    @SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWaypoint?>? = null,
    val routes: List<Route?>? = null,
    val status: String? = null
) {
    data class GeocodedWaypoint(
        @SerializedName("geocoder_status")
        val geocoderStatus: String? = null,
        @SerializedName("place_id")
        val placeId: String? = null,
        val types: List<String?>? = null
    )

    data class Route(
        val bounds: Bounds? = null,
        @SerializedName("overview_polyline")
        val overviewPolyline: OverviewPolyline? = null
    ) {
        data class Bounds(
            val northeast: Northeast? = null,
            val southwest: Southwest? = null
        ) {
            data class Northeast(
                val lat: Double? = null,
                val lng: Double? = null
            )

            data class Southwest(
                val lat: Double? = null,
                val lng: Double? = null
            )
        }

        data class OverviewPolyline(
            val points: String? = null
        )
    }
}
