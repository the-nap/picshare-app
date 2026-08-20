package com.picshare.app.ui.post.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import com.picshare.app.BuildConfig
import com.picshare.app.R
import com.picshare.app.ui.navigation.NavEvent
import com.picshare.app.ui.navigation.Route
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Heart

@Composable
fun PostScreen(
  postId: String,
  viewModel: PostViewModel = hiltViewModel(),
  imageLoader: ImageLoader = viewModel.imageLoader,
  onNavigationEvent: (NavEvent) -> Unit
) {

  LaunchedEffect(postId) {
    viewModel.load(postId)
  }

  val state by viewModel.uiState.collectAsStateWithLifecycle()

  PostContent(
    username = state.user?.username,
    userId = state.user?.id,
    avatarUrl = "${BuildConfig.AVATAR_URL}/${state.user?.id}",
    imageUrl = "${BuildConfig.MEDIA_URL}/${state.post?.id}",
    description = state.post?.description,
    tags = state.post?.tags,
    likes = state.post?.likesNumber,
    onLikeClick = {println("test")},
    showDeleteButton = state.isOwned,
    onDeleteClick = {println("test2")},
    imageLoader = imageLoader,
    onNavigationEvent = onNavigationEvent
  )
}
@Composable
private fun PostContent(
  username: String?,
  userId: String?,
  avatarUrl: String?,
  imageUrl: String?,
  description: String?,
  tags: String?,
  likes: Number?,
  likeIcon: ImageVector = CssGgIcons.Heart,
  onLikeClick: () -> Unit,
  showDeleteButton: Boolean,
  onDeleteClick: () -> Unit,
  imageLoader: ImageLoader,
  onNavigationEvent: (NavEvent) -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
      Column {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(
              horizontal = 16.dp,
              vertical = 12.dp
            )
            .clickable(
              onClick = {
                onNavigationEvent(
                  NavEvent.OnNavigateTo(Route.User(userId = userId))
                )
              }
            ),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
          SubcomposeAsyncImage(
            imageLoader = imageLoader,
            model = avatarUrl ?: R.drawable.default_avatar,
            contentDescription = "User avatar",
            loading = {CircularProgressIndicator(modifier = Modifier.size(50.dp))},
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
          Text(
            text = username ?: "Username",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        var aspectRatio by remember { mutableFloatStateOf(1f) }
        SubcomposeAsyncImage (
          imageLoader = imageLoader,
          model = imageUrl ?: R.drawable.default_image,
          loading = { CircularProgressIndicator(modifier = Modifier.size(50.dp)) },
          onSuccess = { state ->
            val size = state.result.image
            aspectRatio = size.width.toFloat() / size.height.toFloat()
          },
          contentDescription = "image",
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 900.dp)
            .aspectRatio(aspectRatio),
          contentScale = ContentScale.Fit
        )
        Column(
          modifier = Modifier.padding(20.dp)
        ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
              IconButton(
                onClick = onLikeClick,
              ) {
                Icon(
                  imageVector = likeIcon,
                  contentDescription = "Like"
                )
              }

              Text(
                text = "${likes ?: 0}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

          if (!description.isNullOrBlank()) {
            Text(
              text = description,
              modifier = Modifier.padding(top = 8.dp),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurface,
              softWrap = true
            )
          }

          if (!tags.isNullOrBlank()) {
            Text(
              text = tags,
              modifier = Modifier.padding(top = 8.dp),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              softWrap = true
            )
          }
        }
      }
    }

    if (showDeleteButton) {
      Button(
        onClick = onDeleteClick,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.error,
          contentColor = MaterialTheme.colorScheme.onError
        )
      ) {
        Text("Delete")
      }
    }
  }
}