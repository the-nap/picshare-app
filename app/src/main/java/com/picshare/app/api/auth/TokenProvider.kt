package com.picshare.app.api.auth

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class TokenProvider @Inject constructor(
  private val appContext: Application
) {

  suspend fun getValidAccessToken(): String? = withContext(Dispatchers.IO) {
    val authState = TokenStorage.load(appContext) ?: return@withContext  null
    suspendCancellableCoroutine { cont ->

      val authService = AuthorizationService(appContext)
      authState.performActionWithFreshTokens(authService){ accessToken, _, ex ->
        authService.dispose()

        if(accessToken != null){
          TokenStorage.save(appContext, authState)
          cont.resume(accessToken)
        } else {
          TokenStorage.clear(appContext)
          cont.resumeWithException(
            ex ?: Exception("Token refresh failed")
          )
        }
      }
    }
  }
}