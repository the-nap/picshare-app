package com.picshare.app.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.picshare.app.R
import com.picshare.app.api.auth.AuthRepository
import com.picshare.app.api.auth.TokenProvider
import com.picshare.app.ui.theme.PicshareTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
  private val TAG: String? = LoginActivity::class.simpleName
  private var isLoggingIn by mutableStateOf(false)

  @Inject
  lateinit var authRepository: AuthRepository

  @Inject
  lateinit var tokenProvider: TokenProvider

  private val authLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    lifecycleScope.launch {
      try {
        Log.d(TAG, "resultCode=${result.resultCode}")
        Log.d(TAG, "data=${result.data}")
        Log.d(TAG, "data?.data(uri)=${result.data?.data}")
        Log.d(TAG, "extras keys=${result.data?.extras?.keySet()?.joinToString()}")

        if (result.resultCode != RESULT_OK) {
          Log.e(TAG, "Auth canceled/failed before repository handling")
          return@launch
        }
        val success = authRepository.handleAuthResponse(result.data)
        if (success)
          goToMainActivity()
        else {
          Log.e(TAG, "OAuth authentication failed")
        }
      } catch (e: Exception) {
        Log.e(TAG, "Error in authLauncher()", e)
      } finally {
        isLoggingIn = false
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PicshareTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          var isCheckingAuth by remember { mutableStateOf(true) }

          LaunchedEffect(Unit) {
            try {
              if (authRepository.tryRestoreSession()) {
                goToMainActivity()
                finish()
                return@LaunchedEffect
              }
            } catch (e: Exception) {
              Log.e(TAG, "Error checking existing token", e)
            }
            isCheckingAuth = false
          }

          if (isCheckingAuth) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator()
            }
          } else {
            LoginScreen()
          }
        }
      }
    }
  }

  fun goToMainActivity() {
    Log.d(TAG, "Going to Main Activity")
    startActivity(Intent(this, MainActivity::class.java))
    finish()
  }

  private fun startLogin() {
    isLoggingIn = true
    lifecycleScope.launch {
      try {
        Log.d(TAG, "AuthLauncher: $authLauncher")
        authLauncher.launch(authRepository.getAuthorizationRequest())
        Log.d(TAG, "AuthLauncher: All good")
      } catch (e: IllegalStateException) {
        Log.e(TAG, "Network Error()", e)
        Toast.makeText(
          this@LoginActivity,
          "Network error, server is probably offline",
          Toast.LENGTH_LONG
        )
          .show()
        isLoggingIn = false
      } catch (e: Exception) {
        Log.e(TAG, "Error in startLogin()", e)
        Toast.makeText(
          this@LoginActivity,
          "An error occurred while starting login",
          Toast.LENGTH_LONG
        )
          .show()
        isLoggingIn = false
      }
    }
  }

  @Composable
  fun LoginScreen() {
    Box(
      modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
      contentAlignment = Alignment.Center,
    ) {
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
      Column() {
        Image(
          painter = painterResource(id = R.drawable.logo_complete),
          contentDescription = ""
        )
        Spacer(Modifier.height(48.dp))

        LoginButtons()
      }
    }
  }

  @Composable
  fun LoginButtons() {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .shadow(8.dp),
      shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
      )
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
      ) {
        Text(text = "Get started by signing in to your account")
        Spacer(Modifier.height(24.dp))
        AccessButton(
          onClick = { startLogin() },
        )
      }
    }

  }

  @Composable
  fun AccessButton(
    onClick: () -> Unit,
  ) {
    Button(
      onClick = onClick,
      enabled = !isLoggingIn,
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .shadow(
          elevation = 8.dp,
          shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
          ambientColor = Color.Black.copy(alpha = 0.4f),
          spotColor = Color.Black.copy(alpha = 0.4f)
        ),
      shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF63B3ED),
        contentColor = Color(0xFF1A1E27)
      ),
      contentPadding = PaddingValues(
        horizontal = 45.dp,
        vertical = 18.dp
      )
    ) {
      if (isLoggingIn) {
        CircularProgressIndicator()
      } else {
        Text(
          text = "LOG IN",
          fontSize = 19.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.08.em
        )
      }
    }
  }
}