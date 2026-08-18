package com.picshare.app.ui.user

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.picshare.app.R

data class UserScreenButton(
  val text: String,
  val onClick: () -> Unit
)

@Composable
fun UserScreen(
  avatarUrl: String?,
  username: String,
  bio: String,
  followersCount: Int,
  followedCount: Int,
  button: UserScreenButton? = null,
  isLoading: Boolean = false,
  imageLoader: ImageLoader? = null,
  avatarSize: Dp = 88.dp,
) {
  if (isLoading) {
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
    return
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface)
      .padding(16.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      ProfileAvatar(
        avatarUrl = avatarUrl,
        username = username,
        imageLoader = imageLoader,
        size = avatarSize
      )

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = username,
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        if (bio.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = bio,
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
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        StatItem(label = "Followers", count = followersCount)
        StatItem(label = "Followed", count = followedCount)
      }

      button?.let {
        Button(
          onClick = it.onClick,
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          )
        ) {
          Text(it.text)
        }
      }
    }
  }
}

@Composable
private fun StatItem(label: String, count: Int) {
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
  avatarUrl: String?,
  username: String,
  imageLoader: ImageLoader?,
  size: Dp = 88.dp
) {
  val avatarModifier = Modifier
    .size(size)
    .clip(RoundedCornerShape(percent = 40))

  if (LocalInspectionMode.current) {
    // @Preview has no real network stack / configured ImageLoader.
    // Render a stand-in instead of hitting AsyncImage — design-time only.
    Box(
      modifier = avatarModifier.background(MaterialTheme.colorScheme.surfaceVariant),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = username.take(1).uppercase(),
        style = MaterialTheme.typography.headlineSmall
      )
    }
    return
  }

  val context = LocalContext.current
  val loader = imageLoader ?: remember(context) { ImageLoader(context) }

  AsyncImage(
    model = avatarUrl,
    contentDescription = "$username's profile picture",
    imageLoader = loader,
    error = painterResource(id = R.mipmap.default_avatar),
    contentScale = ContentScale.Crop,
    modifier = avatarModifier
  )
}

// ---- Previews (no Gallery involved) ----

@Preview(showBackground = true, name = "UserScreen - with button")
@Composable
private fun UserScreenPreview() {
  MaterialTheme {
    UserScreen(
      avatarUrl = null,
      username = "johndoe",
      bio = "Kotlin & Compose enthusiast. Building things one composable at a time.",
      followersCount = 1284,
      followedCount = 312,
      button = UserScreenButton(text = "Follow") { }
    )
  }
}

@Preview(showBackground = true, name = "UserScreen - long bio")
@Composable
private fun UserScreenLongBioPreview() {
  MaterialTheme {
    UserScreen(
      avatarUrl = null,
      username = "averyverylongusername",
      bio = "This bio is intentionally long to check that it wraps onto a few " +
          "lines and then ellipsizes instead of pushing the layout off screen.",
      followersCount = 12,
      followedCount = 5,
      button = UserScreenButton(text = "Follow") { }
    )
  }
}

@Preview(showBackground = true, name = "UserScreen - no button")
@Composable
private fun UserScreenNoButtonPreview() {
  MaterialTheme {
    UserScreen(
      avatarUrl = null,
      username = "janedoe",
      bio = "Own profile — no follow action shown.",
      followersCount = 42,
      followedCount = 7
    )
  }
}

@Preview(showBackground = true, name = "UserScreen - loading")
@Composable
private fun UserScreenLoadingPreview() {
  MaterialTheme {
    UserScreen(
      avatarUrl = null,
      username = "",
      bio = "",
      followersCount = 0,
      followedCount = 0,
      isLoading = true
    )
  }
}
