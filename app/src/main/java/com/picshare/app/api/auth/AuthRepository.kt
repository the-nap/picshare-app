package com.picshare.app.api.auth

import android.content.Context
import android.content.Intent

interface AuthRepository {

  val currentUserId: String?

  suspend fun getAuthorizationRequest(): Intent
  suspend fun handleAuthResponse(data: Intent?): Boolean
  suspend fun getValidAccessToken(): String?
  suspend fun logout(context: Context, data: Intent)

}