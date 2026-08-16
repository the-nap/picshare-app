package com.picshare.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PicshareDarkColors = darkColorScheme(
    primary = Color(0xFF63B3ED),
    onPrimary = Color(0xFF101114),

    background = Color(0xFF121316),
    onBackground = Color(0xFFF5F5F5),

    surface = Color(0xFF262A33),
    onSurface = Color(0xFFF5F5F5),

    surfaceContainer = Color(0xFF2D313C),

    onSurfaceVariant = Color(0xFFB5B8C0),
    outline = Color(0xFF555A66),

    secondary = Color(0xFF7B3FA0),
)

@Composable
fun PicshareTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PicshareDarkColors,
        content = content
    )
}
