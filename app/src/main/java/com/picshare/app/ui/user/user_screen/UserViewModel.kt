package com.picshare.app.ui.user.user_screen

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.api.auth.AuthRepository
import com.picshare.app.data.model.UserModel
import com.picshare.app.data.repository.UserRepository
import com.picshare.app.ui.LoginActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

  private val _buttonState = MutableStateFlow(ButtonState(onClick = { logout() }, text = "Log Out"))
  val buttonState = _buttonState.asStateFlow()

  fun set(userId: String?){
    if(userId == null || userId ==authRepository.currentUserId)
      getThisUser()
    else {
      fetchUser(userId)
      assignButton()

    }
  }

  fun fetchUser(id: String){
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      when(val result = userRepository.getUser(id)){
        is NetworkResult.Success -> {
            _uiState.update { it.copy(
              user = result.data,
              isLoading = false
            ) }
        }
        is NetworkResult.Error -> {
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
  fun assignButton(){
    viewModelScope.launch {
      when(val result = userRepository.follows(uiState.value.user!!.id)){
        is NetworkResult.Success -> {
          if(result.data)
            _buttonState.update { it.copy(
              text = "Unfollow",
              onClick = { unfollow() }
            ) }
          else
            _buttonState.update { it.copy(
              text = "Follow",
              onClick = { follow() }
            ) }
        }
        is NetworkResult.Error -> return@launch
      }

    }
  }

  fun follow(){
    viewModelScope.launch {
      userRepository.follow(uiState.value.user!!.id)
    }
  }
  fun unfollow(){
    viewModelScope.launch {
      userRepository.unfollow(uiState.value.user!!.id)
    }
  }

  fun logout(){
    viewModelScope.launch{
      authRepository.logout(
        context,
        Intent(context, LoginActivity::class.java))
    }
  }
  private fun getThisUser(){
    if(authRepository.currentUserId == null)
      return
    viewModelScope.launch{
      _uiState.update {
        it.copy( isLoading = true )
      }
      when(val result = userRepository.getUser(authRepository.currentUserId!!)){
        is NetworkResult.Success ->{
          _uiState.update {
            it.copy(
              user = result.data,
              isLoading = false
            )
          }
            _buttonState.update {
              it.copy(
                onClick = { logout() },
                text = "Log Out",
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
