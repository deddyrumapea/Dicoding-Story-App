package com.romnan.dicodingstory.features.home.presentation.model

sealed class HomeEvent {
    object Logout : HomeEvent()
}