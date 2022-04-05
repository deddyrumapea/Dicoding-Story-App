package com.romnan.dicodingstory.features.home.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefRepo: PreferencesRepository
) : ViewModel() {
    private val _isLoggedIn = MutableLiveData(true)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private var checkLoginStateJob: Job? = null

    init {
        checkLoginState()
    }

    private fun checkLoginState() {
        checkLoginStateJob?.cancel()
        checkLoginStateJob = viewModelScope.launch {
            val loginToken = prefRepo.getAppPreferences().first().loginToken
            _isLoggedIn.value = loginToken.isNotBlank()
        }
    }
}