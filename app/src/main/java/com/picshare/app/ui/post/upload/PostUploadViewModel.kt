package com.picshare.app.ui.post.upload

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.data.model.PostModel
import com.picshare.app.data.repository.PostRepository
import com.picshare.app.data.repository.UserRepository
import com.picshare.app.ui.events.AppEvent
import com.picshare.app.ui.events.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
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
  private val postRepository: PostRepository,
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

  val errorsState: StateFlow<UploadFormErrors> = uiState
    .map { validate(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UploadFormErrors())

  val isValid: StateFlow<Boolean> = errorsState
    .map { it.isValid }
    .stateIn(viewModelScope, SharingStarted.Eagerly, false)

  fun updateFile(uri: Uri, size: Long) {
    _uiState.update { it.copy(uri = uri, sizeBytes = size) }
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
    viewModelScope.launch{
      _uiState.update { it.copy(isLoading = true) }
      when(val result = postRepository.upload(state.uri!!, state.post)){
        is NetworkResult.Success -> {
          eventBus.send(AppEvent.Message("Post uploaded succesfully"))
          reset()
        }
        is NetworkResult.Error -> {
          Log.e(TAG, "error: ${result.message}")
          eventBus.send(AppEvent.Message("An error occurred, try again."))
        }
      }
      _uiState.update { it.copy(isLoading = false) }
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