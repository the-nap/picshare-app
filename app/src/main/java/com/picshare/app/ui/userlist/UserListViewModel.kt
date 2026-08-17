package com.picshare.app.ui.userlist

import androidx.lifecycle.ViewModel
import coil3.ImageLoader
import com.picshare.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
  private val userRepository: UserRepository,
  val imageLoader: ImageLoader
): ViewModel(){

  private val _uiState = MutableStateFlow(UserListUiState())
  val uiState = _uiState.asStateFlow()

}