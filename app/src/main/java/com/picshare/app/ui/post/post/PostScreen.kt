package com.picshare.app.ui.post.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.picshare.app.BuildConfig
import com.picshare.app.R
import com.picshare.app.data.model.UserModel
import com.picshare.app.ui.post.SelectedPostViewModel
import com.picshare.app.ui.theme.PicshareTheme
import compose.icons.CssGgIcons
import compose.icons.cssggicons.Heart

@Composable
fun PostScreen(
  viewModel: SelectedPostViewModel = hiltViewModel(),
  imageLoader: ImageLoader = viewModel.imageLoader
) {

  val post by viewModel.selectedPost.collectAsStateWithLifecycle()
  lateinit var user: UserModel

  PostContent(
    username = user.username,
    avatarUrl = "${BuildConfig.AVATAR_URL}/${user.id}",
    imageUrl = "${BuildConfig.MEDIA_URL}/${post?.id}",
    description = post?.description,
    tags = post?.tags,
    likes = post?.likesNumber,
    showLikeButton = false,
    likeEnabled = false,
    onLikeClick = {println("test")},
    showDeleteButton = false,
    onDeleteClick = {println("test2")},
    imageLoader = imageLoader
  )
}
@Composable
private fun PostContent(
  username: String?,
  avatarUrl: String?,
  imageUrl: String?,
  description: String?,
  tags: String?,
  likes: Number?,
  showLikeButton: Boolean,
  likeIcon: ImageVector = CssGgIcons.Heart,
  likeEnabled: Boolean,
  onLikeClick: () -> Unit,
  showDeleteButton: Boolean,
  onDeleteClick: () -> Unit,
  imageLoader: ImageLoader
  ) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
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
            ),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
          AsyncImage(
            imageLoader = imageLoader,
            model = avatarUrl ?: R.drawable.default_avatar,
            contentDescription = "User avatar",
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
        AsyncImage(
          imageLoader = imageLoader,
          model = imageUrl ?: R.drawable.default_image,
          contentDescription = "image",
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 600.dp),
          contentScale = ContentScale.Fit
        )
        Column(
          modifier = Modifier.padding(20.dp)
        ) {
          if (showLikeButton) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
              IconButton(
                onClick = onLikeClick,
                enabled = likeEnabled
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

@Preview(
  name = "Post Content",
  showBackground = true,
  backgroundColor = 0xFF121316
)
@Composable
private fun PostContentPreview() {
  val context = LocalContext.current

  val imageLoader = ImageLoader.Builder(context)
    .build()

  PicshareTheme {
    PostContent(
      username = "leonardo",
      avatarUrl = null,
      imageUrl = null,
      description = "A beautiful day at the beach 🌊",
      tags = "#beach #summer #italy",
      likes = 42,
      showLikeButton = true,
      likeIcon = CssGgIcons.Heart,
      likeEnabled = true,
      onLikeClick = {},
      showDeleteButton = true,
      onDeleteClick = {},
      imageLoader = imageLoader
    )
  }
}