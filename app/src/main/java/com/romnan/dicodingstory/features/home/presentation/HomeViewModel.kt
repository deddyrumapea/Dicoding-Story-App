package com.romnan.dicodingstory.features.home.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.home.domain.model.Story
import com.romnan.dicodingstory.features.home.domain.repository.HomeRepository
import com.romnan.dicodingstory.features.home.presentation.model.HomeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepo: HomeRepository,
    private val prefRepo: PreferencesRepository
) : ViewModel() {
    private val _storiesList = MutableLiveData<List<Story>>()
    val storiesList: LiveData<List<Story>> = _storiesList

    private val _errorMessage = MutableLiveData<UIText>()
    val errorMessage: LiveData<UIText> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private var getAllStoriesJob: Job? = null
    private var collectLoginStateJob: Job? = null
    private var logoutJob: Job? = null

    init {
        collectLoginState()
        getAllStories()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Logout -> logout()
            HomeEvent.RefreshStories -> getAllStories()
        }
    }

    private fun getAllStories() {
        getAllStoriesJob?.cancel()
        getAllStoriesJob = viewModelScope.launch {
            homeRepo.getAllStories().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _errorMessage.value =
                            result.uiText ?: UIText.StringResource(R.string.em_unknown)
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                        _storiesList.value = result.data
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _storiesList.value = result.data
                    }
                }
            }.launchIn(this)
        }
    }

    private fun logout() {
        logoutJob?.cancel()
        logoutJob = viewModelScope.launch { prefRepo.deleteLoginResult() }
    }

    private fun collectLoginState() {
        collectLoginStateJob?.cancel()
        collectLoginStateJob = viewModelScope.launch {
            prefRepo.getAppPreferences().onEach { appPref ->
                val loginState = appPref.loginResult
                _isLoggedIn.value = loginState.token.isNotBlank()
            }.launchIn(this)
        }
    }
}