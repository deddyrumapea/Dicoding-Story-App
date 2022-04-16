package com.romnan.dicodingstory.features.storiesMap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.layers.domain.repository.CoreRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoriesMapViewModel @Inject constructor(
    private val coreRepo: CoreRepository
) : ViewModel() {
    private val _storiesList = MutableLiveData<List<Story>>()
    val storiesList: LiveData<List<Story>> = _storiesList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<UIText>()
    val errorMessage: LiveData<UIText> = _errorMessage

    init {
        getStoriesList()
    }

    private var getStoriesListJob: Job? = null

    private fun getStoriesList() {
        getStoriesListJob?.cancel()
        getStoriesListJob = viewModelScope.launch {
            coreRepo.getStoriesWithLatLong(20, 100).onEach { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _errorMessage.value = resource.uiText
                        _isLoading.value = false
                        _storiesList.value = resource.data
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                        _storiesList.value = resource.data
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _storiesList.value = resource.data
                    }
                }
            }.launchIn(this)
        }
    }
}