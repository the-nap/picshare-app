package com.picshare.app.auth

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.ResponseTypeValues
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

  fun exchangeCodeForTokens(response: AuthorizationResponse, authService: AuthorizationService, context: Context){
    val tokenRequest = response.createTokenExchangeRequest()

    authService.performTokenRequest(tokenRequest){ tokenResponse, ex ->
      if(tokenResponse != null) {
        val authState = AuthState(response, tokenResponse, null)
        TokenStorage.save(context, authState)
      } else {
        Log.e(context.toString(), ex?.message ?: "Error in exchanging tokens", ex)
      }
    }
  }

  fun refreshToken(context: Context, onToken: (String) -> Unit, onError: (Exception) -> Unit){
    val authState = TokenStorage.load(context) ?: run {
      onError(Exception("Not authenticated")); return
    }
    val authService = AuthorizationService(context)
    authState.performActionWithFreshTokens(authService){ accessToken, _, ex ->
      authService.dispose()
      if(accessToken != null){
        TokenStorage.save(context, authState)
        onToken(accessToken)
      } else {
        TokenStorage.clear(context)
        onError(ex ?: Exception("Token refresh failed"))
      }
    }
  }

  fun logout(context: Context){
    val authState = TokenStorage.load(context) ?: return
    val authService = AuthorizationService(context)

    val serviceConfig = authState.authorizationServiceConfiguration
    val idToken = authState.idToken

    TokenStorage.clear(context)

    if(serviceConfig != null && idToken != null){
      val endSessionRequest = EndSessionRequest.Builder(serviceConfig)
        .setIdTokenHint(idToken)
        .setPostLogoutRedirectUri(
          "com.picshare.app:/oauth2redirect".toUri()
        )
        .build()

      val endSessionIntent = authService.getEndSessionRequestIntent(endSessionRequest)
      context.startActivity(endSessionIntent)
    }
    authService.dispose()
  }
}