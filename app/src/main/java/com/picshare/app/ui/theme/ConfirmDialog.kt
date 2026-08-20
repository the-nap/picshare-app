package com.picshare.app.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun ConfirmDialog(
  onConfirmation: () -> Unit,
  onDismissRequest: () -> Unit,
  dialogTitle: String,
  dialogText: String,
  onConfirmMessage: String,
){
  val context = LocalContext.current
    AlertDialog(
      title = { Text(dialogTitle) },
      text = { Text(dialogText) },
      onDismissRequest = {
        onDismissRequest()
      },
      confirmButton = {
        TextButton(
          onClick = {
            onConfirmation()
          }
        ){
          Text("Confirm")
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            onDismissRequest()
          }
        ){
          Text("Dismiss")
        }
      }
    )
}