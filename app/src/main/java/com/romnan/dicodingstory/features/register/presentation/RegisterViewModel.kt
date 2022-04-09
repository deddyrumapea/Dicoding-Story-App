package com.romnan.dicodingstory.features.register.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.register.domain.repository.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepo: RegisterRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: LiveData<Boolean> = _isRegistered

    private val _errorMessage = MutableLiveData<UIText>()
    val errorMessage: LiveData<UIText> = _errorMessage

    private var registerJob: Job? = null

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.SendRegisterRequest -> register(
                name = event.name,
                email = event.email,
                password = event.password
            )
        }
    }

    private fun register(name: String, email: String, password: String) {
        registerJob?.cancel()
        registerJob = viewModelScope.launch {
            registerRepo.register(name = name, email = email, password = password)
                .onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _isLoading.value = false
                            _isRegistered.value = false
                            _errorMessage.value = result.uiText
                                ?: UIText.StringResource(R.string.em_unknown)
                        }

                        is Resource.Loading -> {
                            _isLoading.value = true
                            _isRegistered.value = false
                        }

                        is Resource.Success -> {
                            _isLoading.value = false
                            _isRegistered.value = true
                        }
                    }
                }.launchIn(this)
        }
    }

}