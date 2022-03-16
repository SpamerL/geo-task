package com.spamerl.geo_task.presentation.ui.result

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.PolyUtil
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline
import com.spamerl.geo_task.R
import com.spamerl.geo_task.data.model.DirectionsAPIResponse
import com.spamerl.geo_task.databinding.ResultFragmentBinding
import com.spamerl.geo_task.presentation.ui.path.viewPager.ViewPagerViewModel
import kotlinx.coroutines.flow.collect

class ResultFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = ResultFragment()
    }

    private var _binding: ResultFragmentBinding? = null
    private val binding get() = _binding!!
    private var mGoogleMap: GoogleMap? = null
    private val viewModel: ViewPagerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ResultFragmentBinding.inflate(inflater, container, false)

        val map = childFragmentManager.findFragmentById(R.id.result_map) as SupportMapFragment
        map.getMapAsync(this)

        return binding.root
    }

    private fun setupFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.path.collect {
                if (it != DirectionsAPIResponse()) {
                    val polyline = PolyUtil.decode(it.routes?.get(0)?.overviewPolyline!!.points)
                    mGoogleMap!!.addPolyline {
                        addAll(polyline)
                        color(Color.BLACK)
                    }
                    mGoogleMap!!.addMarker {
                        position(viewModel.originLatLng.value)
                    }
                    mGoogleMap!!.addMarker {
                        position(viewModel.destinationLatLng.value)
                    }

                    mGoogleMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            viewModel.midpoint(),
                            10f
                        )
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap!!.isMyLocationEnabled = true
        setupFlow()
    }
}
