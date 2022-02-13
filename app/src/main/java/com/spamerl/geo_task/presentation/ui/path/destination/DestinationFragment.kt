package com.spamerl.geo_task.presentation.ui.path.destination

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.spamerl.geo_task.R
import com.spamerl.geo_task.databinding.DestinationFragmentBinding
import com.spamerl.geo_task.presentation.ui.path.viewPager.ViewPagerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DestinationFragment : Fragment(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private var _binding: DestinationFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ViewPagerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DestinationFragmentBinding.inflate(inflater, container, false)

        var selectedPlace: LatLng? = null

        val googleMap = childFragmentManager.findFragmentById(R.id.destination_map) as SupportMapFragment
        googleMap.getMapAsync(this)

        val appContext = requireContext().applicationContext

        Places.initialize(
            appContext,
            ""
        )
        val placesClient = Places.createClient(requireContext())

        val placesAutocomplete = childFragmentManager.findFragmentById(R.id.places_autocomplete) as AutocompleteSupportFragment
        placesAutocomplete.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        placesAutocomplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Log.d("place error:", p0.toString())
            }

            override fun onPlaceSelected(place: Place) {
                mGoogleMap?.addMarker(
                    MarkerOptions()
                        .position(place.latLng!!)
                )
                mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 12f))
                selectedPlace = place.latLng!!
            }
        })

        val dest_bt = binding.destBt
        dest_bt.setOnClickListener {
            viewModel.destinationLatLng.value = selectedPlace!!
            findNavController().navigate(R.id.resultFragment)
        }

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0

        // permission check
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
    }
}
