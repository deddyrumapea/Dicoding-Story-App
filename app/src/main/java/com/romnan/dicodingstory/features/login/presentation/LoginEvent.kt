package com.romnan.dicodingstory.features.login.presentation

sealed class LoginEvent {
    data class SendLoginRequest(val email: String, val password: String) : LoginEvent()
}