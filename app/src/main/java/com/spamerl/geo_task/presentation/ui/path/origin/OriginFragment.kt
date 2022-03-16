package com.spamerl.geo_task.presentation.ui.path.origin

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
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
import com.spamerl.geo_task.databinding.OriginFragmentBinding
import com.spamerl.geo_task.domain.model.PlacesSearchEventEmpty
import com.spamerl.geo_task.domain.model.PlacesSearchEventError
import com.spamerl.geo_task.domain.model.PlacesSearchEventFound
import com.spamerl.geo_task.domain.model.PlacesSearchEventLoading
import com.spamerl.geo_task.presentation.ui.path.viewPager.ViewPagerViewModel
import com.spamerl.geo_task.presentation.ui.util.PlacesPredictionAdapter
import com.spamerl.geo_task.presentation.ui.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OriginFragment : Fragment(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private var _binding: OriginFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViewPagerViewModel by activityViewModels()
    private var adapter: PlacesPredictionAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OriginFragmentBinding.inflate(inflater, container, false)

        val map = childFragmentManager.findFragmentById(R.id.origin_map) as SupportMapFragment
        map.getMapAsync(this)

        setupUI(binding)

        setupFlow()

        return binding.root
    }

    private fun setupUI(binding: OriginFragmentBinding) {
        val autoCompleteTextView = binding.originSearchTv
        adapter = PlacesPredictionAdapter(requireContext(), R.layout.autocomplete_item)
        binding.originSearchTv.setAdapter(adapter)
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 1) {
                    viewModel.autocomplete(s.toString())
                    Log.e("debug -- ", s.toString())
                }
            }
        })

        autoCompleteTextView.dropDownHeight = 400

        autoCompleteTextView.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                val googlePlace: AutocompletePrediction =
                    autoCompleteTextView.adapter.getItem(position) as AutocompletePrediction

                autoCompleteTextView.setText(googlePlace.getPrimaryText(null).toString())

                viewModel.getLatLng(googlePlace.placeId, true)

                mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.originLatLng.value, 12f))

                requireView().hideKeyboard()
            }
    }

    private fun setupFlow() {
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userLocationLatLng.collect {
                if (it != LatLng(0.0, 0.0)) {
                    mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 12f))

                    viewModel.unsubscribeToLocationUpdates()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.originLatLng.collect {
                if (it != LatLng(0.0, 0.0)) {
                    mGoogleMap!!.clear()

                    mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))

                    mGoogleMap!!.addMarker {
                        position(it)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        googleMap.isMyLocationEnabled = true

        setupFlow()
    }
}
