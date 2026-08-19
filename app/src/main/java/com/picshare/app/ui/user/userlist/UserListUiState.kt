package com.picshare.app.ui.user.userlist

import com.picshare.app.data.model.UserModel

data class UserListUiState(
  val isLoading: Boolean = false,
  val error: String? = null,
  val hasMore: Boolean = false,
  val username: String = "",
  val currentOffset: Int = 0,
  val users: List<UserModel> = emptyList()
)
