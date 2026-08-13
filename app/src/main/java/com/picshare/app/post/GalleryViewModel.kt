package com.picshare.app.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor (
  private val repository: PostRepository,
  private val key: String,
  private val toSearch: String?
): ViewModel() {

  private var offset = 0
  private val max = 12
  var posts = mutableStateListOf<PostModel>()

  var isLoading by mutableStateOf(false)
  private set

  var hasMore by mutableStateOf(true)
  private set

  init {
    loadNextPage()
  }

  fun noPosts(): Boolean =
    posts.isEmpty()

  fun loadNextPage() {
    if(isLoading || !hasMore)
      return
    viewModelScope.launch {
      isLoading = true
      try {
          val newPosts = repository.getPosts(
            key = key,
            toSearch = toSearch,
            offset = offset,
            max = max
          )
        posts.addAll(newPosts)
        hasMore = newPosts.size < max
        offset++
      } finally {
        isLoading = false
      }
    }
  }
}