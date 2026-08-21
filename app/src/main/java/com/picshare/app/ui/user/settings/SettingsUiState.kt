package com.picshare.app.ui.user.settings

import com.picshare.app.data.model.UserModel

data class SettingsUiState(
  val user: UserModel? = null,
  val isLoading: Boolean = false,
  val error: String? = null
)
