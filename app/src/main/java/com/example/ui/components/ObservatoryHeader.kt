package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.BorderAmber
import com.example.ui.theme.BorderAmberStrong
import com.example.ui.theme.ObservatorySurfaceContainer
import com.example.ui.theme.PhosphorGreen
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Reusable Holographic Glassmorphic Header for God's Eye Observatory.
 * Features an animated amber/phosphor pulsating status beacon, prominent branding,
 * customizable status label, and an optional action slot.
 */
@Composable
fun ObservatoryHeader(
  modifier: Modifier = Modifier,
  title: String = "God's Eye",
  subtitle: String = "TACTICAL OBSERVATORY // LEVEL 4 CLEARANCE",
  statusLabel: String = "ONLINE",
  statusColor: Color = AmberPrimary,
  showEmblem: Boolean = true,
  actions: @Composable (RowScope.() -> Unit)? = null
) {
  // Pulsating animation for the status beacon
  val infiniteTransition = rememberInfiniteTransition(label = "HeaderBeaconPulse")
  val beaconScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "BeaconScale"
  )
  val beaconAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "BeaconAlpha"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("observatory_header"),
    color = ObservatorySurfaceContainer.copy(alpha = 0.92f),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.2.dp,
      brush = Brush.horizontalGradient(
        colors = listOf(
          BorderAmberStrong,
          BorderAmber,
          TacticalCyan.copy(alpha = 0.35f),
          Color.Transparent
        )
      )
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (showEmblem) {
        // Glowing Holographic Emblem
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(AmberContainer)
            .border(1.5.dp, AmberPrimary, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_gods_eye_logo),
            contentDescription = "God's Eye Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            color = AmberPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.5.sp
          )
        }

        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall,
          color = TacticalCyan,
          fontSize = 9.sp,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 0.5.sp
        )
      }

      // Animated Status Beacon Pill
      Surface(
        color = statusColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.6f)),
        modifier = Modifier.padding(end = if (actions != null) 4.dp else 0.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier.size(10.dp),
            contentAlignment = Alignment.Center
          ) {
            // Pulsing halo
            Box(
              modifier = Modifier
                .size(10.dp)
                .scale(beaconScale)
                .background(statusColor.copy(alpha = beaconAlpha * 0.4f), CircleShape)
            )
            // Core beacon dot
            Box(
              modifier = Modifier
                .size(6.dp)
                .background(statusColor, CircleShape)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Text(
            text = statusLabel,
            style = MaterialTheme.typography.labelSmall,
            color = statusColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      // Optional Trailing Actions (e.g. Sign Out or Settings button)
      actions?.let { actionsScope ->
        actionsScope()
      }
    }
  }
}
