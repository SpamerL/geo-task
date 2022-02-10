package com.spamerl.geo_task.presentation.ui.path.origin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.spamerl.geo_task.R
import com.spamerl.geo_task.databinding.OriginFragmentBinding
import com.spamerl.geo_task.presentation.ui.path.viewPager.ViewPagerFragment
import com.spamerl.geo_task.presentation.ui.path.viewPager.ViewPagerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OriginFragment : Fragment(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private var _binding: OriginFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ViewPagerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OriginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appContext = requireContext().applicationContext
        val map = childFragmentManager.findFragmentById(R.id.origin_map) as SupportMapFragment
        map.getMapAsync(this)

        Places.initialize(
            appContext,
            "AIzaSyB0nXxVKfiGQWF33jdqatGhr8EM2yfM5L4"
        )

        val placesClient = Places.createClient(requireContext())
        val info = Places.isInitialized()
        Log.d("Places.isInitialized ?", info.toString())

        val placesAutocomplete = childFragmentManager.findFragmentById(R.id.places_autocomplete) as AutocompleteSupportFragment

        placesAutocomplete.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        placesAutocomplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                Log.d("place error:", status.toString())
            }

            override fun onPlaceSelected(place: Place) {
                mGoogleMap?.addMarker(
                    MarkerOptions()
                        .position(place.latLng!!)
                )
                mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 12f))
                viewModel.originLatLng.value = place.latLng!!
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        val appContext = context?.applicationContext
        if (ActivityCompat.checkSelfPermission(
                appContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        googleMap.isMyLocationEnabled = true
    }
}
