package com.picshare.app.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picshare.app.data.repository.PostRepository
import com.picshare.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SearchViewModel (
): ViewModel(){

  private val _uiState = MutableStateFlow(SearchUiState())
  val uiState = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      val query = uiState
        .map {it.typedText}
        .distinctUntilChanged()
        .debounce(300.milliseconds)

      val type = uiState
        .map{ it.searchType }
        .distinctUntilChanged()

      combine(query, type) {query, type ->
        query to type
      }.collect { (query, type) ->
        Log.d("SearchViewModel", "Flow collect: query='$query', type=$type")
        if(query.length > 2)
          setSearch(query, type)
        else
          _uiState.update { it.copy(query = "") }
      }
    }
  }
  fun setSearch(query: String, type: SearchType){
    Log.d("SearchViewModel", "setSearch: query='$query', type=$type")
    when(type) {
      SearchType.USERS -> {} //ignore this
      SearchType.TAGS -> { _uiState.update { it.copy(query = query) } }
    }
  }

  fun onQueryChange(query: String){
    _uiState.update {
      it.copy(typedText = query)
    }
  }
  fun onTabChange(type: SearchType){
    _uiState.update {
      it.copy(searchType = type)
    }
  }
}
