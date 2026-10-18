package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.TacticalHaptics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ObservedTarget
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.BorderAmber
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.PhosphorGreen
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ObservatoryViewModel
import com.example.ui.viewmodel.UiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReconScreen(
  viewModel: ObservatoryViewModel,
  onNavigateToAnalyst: (String) -> Unit = {}
) {
  val selectedPlatform by viewModel.selectedPlatform.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val activeTargetState by viewModel.activeTargetState.collectAsState()
  val actionStatus by viewModel.watchlistActionStatus.collectAsState()

  val platforms = listOf("YOUTUBE", "ROBLOX", "GITHUB", "TIKTOK", "TWITCH", "KICK")

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBlack)
      .padding(horizontal = 16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))

      // Status HUD Banner
      Surface(
        color = ObsidianElevated,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderAmber),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Radar,
            contentDescription = null,
            tint = AmberPrimary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "RECONNAISSANCE HUD // PUBLIC CREATOR MONITORING",
            style = MaterialTheme.typography.labelSmall,
            color = AmberLight,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Platform selection chips
      Text(
        text = "TARGET PLATFORM:",
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(platforms) { platform ->
          val isSelected = selectedPlatform == platform
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.setPlatform(platform) },
            label = {
              Text(
                text = platform,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = AmberPrimary,
              selectedLabelColor = ObsidianBlack,
              containerColor = ObsidianCard,
              labelColor = TextPrimary
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) AmberPrimary else Color(0x33FFFFFF)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        label = { Text("Enter account handle or username (e.g., mrbeast, torvalds)") },
        placeholder = { Text("Search public $selectedPlatform profile...") },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("recon_search_input"),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { viewModel.searchTarget() }),
        trailingIcon = {
          IconButton(
            onClick = { viewModel.searchTarget() },
            modifier = Modifier.testTag("recon_search_button")
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = AmberPrimary
            )
          }
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = AmberPrimary,
          unfocusedBorderColor = Color(0x44FFB300),
          focusedLabelColor = AmberLight,
          unfocusedLabelColor = TextSecondary,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
          focusedContainerColor = ObsidianCard,
          unfocusedContainerColor = ObsidianCard
        ),
        shape = RoundedCornerShape(10.dp)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Quick-Load Presets
      Text(
        text = "POPULAR TARGET PRESETS:",
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        fontSize = 10.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(viewModel.curatedPresets) { preset ->
          Surface(
            color = ObsidianCard,
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFB300)),
            modifier = Modifier.clickable { viewModel.selectPreset(preset) }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${preset.platform}: ",
                style = MaterialTheme.typography.labelSmall,
                color = TacticalCyan,
                fontSize = 10.sp
              )
              Text(
                text = "@${preset.username}",
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Watchlist Action Status Notification
      actionStatus?.let { msg ->
        Surface(
          color = Color(0x2200E676),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, PhosphorGreen),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = PhosphorGreen,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = msg,
              style = MaterialTheme.typography.bodyMedium,
              color = PhosphorGreen,
              fontSize = 12.sp
            )
          }
        }
      }
    }

    // Target Result Presentation
    item {
      when (val state = activeTargetState) {
        is UiState.Loading -> {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(color = AmberPrimary)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "SCANNING PLATFORM TELEMETRY...",
                style = MaterialTheme.typography.labelSmall,
                color = AmberLight
              )
            }
          }
        }

        is UiState.Error -> {
          Surface(
            color = Color(0x22FF1744),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "RECONNAISSANCE ERROR",
                style = MaterialTheme.typography.labelLarge,
                color = CrimsonAlert
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = state.message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
              )
            }
          }
        }

        is UiState.Success -> {
          TargetResultCard(
            target = state.data,
            onAddToWatchlist = { viewModel.addToWatchlist(state.data) },
            onAskAnalyst = { onNavigateToAnalyst("Provide a deep tactical analysis of ${state.data.platform} creator @${state.data.username}. Analyze audience reach, estimated growth milestones, and recent public activity.") }
          )
        }

        null -> {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Radar,
                contentDescription = null,
                tint = Color(0x33FFB300),
                modifier = Modifier.size(64.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "TARGET ACQUISITION IDLE",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
              )
              Text(
                text = "Select a platform and enter a public handle above to begin scan.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun TargetResultCard(
  target: ObservedTarget,
  onAddToWatchlist: () -> Unit,
  onAskAnalyst: () -> Unit
) {
  val haptic = LocalHapticFeedback.current
  val formattedCount = remember(target.followerCount) {
    NumberFormat.getNumberInstance(Locale.US).format(target.followerCount)
  }

  AnimatedVisibility(
    visible = true,
    enter = fadeIn() + slideInVertically()
  ) {
    Surface(
      color = ObsidianCard,
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberPrimary),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)
        .testTag("target_result_card")
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        // Card Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Avatar
          if (!target.avatarUrl.isNullOrBlank()) {
            AsyncImage(
              model = target.avatarUrl,
              contentDescription = "${target.displayName} Avatar",
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(1.5.dp, AmberPrimary, CircleShape),
              contentScale = ContentScale.Crop
            )
          } else {
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(ObsidianElevated)
                .border(1.5.dp, AmberPrimary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = target.username.take(2).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = AmberPrimary
              )
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = target.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontSize = 17.sp,
                maxLines = 1
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified",
                tint = TacticalCyan,
                modifier = Modifier.size(16.dp)
              )
            }

            Text(
              text = "@${target.username} • ${target.platform}",
              style = MaterialTheme.typography.labelSmall,
              color = TacticalCyan,
              fontSize = 11.sp
            )

            target.status?.let { status ->
              Text(
                text = "STATUS: $status",
                style = MaterialTheme.typography.labelSmall,
                color = PhosphorGreen,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 2.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Metric Box
        Surface(
          color = ObsidianBlack,
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFB300)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = target.metricLabel.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp
              )
              Text(
                text = formattedCount,
                style = MaterialTheme.typography.displayLarge,
                color = AmberPrimary,
                fontSize = 24.sp
              )
            }

            Surface(
              color = Color(0x2200E5FF),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "VERIFIED METRIC",
                style = MaterialTheme.typography.labelSmall,
                color = TacticalCyan,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        // Bio Section
        if (!target.bio.isNullOrBlank()) {
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "PUBLIC BIO // OVERVIEW:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = target.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
        }

        // Action Buttons
        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              TacticalHaptics.lock(haptic)
              onAddToWatchlist()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = AmberPrimary,
              contentColor = ObsidianBlack
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("add_to_watchlist_button")
          ) {
            Icon(
              imageVector = Icons.Default.BookmarkAdd,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Add to Dossier",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = {
              TacticalHaptics.click(haptic)
              onAskAnalyst()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF1E2838),
              contentColor = TacticalCyan
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("ask_analyst_button")
          ) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "AI Intel Report",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
