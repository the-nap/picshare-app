package com.picshare.app.ui.userlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.picshare.app.BuildConfig

@Composable
fun UserList (
  viewModel: UserListViewModel = hiltViewModel(),
  imageLoader: ImageLoader = viewModel.imageLoader,
  username: String
){
  val state by viewModel.uiState.collectAsState()

  when{
    state.users.isEmpty() ->
      Text(
        text = "No users found"
      )

    state.isLoading ->
      CircularProgressIndicator()

    !state.error.isNullOrEmpty() ->
      Text(
        text = state.error.toString()
      )

    else ->
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
      ) {
        itemsIndexed(state.users){ _, user ->
          Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .padding(2.dp)
          ){
            AsyncImage(
              modifier = Modifier
                .padding()
                .clip(
                RoundedCornerShape(100)
              ),
              imageLoader = imageLoader,
              model = "${BuildConfig.AVATAR_URL}/${user.id}",
              placeholder = ColorPainter(Color.Cyan),
              error = ColorPainter(Color.Red),
              contentScale = ContentScale.Fit,
              contentDescription = null,
            )
            Text(
              text = user.username
            )
            Text(
              text = "Followers: ${user.followersCount}"
            )
          }
        }
      }
  }
}