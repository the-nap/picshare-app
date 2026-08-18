package com.picshare.app.ui.userlist

data class UserListRequest(
  val username: String,
  val currentOffset: Int,
  val resetFlag: Boolean
)