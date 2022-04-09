package com.romnan.dicodingstory.features.addStory.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.addStory.domain.model.NewStory
import com.romnan.dicodingstory.features.addStory.domain.repository.AddStoryRepository
import com.romnan.dicodingstory.features.addStory.presentation.model.AddStoryEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val repository: AddStoryRepository
) : ViewModel() {

    private var _tempJpegUri: Uri? = null
    val tempJpegUri: Uri? get() = _tempJpegUri

    private val _storyPhoto = MutableLiveData<File>()
    val photoFile: LiveData<File> = _storyPhoto

    private val _isUploading = MutableLiveData<Boolean>()
    val isUploading: LiveData<Boolean> = _isUploading

    private val _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean> = _isUploaded

    private val _errorMessage = MutableLiveData<UIText>()
    val errorMessage: LiveData<UIText> = _errorMessage

    fun onEvent(event: AddStoryEvent) {
        when (event) {
            is AddStoryEvent.UploadImage -> uploadStory(event.description)
            is AddStoryEvent.ImageCaptured -> setStoryPhotoToCapturedJpeg()
            is AddStoryEvent.OpenCamera -> refreshTempJpegUri()
            is AddStoryEvent.ImageSelected -> setStoryPhotoToSelectedJpeg(event.selectedJpegUri)
        }
    }

    private var setStoryPhotoToSelectedJpegJob: Job? = null
    private fun setStoryPhotoToSelectedJpeg(selectedJpegUri: Uri) {
        setStoryPhotoToSelectedJpegJob?.cancel()
        setStoryPhotoToSelectedJpegJob = viewModelScope.launch {
            _storyPhoto.value = repository.findJpegByUri(selectedJpegUri)
        }
    }

    private var setStoryPhotoToCapturedJpegJob: Job? = null
    private fun setStoryPhotoToCapturedJpeg() {
        setStoryPhotoToCapturedJpegJob?.cancel()
        setStoryPhotoToCapturedJpegJob = viewModelScope.launch {
            _storyPhoto.value = tempJpegUri?.let { repository.findJpegByUri(it) }
        }
    }

    private var uploadStory: Job? = null
    private fun uploadStory(description: String) {
        if (isUploading.value == true || isUploaded.value == true) {
            _errorMessage.value = UIText.StringResource(R.string.em_being_uploaded)
            return
        }

        val newStory = NewStory(
            description = description,
            photo = photoFile.value ?: return
        )

        uploadStory?.cancel()
        uploadStory = viewModelScope.launch {
            repository.uploadStory(newStory).onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _errorMessage.value = result.uiText
                        _isUploading.value = false
                        _isUploaded.value = false
                    }
                    is Resource.Loading -> {
                        _isUploading.value = true
                        _isUploaded.value = false
                    }
                    is Resource.Success -> {
                        _isUploading.value = false
                        _isUploaded.value = true
                    }
                }
            }.launchIn(this)
        }
    }

    // TODO: change this runblocking
    private fun refreshTempJpegUri() = runBlocking {
        _tempJpegUri = repository.getNewTempJpegUri()
    }
}
