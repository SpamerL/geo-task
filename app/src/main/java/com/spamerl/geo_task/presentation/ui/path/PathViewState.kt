package com.spamerl.geo_task.presentation.ui.path

sealed class PathViewState {
    object Success : PathViewState()
    data class Error(val message: String) : PathViewState()
    object Loading : PathViewState()
    object Empty : PathViewState()
}