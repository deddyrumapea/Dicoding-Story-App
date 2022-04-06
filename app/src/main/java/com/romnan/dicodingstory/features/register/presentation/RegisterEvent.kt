package com.romnan.dicodingstory.features.register.presentation

sealed class RegisterEvent {
    data class SendRegisterRequest(
        val name: String,
        val email: String,
        val password: String
    ) : RegisterEvent()
}
