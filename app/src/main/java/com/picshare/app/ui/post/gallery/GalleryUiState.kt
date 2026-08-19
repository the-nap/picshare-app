package com.picshare.app.ui.post.gallery

import com.picshare.app.data.model.PostModel

data class GalleryUiState(
  val isLoading: Boolean = false,
  val error: String? = null,
  val hasMore: Boolean = false,
  val posts: List<PostModel> = listOf(),
  val key: String = "",
  val toSearch: String = "",
  val currentOffset: Int = 0
)
