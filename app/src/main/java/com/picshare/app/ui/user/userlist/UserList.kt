package com.picshare.app.ui.user.userlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.picshare.app.BuildConfig
import com.picshare.app.data.model.UserModel

@Composable
fun UserList (
  viewModel: UserListViewModel = hiltViewModel(),
  imageLoader: ImageLoader = viewModel.imageLoader,
  username: String
) {
  val state by viewModel.uiState.collectAsState()
  val listState = rememberLazyListState()

  LaunchedEffect(username) {
    viewModel.set(username)
  }

  LaunchedEffect(listState) {
    snapshotFlow {
      val lastIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
      lastIndex to state.users.size
    }.collect { (lastVisibleIndex, totalItems) ->
      if (lastVisibleIndex != null &&
        lastVisibleIndex >= totalItems - 5
      ) {
        viewModel.getNext()
      }
    }
  }

  when {
    state.isLoading && state.users.isEmpty() ->
      CircularProgressIndicator()


    !state.error.isNullOrEmpty() ->
      Text(
        text = state.error.toString()
      )

    state.users.isEmpty() ->
      Text(
        text = "No users found"
      )

    else ->
      LazyColumn(modifier = Modifier.fillMaxWidth()) {
        itemsIndexed(state.users) { _, user ->
          UserListItem(user = user, imageLoader = imageLoader)
          HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp),
            color = MaterialTheme.colorScheme.outlineVariant
          )
        }
      }
  }
  }

  @Composable
  fun UserListItem(
    user: UserModel,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      AsyncImage(
        imageLoader = imageLoader,
        model = "${BuildConfig.AVATAR_URL}/${user.id}",
        placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
        error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
        contentScale = ContentScale.Fit,
        contentDescription = "${user.username}'s avatar",
        modifier = Modifier
          .size(48.dp)
          .clip(
            RoundedCornerShape(100)
          )
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = user.username,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${user.followersCount} followers",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }

