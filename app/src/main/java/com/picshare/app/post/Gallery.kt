package com.picshare.app.post

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.picshare.app.BuildConfig

@Composable
fun Gallery (
  galleryViewModel: GalleryViewModel = viewModel()
) {

  val context = LocalContext.current
  val gridState = rememberLazyStaggeredGridState()

  LaunchedEffect(gridState) {
    snapshotFlow {
      gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
    }.collect { lastVisibleIndex ->
      if (lastVisibleIndex != null &&
        lastVisibleIndex >= galleryViewModel.posts.size -5){
          galleryViewModel.loadNextPage()
      }
    }
  }

  LaunchedEffect(Unit) {
    galleryViewModel.errorEvents.collect { message ->
      Toast.makeText(context, message, Toast.LENGTH_SHORT)
    }
  }

  if (galleryViewModel.noPosts())
    Text(
      text = "No posts to show",
      modifier = Modifier
        .fillMaxWidth()
    )
  else
    LazyVerticalStaggeredGrid(
      state = gridState,
      columns = StaggeredGridCells.Fixed(2),
      verticalItemSpacing = 4.dp,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      content = {
        items(galleryViewModel.posts) { post ->
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data("${BuildConfig.API_URL}/post/preview/$post.id")
              .crossfade(true),
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