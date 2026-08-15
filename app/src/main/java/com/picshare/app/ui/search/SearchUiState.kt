package com.picshare.app.ui.search

data class SearchUiState(
  val isLoading: Boolean = false,
  val error: String? = null,
  val searchType: SearchType = SearchType.USERS,
  val toSearch: String = ""
)

enum class SearchType {
  USERS,
  TAGS
}
