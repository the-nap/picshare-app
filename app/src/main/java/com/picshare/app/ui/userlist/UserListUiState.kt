package com.picshare.app.ui.userlist

import com.picshare.app.data.model.UserModel

data class UserListUiState(
  val isLoading: Boolean = false,
  val error: String? = null,
  val users: List<UserModel> = emptyList()
)
