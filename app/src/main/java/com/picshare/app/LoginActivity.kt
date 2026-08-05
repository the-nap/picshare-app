package com.picshare.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.picshare.app.auth.AuthManager
import com.picshare.app.ui.theme.PicshareTheme
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService

class LoginActivity : ComponentActivity() {
  private lateinit var authService: AuthorizationService

  private val authLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    handleAuthorizationResult(result.resultCode, result.data)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    authService = AuthorizationService(this)

    enableEdgeToEdge()
    setContent {
      PicshareTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
        ) {
          LoginScreen()
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    authService.dispose()
  }

  private fun startLogin() {
    lifecycleScope.launch {
      try {
        val serviceConfig = AuthManager.discoverServiceConfig()
        val authRequest = AuthManager.buildAuthRequest(serviceConfig)
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
      } catch (e: Exception) {}
    }
  }

  private fun handleAuthorizationResult(resultCode: Int, data: Intent?){
    if (resultCode == Activity.RESULT_CANCELED || data == null){
      return
    }
    val response = net.openid.appauth.AuthorizationResponse.fromIntent(data)
    val ex = net.openid.appauth.AuthorizationException.fromIntent(data)
    if(response != null){
      AuthManager.exchangeCodeForTokens(response, authService, this)
    } else {
      Log.e(LoginActivity::class.java.name, ex?.error!!)
    }
  }

}

@Composable
fun LoginScreen() {
  Box(
    modifier = Modifier.fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center,
  ){
    LoginCard()

  }
}

@Composable
fun LoginCard() {
  Card(
    elevation = CardDefaults.cardElevation(
      defaultElevation = 24.dp
    ),
    modifier = Modifier
      .padding(16.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(){
      Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = ""
      )
      Spacer(Modifier.height(48.dp))

      LoginButtons()
    }
  }
}

@Composable
fun LoginButtons(){
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .shadow(8.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(32.dp)
    ) {
      Text( text = "Get started by signing in to your account")
      Spacer(Modifier.height(24.dp))
    }
  }

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
  PicshareTheme {
    LoginScreen()
  }
}