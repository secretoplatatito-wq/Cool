package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PhosphorGreen
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class GlobePin(
  val name: String,
  val country: String,
  val latDeg: Float,
  val lonDeg: Float,
  val status: String = "ACTIVE"
)

val MajorObservationNodes = listOf(
  GlobePin("Tokyo", "Japan", 35.6762f, 139.6503f, "NODE-01"),
  GlobePin("New York", "USA", 40.7128f, -74.0060f, "NODE-02"),
  GlobePin("London", "UK", 51.5074f, -0.1278f, "NODE-03"),
  GlobePin("San Francisco", "USA", 37.7749f, -122.4194f, "NODE-04"),
  GlobePin("Singapore", "Singapore", 1.3521f, 103.8198f, "NODE-05"),
  GlobePin("Sydney", "Australia", -33.8688f, 151.2093f, "NODE-06"),
  GlobePin("Dubai", "UAE", 25.2048f, 55.2708f, "NODE-07"),
  GlobePin("Berlin", "Germany", 52.5200f, 13.4050f, "NODE-08"),
  GlobePin("São Paulo", "Brazil", -23.5505f, -46.6333f, "NODE-09"),
  GlobePin("Seoul", "South Korea", 37.5665f, 126.9780f, "NODE-10")
)

@Composable
fun Globe3DCanvas(
  modifier: Modifier = Modifier,
  onPinSelected: (GlobePin) -> Unit = {}
) {
  var yaw by remember { mutableFloatStateOf(0.4f) }
  var pitch by remember { mutableFloatStateOf(0.2f) }
  var autoRotate by remember { mutableStateOf(true) }
  var selectedPin by remember { mutableStateOf<GlobePin?>(null) }

  val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
  val radarSweepAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "RadarSweep"
  )

  val satelliteOrbitAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(12000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "SatelliteOrbit"
  )

  val autoRotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2 * PI).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(28000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "GlobeAutoRotate"
  )

  val effectiveYaw = if (autoRotate) yaw + autoRotationAngle else yaw

  Box(
    modifier = modifier
      .background(ObsidianBlack)
      .pointerInput(autoRotate) {
        detectDragGestures(
          onDragStart = { autoRotate = false },
          onDrag = { change, dragAmount ->
            change.consume()
            yaw += dragAmount.x * 0.005f
            pitch = (pitch - dragAmount.y * 0.005f).coerceIn(-1.2f, 1.2f)
          }
        )
      }
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(effectiveYaw, pitch) {
          detectTapGestures { tapOffset ->
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = minOf(cx, cy) * 0.72f

            var closestPin: GlobePin? = null
            var closestDist = Float.MAX_VALUE

            MajorObservationNodes.forEach { pin ->
              val (px, py, pz) = projectLatLon(pin.latDeg, pin.lonDeg, radius, effectiveYaw, pitch)
              if (pz > 0) {
                val dist = hypot(tapOffset.x - (cx + px), tapOffset.y - (cy + py))
                if (dist < 48f && dist < closestDist) {
                  closestDist = dist
                  closestPin = pin
                }
              }
            }

            closestPin?.let {
              selectedPin = it
              onPinSelected(it)
            }
          }
        }
    ) {
      val cx = size.width / 2f
      val cy = size.height / 2f
      val radius = minOf(cx, cy) * 0.72f

      // 1. Radar background halo
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0x33FFB300),
            Color(0x1100E5FF),
            Color.Transparent
          ),
          center = Offset(cx, cy),
          radius = radius * 1.35f
        ),
        radius = radius * 1.35f,
        center = Offset(cx, cy)
      )

      // 2. Tactical Radar Sweeper
      drawArc(
        brush = Brush.sweepGradient(
          colors = listOf(
            Color.Transparent,
            Color(0x05FFB300),
            Color(0x28FFB300)
          ),
          center = Offset(cx, cy)
        ),
        startAngle = radarSweepAngle,
        sweepAngle = 60f,
        useCenter = true,
        topLeft = Offset(cx - radius * 1.15f, cy - radius * 1.15f),
        size = androidx.compose.ui.geometry.Size(radius * 2.3f, radius * 2.3f)
      )

      // 3. Outer Reticle Rings
      drawCircle(
        color = Color(0x33FFB300),
        radius = radius * 1.15f,
        center = Offset(cx, cy),
        style = Stroke(
          width = 1.2f,
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
        )
      )

      drawCircle(
        color = Color(0x2200E5FF),
        radius = radius * 1.05f,
        center = Offset(cx, cy),
        style = Stroke(width = 1f)
      )

      // 4. Globe Sphere Base Silhouette
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF141A24),
            Color(0xFF0C1017)
          ),
          center = Offset(cx - radius * 0.3f, cy - radius * 0.3f),
          radius = radius
        ),
        radius = radius,
        center = Offset(cx, cy)
      )

      // 5. Parallels (Latitude Graticules)
      val latSteps = listOf(-60f, -30f, 0f, 30f, 60f)
      latSteps.forEach { lat ->
        drawLatitudeRing(lat, radius, cx, cy, effectiveYaw, pitch)
      }

      // 6. Meridians (Longitude Graticules)
      val lonSteps = listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f)
      lonSteps.forEach { lon ->
        drawLongitudeArc(lon, radius, cx, cy, effectiveYaw, pitch)
      }

      // 7. Tactical Orbital Ring with Recon Satellite
      drawOrbitalRing(radius * 1.25f, cx, cy, effectiveYaw * 0.5f, pitch * 0.5f, satelliteOrbitAngle)

      // 8. Observation Nodes (Pins)
      MajorObservationNodes.forEach { pin ->
        val (px, py, pz) = projectLatLon(pin.latDeg, pin.lonDeg, radius, effectiveYaw, pitch)
        if (pz > 0) {
          val isSelected = selectedPin?.name == pin.name
          val pinColor = if (isSelected) PhosphorGreen else AmberPrimary
          val center = Offset(cx + px, cy + py)

          // Pulsing pin ring
          drawCircle(
            color = pinColor.copy(alpha = 0.4f),
            radius = if (isSelected) 10f else 6f,
            center = center,
            style = Stroke(width = 1.5f)
          )

          drawCircle(
            color = pinColor,
            radius = if (isSelected) 4.5f else 2.8f,
            center = center
          )
        }
      }

      // 9. HUD Reticle Crosshair Brackets
      drawHudBrackets(cx, cy, radius * 1.25f)
    }

    // Top Telemetry Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "ORBITAL TELEMETRY // GOD'S EYE",
          style = MaterialTheme.typography.labelSmall,
          color = AmberPrimary,
          fontSize = 11.sp
        )
        val latText = String.format("%.2f° %s", kotlin.math.abs(pitch * 180f / PI), if (pitch >= 0) "N" else "S")
        val lonText = String.format("%.2f° %s", kotlin.math.abs((effectiveYaw * 180f / PI) % 360f), "E")
        Text(
          text = "LAT: $latText | LON: $lonText",
          style = MaterialTheme.typography.labelSmall,
          color = TacticalCyan,
          fontSize = 10.sp
        )
      }

      IconButton(
        onClick = { autoRotate = !autoRotate },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.RotateRight,
          contentDescription = "Toggle Orbit Auto-Rotation",
          tint = if (autoRotate) PhosphorGreen else TextSecondary
        )
      }

      IconButton(
        onClick = {
          yaw = 0f
          pitch = 0.2f
          autoRotate = true
          selectedPin = null
        },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Default.MyLocation,
          contentDescription = "Reset Sensor Angles",
          tint = AmberPrimary
        )
      }
    }

    // Selected Pin Card at bottom of Globe
    selectedPin?.let { pin ->
      Surface(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(16.dp)
          .fillMaxWidth(0.92f),
        color = Color(0xF0121721),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFB300))
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.GpsFixed,
            contentDescription = null,
            tint = PhosphorGreen,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.size(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "${pin.name}, ${pin.country}".uppercase(),
              style = MaterialTheme.typography.titleLarge,
              color = TextPrimary,
              fontSize = 14.sp
            )
            Text(
              text = "STATUS: ${pin.status} // COORD: ${pin.latDeg}°N, ${pin.lonDeg}°E",
              style = MaterialTheme.typography.labelSmall,
              color = TacticalCyan,
              fontSize = 10.sp
            )
          }
          IconButton(onClick = { selectedPin = null }) {
            Icon(
              imageVector = Icons.Default.Radar,
              contentDescription = "Dismiss Pin",
              tint = AmberLight
            )
          }
        }
      }
    }
  }
}

