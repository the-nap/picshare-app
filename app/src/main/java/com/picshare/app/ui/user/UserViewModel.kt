package com.picshare.app.ui.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picshare.app.auth.AuthRepository
import com.picshare.app.user.UserModel
import kotlinx.coroutines.launch

class UserViewModel (
  private val userRepository: UserRepository,
  private val authRepository: AuthRepository
): ViewModel(){

  lateinit var currentUser: UserModel
  private set

  init {
    viewModelScope.launch {
      currentUser = userRepository.getUser(
        authRepository.currentUserId!!
      )
    }
  }


}