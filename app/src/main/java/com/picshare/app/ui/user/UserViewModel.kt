package com.picshare.app.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.auth.AuthRepository
import com.picshare.app.user.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UserUiState {
  object Loading : UserUiState
  data class Success(val user: UserModel) : UserUiState
  data class Error(val message: String) : UserUiState
}

class UserViewModel(
  private val userRepository: UserRepository,
  private val authRepository: AuthRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
  val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

  init {
    fetchCurrentUser()
  }

  fun fetchCurrentUser() {
    val userId = authRepository.currentUserId
    if (userId == null) {
      _uiState.value = UserUiState.Error("User not logged in")
      return
    }

    viewModelScope.launch {
      _uiState.value = UserUiState.Loading
      when (val result = userRepository.getUser(userId)) {
        is NetworkResult.Success -> {
          _uiState.value = UserUiState.Success(result.data)
        }
        is NetworkResult.Error -> {
          _uiState.value = UserUiState.Error(result.message)
        }
      }
    }
  }
}