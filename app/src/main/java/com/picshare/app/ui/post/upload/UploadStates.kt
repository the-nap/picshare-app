package com.picshare.app.ui.post.upload

import android.net.Uri
import com.picshare.app.data.model.PostModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UploadStatusBus {
  private val _status = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
  val status: StateFlow<UploadStatus> = _status.asStateFlow()

  fun update(status: UploadStatus){
    _status.value = status
  }
}

sealed interface UploadStatus {
  object Idle : UploadStatus
  object InProgress : UploadStatus
  object Success : UploadStatus
  data class Error(val message: String) : UploadStatus
}
data class ImageUploadState(
  val uri: Uri? = null,
  val sizeBytes: Long = -1L,
  val post: PostModel,
  val isLoading: Boolean = false,
  val error: String? = null
)
data class UploadFormErrors(
  val file: String? = null,
  val size: String? = null,
  val description: String? = null,
  val tags: String? = null
){
  val isValid: Boolean get() = file == null && description == null && tags == null && size == null
}