package com.picshare.app.auth

import android.content.Intent

interface AuthRepository {

  val currentUserId: String?

  suspend fun getAuthorizationRequest(): Intent
  suspend fun handleAuthResponse(data: Intent?)
  suspend fun getValidAccessToken(): String
  suspend fun logout()

}