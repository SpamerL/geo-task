package com.spamerl.geo_task.presentation.ui.path

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.spamerl.geo_task.R
import com.spamerl.geo_task.databinding.FragmentPathBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PathFragment : Fragment(), OnMapReadyCallback {

    private val viewModel by viewModels<PathViewModel>()
    private var binding: FragmentPathBinding? = null
    private var mGoogleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPathBinding.inflate(inflater, container, false)
        with(binding) {
            this?.originTf!!.doOnTextChanged { text, _, _, _ ->
                viewModel._origin.value = text.toString()
                Log.d("originTf", viewModel.origin.value)
            }
            destinationTf.doOnTextChanged { text, _, _, _ ->
                viewModel._destination.value = text.toString()
                Log.d("destinationTf", viewModel.destination.value)
            }
            pathBt.setOnClickListener {
                val info = viewModel.getDirection(viewModel.origin.value, viewModel.destination.value)
                Log.d("path", viewModel.path.value.status.toString())
            }
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_Fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

    }
}
