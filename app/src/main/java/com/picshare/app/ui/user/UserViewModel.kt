package com.picshare.app.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.api.auth.AuthRepository
import com.picshare.app.data.model.UserModel
import com.picshare.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val authRepository: AuthRepository,
  val imageLoader: ImageLoader
): ViewModel(){

}
