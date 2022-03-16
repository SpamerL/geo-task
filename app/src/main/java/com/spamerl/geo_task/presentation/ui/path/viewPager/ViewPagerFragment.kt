package com.spamerl.geo_task.presentation.ui.path.viewPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayoutMediator
import com.spamerl.geo_task.R
import com.spamerl.geo_task.databinding.ViewPagerFragmentBinding
import com.spamerl.geo_task.presentation.ui.util.margin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ViewPagerFragment : Fragment() {

    private var _binding: ViewPagerFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViewPagerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewPagerFragmentBinding.inflate(inflater, container, false)
        setupUI(binding)
        setupFlow()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = PagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = pathArray[position]
        }.attach()
    }

    private fun setupUI(binding: ViewPagerFragmentBinding) {
        val searchButton = binding.searchBt
        searchButton.setOnClickListener {
            navigateToResult()
        }
    }

    private fun setupFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.searchButtonState.collect {
                when (it) {
                    true -> {
                        binding.searchBt.visibility = View.VISIBLE
                        binding.cardHolder.margin(bottom = 8F)
                    }
                    false -> {
                        binding.searchBt.visibility = View.GONE
                        binding.cardHolder.margin(bottom = 60F)
                    }
                }
            }
        }
    }

    private fun navigateToResult() {
        if (viewModel.originLatLng.value != LatLng(0.0, 0.0) && viewModel.destinationLatLng.value != LatLng(0.0, 0.0)) {
            viewModel.searchPath()
            findNavController().navigate(R.id.action_viewPagerFragment_to_resultFragment)
        }
    }

    private val pathArray = arrayOf(
        "From",
        "To"
    )
}
