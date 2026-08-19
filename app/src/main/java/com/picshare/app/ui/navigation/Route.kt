package com.picshare.app.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

  @Serializable
  data object Feed: Route

  @Serializable
  data object Search: Route

  @Serializable
  data object User: Route

  @Serializable
  data object Post: Route
}