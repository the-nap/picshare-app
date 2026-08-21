package com.picshare.app.ui.user.settings

import android.net.Uri
import com.picshare.app.data.model.UserModel

data class SettingsUiState(
  val user: UserModel? = null,
  val newImageUri: Uri? = null,
  val newBio: String? = null,
  val isLoading: Boolean = false,
  val error: String? = null,
)
