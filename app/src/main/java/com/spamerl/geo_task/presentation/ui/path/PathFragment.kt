package com.spamerl.geo_task.presentation.ui.path

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.spamerl.geo_task.R
import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import com.spamerl.geo_task.databinding.FragmentPathBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PathFragment : Fragment(), OnMapReadyCallback {

    private val viewModel by viewModels<PathViewModel>()
    private var _binding: FragmentPathBinding? = null
    private val binding get() = _binding!!
    private var mGoogleMap: GoogleMap? = null

    val originAdapter = RvListAdapter()
    val destinationAdapter = RvListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPathBinding.inflate(inflater, container, false)
        binding.originPredictionRv.visibility = GONE
        binding.destinationPredictionRv.visibility = GONE

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is PlacesSearchEventFound -> {
                                if (event.originChanged) {
                                    originAdapter.setPredictions(event.places)
                                }
                                if (event.destinationChanged) {
                                    destinationAdapter.setPredictions(event.places)
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.origin.collect {
                        if (it != LatLng(0.0, 0.0)) {
                            binding.originPredictionRv.visibility = GONE
                            binding.destinationPredictionRv.visibility = GONE
                            mGoogleMap!!.addMarker(
                                MarkerOptions().position(it)
                            )
                            mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 8f))
                        }
                    }
                }

                launch {
                    viewModel.destination.collect {
                        if (it != LatLng(0.0, 0.0)) {
                            binding.originPredictionRv.visibility = GONE
                            binding.destinationPredictionRv.visibility = GONE
                            mGoogleMap!!.addMarker(
                                MarkerOptions().position(it)
                            )
                            mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 8f))
                        }
                    }
                }

                launch {
                    viewModel.path.collect {
                        if (it != DirectionsAPIResponse()) {
                            val polyline = PolyUtil.decode(it.routes?.get(0)?.overviewPolyline!!.points)
                            mGoogleMap!!.addPolyline(PolylineOptions().color(Color.BLACK).addAll(polyline))
                        }
                    }
                }
            }
        }

        setupUI()
        initOriginPredictionRecyclerView()
        initDestinationPredictionRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_Fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }

    private fun setupUI() {
        binding.originTf.doOnTextChanged { text, _, _, count ->
            if (count > 1) {
                viewModel.isOriginChanged.value = true
                viewModel.isDestinationChanged.value = false
                viewModel.onSearchQueryChanged(text.toString())
                binding.originPredictionRv.visibility = VISIBLE
            }
        }

        binding.destinationTf.doOnTextChanged { text, _, _, count ->
            if (count > 1) {
                viewModel.isOriginChanged.value = false
                viewModel.isDestinationChanged.value = true
                viewModel.onSearchQueryChanged(text.toString())
                binding.destinationPredictionRv.visibility = VISIBLE
            }
        }

        binding.pathBt.setOnClickListener {
            if (viewModel.origin.value != LatLng(0.0, 0.0) &&
                viewModel.destination.value != LatLng(0.0, 0.0)
            ) {
                viewModel.getDirection(viewModel.origin.value, viewModel.destination.value)
            }
        }
    }

    private fun initOriginPredictionRecyclerView() {
        val context = context
        val linearLayoutManager = LinearLayoutManager(context)
        binding.originPredictionRv.apply {
            layoutManager = linearLayoutManager
            adapter = this@PathFragment.originAdapter
        }
        originAdapter.onPlaceClickListener = { place ->
            viewModel.getLngLatFromID(placeId = place.placeId, "Origin")
            binding.originPredictionRv.visibility = GONE
            binding.originTf.setText(place.getPrimaryText(null))
            binding.originTf.clearFocus()
            viewModel.isOriginChanged.value = false
        }
    }

    private fun initDestinationPredictionRecyclerView() {
        val context = context
        val linearLayoutManager = LinearLayoutManager(context)
        binding.destinationPredictionRv.apply {
            layoutManager = linearLayoutManager
            adapter = this@PathFragment.destinationAdapter
        }
        destinationAdapter.onPlaceClickListener = { place ->
            viewModel.getLngLatFromID(placeId = place.placeId, "Destination")
            binding.destinationPredictionRv.visibility = GONE
            binding.destinationTf.setText(place.getPrimaryText(null))
            binding.destinationTf.clearFocus()
            viewModel.isDestinationChanged.value = false
        }
    }
}
