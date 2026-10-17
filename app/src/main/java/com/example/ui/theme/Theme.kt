package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ObservatoryColorScheme = darkColorScheme(
  primary = AmberPrimary,
  onPrimary = ObservatoryVoid,
  primaryContainer = AmberContainer,
  onPrimaryContainer = OnAmberContainer,
  inversePrimary = AmberDark,

  secondary = TacticalCyan,
  onSecondary = ObservatoryVoid,
  secondaryContainer = TacticalCyanContainer,
  onSecondaryContainer = OnTacticalCyanContainer,

  tertiary = PhosphorGreen,
  onTertiary = ObservatoryVoid,
  tertiaryContainer = PhosphorGreenContainer,
  onTertiaryContainer = OnPhosphorGreenContainer,

  background = ObservatoryVoid,
  onBackground = TextPrimary,

  surface = ObservatorySurface,
  onSurface = TextPrimary,
  surfaceVariant = ObservatorySurfaceVariant,
  onSurfaceVariant = TextSecondary,
  surfaceTint = AmberPrimary,
  inverseSurface = TextPrimary,
  inverseOnSurface = ObservatoryVoid,

  error = CrimsonAlert,
  onError = ObservatoryVoid,
  errorContainer = CrimsonAlertContainer,
  onErrorContainer = OnCrimsonAlertContainer,

  outline = BorderAmber,
  outlineVariant = BorderSubtle,
  scrim = Color(0xCC000000),

  surfaceContainerLowest = ObservatoryVoid,
  surfaceContainerLow = ObservatorySurface,
  surfaceContainer = ObservatorySurfaceContainer,
  surfaceContainerHigh = ObservatorySurfaceContainerHigh,
  surfaceContainerHighest = ObservatorySurfaceContainerHighest
)

@Composable
fun ObservatoryTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = ObservatoryColorScheme,
    typography = Typography,
    shapes = ObservatoryShapes,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  ObservatoryTheme(content = content)
}
