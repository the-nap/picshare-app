package com.picshare.app.ui.post.gallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.picshare.app.api.network.Util
import com.picshare.app.context_awareness.sensors.shake.ShakeDetector
import com.picshare.app.data.repository.PostRepository
import com.picshare.app.ui.events.AppEvent
import com.picshare.app.ui.events.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor (
  private val repository: PostRepository,
  private val eventBus: EventBus,
  private val shakeSensor: ShakeDetector,
  val imageLoader: ImageLoader
): ViewModel() {

  private val TAG = this.javaClass.simpleName
  private val _uiState = MutableStateFlow(GalleryUiState())
  val uiState = _uiState.asStateFlow()

  private val max = 12
  fun getMax() = max

  private val request = MutableSharedFlow<PostBatchRequest>(extraBufferCapacity = 1)

  init {
    shakeSensor.startListening()
    shakeSensor.setOnShakeListener {
      reload()
    }
    viewModelScope.launch{
      eventBus.events.filterIsInstance<AppEvent.PostDeleted>().collect{ event ->
       _uiState.update { state ->
         state.copy(posts = state.posts.filterNot { it.id == event.postId })
       }
      }
    }
    viewModelScope.launch {
      request
        .onEach { Log.d(TAG, "Request received: $it")}
        .collectLatest { request ->
          _uiState.update {
            it.copy(
              key = request.key,
              toSearch = request.toSearch,
              currentOffset = request.offset,
              posts = if(request.resetFlag) emptyList() else it.posts,
              error = null,
              isLoading = true,
              hasMore = true,
            )
          }
          fetchPosts()
        }
    }
  }

  override fun onCleared() {
    shakeSensor.stopListening()
  }

  fun set(key: String, toSearch: String){
    Log.d(TAG, "set: key='$key', toSearch='$toSearch'")
    val state = _uiState.value

    if(state.key == key && state.toSearch == toSearch && state.posts.isNotEmpty()){
      Log.d(TAG,"Already loaded, skipping")
      return
    }


    if((key != "feed") && (toSearch.isEmpty())){
      Log.d(TAG, "Nothing to search, skipping")
      return
    }

    request.tryEmit(
      PostBatchRequest(
        key = key,
        toSearch = toSearch,
        offset = 0,
        resetFlag = true
      )
    )
  }

  fun reload() {
    val state = _uiState.value
    request.tryEmit(
      PostBatchRequest(
        key = state.key,
        toSearch = state.toSearch,
        offset = 0,
        resetFlag = true
      )
    )
  }

  fun getNext(){
    val state = _uiState.value
    if(state.isLoading || !state.hasMore)
      return
    request.tryEmit(
      PostBatchRequest(
        key = state.key,
        toSearch = state.toSearch,
        offset = state.currentOffset + 1,
        resetFlag = false
      )
    )

  }

  private suspend fun fetchPosts(){
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
        is Util.NetworkResult.Success -> {
          Log.d(TAG, "Received ${result.data.size} posts. Total posts now: ${uiState.value.posts.size + result.data.size}")
          _uiState.update{ it.copy(
            posts = it.posts + result.data,
            hasMore = result.data.size == max,
            error = null
          ) }
        }
        is Util.NetworkResult.Error -> {
          Log.e(TAG, "Error fetching posts: ${result.message}")
          _uiState.update{ it.copy(error = result.message) }
        }
      }
    } finally {
      _uiState.update { it.copy(isLoading = false) }
    }
  }
}