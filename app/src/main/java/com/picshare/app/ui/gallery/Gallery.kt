package com.picshare.app.ui.gallery

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.picshare.app.BuildConfig

@Composable
fun Gallery (
  viewModel: GalleryViewModel = hiltViewModel(),
  key: String? = null,
  toSearch: String? = null,
  imageLoader: ImageLoader = viewModel.imageLoader
) {

  val gridState = rememberLazyStaggeredGridState()

  val state by viewModel.uiState.collectAsState()

  Log.d("Gallery", "Recomposition: key='$key', search='$toSearch', state(posts=${state.posts.size}, loading=${state.isLoading})")

  LaunchedEffect(key, toSearch) {
    Log.d("Gallery", "LaunchedEffect(key, toSearch) triggered: key='$key', search='$toSearch'")
    viewModel.initialize(key, toSearch)
  }

  LaunchedEffect(gridState) {
    snapshotFlow {
      val lastIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
      lastIndex to state.posts.size
    }.collect { (lastVisibleIndex, totalItems) ->
      if (lastVisibleIndex != null &&
        (lastVisibleIndex >= totalItems - 5)) {
        viewModel.loadNextPage()
      }
    }
  }

  when {
    state.posts.isEmpty() ->
      Text(
        text = "No posts to show",
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      )

    !state.error.isNullOrBlank() ->
      Text(
        text = state.error.toString(),
        modifier = Modifier.padding(16.dp)
      )

    state.isLoading && state.posts.isEmpty() ->
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }

    else ->
      LazyVerticalStaggeredGrid(
        state = gridState,
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(state.posts) { post ->
          var aspectRatio by remember(post.id) { mutableFloatStateOf(1f) }
          AsyncImage(
            imageLoader = imageLoader,
            model = "${BuildConfig.PREVIEW_URL}/${post.id}",
            placeholder = ColorPainter(Color.Cyan),
            error = ColorPainter(Color.Red),
            contentScale = ContentScale.Fit,
            contentDescription = null,
            onSuccess = { state ->
              val size = state.result.image
              aspectRatio = size.width.toFloat() / size.height.toFloat()
                        },
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(aspectRatio)
              .padding(vertical = 2.dp)
          )
        }
        if (state.isLoading) {
          item(
            span = StaggeredGridItemSpan.FullLine
          ) {
            Box(
              modifier = Modifier.fillMaxWidth()
                .padding(vertical = 16.dp),
              contentAlignment = Alignment.Center

            ) {
              CircularProgressIndicator()
            }
          }
        }
      }
  }
}