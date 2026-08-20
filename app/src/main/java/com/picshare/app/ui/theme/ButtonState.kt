package com.picshare.app.ui.theme

import androidx.compose.runtime.Composable

data class ButtonState(
  val onClick: () -> Unit = {},
  val aspect: @Composable () -> Unit = {}
)