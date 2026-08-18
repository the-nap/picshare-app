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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor (
  private val repository: PostRepository,
  val imageLoader: ImageLoader
): ViewModel() {

  private val TAG = this.javaClass.simpleName
  private val _uiState = MutableStateFlow(GalleryUiState())
  val uiState = _uiState.asStateFlow()

  private val max = 12

  init {
    viewModelScope.launch {
      uiState
        .distinctUntilChanged { old, new ->
          old.key == new.key && old.toSearch == new.toSearch && old.currentOffset == new.currentOffset
        }
        .filter { state -> !state.isLoading }
        .filter { state -> state.hasMore }
        .collectLatest {
          _uiState.update { it.copy(isLoading = true) }
          loadNextPage()
        }
    }
  }

  fun set(key: String, toSearch: String){
    Log.d(TAG, "set: key='$key', toSearch='$toSearch'")

    if((key != "feed") && (toSearch.isEmpty())){
      Log.d(TAG, "Nothing to search, skipping")
      return
    }

    Log.d(TAG, "set: Resetting state and loading first page")
    _uiState.update { it.copy(
      key = key,
      toSearch = toSearch,
      posts = emptyList(),
      hasMore = true,
      isLoading = false,
      error = null,
      currentOffset = 0
    ) }
  }

  fun getNext(){
    _uiState.update {
      if(!it.isLoading && it.hasMore)
        it.copy( currentOffset = it.currentOffset + 1)
      else it
    }
  }

  private suspend fun loadNextPage(){
    val state = uiState.value
    Log.d(TAG, "loadNextPage: Starting request (key='${state.key}', search='${state.toSearch}', offset=$state.currentOffset)")
    _uiState.update { it.copy(isLoading = true, error = null) }
    try {
      val result = repository.getPosts(
        key = state.key,
        toSearch = state.toSearch,
        offset = state.currentOffset,
        max = max,
      )
      when (result){
        is NetworkResult.Success -> {
          Log.d(TAG, "Received ${result.data.size} posts. Total posts now: ${uiState.value.posts.size + result.data.size}")
          _uiState.update{ it.copy(
            posts = it.posts + result.data,
            hasMore = result.data.size == max
          ) }
        }
        is NetworkResult.Error -> {
          Log.e(TAG, "Error fetching posts: ${result.message}")
          _uiState.update{ it.copy(error = result.message) }
        }
      }
    } finally {
      _uiState.update { it.copy(isLoading = false) }
    }
  }
}