package com.picshare.app.ui.theme

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ConfirmDialog(
  onConfirmation: () -> Unit,
  onDismissRequest: () -> Unit,
  dialogTitle: String,
  dialogText: String,
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