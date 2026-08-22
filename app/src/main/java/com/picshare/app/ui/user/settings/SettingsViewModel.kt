package com.picshare.app.ui.user.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.memory.MemoryCache
import com.picshare.app.BuildConfig
import com.picshare.app.api.auth.AuthRepository
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.data.repository.UserRepository
import com.picshare.app.ui.events.AppEvent
import com.picshare.app.ui.events.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  val imageLoader: ImageLoader,
  private val userRepository: UserRepository,
  private val authRepository: AuthRepository,
  private val eventBus: EventBus,
  @ApplicationContext private val context: Context
): ViewModel() {
  private val TAG = this.javaClass.simpleName
  private val _uiState = MutableStateFlow(SettingsUiState())
  val uiState = _uiState.asStateFlow()

  private val _showDeleteDialog = MutableStateFlow(false)
  val showDeleteDialog = _showDeleteDialog.asStateFlow()

  init {
    _uiState.update{
      it.copy(
        user = userRepository.currentUser,
      )
    }
  }

  fun getImage(): Uri {
    val state = uiState.value
    if(state.newImageUri != null)
      return state.newImageUri
    return "${ BuildConfig.AVATAR_URL }/${state.user?.id}".toUri()
  }

  fun getBio(): String?{
    val state = uiState.value
    if(state.newBio != null)
      return state.newBio
    return state.user?.bio
  }

  fun onImageSelected(uri: Uri){
    _uiState.update {
      it.copy(
        newImageUri = uri
      )
    }
    val state = uiState.value
    Log.d(TAG, "fromDisk: ${imageLoader.diskCache?.remove(state.user!!.id).toString()}")
    Log.d(TAG, "fromMemory: ${imageLoader.memoryCache?.remove(MemoryCache.Key(state.user!!.id))}")
  }
  fun onBioChange(bio: String){
    _uiState.update { it.copy(newBio = bio) }
  }

  fun onSubmit(){
    viewModelScope.launch{
      _uiState.update { it.copy(isLoading = true) }
      val state = uiState.value
      when(val result = userRepository.upload(state.newImageUri, state.newBio)){
        is NetworkResult.Success -> {
          userRepository.refreshCurrentUser(state.user!!.id)
          eventBus.send(AppEvent.Message("User updated correctly"))
        }
        is NetworkResult.Error -> Log.e(TAG, "error: ${result.message}")
      }
      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun showDeleteDialog() {
    _showDeleteDialog.value = true
  }

  fun hideDeleteDialog() {
    _showDeleteDialog.value = false
  }

  fun confirmDelete() {
    _showDeleteDialog.value = false
    viewModelScope.launch {
      when (val result =  userRepository.delete()){
        is NetworkResult.Success -> {
          eventBus.send(AppEvent.Message("User deleted correctly. Logging out..."))
          authRepository.logout(context)
        }
        is NetworkResult.Error -> Log.e(TAG, result.message)
      }
    }
  }
}