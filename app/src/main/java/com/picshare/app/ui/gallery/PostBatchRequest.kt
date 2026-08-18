package com.picshare.app.ui.gallery

data class PostBatchRequest(
  val key: String,
  val toSearch: String,
  val resetFlag: Boolean,
  val offset: Int
)
