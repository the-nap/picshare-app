package com.picshare.app.ui.user.userlist

data class UserListRequest(
  val username: String,
  val currentOffset: Int,
  val resetFlag: Boolean
)