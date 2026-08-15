package com.picshare.app.data.model

import java.io.File

data class ImageUploadModel(
  val file: File,
  val post: PostModel
)
