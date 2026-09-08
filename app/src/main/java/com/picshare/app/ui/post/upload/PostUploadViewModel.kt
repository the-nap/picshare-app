package com.picshare.app.ui.post.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.google.android.gms.location.FusedLocationProviderClient
import com.picshare.app.context_awareness.location.LocationManager
import com.picshare.app.data.model.PostModel
import com.picshare.app.data.repository.UserRepository
import com.picshare.app.ui.events.AppEvent
import com.picshare.app.ui.events.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostUploadViewModel @Inject constructor(
  @ApplicationContext private val appContext: Context,
  private val fusedLocationClient: FusedLocationProviderClient,
  private val userRepository: UserRepository,
  private val eventBus: EventBus,
  val imageLoader: ImageLoader
): ViewModel() {

  private val TAG = this.javaClass.simpleName

  private val tagsPattern = Regex("^[a-zA-Z\\s]+$")
  private val maxSize: Long = 1000*1000*10

  private val _uiState = MutableStateFlow(ImageUploadState(
    post = PostModel(
      id = "",
      userId = userRepository.currentUser.id,
      tags = "",
      description = "",
      likesNumber = 0
    )
  ))
  val uiState = _uiState.asStateFlow()

  private val _address = MutableStateFlow<String?>(null)
  val address: StateFlow<String?> = _address.asStateFlow()

  val errorsState: StateFlow<UploadFormErrors> = uiState
    .map { validate(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UploadFormErrors())

  val isValid: StateFlow<Boolean> = errorsState
    .map { it.isValid }
    .stateIn(viewModelScope, SharingStarted.Eagerly, false)

  fun updateFile(uri: Uri, size: Long) {
    _uiState.update { it.copy(uri = uri, sizeBytes = size) }
  }

  fun onLocationPermissionResult(granted: Boolean){
    if(granted) fetchLocation()
  }

  init{
    viewModelScope.launch {
      UploadStatusBus.status.collect{
        status ->
        _uiState.update {
          it.copy(isLoading = status is UploadStatus.InProgress)
        }
        when (status) {
          is UploadStatus.Success -> {
            eventBus.send(AppEvent.Message("Post uploaded succesfully"))
            UploadStatusBus.update(UploadStatus.Idle)
            reset()
        }
          is UploadStatus.Error -> {
            eventBus.send(AppEvent.Message("Post upload failed"))
            UploadStatusBus.update(UploadStatus.Idle)
            reset()
          }
          else -> {}
        }
      }
    }
  }
  fun fetchLocation(){
    viewModelScope.launch {
      _address.value = LocationManager.fetchLocation(fusedLocationClient)
      if(address.value != null)
        onTagsChange(address.value!!)
    }
  }
  private fun validate(state: ImageUploadState): UploadFormErrors {
    val fileError = when {
      state.uri == null -> "Please upload an image"
      else -> null
    }

    val sizeError = if (state.sizeBytes >= maxSize){
       "File is too big, max size is 10MB"
    } else {
      null
    }

    val descriptionError = if (state.post.description.length > 140) {
      "Max length is 140 characters"
    } else {
      null
    }

    val tagsError = when {
      state.post.tags.isNotEmpty() && !tagsPattern.matches(state.post.tags) ->
        "Only letters are allowed"
      state.post.tags.length > 25 -> "Max length is 25 characters"
      else -> null
    }

    return UploadFormErrors(
      file = fileError,
      description = descriptionError,
      size = sizeError,
      tags = tagsError
    )
  }

  private fun reset() {
    _uiState.update {
      it.copy(
        uri = null,
        post = PostModel(
          id = "",
          userId = userRepository.currentUser.id,
          description = "",
          tags = "",
          likesNumber = 0
        )
      )
    }
  }

  fun onSubmit(){
    val state = uiState.value
    if (!isValid.value) return
    Intent(appContext, UploadService::class.java).also {
      it.putExtra("uri", state.uri)
      it.putExtra("sizeBytes", state.sizeBytes)
      it.putExtra("post", state.post)
      it.action = UploadService.Actions.UPLOAD.toString()
      appContext.startForegroundService(it)
    }
  }

  fun onTagsChange(tags: String){
    _uiState.update {
      it.copy(post = it.post.copy(tags = tags))
    }
  }

  fun onDescriptionChange(description: String){
    _uiState.update {
      it.copy(post = it.post.copy(description = description))
    }
  }
}