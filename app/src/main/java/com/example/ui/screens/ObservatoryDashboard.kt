package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.BorderAmber
import com.example.ui.theme.BorderAmberStrong
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ObservatorySurface
import com.example.ui.theme.ObservatorySurfaceContainer
import com.example.ui.theme.ObservatorySurfaceContainerHigh
import com.example.ui.theme.ObservatorySurfaceVariant
import com.example.ui.theme.ObservatoryVoid
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.PhosphorGreen
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ObservatoryViewModel
import com.example.ui.viewmodel.ReconDestination
import com.example.ui.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ObservatoryDashboard(
  viewModel: ObservatoryViewModel,
  onNavigate: (ReconDestination) -> Unit
) {
  val watchlistState by viewModel.watchlistState.collectAsState()
  val targetCount = when (val state = watchlistState) {
    is UiState.Success -> state.data.size
    else -> 0
  }

  val currentTime = SimpleDateFormat("HH:mm:ss 'UTC'", Locale.US).format(Date())

  val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
  val sweepAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(3500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "SweepAngle"
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(ObservatoryVoid)
      .padding(horizontal = 16.dp)
      .testTag("observatory_dashboard")
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))

      // Hero Command Status Banner
      Surface(
        color = ObservatorySurfaceContainer,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderAmberStrong),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(52.dp)
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

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "GOD'S EYE OBSERVATORY",
                style = MaterialTheme.typography.titleLarge,
                color = AmberPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = "GLOBAL SURVEILLANCE & RECONNAISSANCE",
                style = MaterialTheme.typography.labelSmall,
                color = TacticalCyan,
                fontSize = 10.sp
              )
            }

            Surface(
              color = Color(0x2200E676),
              shape = RoundedCornerShape(6.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, PhosphorGreen)
            ) {
              Text(
                text = "ONLINE",
                style = MaterialTheme.typography.labelSmall,
                color = PhosphorGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // System telemetry line
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "SECTOR 7G // LEO-014 ORBIT",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = currentTime,
              style = MaterialTheme.typography.labelSmall,
              color = AmberLight,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4 Key Telemetry HUD Panels
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        TelemetryCard(
          modifier = Modifier.weight(1f),
          title = "CLOUD DOSSIERS",
          value = "$targetCount",
          unit = "TARGETS",
          color = AmberPrimary,
          onClick = { onNavigate(ReconDestination.DOSSIER_WATCHLIST) }
        )
        TelemetryCard(
          modifier = Modifier.weight(1f),
          title = "GLOBAL NODES",
          value = "10",
          unit = "ACTIVE",
          color = TacticalCyan,
          onClick = { onNavigate(ReconDestination.ORBITAL_GLOBE) }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        TelemetryCard(
          modifier = Modifier.weight(1f),
          title = "RECON PLATFORMS",
          value = "6",
          unit = "YT/RBX/GH/TT/TW/KC",
          color = AmberLight,
          onClick = { onNavigate(ReconDestination.PUBLIC_RECON) }
        )
        TelemetryCard(
          modifier = Modifier.weight(1f),
          title = "AI INTEL LINK",
          value = "READY",
          unit = "GEMINI 3.8/4",
          color = PhosphorGreen,
          onClick = { onNavigate(ReconDestination.ANALYST_AI) }
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Tactical Mini-Radar & Orbit Preview Card
      Surface(
        color = ObservatorySurfaceContainer,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderAmber),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Radar,
                contentDescription = null,
                tint = AmberPrimary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "ORBITAL SCAN RETICLE",
                style = MaterialTheme.typography.labelSmall,
                color = AmberPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
            Text(
              text = "ALT: 412 KM",
              style = MaterialTheme.typography.labelSmall,
              color = TacticalCyan,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Rotating Radar Canvas
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(ObservatoryVoid),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val cx = size.width / 2f
              val cy = size.height / 2f
              val radius = minOf(cx, cy) * 0.85f

              // Radar grid rings
              drawCircle(
                color = Color(0x33FFB300),
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = 1f)
              )
              drawCircle(
                color = Color(0x22FFB300),
                radius = radius * 0.65f,
                center = Offset(cx, cy),
                style = Stroke(width = 0.8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
              )
              drawCircle(
                color = Color(0x2200E5FF),
                radius = radius * 0.35f,
                center = Offset(cx, cy),
                style = Stroke(width = 0.8f)
              )

              // Crosshairs
              drawLine(
                color = Color(0x22FFB300),
                start = Offset(cx - radius, cy),
                end = Offset(cx + radius, cy),
                strokeWidth = 1f
              )
              drawLine(
                color = Color(0x22FFB300),
                start = Offset(cx, cy - radius),
                end = Offset(cx, cy + radius),
                strokeWidth = 1f
              )

              // Rotating sweep beam
              drawArc(
                brush = Brush.sweepGradient(
                  colors = listOf(
                    Color.Transparent,
                    Color(0x05FFB300),
                    Color(0x33FFB300)
                  ),
                  center = Offset(cx, cy)
                ),
                startAngle = sweepAngle,
                sweepAngle = 55f,
                useCenter = true,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2)
              )

              // Static observation blips
              val blips = listOf(
                Offset(cx + radius * 0.45f, cy - radius * 0.35f),
                Offset(cx - radius * 0.5f, cy + radius * 0.2f),
                Offset(cx + radius * 0.2f, cy + radius * 0.55f),
                Offset(cx - radius * 0.25f, cy - radius * 0.5f)
              )

              blips.forEach { blip ->
                drawCircle(color = PhosphorGreen.copy(alpha = 0.5f), radius = 5f, center = blip, style = Stroke(1f))
                drawCircle(color = PhosphorGreen, radius = 2.5f, center = blip)
              }
            }

            // Central button to launch full globe
            Button(
              onClick = { onNavigate(ReconDestination.ORBITAL_GLOBE) },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xCC17202D),
                contentColor = AmberPrimary
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.testTag("launch_full_globe_button")
            ) {
              Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "OPEN 3D GLOBE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section: Tactical Operations
      Text(
        text = "TACTICAL OPERATIONS // QUICK ACCESS",
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        fontSize = 11.sp,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Grid of 4 Launcher Cards
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OperationCard(
            modifier = Modifier.weight(1f),
            title = "3D ORBITAL GLOBE",
            description = "Interactive 3D Earth projection with sensor angles and world node pins.",
            icon = Icons.Default.Public,
            accentColor = AmberPrimary,
            tag = "op_card_globe",
            onClick = { onNavigate(ReconDestination.ORBITAL_GLOBE) }
          )
          OperationCard(
            modifier = Modifier.weight(1f),
            title = "RECON HUD",
            description = "Live creator lookup across YouTube, Roblox, GitHub, TikTok, Twitch, Kick.",
            icon = Icons.Default.Search,
            accentColor = TacticalCyan,
            tag = "op_card_recon",
            onClick = { onNavigate(ReconDestination.PUBLIC_RECON) }
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OperationCard(
            modifier = Modifier.weight(1f),
            title = "CLOUD DOSSIER",
            description = "Zero-trust encrypted watchlist persisted in Firestore with custom intel notes.",
            icon = Icons.Default.FolderSpecial,
            accentColor = PhosphorGreen,
            tag = "op_card_dossier",
            onClick = { onNavigate(ReconDestination.DOSSIER_WATCHLIST) }
          )
          OperationCard(
            modifier = Modifier.weight(1f),
            title = "AI ANALYST",
            description = "Gemini multimodal intelligence terminal with real-time Google Search grounding.",
            icon = Icons.Default.Psychology,
            accentColor = AmberLight,
            tag = "op_card_analyst",
            onClick = { onNavigate(ReconDestination.ANALYST_AI) }
          )
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // Section: Monitored Targets Carousel
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "MONITORED TARGET DOSSIERS",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted,
          fontSize = 11.sp,
          letterSpacing = 1.sp
        )
        Text(
          text = "VIEW ALL",
          style = MaterialTheme.typography.labelSmall,
          color = AmberPrimary,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.clickable { onNavigate(ReconDestination.DOSSIER_WATCHLIST) }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      when (val state = watchlistState) {
        is UiState.Success -> {
          if (state.data.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              items(state.data.take(6)) { target ->
                Surface(
                  color = ObservatorySurfaceContainer,
                  shape = RoundedCornerShape(10.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, BorderAmber),
                  modifier = Modifier
                    .width(180.dp)
                    .clickable { onNavigate(ReconDestination.DOSSIER_WATCHLIST) }
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      if (!target.avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                          model = target.avatarUrl,
                          contentDescription = target.displayName,
                          modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.dp, AmberPrimary, CircleShape),
                          contentScale = ContentScale.Crop
                        )
                      } else {
                        Box(
                          modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(ObservatorySurfaceVariant)
                            .border(1.dp, AmberPrimary, CircleShape),
                          contentAlignment = Alignment.Center
                        ) {
                          Text(
                            text = target.username.take(2).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberPrimary
                          )
                        }
                      }
                      Spacer(modifier = Modifier.width(8.dp))
                      Column {
                        Text(
                          text = target.displayName,
                          style = MaterialTheme.typography.labelMedium,
                          color = TextPrimary,
                          maxLines = 1,
                          fontSize = 12.sp
                        )
                        Text(
                          text = target.platform,
                          style = MaterialTheme.typography.labelSmall,
                          color = TacticalCyan,
                          fontSize = 9.sp
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = "${target.followerCount} ${target.metricLabel}",
                      style = MaterialTheme.typography.labelSmall,
                      color = AmberLight,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          } else {
            // Preset suggestion strip
            Surface(
              color = ObservatorySurfaceContainer,
              shape = RoundedCornerShape(10.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.GpsFixed,
                  contentDescription = null,
                  tint = AmberLight,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "NO TARGETS PINNED YET",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "Launch Recon HUD to scan and lock creators into your cloud dossier.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                  )
                }
                Button(
                  onClick = { onNavigate(ReconDestination.PUBLIC_RECON) },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = AmberPrimary,
                    contentColor = ObservatoryVoid
                  ),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text("Scan", fontSize = 11.sp)
                }
              }
            }
          }
        }
        else -> {
          Spacer(modifier = Modifier.height(8.dp))
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Observatory Security & Log Stream
      Surface(
        color = ObservatorySurface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = PhosphorGreen,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "OBSERVATORY LOG FEED // CIPHER ZERO-TRUST",
              style = MaterialTheme.typography.labelSmall,
              color = PhosphorGreen,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "• [LEO-014] Orbit telemetry synchronized at 412 km altitude\n• [FIRESTORE] Cloud collection encrypted with user-isolated rules\n• [GEMINI] Satellite intelligence grounded with Google Search",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(90.dp))
    }
  }
}

@Composable
private fun TelemetryCard(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  unit: String,
  color: Color,
  onClick: () -> Unit
) {
  Surface(
    color = ObservatorySurfaceContainer,
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
    modifier = modifier.clickable { onClick() }
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary,
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        color = color,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = unit,
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        fontSize = 8.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}

@Composable
private fun OperationCard(
  modifier: Modifier = Modifier,
  title: String,
  description: String,
  icon: ImageVector,
  accentColor: Color,
  tag: String,
  onClick: () -> Unit
) {
  Surface(
    color = ObservatorySurfaceContainer,
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
    modifier = modifier
      .clickable { onClick() }
      .testTag(tag)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(accentColor.copy(alpha = 0.15f))
          .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        maxLines = 3
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = "LAUNCH",
          style = MaterialTheme.typography.labelSmall,
          color = accentColor,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(10.dp)
        )
      }
    }
  }
}
