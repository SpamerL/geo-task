package com.spamerl.geo_task.presentation.ui.path

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
import com.google.maps.android.PolyUtil
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline
import com.spamerl.geo_task.databinding.FragmentPathBinding
import com.spamerl.geo_task.presentation.ui.path.viewPager.ViewPagerViewModel
import kotlinx.coroutines.flow.collect

class PathFragment : Fragment(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private var _binding: FragmentPathBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViewPagerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPathBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun setupFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.path.collect {
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        setupFlow()
        mGoogleMap!!.isMyLocationEnabled = true
    }
}
