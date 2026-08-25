package com.picshare.app.api.auth

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import com.picshare.app.BuildConfig
import com.picshare.app.BuildConfig.CLIENT_ID
import com.picshare.app.BuildConfig.REDIRECT_URI
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.data.repository.UserRepository
import com.picshare.app.activity.LoginActivity
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
  private val appContext: Application,
  private val userRepository: UserRepository,
  private val tokenProvider: TokenProvider
) : AuthRepository {

  private val issuerUri = "${BuildConfig.AUTH_URL}/realms/${BuildConfig.REALM}".toUri()
  private val TAG = this.javaClass.simpleName

  override suspend fun getAuthorizationRequest(): Intent {
    var serviceConfig: AuthorizationServiceConfiguration
    try{
      serviceConfig = discoverEndpoints()
    } catch (e: AuthorizationException) {
      if (e.type == AuthorizationException.TYPE_GENERAL_ERROR &&
        e.code == AuthorizationException.GeneralErrors.NETWORK_ERROR.code) {
        throw IllegalStateException(e)
      }
      throw e
    }
    val authRequest = createAuthRequest(serviceConfig)
    Log.d("AuthRepo", "AuthRequest: $authRequest")

    val authService = AuthorizationService(appContext)
    Log.d("AuthRepo", "AuthService: $authService")
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
    Log.d("AuthRepo", "discoverEndpoints: starting fetch for $issuerUri")
    return suspendCancellableCoroutine { cont ->
      AuthorizationServiceConfiguration.fetchFromIssuer(issuerUri) { config, ex ->
        Log.d("AuthRepo", "discoverEndpoints: callback fired, config=$config ex=$ex")
        if (config != null) cont.resume(config)
        else cont.resumeWithException(ex ?: Exception("Discovery failed"))
      }
    }
  }

  override suspend fun handleAuthResponse(data: Intent?): Boolean {
    requireNotNull(data)
    val response = AuthorizationResponse.fromIntent(data)
    val exception = AuthorizationException.fromIntent(data)

    if(response == null){
      Log.e("AuthRepo", "Authorization Failed", exception)
      return false
    }

    val newAuthState = getTokenFromCode(response)

    TokenStorage.save(appContext, newAuthState)
    val userId = extractUserId(newAuthState)
    val result = userRepository.refreshCurrentUser(userId)
    return result is NetworkResult.Success
  }

  override suspend fun tryRestoreSession(): Boolean {
    tokenProvider.getValidAccessToken() ?: return false
    val authState = TokenStorage.load(appContext) ?: return false
    val userId = extractUserId(authState)
    val result = userRepository.refreshCurrentUser(userId)
    return result is NetworkResult.Success
  }

  private fun extractUserId(authState: AuthState): String =
    authState.parsedIdToken?.subject!!.split(":")[2]

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

  override suspend fun logout(context: Context) {
    val authState = TokenStorage.load(appContext) ?: return
    val authService = AuthorizationService(appContext)

    val serviceConfig = authState.authorizationServiceConfiguration

    val idToken = authState.idToken

    TokenStorage.clear(appContext)

    if(serviceConfig != null && idToken != null){
      userRepository.clear()
      val endSessionRequest = EndSessionRequest.Builder(serviceConfig)
        .setIdTokenHint(idToken)
        .setPostLogoutRedirectUri(
          "com.picshare.app:/oauth2redirect".toUri()
        )
        .build()
      authService.performEndSessionRequest(
        endSessionRequest,
        PendingIntent.getActivity(
          context,
          0,
          Intent(context, LoginActivity::class.java),
          PendingIntent.FLAG_IMMUTABLE)
      )
    }
    authService.dispose()
  }
}