package com.picshare.app.ui.userlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
  private val userRepository: UserRepository,
  val imageLoader: ImageLoader
): ViewModel(){

  private val TAG = this.javaClass.simpleName
  private val _uiState = MutableStateFlow(UserListUiState())
  val uiState = _uiState.asStateFlow()

  private var offset = 0;
  private val max = 24


  fun initialize(username: String){
    if (username.isEmpty()) {
      offset = 0;
      return
    }
    fetchUsers(username)
  }

  fun fetchUsers(username: String){

    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = false, error = null) }
      try {
        when (val result = userRepository.contains(username, offset, max)){
          is NetworkResult.Success -> {
            Log.d(TAG, "Received ${result.data.size} users. Total users now: ${uiState.value.users.size + result.data.size}")
            _uiState.update{ it.copy(
              users = it.users + result.data,
              hasMore = result.data.size == max
            ) }
            offset++
          }
          is NetworkResult.Error -> {
            Log.e(TAG, "Error fetching users: ${result.message}")
            _uiState.update{ it.copy(error = result.message) }
          }
        }
      } finally {
        _uiState.update { it.copy(isLoading = false) }
      }
    }
  }
}