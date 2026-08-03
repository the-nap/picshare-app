package com.picshare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.picshare.app.ui.theme.PicshareTheme

class LoginActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
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