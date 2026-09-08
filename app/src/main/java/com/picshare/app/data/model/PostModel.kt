package com.picshare.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostModel(
  val id: String,
  val userId: String,
  val description: String,
  val tags: String,
  val likesNumber: Number
): Parcelable
