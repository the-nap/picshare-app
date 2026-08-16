package com.picshare.app.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
): ViewModel() {

  private val _uiState = MutableStateFlow(GalleryUiState())
  val uiState = _uiState.asStateFlow()


  private var offset = 0
  private val max = 12

  fun initialize(key: String, toSearch: String){
    if(key.isEmpty() || toSearch.isEmpty())
      return
    if(key == uiState.value.key && toSearch == uiState.value.toSearch)
      return
    _uiState.update { it.copy(
      key = key,
      toSearch = toSearch,
      posts = emptyList(),
    ) }
    loadNextPage()
  }
  fun loadNextPage() {
    val state = _uiState.value
    if(
      state.isLoading ||
      !state.hasMore ||
      state.key.isBlank()
    ) return
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true, error = null) }
      try {
        val result = repository.getPosts(
          key = _uiState.value.key,
          toSearch = _uiState.value.toSearch,
          offset = offset,
          max = max
        )
        when (result){
          is NetworkResult.Success -> {
            _uiState.update{ it.copy(
              posts = it.posts + result.data,
              hasMore = result.data.size == max
            ) }
            offset++
          }
          is NetworkResult.Error -> {
            _uiState.update{ it.copy(error = result.message) }
          }
        }
      } finally {
        _uiState.update { it.copy(isLoading = false) }
      }
    }
  }
}