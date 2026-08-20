package com.picshare.app.ui.theme

data class ButtonState(
  val onClick: () -> Unit = {},
  val isLoading: Boolean = false,
  val text: String = ""
)

data class LikeButtonState(
  val onClick: () -> Unit = {},
  val isLiked: Boolean = false,
  val likesNumber: Int = 0,
  val isLoading: Boolean = false,
)
