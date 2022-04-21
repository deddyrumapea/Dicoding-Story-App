package com.romnan.dicodingstory.features.login.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.login.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _errorMessage = MutableLiveData<UIText>()
    val errorMessage: LiveData<UIText> = _errorMessage

    private var checkLoginStateJob: Job? = null
    private var loginJob: Job? = null

    init {
        initLoginState()
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SendLoginRequest -> login(event.email, event.password)
        }
    }

    private fun login(email: String, password: String) {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            loginRepository.login(email = email, password = password).onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _isLoading.value = false
                        _isLoggedIn.value = false
                        _errorMessage.value = result.uiText
                            ?: UIText.StringResource(R.string.em_unknown)
                    }

                    is Resource.Loading -> {
                        _isLoading.value = true
                        _isLoggedIn.value = false
                    }

                    is Resource.Success -> {
                        _isLoading.value = false
                        _isLoggedIn.value = true
                    }
                }
            }.launchIn(this)
        }
    }

    private fun initLoginState() {
        checkLoginStateJob?.cancel()
        checkLoginStateJob = viewModelScope.launch {
            val loginToken = preferencesRepository.getAppPreferences().first().loginResult.token
            _isLoggedIn.value = loginToken.isNotBlank()
        }
    }
}