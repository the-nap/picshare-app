package com.picshare.app.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SearchScreen(
  viewModel: SearchViewModel = hiltViewModel()
){
  val query by viewModel.query.collectAsState()
  val searchType by viewModel.searchType.collectAsState()

  SearchContent(
    query = query,
    searchType = searchType,
    onQueryChange = viewModel::onQueryChange,
    onSearchTypeChange = viewModel::changeType
  )
}

@Composable
fun SearchContent(
  query: String,
  searchType: SearchType,
  onQueryChange: (String) -> Unit,
  onSearchTypeChange: (SearchType) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    TextField(
      value = query,
      onValueChange = onQueryChange,
      placeholder = {
        Text("Search...")
      },
    )
    PrimaryTabRow(
      selectedTabIndex = when(searchType){
        SearchType.USERS -> 0
        SearchType.TAGS -> 1
      },
    ){
      Tab(
        selected = searchType == SearchType.USERS,
        onClick = { onSearchTypeChange(SearchType.USERS) },
        text = { Text("Users") }
      )
      Tab(
        selected = searchType == SearchType.TAGS,
        onClick = { onSearchTypeChange(SearchType.TAGS) },
        text = { Text("Tags") }
      )
    }
  }
}

@Preview()
@Composable
fun SearchPreview(){
  SearchContent(
    query = "Search text",
    searchType = SearchType.USERS,
    onQueryChange = {},
    onSearchTypeChange = {}
  )
}