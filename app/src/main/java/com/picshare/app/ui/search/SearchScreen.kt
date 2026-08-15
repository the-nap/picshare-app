package com.picshare.app.ui.search

import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SearchScreen(
  viewModel: SearchViewModel = hiltViewModel()
){
  Box(
    modifier = Modifier
      .fillMaxWidth()
  ){

    Spacer(
      modifier = Modifier.height(5.dp)
    )
    TextField(
      value = viewModel.query.collectAsState().value,
      onValueChange = viewModel::onQueryChange,
      placeholder = {
        Text("Search...")
      }
    )
    Spacer(
      modifier = Modifier.height(5.dp)
    )

  }
}