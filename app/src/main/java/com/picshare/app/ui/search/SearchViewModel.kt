package com.picshare.app.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picshare.app.data.repository.PostRepository
import com.picshare.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor (
  private val postRepository: PostRepository,
  private val userRepository: UserRepository,
  savedStateHandle: SavedStateHandle
): ViewModel(){

  private val _uiState = MutableStateFlow(SearchUiState())
  private val uiState = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      val query = uiState
        .map {it.toSearch}
        .debounce(300.milliseconds)
        .distinctUntilChanged()

      val type = uiState
        .map{ it.searchType }
        .distinctUntilChanged()

      combine(query, type) {query, type ->
        query to type
      }.collect { (query, type) ->
        if(query.length > 2)
          search(query, type)
      }
    }
  }
  fun search(query: String, type: SearchType){
    when(type) {
      SearchType.USERS -> {}
      SearchType.TAGS -> {}
    }

  }
}
