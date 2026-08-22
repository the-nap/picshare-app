package com.picshare.app.ui.post.upload

import android.provider.OpenableColumns
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage

@Composable
fun PostUploadScreen(
  viewModel: PostUploadViewModel = hiltViewModel(),
  imageLoader: ImageLoader = viewModel.imageLoader
) {

  val maxDescriptionLength = 140
  val maxTagsLength = 25

  val state by viewModel.uiState.collectAsState()

  val context = LocalContext.current
  val pickImageLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
  ) { uri ->
    uri?.let {
      val size = context.contentResolver.query(it, arrayOf(OpenableColumns.SIZE), null, null, null)
        ?.use { c -> if (c.moveToFirst()) c.getLong(0) else -1L } ?: -1L
      viewModel.updateFile(it, size)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {

    Text(
      text = "Profile Picture",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(12.dp))

    Column(
      modifier = Modifier
        .size(96.dp)
        .background(MaterialTheme.colorScheme.surfaceVariant),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val imageUri = viewModel.getImage()
      if(imageUri != null)
      AsyncImage(
        imageLoader = imageLoader,
        model = imageUri,
        contentDescription = "Profile picture preview",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
      else
        Text(
          text = "Upload an image to see it here",
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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

    val description = state.post.description
    OutlinedTextField(
      value = description,
      onValueChange = { if (it.length <= maxDescriptionLength) viewModel.onDescriptionChange(it) },
      label = { Text("Add a description to your post") },
      minLines = 5,
      maxLines = 10,
      supportingText = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text("${description.length}/$maxDescriptionLength")
        }
      },
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(16.dp))

    val tags = state.post.tags
    OutlinedTextField(
      value = tags,
      onValueChange = { if (it.length <= maxTagsLength) viewModel.onTagsChange(it) },
      label = { Text("Add some tags to search your post") },
      minLines = 5,
      maxLines = 10,
      supportingText = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text("${tags.length}/$maxTagsLength")
        }
      },
      modifier = Modifier.fillMaxWidth(),
    )

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
  }
}