private fun projectLatLon(
  latDeg: Float,
  lonDeg: Float,
  radius: Float,
  yaw: Float,
  pitch: Float
): Triple<Float, Float, Float> {
  val latRad = (latDeg * PI / 180f).toFloat()
  val lonRad = (lonDeg * PI / 180f).toFloat()

  val x = radius * cos(latRad) * sin(lonRad)
  val y = -radius * sin(latRad)
  val z = radius * cos(latRad) * cos(lonRad)

  // Yaw around Y
  val x1 = x * cos(yaw) + z * sin(yaw)
  val z1 = -x * sin(yaw) + z * cos(yaw)

  // Pitch around X
  val y2 = y * cos(pitch) - z1 * sin(pitch)
  val z2 = y * sin(pitch) + z1 * cos(pitch)

  return Triple(x1, y2, z2)
}

private fun DrawScope.drawLatitudeRing(
  latDeg: Float,
  radius: Float,
  cx: Float,
  cy: Float,
  yaw: Float,
  pitch: Float
) {
  val path = Path()
  var started = false

  for (lon in 0..360 step 6) {
    val (px, py, pz) = projectLatLon(latDeg, lon.toFloat(), radius, yaw, pitch)
    if (pz > 0) {
      val pt = Offset(cx + px, cy + py)
      if (!started) {
        path.moveTo(pt.x, pt.y)
        started = true
      } else {
        path.lineTo(pt.x, pt.y)
      }
    } else {
      started = false
    }
  }

  drawPath(
    path = path,
    color = Color(0x33FFB300),
    style = Stroke(width = if (latDeg == 0f) 1.5f else 0.8f)
  )
}

