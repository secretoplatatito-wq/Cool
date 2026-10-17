package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ObservatoryDarkColorScheme = darkColorScheme(
  primary = AmberPrimary,
  onPrimary = ObsidianBlack,
  primaryContainer = AmberContainer,
  onPrimaryContainer = OnAmberContainer,
  secondary = TacticalCyan,
  onSecondary = ObsidianBlack,
  secondaryContainer = Color(0xFF00363D),
  onSecondaryContainer = Color(0xFF80F1FF),
  tertiary = PhosphorGreen,
  onTertiary = ObsidianBlack,
  background = ObsidianBlack,
  onBackground = TextPrimary,
  surface = ObsidianSurface,
  onSurface = TextPrimary,
  surfaceVariant = ObsidianElevated,
  onSurfaceVariant = TextSecondary,
  outline = BorderAmber,
  outlineVariant = BorderSubtle,
  error = CrimsonAlert,
  onError = ObsidianBlack
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = ObservatoryDarkColorScheme,
    typography = Typography,
    content = content
  )
}
