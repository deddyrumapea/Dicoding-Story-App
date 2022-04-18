package com.romnan.dicodingstory.features.addStory.presentation

import android.location.Location
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.addStory.domain.model.JpegCamState
import com.romnan.dicodingstory.features.addStory.domain.model.NewStory
import com.romnan.dicodingstory.features.addStory.domain.repository.AddStoryRepository
import com.romnan.dicodingstory.features.addStory.presentation.model.AddStoryEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val addStoryRepository: AddStoryRepository
) : ViewModel() {

    private var _tempJpegUri: Uri? = null
    private val tempJpegUri get() = _tempJpegUri

    private var _jpegCamState = MutableLiveData<JpegCamState>()
    val jpegCamState: LiveData<JpegCamState> = _jpegCamState

    private val _storyPhoto = MutableLiveData<File>()
    val photoFile: LiveData<File> = _storyPhoto

    private val _isUploading = MutableLiveData<Boolean>()
    val isUploading: LiveData<Boolean> = _isUploading

    private val _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean> = _isUploaded

    private val _errorMessage = MutableLiveData<UIText>()
    val errorMessage: LiveData<UIText> = _errorMessage

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    fun onEvent(event: AddStoryEvent) {
        when (event) {
            is AddStoryEvent.UploadImage -> uploadStory(event.description)
            is AddStoryEvent.ImageCaptured -> setStoryPhotoToCapturedJpeg()
            is AddStoryEvent.LaunchCamera -> openJpegCam()
            is AddStoryEvent.ImageSelected -> setStoryPhotoToSelectedJpeg(event.selectedJpegUri)
            is AddStoryEvent.AddLocation -> setLocation(event.location)
        }
    }

    private fun setLocation(location: Location) {
        _location.value = location
    }

    private fun openJpegCam() {
        viewModelScope.launch {
            _jpegCamState.value = JpegCamState.Opening
            addStoryRepository.getNewTempJpegUri().let {
                _tempJpegUri = it
                _jpegCamState.value = JpegCamState.Opened(it)
            }
        }
    }

    private var setStoryPhotoToSelectedJpegJob: Job? = null
    private fun setStoryPhotoToSelectedJpeg(selectedJpegUri: Uri) {
        setStoryPhotoToSelectedJpegJob?.cancel()
        setStoryPhotoToSelectedJpegJob = viewModelScope.launch {
            _storyPhoto.value = addStoryRepository.findJpegByUri(selectedJpegUri)
        }
    }

    private var setStoryPhotoToCapturedJpegJob: Job? = null
    private fun setStoryPhotoToCapturedJpeg() {
        setStoryPhotoToCapturedJpegJob?.cancel()
        setStoryPhotoToCapturedJpegJob = viewModelScope.launch {
            _jpegCamState.value = JpegCamState.Closed
            _storyPhoto.value = tempJpegUri?.let { addStoryRepository.findJpegByUri(it) }
        }
    }

    private var uploadStory: Job? = null
    private fun uploadStory(description: String) {
        if (isUploading.value == true || isUploaded.value == true) {
            _errorMessage.value = UIText.StringResource(R.string.em_being_uploaded)
            return
        }

        val newStory = location.value?.let {
            NewStory(
                description = description,
                photo = photoFile.value,
                lat = it.latitude.toFloat(),
                lon = it.longitude.toFloat()
            )
        } ?: NewStory(
            description = description,
            photo = photoFile.value
        )

        uploadStory?.cancel()
        uploadStory = viewModelScope.launch {
            addStoryRepository.uploadStory(newStory).onEach { result ->
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
}
