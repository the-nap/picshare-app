package com.picshare.app.post

import android.icu.text.StringSearch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun Gallery(
  key: String,
  toSearch: String,
  postRepository: PostRepository
){
  var posts by remember { mutableStateOf<List<PostModel>>(emptyList()) }
  var loading by remember { mutableStateOf(true)}
  var error by remember { mutableStateOf<String?>(null)}

  LaunchedEffect(key, toSearch) {
    try {
      posts = postRepository.getPosts(toSearch, key) ?: emptyList()
      loading = false
      }catch (e: Exception){
        error = e.message
        loading = false
      }
  }

  when {
    loading -> CircularProgressIndicator()
    error != null -> Text("Error: $error")
    else -> {
      LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
          items(posts){ post ->
            AsyncImage(
              model = post,
              contentScale = ContentScale.Crop,
              contentDescription = null,
              modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
            )
          }
        }
      )
    }
  }
}