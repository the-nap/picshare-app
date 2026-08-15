package com.picshare.app.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picshare.app.post.PostRepository
import com.picshare.app.ui.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
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

  private val _query = MutableStateFlow<String>("")
  val query = _query.asStateFlow()

  private val _searchType = MutableStateFlow<SearchType>(SearchType.USERS)
  val searchType = _searchType.asStateFlow()

  init {
    viewModelScope.launch {
      combine(
        _query,
        _searchType
      ){ query, type ->
        query to type
      }
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .filter{ (query, _) -> query.length > 3 }
        .collectLatest { (query, type) ->
          search(query, type)
        }

    }
  }

  fun onQueryChange(value: String){
    _query.value = value
  }

  fun changeType(type: SearchType){
    _searchType.value = type
  }

  fun search(query: String, type: SearchType){
    when(type) {
      SearchType.USERS -> {}
      SearchType.TAGS -> {}
    }

  }
}

enum class SearchType {
  USERS,
  TAGS
}
