package com.picshare.app.ui.post.post

import com.picshare.app.data.model.PostModel
import com.picshare.app.data.model.UserModel

data class PostUiState(
  val post: PostModel? = null,
  val user: UserModel? = null,
  val isOwned: Boolean = false,
  val isLoading: Boolean = false,
  val error: String? = null
)
