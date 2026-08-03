package com.picshare.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    background = AppBackground,
    surface = CardBackground1,
    surfaceVariant = CardBackground2,
    tertiary = Pink80
)


@Composable
fun PicshareTheme(
    content: @Composable () -> Unit
){
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}