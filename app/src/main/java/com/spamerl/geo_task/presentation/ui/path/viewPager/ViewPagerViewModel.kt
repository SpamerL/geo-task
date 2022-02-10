package com.spamerl.geo_task.presentation.ui.path.viewPager

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.spamerl.geo_task.domain.usecase.DirectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ViewPagerViewModel @Inject constructor(
    private val useCase: DirectionUseCase
) : ViewModel() {

    var originLatLng: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))

    var destinationLatLng: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))
    // TODO: Implement the ViewModel
}
