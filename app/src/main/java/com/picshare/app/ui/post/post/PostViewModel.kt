package com.picshare.app.ui.post.post

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.auth.AuthRepository
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.data.repository.PostRepository
import com.picshare.app.data.repository.UserRepository
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

  fun load(postId: String) {
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
            fetchUser(post.userId)
          }
          is NetworkResult.Error -> _uiState.update { it.copy(error = result.message) }
        }
      }finally {
        _uiState.update { it.copy(isLoading = false) }
      }
    }
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