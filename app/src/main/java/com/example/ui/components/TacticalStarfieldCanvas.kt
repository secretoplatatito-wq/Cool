package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TextPrimary
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

private class CelestialParticle(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  val radius: Float,
  val color: Color,
  var twinklePhase: Float,
  val twinkleSpeed: Float
)

/**
 * High-performance hardware-accelerated interactive starfield canvas inspired by unseen.co.
 * Features touch-responsive gravitational drift, velocity damping, and subtle constellation lines.
 */
@Composable
fun TacticalStarfieldCanvas(
  modifier: Modifier = Modifier,
  particleCount: Int = 75,
  interactive: Boolean = true
) {
  var touchPos by remember { mutableStateOf<Offset?>(null) }
  var isTouching by remember { mutableStateOf(false) }

  // Mutable particle collection instantiated once to prevent GC overhead
  val particles = remember {
    val rng = Random(42)
    val colorPalette = listOf(
      AmberPrimary.copy(alpha = 0.85f),
      AmberLight.copy(alpha = 0.9f),
      TextPrimary.copy(alpha = 0.75f),
      TacticalCyan.copy(alpha = 0.65f),
      Color(0xFFFFE082).copy(alpha = 0.8f)
    )

    List(particleCount) {
      CelestialParticle(
        x = rng.nextFloat() * 1000f,
        y = rng.nextFloat() * 2000f,
        vx = (rng.nextFloat() - 0.5f) * 0.45f,
        vy = (rng.nextFloat() - 0.5f) * 0.45f,
        radius = 1.0f + rng.nextFloat() * 2.2f,
        color = colorPalette[rng.nextInt(colorPalette.size)],
        twinklePhase = rng.nextFloat() * 6.28f,
        twinkleSpeed = 0.025f + rng.nextFloat() * 0.045f
      )
    }
  }

  // Animation frame loop for 60/120Hz smooth kinetic drift
  var frameTick by remember { mutableFloatStateOf(0f) }
  LaunchedEffect(Unit) {
    while (true) {
      withFrameNanos { nanos ->
        frameTick = nanos / 1_000_000f
      }
    }
  }

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .then(
        if (interactive) {
          Modifier
            .pointerInput(Unit) {
              detectTapGestures(
                onPress = { offset ->
                  touchPos = offset
                  isTouching = true
                  tryAwaitRelease()
                  isTouching = false
                  touchPos = null
                }
              )
            }
            .pointerInput(Unit) {
              detectDragGestures(
                onDragStart = { offset ->
                  touchPos = offset
                  isTouching = true
                },
                onDragEnd = {
                  isTouching = false
                  touchPos = null
                },
                onDragCancel = {
                  isTouching = false
                  touchPos = null
                },
                onDrag = { change, _ ->
                  touchPos = change.position
                  change.consume()
                }
              )
            }
        } else {
          Modifier
        }
      )
  ) {
    // Read frameTick to trigger recomposition per frame
    val currentTick = frameTick
    val w = size.width
    val h = size.height

    if (w <= 0f || h <= 0f) return@Canvas

    val currentTouch = touchPos
    val gravityMagnitude = 180f

    // 1. Update and draw particles
    val pCount = particles.size
    for (i in 0 until pCount) {
      val p = particles[i]

      // Gravitational attraction toward touch coordinate
      if (isTouching && currentTouch != null) {
        val dx = currentTouch.x - p.x
        val dy = currentTouch.y - p.y
        val dist = hypot(dx, dy).coerceAtLeast(35f)
        val force = (gravityMagnitude / (dist * dist)).coerceIn(0.005f, 0.55f)
        p.vx += (dx / dist) * force
        p.vy += (dy / dist) * force
      }

      // Natural damping & subtle cosmic float
      p.vx *= 0.985f
      p.vy *= 0.985f

      // Ambient wandering drift
      p.x += p.vx + 0.12f * sin(p.twinklePhase)
      p.y += p.vy + 0.08f * cos(p.twinklePhase)

      // Screen edge boundary wrap
      if (p.x < -20f) p.x = w + 20f
      if (p.x > w + 20f) p.x = -20f
      if (p.y < -20f) p.y = h + 20f
      if (p.y > h + 20f) p.y = -20f

      // Twinkle calculation
      p.twinklePhase = (p.twinklePhase + p.twinkleSpeed) % 6.28318f
      val alphaMultiplier = 0.45f + 0.55f * ((sin(p.twinklePhase) + 1f) / 2f)

      // Draw particle core
      drawCircle(
        color = p.color.copy(alpha = (p.color.alpha * alphaMultiplier).coerceIn(0.1f, 1f)),
        radius = p.radius,
        center = Offset(p.x, p.y)
      )

      // Soft glow halo for larger stars
      if (p.radius > 2.0f) {
        drawCircle(
          color = p.color.copy(alpha = 0.15f * alphaMultiplier),
          radius = p.radius * 2.8f,
          center = Offset(p.x, p.y)
        )
      }
    }

    // 2. Subtle geometric constellation lines between proximate stars
    val connectionDistanceThreshold = 75f
    val maxConnectionsPerParticle = 2

    for (i in 0 until pCount) {
      val p1 = particles[i]
      var connected = 0

      for (j in (i + 1) until pCount) {
        val p2 = particles[j]
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        val dist = hypot(dx, dy)

        if (dist < connectionDistanceThreshold) {
          val lineAlpha = (1f - (dist / connectionDistanceThreshold)) * 0.12f
          drawLine(
            color = AmberLight.copy(alpha = lineAlpha),
            start = Offset(p1.x, p1.y),
            end = Offset(p2.x, p2.y),
            strokeWidth = 0.8f,
            cap = StrokeCap.Round
          )
          connected++
          if (connected >= maxConnectionsPerParticle) break
        }
      }
    }

    // 3. Subtle gravity well cursor ripple when dragging
    if (isTouching && currentTouch != null) {
      drawCircle(
        color = AmberPrimary.copy(alpha = 0.20f),
        radius = 28f,
        center = currentTouch,
        style = androidx.compose.ui.graphics.drawscope.Stroke(1.2f)
      )
      drawCircle(
        color = AmberPrimary.copy(alpha = 0.08f),
        radius = 48f,
        center = currentTouch,
        style = androidx.compose.ui.graphics.drawscope.Stroke(0.8f)
      )
    }
  }
}
