package com.picshare.app.data.model

data class PostModel(
  val id: String,
  val userId: String,
  val description: String?,
  val tags: String,
  val likesNumber: Number
)
