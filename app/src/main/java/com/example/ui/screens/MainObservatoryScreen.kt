package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.ui.components.Globe3DCanvas
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.BorderAmber
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.PhosphorGreen
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ObservatoryViewModel
import com.example.ui.viewmodel.ReconDestination

@Composable
fun MainObservatoryScreen(
  viewModel: ObservatoryViewModel,
  onSignOut: () -> Unit
) {
  val currentDestination by viewModel.activeDestination.collectAsState()
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    containerColor = ObsidianBlack,
    bottomBar = {
      ObservatoryBottomBar(
        currentDestination = currentDestination,
        onNavigate = { viewModel.setDestination(it) }
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .statusBarsPadding()
    ) {
      // Top Command Header
      ObservatoryHeader(
        onSignOut = {
          val credentialManager = CredentialManager.create(context)
          signOutUser(context, credentialManager, coroutineScope, onSignOut)
        }
      )

      // Main Destination View
      Box(modifier = Modifier.weight(1f)) {
        when (currentDestination) {
          ReconDestination.ORBITAL_GLOBE -> {
            Globe3DCanvas(
              modifier = Modifier.fillMaxSize(),
              onPinSelected = { pin ->
                viewModel.setSearchQuery(pin.name)
              }
            )
          }

          ReconDestination.PUBLIC_RECON -> {
            ReconScreen(
              viewModel = viewModel,
              onNavigateToAnalyst = { prompt ->
                viewModel.sendAnalystPrompt(prompt)
                viewModel.setDestination(ReconDestination.ANALYST_AI)
              }
            )
          }

          ReconDestination.DOSSIER_WATCHLIST -> {
            WatchlistScreen(viewModel = viewModel)
          }

          ReconDestination.ANALYST_AI -> {
            AnalystChatScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}

@Composable
fun ObservatoryHeader(
  onSignOut: () -> Unit
) {
  Surface(
    color = ObsidianCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderAmber),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(10.dp)
          .background(PhosphorGreen, CircleShape)
          .border(1.dp, Color.White, CircleShape)
      )

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "GOD'S EYE OBSERVATORY",
          style = MaterialTheme.typography.titleMedium,
          color = AmberPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Text(
          text = "SEC-LEVEL 4 // SATELLITE TELEMETRY ONLINE",
          style = MaterialTheme.typography.labelSmall,
          color = TacticalCyan,
          fontSize = 9.sp
        )
      }

      IconButton(
        onClick = onSignOut,
        modifier = Modifier.testTag("sign_out_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Logout,
          contentDescription = "Sign Out",
          tint = AmberLight
        )
      }
    }
  }
}

@Composable
fun ObservatoryBottomBar(
  currentDestination: ReconDestination,
  onNavigate: (ReconDestination) -> Unit
) {
  NavigationBar(
    containerColor = ObsidianCard,
    contentColor = AmberPrimary,
    modifier = Modifier
      .navigationBarsPadding()
      .border(1.dp, BorderAmber)
  ) {
    NavigationBarItem(
      selected = currentDestination == ReconDestination.ORBITAL_GLOBE,
      onClick = { onNavigate(ReconDestination.ORBITAL_GLOBE) },
      icon = {
        Icon(
          imageVector = Icons.Default.Public,
          contentDescription = "3D Globe"
        )
      },
      label = {
        Text(
          text = "3D GLOBE",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = ObsidianBlack,
        selectedTextColor = AmberPrimary,
        indicatorColor = AmberPrimary,
        unselectedIconColor = TextSecondary,
        unselectedTextColor = TextSecondary
      ),
      modifier = Modifier.testTag("nav_globe")
    )

    NavigationBarItem(
      selected = currentDestination == ReconDestination.PUBLIC_RECON,
      onClick = { onNavigate(ReconDestination.PUBLIC_RECON) },
      icon = {
        Icon(
          imageVector = Icons.Default.Radar,
          contentDescription = "Recon HUD"
        )
      },
      label = {
        Text(
          text = "RECON",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = ObsidianBlack,
        selectedTextColor = AmberPrimary,
        indicatorColor = AmberPrimary,
        unselectedIconColor = TextSecondary,
        unselectedTextColor = TextSecondary
      ),
      modifier = Modifier.testTag("nav_recon")
    )

    NavigationBarItem(
      selected = currentDestination == ReconDestination.DOSSIER_WATCHLIST,
      onClick = { onNavigate(ReconDestination.DOSSIER_WATCHLIST) },
      icon = {
        Icon(
          imageVector = Icons.Default.FolderSpecial,
          contentDescription = "Cloud Dossier"
        )
      },
      label = {
        Text(
          text = "DOSSIER",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = ObsidianBlack,
        selectedTextColor = AmberPrimary,
        indicatorColor = AmberPrimary,
        unselectedIconColor = TextSecondary,
        unselectedTextColor = TextSecondary
      ),
      modifier = Modifier.testTag("nav_dossier")
    )

    NavigationBarItem(
      selected = currentDestination == ReconDestination.ANALYST_AI,
      onClick = { onNavigate(ReconDestination.ANALYST_AI) },
      icon = {
        Icon(
          imageVector = Icons.Default.Psychology,
          contentDescription = "AI Analyst"
        )
      },
      label = {
        Text(
          text = "AI INTEL",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = ObsidianBlack,
        selectedTextColor = AmberPrimary,
        indicatorColor = AmberPrimary,
        unselectedIconColor = TextSecondary,
        unselectedTextColor = TextSecondary
      ),
      modifier = Modifier.testTag("nav_analyst")
    )
  }
}
