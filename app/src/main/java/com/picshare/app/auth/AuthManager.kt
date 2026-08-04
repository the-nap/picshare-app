package com.picshare.app.auth

import androidx.core.net.toUri
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object AuthManager {

  private const val KEYCLOAK_BASE = ""
  private const val REALM = "picshare"
  private const val CLIENT_ID = "picshare-app"
  private const val REDIRECT_URI = "com.picshare.app:/oauth2redirect"

  private val issuerUri = "$KEYCLOAK_BASE/realms/$REALM".toUri()
  private val redirectUri = REDIRECT_URI.toUri()

  suspend fun discoverServiceConfig(): AuthorizationServiceConfiguration =
    suspendCancellableCoroutine { cont ->
      AuthorizationServiceConfiguration.fetchFromIssuer(issuerUri) { config, ex ->
        if (config != null) cont.resume(config)
        else cont.resumeWithException(ex ?: Exception("Discovery failed"))
      }
    }

  fun buildAuthRequest(
    serviceConfig: AuthorizationServiceConfiguration
  ): AuthorizationRequest {
    return AuthorizationRequest.Builder(
      serviceConfig,
      CLIENT_ID,
      ResponseTypeValues.CODE,
      redirectUri
    )
      .setScope("openid email profile offline_access")
      .build()
  }
}