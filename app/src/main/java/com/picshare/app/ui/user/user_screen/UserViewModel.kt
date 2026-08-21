package com.picshare.app.ui.user.user_screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.api.auth.AuthRepository
import com.picshare.app.data.repository.UserRepository
import com.picshare.app.ui.LoginActivity
import com.picshare.app.ui.theme.ButtonState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val authRepository: AuthRepository,
  val imageLoader: ImageLoader,
  @ApplicationContext private val context: Context
): ViewModel(){

  private val TAG = this.javaClass.simpleName

  private val _uiState = MutableStateFlow(UserUiState())
  val uiState = _uiState.asStateFlow()

  private val _buttonState = MutableStateFlow(ButtonState(text = "") )
  val buttonState = _buttonState.asStateFlow()

  init {
    _buttonState.update { it.copy(onClick = ::onClick) }
  }

  fun set(userId: String?) {
    Log.d(TAG, "set() called with userId: $userId")
    _uiState.update {
      it.copy(
        user = null,
        error = null,
        isFollowed = null,
        isMe = false,
        isLoading = true
      )
    }

    if (userId == null || userId == userRepository.currentUser.id) {
      getThisUser()
      _buttonState.update { it.copy(text = "Log Out") }
    } else {
      fetchUser(userId)
    }
  }

  fun fetchUser(id: String) {
    Log.d(TAG, "fetchUser() called with id: $id")
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }

      val userDeferred = async { userRepository.getUser(id) }
      val followsDeferred = async { userRepository.follows(id) }

      when (val result = userDeferred.await()) {
        is NetworkResult.Success -> {
          Log.d(TAG, "fetchUser: Successfully fetched user: ${result.data}")
          _uiState.update { it.copy(user = result.data, isMe = false) }
        }
        is NetworkResult.Error -> {
          Log.e(TAG, "fetchUser: Error fetching user: ${result.message}")
          _uiState.update { it.copy(error = result.message) }
        }
      }

      when (val result = followsDeferred.await()) {
        is NetworkResult.Success -> {
          Log.d(TAG, "fetchUser: Successfully fetched follows status: ${result.data}")
          _uiState.update { it.copy(isFollowed = result.data) }
          _buttonState.update { it.copy(text = if (result.data) "Unfollow" else "Follow") }
        }
        is NetworkResult.Error -> {
          Log.e(TAG, "fetchUser: Error fetching follows status: ${result.message}")
          _uiState.update { it.copy(error = result.message) }
        }
      }

      _uiState.update { it.copy(isLoading = false) }
    }
  }

  private fun onClick() {
    if(buttonState.value.isLoading) return
    if (uiState.value.isMe) logout()
    else if (uiState.value.isFollowed == true) unfollow() else follow()
  }

  fun follow(){
    viewModelScope.launch {
      val user = uiState.value.user ?: return@launch
      Log.d(TAG, "follow() called for user id: ${user.id}")
      _buttonState.update{it.copy(isLoading = true)}
      when (val result = userRepository.follow(user.id)){
        is NetworkResult.Success -> {
          Log.d(TAG, "follow: Successfully followed user ${user.id}")
          _uiState.update { it.copy(isFollowed = true) }
          _buttonState.update { it.copy(text = "Unfollow") }
        }
        is NetworkResult.Error -> Log.e(TAG, "follow: Error following user ${user.id}: ${result.message}")
      }
      _buttonState.update{it.copy(isLoading = false)}
    }
  }
  fun unfollow(){
    viewModelScope.launch {
      val user = uiState.value.user ?: return@launch
      Log.d(TAG, "unfollow() called for user id: ${user.id}")
      _buttonState.update{it.copy(isLoading = true)}
      when (val result = userRepository.unfollow(user.id)){
        is NetworkResult.Success -> {
          Log.d(TAG, "unfollow: Successfully unfollowed user ${user.id}")
          _uiState.update { it.copy(isFollowed = false) }
          _buttonState.update { it.copy(text = "Follow") }
        }
        is NetworkResult.Error -> Log.e(TAG, "unfollow: Error unfollowing user ${user.id}: ${result.message}")
      }
      _buttonState.update{it.copy(isLoading = false)}
    }
  }

  fun logout(){
    Log.d(TAG, "logout() called")
    viewModelScope.launch{
      authRepository.logout(context) }
  }
  private fun getThisUser(){
    val user = userRepository.currentUser
    _uiState.update {
      it.copy(
        user = user,
        isLoading = false,
        isMe = true,
      )
    }
  }
}
