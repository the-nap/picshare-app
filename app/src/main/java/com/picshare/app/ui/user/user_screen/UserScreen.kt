package com.picshare.app.ui.user.user_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.picshare.app.BuildConfig
import com.picshare.app.R
import com.picshare.app.data.model.UserModel
import com.picshare.app.ui.post.gallery.Gallery

@Composable
fun UserScreen(
  viewModel: UserViewModel = hiltViewModel(),
  userId: String?,
  imageLoader: ImageLoader = viewModel.imageLoader,
) {

  val state by viewModel.uiState.collectAsState()

  LaunchedEffect(userId) {
    viewModel.set(userId)
  }

  when {
    (state.isLoading || state.user == null) -> {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(16.dp)
          .heightIn(min = 140.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }

    else -> {
      val user = state.user!!
      Log.d("UserScreen", user.toString())
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.background)
      ) {
        Column(
          modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(32.dp))
            .shadow(8.dp)
            .padding(12.dp),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            ProfileAvatar(
              user = user,
              imageLoader = imageLoader
            )

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = user.username,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (user.bio != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = user.bio,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 3,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Bottom row: follower/followed stats + action button
          Row(
            modifier = Modifier
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
              StatItem(label = "Followers", count = user.followersCount)
              StatItem(label = "Followed", count = user.followedCount)
            }
            val button by viewModel.buttonState.collectAsState()
            Button(
              onClick = button.onClick,
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
              )
            ) {
              Text(
                text = button.text
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Gallery(key = "user", toSearch = user.id)
      }
    }
  }
}

@Composable
private fun StatItem(label: String, count: Number) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = count.toString(),
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun ProfileAvatar(
  user: UserModel,
  imageLoader: ImageLoader
) {
  val avatarModifier = Modifier
    .size(88.dp)
    .clip(RoundedCornerShape(percent = 40))

  AsyncImage(
    model = "${BuildConfig.AVATAR_URL}/${user.id}",
    contentDescription = "${user.username}'s profile picture",
    imageLoader = imageLoader,
    error = painterResource(id = R.drawable.default_avatar),
    contentScale = ContentScale.Crop,
    modifier = avatarModifier
  )
}