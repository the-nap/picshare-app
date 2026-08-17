package com.picshare.app.ui.gallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor (
  private val repository: PostRepository,
  val imageLoader: ImageLoader
): ViewModel() {

  private val _uiState = MutableStateFlow(GalleryUiState())
  val uiState = _uiState.asStateFlow()

  private var offset = 0
  private val max = 12

  fun initialize(key: String?, toSearch: String?){
    val newKey = key ?: "feed"
    val newSearch = toSearch ?: ""

    Log.d("GalleryViewModel", "initialize: key='$newKey', toSearch='$newSearch'")

    if ((newKey == uiState.value.key) && (newSearch == uiState.value.toSearch) && uiState.value.posts.isNotEmpty()) {
      Log.d("GalleryViewModel", "initialize: Already initialized with these values, skipping")
      return
    }

    if((newKey != "feed") && (newSearch.isEmpty())){
      Log.d("GalleryViewModel", "Nothing to search, skipping")
      return
    }

    Log.d("GalleryViewModel", "initialize: Resetting state and loading first page")
    offset = 0
    _uiState.update { it.copy(
      key = newKey,
      toSearch = newSearch,
      posts = emptyList(),
      hasMore = true
    ) }
    loadNextPage()
  }
  fun loadNextPage() {
    val state = _uiState.value
    if (state.isLoading) {
      Log.d("GalleryViewModel", "loadNextPage: Already loading, skipping")
      return
    }
    if (!state.hasMore) {
      Log.d("GalleryViewModel", "loadNextPage: No more items, skipping")
      return
    }
    if (state.key.isBlank()) {
      Log.d("GalleryViewModel", "loadNextPage: Key is blank, skipping")
      return
    }

    Log.d("GalleryViewModel", "loadNextPage: Starting request (key='${state.key}', search='${state.toSearch}', offset=$offset)")
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true, error = null) }
      try {
        val result = repository.getPosts(
          key = _uiState.value.key,
          toSearch = _uiState.value.toSearch,
          offset = offset,
          max = max,
        )
        when (result){
          is NetworkResult.Success -> {
            Log.d("GalleryViewModel", "Received ${result.data.size} posts. Total posts now: ${uiState.value.posts.size + result.data.size}")
            _uiState.update{ it.copy(
              posts = it.posts + result.data,
              hasMore = result.data.size == max
            ) }
            offset++
          }
          is NetworkResult.Error -> {
            Log.e("GalleryViewModel", "Error fetching posts: ${result.message}")
            _uiState.update{ it.copy(error = result.message) }
          }
        }
      } finally {
        _uiState.update { it.copy(isLoading = false) }
      }
    }
  }
}