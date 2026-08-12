package com.picshare.app.user

data class UserModel(
  val id: String,
  val username: String,
  val bio: String,
  val followersCount: Number,
  val followedCount: Number
)