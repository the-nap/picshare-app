package com.picshare.app.post

import java.io.File

data class ImageUploadModel(
  val file: File,
  val post: PostModel
)
