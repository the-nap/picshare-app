package com.picshare.app.ui.user.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.picshare.app.ui.theme.ConfirmDialog

@Composable
fun SettingsScreen(
  modifier: Modifier = Modifier,
  maxBioLength: Int = 140,
  viewModel: SettingsViewModel = hiltViewModel(),
  imageLoader: ImageLoader = viewModel.imageLoader
) {

  val state by viewModel.uiState.collectAsState()
  val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()

  val pickImageLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
  ) { uri -> uri?.let { viewModel.onImageSelected(it) } }

  if (showDeleteDialog) {
    ConfirmDialog(
      dialogTitle = "Delete ${state.user?.username}'s profile?",
      dialogText = "This user and all its data will be lost",
      onConfirmation = { viewModel.confirmDelete() },
      onDismissRequest = { viewModel.hideDeleteDialog() },
      onConfirmMessage = "User Deleted"
    )
  }
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

    Column(
      modifier = Modifier
        .size(96.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val user = state.user
      AsyncImage(
        imageLoader = imageLoader,
        model = ImageRequest.Builder(LocalContext.current)
          .data(viewModel.getImage())
          .memoryCacheKey(user?.id)
          .diskCacheKey(user?.id)
          .build(),
        contentDescription = "Profile picture preview",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
    }
    Spacer(Modifier.height(12.dp))

    OutlinedButton(onClick = {
      pickImageLauncher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
      )
    }) {
      Text("Choose Photo")
    }

    Spacer(Modifier.height(32.dp))

// --- Bio ---
    val bio = viewModel.getBio() ?: ""
    OutlinedTextField(
      value = bio,
      onValueChange = { if (it.length <= maxBioLength) viewModel.onBioChange(it) },
      label = { Text("Add a bio to your profile") },
      minLines = 5,
      maxLines = 10,
      supportingText = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text("${bio.length}/$maxBioLength")
        }
      },
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(16.dp))

    Button(
      onClick = { viewModel.onSubmit() },
      enabled = !state.isLoading,
      modifier = Modifier.fillMaxWidth(),
    ) {
      if (state.isLoading) {
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

    Button(
      onClick = { viewModel.showDeleteDialog() },
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text("Delete Profile")
    }
  }
}