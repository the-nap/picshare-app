package com.picshare.app.api.auth

import android.content.Context
import android.content.Intent

interface AuthRepository {

  suspend fun getAuthorizationRequest(): Intent
  suspend fun handleAuthResponse(data: Intent?): Boolean
  suspend fun logout(context: Context)

  suspend fun tryRestoreSession(): Boolean


}