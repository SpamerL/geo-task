package com.spamerl.geo_task.presentation.ui.path.destination

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.ktx.addMarker
import com.spamerl.geo_task.R
import com.spamerl.geo_task.databinding.DestinationFragmentBinding
import com.spamerl.geo_task.domain.model.PlacesSearchEventEmpty
import com.spamerl.geo_task.domain.model.PlacesSearchEventError
import com.spamerl.geo_task.domain.model.PlacesSearchEventFound
import com.spamerl.geo_task.domain.model.PlacesSearchEventLoading
import com.spamerl.geo_task.presentation.ui.path.viewPager.ViewPagerViewModel
import com.spamerl.geo_task.presentation.ui.util.PlacesPredictionAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DestinationFragment : Fragment(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private var _binding: DestinationFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViewPagerViewModel by activityViewModels()
    private var adapter: PlacesPredictionAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DestinationFragmentBinding.inflate(inflater, container, false)

        val googleMap = childFragmentManager.findFragmentById(R.id.destination_map) as SupportMapFragment

        googleMap.getMapAsync(this)

        adapter = PlacesPredictionAdapter(requireContext(), R.layout.autocomplete_item)

        setupUI(binding)

        setupFlow()

        return binding.root
    }

    private fun setupUI(binding: DestinationFragmentBinding) {

        val autocompletePrediction = binding.destinationSearchTv

        autocompletePrediction.setAdapter(adapter!!)

        autocompletePrediction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 1) {
                    viewModel.autocomplete(s.toString())
                    Log.e("debug -- ", s.toString())
                }
            }
        })

        autocompletePrediction.dropDownHeight = 400

        autocompletePrediction.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                val googlePlace: AutocompletePrediction =
                    autocompletePrediction.adapter.getItem(position) as AutocompletePrediction

                autocompletePrediction.setText(googlePlace.getPrimaryText(null).toString())

                viewModel.getLatLng(googlePlace.placeId, false)
            }
    }

    private fun setupFlow() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.destinationLatLng.collect {
                if (it != LatLng(0.0, 0.0)) {
                    mGoogleMap!!.addMarker {
                        position(it)
                    }

                    mGoogleMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            it,
                            12f
                        )
                    )
                    if (viewModel.originLatLng.value != LatLng(0.0, 0.0)) {
                        mGoogleMap!!.addMarker {
                            position(viewModel.originLatLng.value)
                        }
                        viewModel.changeSearchButtonState()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is PlacesSearchEventFound -> {
                        adapter!!.setData(event.places)

                        adapter!!.notifyDataSetChanged()
                    }
                    is PlacesSearchEventError -> {
                        Toast.makeText(requireContext(), event.exception.message, Toast.LENGTH_SHORT).show()
                    }
                    is PlacesSearchEventEmpty -> {
                        Toast.makeText(requireContext(), "empty", Toast.LENGTH_SHORT).show()
                    }
                    is PlacesSearchEventLoading -> {
                        Toast.makeText(requireContext(), "search", Toast.LENGTH_SHORT).show()

                        Toast.makeText(requireContext(), viewModel.userLocationLatLng.value.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        googleMap.isMyLocationEnabled = true

        mGoogleMap!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                viewModel.userLocationLatLng.value,
                12f
            )
        )
    }
}
