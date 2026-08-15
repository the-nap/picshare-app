package com.picshare.app.data.model

data class UserModel(
  val id: String,
  val username: String,
  val bio: String,
  val followersCount: Number,
  val followedCount: Number
)