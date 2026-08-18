package com.picshare.app.ui.user

import com.picshare.app.data.model.UserModel

data class UserUiState(
  val isLoading: Boolean = false,
  val error: String? = null,
  val user: UserModel? = null,
  val isMe: Boolean = false
)
