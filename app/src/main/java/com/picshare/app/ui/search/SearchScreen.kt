package com.picshare.app.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.picshare.app.ui.theme.PicshareTheme

@Composable
fun SearchScreen(
  viewModel: SearchViewModel = hiltViewModel()
){
  val state = viewModel.uiState.collectAsState()

  SearchContent(
    query = state.value.toSearch,
    searchType = state.value.searchType,
    onQueryChange = viewModel::onQueryChange,
    onTabChange = viewModel::onTabChange
  )
}

@Composable
fun SearchContent(
  query: String,
  searchType: SearchType,
  onQueryChange: (String) -> Unit,
  onTabChange: (SearchType) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally

  ) {
    OutlinedTextField(
      value = query,
      onValueChange = onQueryChange,
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
      placeholder = {
        Text(
          text = "Search...",
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      },
      colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,

        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,

        cursorColor = MaterialTheme.colorScheme.primary
      )
    )
    PrimaryTabRow(
      selectedTabIndex = when (searchType) {
        SearchType.USERS -> 0
        SearchType.TAGS -> 1
      },
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
      divider = {}
    ) {
      Tab(
        selected = searchType == SearchType.USERS,
        onClick = { onTabChange(SearchType.USERS) },
        selectedContentColor = MaterialTheme.colorScheme.primary,
        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        text = {
          Text("Users")
        }
      )

      Tab(
        selected = searchType == SearchType.TAGS,
        onClick = { onTabChange(SearchType.TAGS) },
        selectedContentColor = MaterialTheme.colorScheme.primary,
        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        text = {
          Text("Tags")
        }
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
    onTabChange = {}
  )
}