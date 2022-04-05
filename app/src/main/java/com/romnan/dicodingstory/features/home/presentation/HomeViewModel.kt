package com.romnan.dicodingstory.features.home.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefRepo: PreferencesRepository
) : ViewModel() {
    private val _isLoggedIn = MutableLiveData(true)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _userName = MutableLiveData("")
    val userName: LiveData<String> = _userName

    private val _userId = MutableLiveData("")
    val userId: LiveData<String> = _userId

    private var initLoginStateJob: Job? = null

    init {
        updateLoginState()
    }

    private fun updateLoginState() {
        initLoginStateJob?.cancel()
        initLoginStateJob = viewModelScope.launch {
            prefRepo.getAppPreferences().onEach { appPref ->
                val loginState = appPref.loginResult
                _isLoggedIn.value = loginState.token.isNotBlank()
                _userId.value = loginState.userId
                _userName.value = loginState.name
            }.launchIn(this)
        }
    }
}