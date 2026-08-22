package com.picshare.app.ui.post.upload

import android.net.Uri
import com.picshare.app.data.model.PostModel

data class ImageUploadModel(
  val uri: Uri? = null,
  val post: PostModel,
  val isLoading: Boolean = false,
  val error: String? = null
)
data class UploadFormErrors(
  val file: String? = null,
  val description: String? = null,
  val tags: String? = null
){
  val isValid: Boolean get() = file == null && description == null && tags == null
}

data class UploadFile(
  val uri: Uri,
  val sizeBytes: Long,
  val displayName: String? = null
)
