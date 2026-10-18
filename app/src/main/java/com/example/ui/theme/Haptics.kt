package com.example.ui.theme

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Tactile feedback triggers providing crisp physical responses
 * on primary interactive milestones inspired by unseen.co.
 */
object TacticalHaptics {
  fun click(haptic: HapticFeedback) {
    try {
      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    } catch (_: Exception) {}
  }

  fun lock(haptic: HapticFeedback) {
    try {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    } catch (_: Exception) {}
  }

  fun tick(haptic: HapticFeedback) {
    try {
      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    } catch (_: Exception) {}
  }
}
