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
  val imageLoader: ImageLoader
): ViewModel() {

  private val TAG = this.javaClass.simpleName

  private val maxFileSize = 1000L * 1000 * 10
  private val tagsPattern = Regex("^[a-zA-Z\\s]+$")

  private val _uiState = MutableStateFlow(ImageUploadModel(
    post = PostModel(
      id = "",
      userId = userRepository.currentUser.id,
      tags = "",
      description = "",
      likesNumber = 0
    )
  ))
  val uiState = _uiState.asStateFlow()

  val formErrors: StateFlow<UploadFormErrors> = _uiState
    .map{ validate(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UploadFormErrors())

  val isValid: StateFlow<Boolean> = formErrors
    .map{ it.isValid }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

  fun updateFile(uri: Uri) {
    _uiState.update { it.copy(uri = uri) }
  }

  fun updateDescription(description: String) {
    _uiState.update { it.copy(post = it.post.copy(description = description)) }
  }

  fun updateTags(tags: String) {
    _uiState.update { it.copy(post = it.post.copy(tags = tags)) }
  }

  private fun validate(model: ImageUploadModel): UploadFormErrors {
    val fileError = when {
      model.uri == null -> "Please upload an image"
      else -> null
    }

    val descriptionError = if (model.post.description.length > 140) {
      "Max length is 140 characters"
    } else {
      null
    }

    val tagsError = when {
      model.post.tags.isNotEmpty() && !tagsPattern.matches(model.post.tags) ->
        "Only letters are allowed"
      model.post.tags.length > 25 -> "Max length is 25 characters"
      else -> null
    }

    return UploadFormErrors(
      file = fileError,
      description = descriptionError,
      tags = tagsError
    )
  }

  fun getImage(): Uri? {
    val state = uiState.value
    if(state.uri != null)
      return state.uri
    return null
  }

  fun onSubmit(){
    val state = uiState.value
    if (state.uri == null) return
    viewModelScope.launch{
      _uiState.update { it.copy(isLoading = true) }
      when(val result = postRepository.upload(state.uri, state.post)){
        is NetworkResult.Success -> {}
        is NetworkResult.Error -> Log.e(TAG, "error: ${result.message}")
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