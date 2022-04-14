package com.romnan.dicodingstory.features.home.presentation

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.layers.domain.repository.CoreRepository
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.features.home.presentation.model.HomeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    coreRepo: CoreRepository,
    private val prefRepo: PreferencesRepository
) : ViewModel() {
    val storiesList: LiveData<PagingData<Story>> =
        coreRepo.getPagedStories()
            .cachedIn(viewModelScope)
            .asLiveData()

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private var collectLoginStateJob: Job? = null
    private var logoutJob: Job? = null

    init {
        collectLoginState()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Logout -> logout()
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