private fun DrawScope.drawLongitudeArc(
  lonDeg: Float,
  radius: Float,
  cx: Float,
  cy: Float,
  yaw: Float,
  pitch: Float
) {
  val path = Path()
  var started = false

  for (lat in -90..90 step 5) {
    val (px, py, pz) = projectLatLon(lat.toFloat(), lonDeg, radius, yaw, pitch)
    if (pz > 0) {
      val pt = Offset(cx + px, cy + py)
      if (!started) {
        path.moveTo(pt.x, pt.y)
        started = true
      } else {
        path.lineTo(pt.x, pt.y)
      }
    } else {
      started = false
    }
  }

  drawPath(
    path = path,
    color = Color(0x2800E5FF),
    style = Stroke(width = 0.8f)
  )
}

private fun DrawScope.drawOrbitalRing(
  orbitRadius: Float,
  cx: Float,
  cy: Float,
  yaw: Float,
  pitch: Float,
  satAngleDeg: Float
) {
  val path = Path()
  var started = false

  for (angle in 0..360 step 6) {
    val rad = (angle * PI / 180f).toFloat()
    val x = orbitRadius * cos(rad)
    val y = 0f
    val z = orbitRadius * sin(rad)

    val x1 = x * cos(yaw) + z * sin(yaw)
    val z1 = -x * sin(yaw) + z * cos(yaw)
    val y2 = y * cos(pitch) - z1 * sin(pitch)
    val z2 = y * sin(pitch) + z1 * cos(pitch)

    if (z2 > -orbitRadius * 0.4f) {
      val pt = Offset(cx + x1, cy + y2)
      if (!started) {
        path.moveTo(pt.x, pt.y)
        started = true
      } else {
        path.lineTo(pt.x, pt.y)
      }
    } else {
      started = false
    }
  }

  drawPath(
    path = path,
    color = Color(0x3300E5FF),
    style = Stroke(
      width = 1f,
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
  )

  // Satellite beacon point
  val satRad = (satAngleDeg * PI / 180f).toFloat()
  val sx = orbitRadius * cos(satRad)
  val sy = 0f
  val sz = orbitRadius * sin(satRad)

  val sx1 = sx * cos(yaw) + sz * sin(yaw)
  val sz1 = -sx * sin(yaw) + sz * cos(yaw)
  val sy2 = sy * cos(pitch) - sz1 * sin(pitch)
  val sz2 = sy * sin(pitch) + sz1 * cos(pitch)

  if (sz2 > -orbitRadius * 0.4f) {
    val satPt = Offset(cx + sx1, cy + sy2)
    drawCircle(
      color = Color(0xFFFFD54F),
      radius = 4f,
      center = satPt
    )
    drawCircle(
      color = Color(0x66FFB300),
      radius = 9f,
      center = satPt,
      style = Stroke(width = 1.2f)
    )
  }
}

private fun DrawScope.drawHudBrackets(cx: Float, cy: Float, size: Float) {
  val bracketLen = 22f
  val half = size * 0.95f
  val color = Color(0x55FFB300)
  val stroke = 1.8f

  // Top-left
  drawLine(color, Offset(cx - half, cy - half + bracketLen), Offset(cx - half, cy - half), stroke)
  drawLine(color, Offset(cx - half, cy - half), Offset(cx - half + bracketLen, cy - half), stroke)

  // Top-right
  drawLine(color, Offset(cx + half - bracketLen, cy - half), Offset(cx + half, cy - half), stroke)
  drawLine(color, Offset(cx + half, cy - half), Offset(cx + half, cy - half + bracketLen), stroke)

  // Bottom-left
  drawLine(color, Offset(cx - half, cy + half - bracketLen), Offset(cx - half, cy + half), stroke)
  drawLine(color, Offset(cx - half, cy + half), Offset(cx - half + bracketLen, cy + half), stroke)

  // Bottom-right
  drawLine(color, Offset(cx + half - bracketLen, cy + half), Offset(cx + half, cy + half), stroke)
  drawLine(color, Offset(cx + half, cy + half), Offset(cx + half, cy + half - bracketLen), stroke)
}
