package com.picshare.app.ui.user.userlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
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

  private val request = MutableSharedFlow<UserListRequest>(extraBufferCapacity = 1)

  init {
    viewModelScope.launch {
      request
        .onEach{ Log.d(TAG, "Request received: $it")}
        .filter { it.username.isNotEmpty() }
        .collectLatest { request ->
          Log.d(TAG, "Collecting request: $request")
          _uiState.update { it.copy(
            error = null,
            isLoading = true,
            username = request.username,
            hasMore = true,
            users = if(request.resetFlag) emptyList() else it.users,
            currentOffset = request.currentOffset
          ) }
          fetchUsers()
        }
    }
  }


  fun set(username: String){
    if (username.isEmpty()) {
      return
    }
    if(username == _uiState.value.username)
      return
    Log.d(TAG, "Set: $username")
    request.tryEmit(
      UserListRequest(
        username = username,
        currentOffset = 0,
        resetFlag = true
      )
    )
  }

  fun getNext() {
    val state = _uiState.value
    if(state.isLoading || !state.hasMore)
      return
    request.tryEmit(
      UserListRequest(
        username = state.username,
        currentOffset = state.currentOffset + 1,
        resetFlag = false
      )
    )
  }

  private val max = 24
  private suspend fun fetchUsers(){
    val state = uiState.value
    Log.d(TAG, "fetchUsers: ${state.username}, ${state.currentOffset}")
    try{
      when (val result = userRepository.contains(state.username, state.currentOffset, max)) {
        is NetworkResult.Success -> {
          Log.d(
            TAG,
            "Received ${result.data.size} users. Total users now: ${uiState.value.users.size + result.data.size}"
          )
          _uiState.update {
            it.copy(
              users = it.users + result.data,
              hasMore = result.data.size == max,
              error = null,
            )
          }
        }

        is NetworkResult.Error -> {
          Log.e(TAG, "Error fetching users: ${result.message}")
          _uiState.update { it.copy(error = result.message) }
        }
      }
    } finally {
      _uiState.update { it.copy(isLoading = false) }
    }
  }
}