package com.picshare.app.ui.gallery

import com.picshare.app.data.model.PostModel

data class GalleryUiState(
  val isLoading: Boolean = false,
  val error: String? = null,
  val hasMore: Boolean = true,
  val posts: List<PostModel> = listOf(),
  val key: String = "feed",
  val toSearch: String = "",
)
