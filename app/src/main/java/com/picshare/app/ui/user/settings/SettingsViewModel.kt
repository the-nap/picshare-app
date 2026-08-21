package com.picshare.app.ui.user.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import com.picshare.app.api.auth.AuthRepository
import com.picshare.app.data.model.UserModel
import com.picshare.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  val imageLoader: ImageLoader,
  private val userRepository: UserRepository,
  private val authRepository: AuthRepository
): ViewModel() {
  private val _uiState = MutableStateFlow(SettingsUiState())
  val uiState = _uiState.asStateFlow()

}