package com.picshare.app.ui.post.post

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.auth.AuthRepository
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.data.repository.PostRepository
import com.picshare.app.data.repository.UserRepository
import com.picshare.app.ui.theme.LikeButtonState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val authRepository: AuthRepository,
  private val postRepository: PostRepository,
  val imageLoader: ImageLoader
): ViewModel(){

  private val TAG = this.javaClass.simpleName
  private val _uiState = MutableStateFlow(PostUiState())
  val uiState = _uiState.asStateFlow()

  private val _buttonState = MutableStateFlow(LikeButtonState())
  val buttonState = _buttonState.asStateFlow()

  private val _showDeleteDialog = MutableStateFlow(false)
  val showDeleteDialog = _showDeleteDialog.asStateFlow()

  init {
    _buttonState.update { it.copy(onClick = ::onLikeClick) }
  }

  fun showDeleteDialog() {
    _showDeleteDialog.value = true
  }

  fun hideDeleteDialog() {
    _showDeleteDialog.value = false
  }

  fun confirmDelete() {
    _showDeleteDialog.value = false
    viewModelScope.launch {
      when (val result =  postRepository.delete(uiState.value.post!!.id)){
        is NetworkResult.Success -> _uiState.update { it.copy( isDeleted = true ) }
        is NetworkResult.Error -> Log.e(TAG, result.message)
      }
    }
  }

  fun addLike(){
    viewModelScope.launch {
      val post = uiState.value.post ?: return@launch
      _buttonState.update { it.copy(isLoading = true) }
      when (val result = postRepository.like(post.id)) {
        is NetworkResult.Success -> _buttonState.update { it.copy( likesNumber = it.likesNumber + 1, isLiked = true ) }
        is NetworkResult.Error -> Log.e(TAG, "Failed to add like: ${result.message}")
      }
      _buttonState.update { it.copy(isLoading = false) }
    }
  }

  fun removeLike(){
    viewModelScope.launch {
      val post = uiState.value.post ?: return@launch
      _buttonState.update { it.copy(isLoading = true) }
      when (val result = postRepository.like(post.id)) {
        is NetworkResult.Success -> _buttonState.update { it.copy( likesNumber = it.likesNumber - 1, isLiked = false ) }
        is NetworkResult.Error -> Log.e(TAG, "Failed to remove like: ${result.message}")
      }
      _buttonState.update { it.copy(isLoading = false) }
    }

  }
  fun load(postId: String) {
    viewModelScope.launch {
      _buttonState.update { it.copy(isLoading = true) }
      when (val result = postRepository.isLiked(postId)) {
        is NetworkResult.Success -> _buttonState.update {
          it.copy(isLiked = result.data, onClick = ::onLikeClick)
        }
        is NetworkResult.Error -> Log.e(TAG, "Failed to fetch like status: ${result.message}")
      }
      _buttonState.update { it.copy(isLoading = false) }
    }
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      try {
        when (val result = postRepository.getPost(postId)) {
          is NetworkResult.Success -> {
            val post = result.data
            _uiState.update {
              it.copy(
                post = post,
              )
            }
            _buttonState.update { it.copy(likesNumber = post.likesNumber.toInt()) }
            fetchUser(post.userId)
          }
          is NetworkResult.Error -> _uiState.update { it.copy(error = result.message) }
        }
      } finally {
        _uiState.update { it.copy(isLoading = false) }
      }
    }
  }

  private fun onLikeClick() {
    if(buttonState.value.isLoading) return
    if (_buttonState.value.isLiked) removeLike() else addLike()
  }

  private suspend fun fetchUser(userId: String){
    when (val userResult = userRepository.getUser(userId)) {
      is NetworkResult.Success -> {
        _uiState.update {
          it.copy(
            user = userResult.data,
            isOwned = userResult.data.id == authRepository.currentUserId,
          )
        }
      }
      is NetworkResult.Error -> {
        Log.e(TAG, "Failed to fetch user: ${userResult.message}")
        _uiState.update { it.copy(error = userResult.message) }
      }
    }
  }
}