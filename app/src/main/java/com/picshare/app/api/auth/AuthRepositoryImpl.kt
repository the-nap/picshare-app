package com.picshare.app.api.auth

import android.app.Application
import android.content.Intent
import androidx.core.net.toUri
import com.picshare.app.BuildConfig
import com.picshare.app.BuildConfig.CLIENT_ID
import com.picshare.app.BuildConfig.REDIRECT_URI
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImpl @Inject constructor(
  private val appContext: Application
) : AuthRepository {

  private val issuerUri = "${BuildConfig.AUTH_URL}/realms/${BuildConfig.REALM}".toUri()

  override var currentUserId: String? = null


  override suspend fun getAuthorizationRequest(): Intent {
    val serviceConfig = discoverEndpoints()
    val authRequest = createAuthRequest(serviceConfig)

    val authService = AuthorizationService(appContext)
    return authService.getAuthorizationRequestIntent(authRequest)
  }

  private fun createAuthRequest(serviceConfig: AuthorizationServiceConfiguration): AuthorizationRequest =
  AuthorizationRequest.Builder(
    serviceConfig,
    CLIENT_ID,
    ResponseTypeValues.CODE,
    REDIRECT_URI.toUri()
  )
    .setScope("openid email profile offline_access")
    .build()

  private suspend fun discoverEndpoints(): AuthorizationServiceConfiguration {
    return suspendCancellableCoroutine { cont ->
      AuthorizationServiceConfiguration.fetchFromIssuer(issuerUri) { config, ex ->
        if (config != null) cont.resume(config)
        else cont.resumeWithException(ex ?: Exception("Discovery failed"))
      }
    }
  }

  override suspend fun handleAuthResponse(data: Intent?) {
    requireNotNull(data)
    val response = AuthorizationResponse.fromIntent(data)
    val exception = AuthorizationException.fromIntent(data)

    if(response == null){
      throw exception ?: Exception("Authorization failed")
    }

    val newAuthState = getTokenFromCode(response)

    currentUserId = extractUserId(newAuthState)

    TokenStorage.save(appContext, newAuthState)
  }

  private fun extractUserId(authState: AuthState): String? =
    authState.parsedIdToken?.subject

  private suspend fun getTokenFromCode(response: AuthorizationResponse): AuthState {
    return suspendCancellableCoroutine { cont ->
      val authService = AuthorizationService(appContext)
      val tokenRequest = response.createTokenExchangeRequest()

      authService.performTokenRequest(tokenRequest){ tokenResponse, ex ->
        authService.dispose()

        if(tokenResponse != null){
          cont.resume(
            AuthState(response, tokenResponse,null)
          )
        } else {
          cont.resumeWithException(
            ex ?: Exception("Token exchange failed")
          )
        }
      }
    }
  }

  override suspend fun getValidAccessToken(): String {
    return suspendCancellableCoroutine { cont ->
      val authState = TokenStorage.load(appContext)

      if(authState == null){
        cont.resumeWithException(
          Exception("Not authenticated")
        )
        return@suspendCancellableCoroutine
      }

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

  override suspend fun logout() {
    val authState = TokenStorage.load(appContext) ?: return
    val authService = AuthorizationService(appContext)

    val serviceConfig = authState.authorizationServiceConfiguration
    val idToken = authState.idToken

    TokenStorage.clear(appContext)

    if(serviceConfig != null && idToken != null){
      val endSessionRequest = EndSessionRequest.Builder(serviceConfig)
        .setIdTokenHint(idToken)
        .setPostLogoutRedirectUri(
          "com.picshare.app:/oauth2redirect".toUri()
        )
        .build()

      val endSessionIntent = authService.getEndSessionRequestIntent(endSessionRequest)
      appContext.startActivity(endSessionIntent)
    }
    authService.dispose()
  }
}