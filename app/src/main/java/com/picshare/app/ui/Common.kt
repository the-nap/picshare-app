package com.picshare.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.picshare.app.R
import com.picshare.app.ui.theme.PicshareTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header() {
  CenterAlignedTopAppBar(
    modifier = Modifier
      .fillMaxWidth(),
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background
    ),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Image(
          painter = painterResource(R.drawable.logo_image),
          contentDescription = "Logo",
          modifier = Modifier.size(24.dp),
          contentScale = ContentScale.Fit
        )
        Image(
          painter = painterResource(R.drawable.logo_text),
          contentDescription = "Picshare",
          modifier = Modifier.height(18.dp),
          contentScale = ContentScale.Fit
        )
      }
    }
  )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun HeaderPreview() {
  PicshareTheme {
    Header()
  }
}
