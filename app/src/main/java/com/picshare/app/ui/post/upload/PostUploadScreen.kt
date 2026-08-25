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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
  val errors by viewModel.errorsState.collectAsState()

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
      .padding(24.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {

    Text(
      text = "Post something",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(12.dp))

    val fileError = errors.file ?: errors.size
    Column {
      if (state.uri != null) {
        Column(
          modifier = Modifier
            .size(96.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          AsyncImage(
            imageLoader = imageLoader,
            model = state.uri,
            contentDescription = "Profile picture preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
        }
      } else {
        Text(
          text = "Upload an image to see it here",
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        )
      }
      if (fileError != null) {
        Text(
          text = fileError,
          color = MaterialTheme.colorScheme.error,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
        )
      }
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


    val description = state.post.description
    val descriptionError = errors.description
    OutlinedTextField(
      value = description,
      onValueChange = { if (it.length <= maxDescriptionLength) viewModel.onDescriptionChange(it) },
      label = { Text("Add a description to your post") },
      minLines = 5,
      maxLines = 10,
      isError = descriptionError != null,
      supportingText = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = descriptionError ?: "",
            color = MaterialTheme.colorScheme.error
          )
          Text("${description.length}/$maxDescriptionLength")
        }
      },
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(16.dp))

    val tags = state.post.tags
    val tagsError = errors.tags
    OutlinedTextField(
      value = tags,
      onValueChange = { if (it.length <= maxTagsLength) viewModel.onTagsChange(it) },
      label = { Text("Add some tags to search your post") },
      minLines = 2,
      maxLines = 4,
      isError = tagsError != null,
      supportingText = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = tagsError ?: "",
            color = MaterialTheme.colorScheme.error
          )
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