package com.picshare.app.ui.search

data class SearchUiState(
  val query: String = "",
  val searchType: SearchType = SearchType.USERS,
  val typedText: String = ""
)

enum class SearchType {
  USERS,
  TAGS
}
