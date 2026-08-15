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

  init {
    viewModelScope.launch {
      _query
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .filter{ it.length > 3 }
        .collectLatest { searchQuery ->
          search(searchQuery)
        }
    }
  }

  fun onQueryChange(value: String){
    _query.value = value
  }

  fun search(query: String){

  }
}