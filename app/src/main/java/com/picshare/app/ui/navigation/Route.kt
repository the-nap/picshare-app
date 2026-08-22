package com.picshare.app.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
  @Serializable
  object Feed: Route
  @Serializable
  object Search: Route

  @Serializable
  data class User(val userId: String? = null): Route

  @Serializable
  data class Post(val postId: String): Route
  @Serializable
  object Settings: Route
  @Serializable
  object Upload: Route
}
