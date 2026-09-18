package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GarageDarkColorScheme = darkColorScheme(
  primary = GarageRed,
  onPrimary = GarageTextPrimary,
  primaryContainer = GarageRedDark,
  onPrimaryContainer = GarageTextPrimary,
  secondary = GarageRedLight,
  onSecondary = GarageTextPrimary,
  background = GarageBackground,
  onBackground = GarageTextPrimary,
  surface = GarageCard,
  onSurface = GarageTextPrimary,
  surfaceVariant = GarageCardElevated,
  onSurfaceVariant = GarageTextSecondary,
  outline = GarageBorder
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = GarageDarkColorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun MickoGarageTheme(
  content: @Composable () -> Unit
) {
  MyApplicationTheme(content = content)
}
