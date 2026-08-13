package com.picshare.app.post

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picshare.app.api.network.Util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor (
  private val repository: PostRepository,
  savedStateHandle: SavedStateHandle
): ViewModel() {

  private val key: String = savedStateHandle.get<String>("key") ?: "feed"
  private val toSearch: String? = savedStateHandle.get<String>("toSearch")

  private var offset = 0
  private val max = 12
  var posts = mutableStateListOf<PostModel>()

  private val _errorEvents = Channel<String>()
  val errorEvents = _errorEvents.receiveAsFlow()

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
          val result = repository.getPosts(
            key = key,
            toSearch = toSearch,
            offset = offset,
            max = max
          )
        when (result){
          is NetworkResult.Success -> {
            posts.addAll(result.data)
            hasMore = result.data.size < max
            offset++
          }
          is NetworkResult.Error -> {
            _errorEvents.send(result.message)
          }
        }
      } finally {
        isLoading = false
      }
    }
  }
}