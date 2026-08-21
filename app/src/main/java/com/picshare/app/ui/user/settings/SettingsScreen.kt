package com.picshare.app.ui.user.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.picshare.app.BuildConfig
import com.picshare.app.ui.theme.PicshareTheme

@Composable
fun SettingsScreen(
  viewModel: SettingsViewModel = hiltViewModel(),
  imageLoader: ImageLoader = viewModel.imageLoader
){

  val state by viewModel.uiState.collectAsState()

}

@Composable
fun SettingsContent(
  profileImageUri: Uri?,
  onImageSelected: (Uri) -> Unit,
  fileErrorMessage: String?,
  bio: String,
  onBioChange: (String) -> Unit,
  bioErrorMessage: String?,
  isSubmitting: Boolean,
  onSubmit: () -> Unit,
  isDeleting: Boolean,
  onDeleteProfile: () -> Unit,
  modifier: Modifier = Modifier,
  maxBioLength: Int = 140,
) {
  val pickImageLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent(),
  ) { uri -> uri?.let(onImageSelected) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    // --- Profile picture ---
    Text(
      text = "Profile Picture",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(12.dp))

    if (profileImageUri != null) {
      val inPreview = LocalInspectionMode.current
      Box(
        modifier = Modifier
          .size(96.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
      ) {
//          AsyncImage(
//            model = "${BuildConfig.AVATAR_URL}/${user.id}"
//            contentDescription = "Profile picture preview",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize(),
//          )
//        }
      }
      Spacer(Modifier.height(12.dp))
    }

    OutlinedButton(onClick = { pickImageLauncher.launch("image/*") }) {
      Text("Choose Photo")
    }

    if (fileErrorMessage != null) {
      Text(
        text = fileErrorMessage,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 4.dp),
      )
    }

    Spacer(Modifier.height(32.dp))

    // --- Bio ---
    OutlinedTextField(
      value = bio,
      onValueChange = { if (it.length <= maxBioLength) onBioChange(it) },
      label = { Text("Add a bio to your profile") },
      minLines = 5,
      maxLines = 10,
      isError = bioErrorMessage != null,
      supportingText = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(bioErrorMessage ?: "")
          Text("${bio.length}/$maxBioLength")
        }
      },
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(16.dp))

    Button(
      onClick = onSubmit,
      enabled = !isSubmitting,
      modifier = Modifier.fillMaxWidth(),
    ) {
      if (isSubmitting) {
        CircularProgressIndicator(
          modifier = Modifier.size(18.dp),
          strokeWidth = 2.dp,
          color = MaterialTheme.colorScheme.onPrimary,
        )
      } else {
        Text("Upload")
      }
    }

    Spacer(Modifier.height(32.dp))

    // --- Delete ---
    Button(
      onClick = onDeleteProfile,
      enabled = !isDeleting,
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
      modifier = Modifier.fillMaxWidth(),
    ) {
      if (isDeleting) {
        CircularProgressIndicator(
          modifier = Modifier.size(18.dp),
          strokeWidth = 2.dp,
          color = MaterialTheme.colorScheme.onError,
        )
      } else {
        Text("Delete Profile")
      }
    }
  }
}

@Preview
@Composable
private fun SettingsContentPreview() {
  PicshareTheme {
    SettingsContent(
      profileImageUri = Uri.EMPTY, // any non-null value — preview shows a placeholder, not a real image
      onImageSelected = {},
      fileErrorMessage = null,
      bio = "Hello world!",
      onBioChange = {},
      bioErrorMessage = null,
      isSubmitting = false,
      onSubmit = {},
      isDeleting = false,
      onDeleteProfile = {},
    )
  }
}
