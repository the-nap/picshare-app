package com.picshare.app.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.picshare.app.R
import com.picshare.app.auth.AuthRepository
import com.picshare.app.ui.theme.PicshareTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginActivity : ComponentActivity() {
  private val TAG: String? = LoginActivity::class.simpleName
  @Inject
  lateinit var authRepository: AuthRepository

  private val authLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    lifecycleScope.launch {
      try{
        authRepository.handleAuthResponse(result.data)
        goToMainActivity()
      } catch(e: Exception){
        Log.e(TAG, e.message ?: "Error")
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if(authRepository.currentUserId != null) {
      goToMainActivity()
      return
    }



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

  private fun goToMainActivity(){
    startActivity(Intent(this, MainActivity::class.java))
    finish()
  }

  override fun onDestroy() {
    super.onDestroy()
  }

  private fun startLogin() {
    lifecycleScope.launch {
      try {
        authLauncher.launch(authRepository.getAuthorizationRequest())
      } catch (e: Exception) {
        Log.e(TAG, e.message ?: "Error during login")
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
        AccessButton {
          startLogin()
        }
      }
    }

  }
  @Composable
  fun AccessButton(
    onClick: () -> Unit
  ) {
    Button(
      onClick = onClick,
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
      Text(
        text = "LOG IN",
        fontSize = 19.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.08.em
      )
    }
  }
  @Preview(showBackground = true)
  @Composable
  fun LoginScreenPreview() {
    PicshareTheme {
      LoginScreen()
    }
  }
}