package com.picshare.app.ui.user

import androidx.lifecycle.ViewModel
import com.picshare.app.data.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SelectedUserViewModel @Inject constructor(): ViewModel() {

  private val _selectedUser = MutableStateFlow<UserModel?>(null)
  val selectedUser = _selectedUser.asStateFlow()

  fun setUser(user: UserModel?){
    _selectedUser.value = user
  }

}