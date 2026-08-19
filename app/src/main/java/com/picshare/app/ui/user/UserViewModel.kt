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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val authRepository: AuthRepository,
  val imageLoader: ImageLoader
): ViewModel(){

  private val TAG = this.javaClass.simpleName

  private val _uiState = MutableStateFlow(UserUiState())
  val uiState = _uiState.asStateFlow()

  fun set(user: UserModel?){
    if(user == null)
      getThisUser()
    else _uiState.update { it.copy( user = user) }
  }

  fun follow(){

  }
  fun logout(){

  }
  private fun getThisUser(){
    if(authRepository.currentUserId == null)
      return
    viewModelScope.launch{
      _uiState.update {
        it.copy( isLoading = true )
      }
      when(val result = userRepository.getUser(authRepository.currentUserId!!.split(":")[2])){
        is NetworkResult.Success ->{
          _uiState.update {
            it.copy(
              user = result.data,
              isLoading = false
            )
          }
        }
        is NetworkResult.Error ->
          _uiState.update {
            it.copy(
              error = result.message,
              isLoading = false
            )
          }
      }
    }
  }
}
