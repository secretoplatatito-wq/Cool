package com.example.ui.screens

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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.viewmodel.ReconDestination
import com.example.ui.viewmodel.UiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WatchlistScreen(
  viewModel: ObservatoryViewModel
) {
  val watchlistState by viewModel.watchlistState.collectAsState()
  var platformFilter by remember { mutableStateOf("ALL") }
  var editingTarget by remember { mutableStateOf<ObservedTarget?>(null) }
  var targetToDelete by remember { mutableStateOf<ObservedTarget?>(null) }

  val filterOptions = listOf("ALL", "YOUTUBE", "ROBLOX", "GITHUB", "TIKTOK", "TWITCH", "KICK")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBlack)
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // Header Surface
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
          imageVector = Icons.Default.FolderSpecial,
          contentDescription = null,
          tint = AmberPrimary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "CLOUD DOSSIER // MONITORED TARGETS",
          style = MaterialTheme.typography.labelSmall,
          color = AmberLight,
          fontSize = 11.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Platform Filter Chips
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(filterOptions) { filter ->
        val isSelected = platformFilter == filter
        FilterChip(
          selected = isSelected,
          onClick = { platformFilter = filter },
          label = {
            Text(
              text = filter,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
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

    Spacer(modifier = Modifier.height(12.dp))

    // List or State View
    when (val state = watchlistState) {
      is UiState.Loading -> {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = AmberPrimary)
        }
      }

      is UiState.Error -> {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Surface(
            color = Color(0x22FF1744),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert),
            modifier = Modifier.padding(24.dp)
          ) {
            Text(
              text = "FIRESTORE SYNC ERROR: ${state.message}",
              color = CrimsonAlert,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(16.dp)
            )
          }
        }
      }

      is UiState.Success -> {
        val filteredTargets = state.data.filter {
          platformFilter == "ALL" || it.platform.equals(platformFilter, ignoreCase = true)
        }

        if (filteredTargets.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                tint = Color(0x44FFB300),
                modifier = Modifier.size(56.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "NO DOSSIERS ACQUIRED",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
              )
              Text(
                text = "Search public accounts in Recon HUD and click 'Add to Dossier' to persist them here.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
              Spacer(modifier = Modifier.height(18.dp))
              Button(
                onClick = { viewModel.setDestination(ReconDestination.PUBLIC_RECON) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = AmberPrimary,
                  contentColor = ObsidianBlack
                ),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Recon HUD")
              }
            }
          }
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            items(filteredTargets, key = { it.id }) { target ->
              WatchlistItemCard(
                target = target,
                onEditNotes = { editingTarget = target },
                onDelete = { targetToDelete = target }
              )
            }
            item {
              Spacer(modifier = Modifier.height(80.dp))
            }
          }
        }
      }
    }
  }

  // Edit Notes Dialog
  editingTarget?.let { target ->
    var notesText by remember { mutableStateOf(target.notes ?: "") }
    AlertDialog(
      onDismissRequest = { editingTarget = null },
      containerColor = ObsidianCard,
      title = {
        Text(
          text = "EDIT DOSSIER NOTES // @${target.username}",
          style = MaterialTheme.typography.titleMedium,
          color = AmberPrimary,
          fontSize = 14.sp
        )
      },
      text = {
        Column {
          Text(
            text = "Personal observation logs, milestone records, and verified flags:",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
          )
          OutlinedTextField(
            value = notesText,
            onValueChange = { notesText = it },
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AmberPrimary,
              unfocusedBorderColor = Color(0x44FFB300),
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateTargetNotes(target, notesText)
            editingTarget = null
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = AmberPrimary,
            contentColor = ObsidianBlack
          )
        ) {
          Text("Save Notes")
        }
      },
      dismissButton = {
        TextButton(onClick = { editingTarget = null }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }

  // Delete Confirmation Dialog
  targetToDelete?.let { target ->
    AlertDialog(
      onDismissRequest = { targetToDelete = null },
      containerColor = ObsidianCard,
      title = {
        Text(
          text = "PURGE DOSSIER?",
          style = MaterialTheme.typography.titleMedium,
          color = CrimsonAlert
        )
      },
      text = {
        Text(
          text = "Are you sure you want to remove @${target.username} (${target.platform}) from your cloud watchlist?",
          style = MaterialTheme.typography.bodyMedium,
          color = TextPrimary
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteFromWatchlist(target.id)
            targetToDelete = null
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = CrimsonAlert,
            contentColor = TextPrimary
          )
        ) {
          Text("Purge Target")
        }
      },
      dismissButton = {
        TextButton(onClick = { targetToDelete = null }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}

@Composable
fun WatchlistItemCard(
  target: ObservedTarget,
  onEditNotes: () -> Unit,
  onDelete: () -> Unit
) {
  val formattedCount = remember(target.followerCount) {
    NumberFormat.getNumberInstance(Locale.US).format(target.followerCount)
  }

  Surface(
    color = ObsidianCard,
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFB300)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("watchlist_item_${target.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (!target.avatarUrl.isNullOrBlank()) {
          AsyncImage(
            model = target.avatarUrl,
            contentDescription = "${target.displayName} Avatar",
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .border(1.dp, AmberPrimary, CircleShape),
            contentScale = ContentScale.Crop
          )
        } else {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(ObsidianElevated)
              .border(1.dp, AmberPrimary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = target.username.take(2).uppercase(),
              style = MaterialTheme.typography.titleMedium,
              color = AmberPrimary
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = target.displayName,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontSize = 15.sp,
            maxLines = 1
          )
          Text(
            text = "@${target.username} • ${target.platform}",
            style = MaterialTheme.typography.labelSmall,
            color = TacticalCyan,
            fontSize = 11.sp
          )
          Text(
            text = "$formattedCount ${target.metricLabel}",
            style = MaterialTheme.typography.labelSmall,
            color = AmberLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(onClick = onEditNotes) {
          Icon(
            imageVector = Icons.Default.EditNote,
            contentDescription = "Edit Notes",
            tint = AmberPrimary
          )
        }

        IconButton(onClick = onDelete) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete",
            tint = CrimsonAlert
          )
        }
      }

      if (!target.notes.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          color = ObsidianBlack,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "LOG: ${target.notes}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.padding(8.dp)
          )
        }
      }
    }
  }
}
