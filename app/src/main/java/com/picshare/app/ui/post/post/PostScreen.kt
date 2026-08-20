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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import com.picshare.app.BuildConfig
import com.picshare.app.R
import com.picshare.app.ui.navigation.NavEvent
import com.picshare.app.ui.navigation.Route
import com.picshare.app.ui.theme.ConfirmDialog
import com.picshare.app.ui.theme.LikeButtonState

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
  val buttonState by viewModel.buttonState.collectAsStateWithLifecycle()
  val dialogState by viewModel.showDeleteDialog.collectAsStateWithLifecycle()

  PostContent(
    username = state.user?.username,
    userId = state.user?.id,
    avatarUrl = "${BuildConfig.AVATAR_URL}/${state.user?.id}",
    imageUrl = "${BuildConfig.MEDIA_URL}/${state.post?.id}",
    description = state.post?.description,
    tags = state.post?.tags,
    likeButton = buttonState,
    showDeleteButton = state.isOwned,
    onDeleteClick = {viewModel.showDeleteDialog()},
    imageLoader = imageLoader,
    onNavigationEvent = onNavigationEvent,
    showDeleteDialog = dialogState,
    onConfirm = {viewModel.confirmDelete()},
    onDismiss = {viewModel.hideDeleteDialog()}
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
  likeButton: LikeButtonState,
  showDeleteButton: Boolean,
  onDeleteClick: () -> Unit,
  imageLoader: ImageLoader,
  onNavigationEvent: (NavEvent) -> Unit,
  showDeleteDialog: Boolean,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  if(showDeleteDialog){
    ConfirmDialog(
      dialogTitle = "Delete post",
      dialogText = "This post and all its data will be lost",
      onConfirmation = onConfirm,
      onDismissRequest = onDismiss,
      onConfirmMessage = "Post Deleted"
    )
  }
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
                onClick = likeButton.onClick,
                enabled = !likeButton.isLoading
              ){
                Icon(
                  painter = painterResource((if(likeButton.isLiked) R.drawable.heart_full else R.drawable.heart_empty)),
                  contentDescription = null,
                  tint = Color.Unspecified
                )
              }

              Text(
                text = "${likeButton.likesNumber}",
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