package com.picshare.app.ui.post.gallery

data class PostBatchRequest(
  val key: String,
  val toSearch: String,
  val resetFlag: Boolean,
  val offset: Int
)
