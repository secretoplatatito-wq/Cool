package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ObservedTarget
import com.example.data.repository.DomainIntelResult
import com.example.data.repository.EmailIntelResult
import com.example.data.repository.OsintAccountResult
import com.example.data.repository.PhoneIntelResult
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.BorderAmber
import com.example.ui.theme.BorderAmberStrong
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CoffeeCard
import com.example.ui.theme.CoffeeElevated
import com.example.ui.theme.CoffeeSurface
import com.example.ui.theme.CoffeeSurfaceVariant
import com.example.ui.theme.CreamIvory
import com.example.ui.theme.EspressoVoid
import com.example.ui.theme.LinenWhite
import com.example.ui.theme.ParchmentDim
import com.example.ui.theme.ParchmentMuted
import com.example.ui.theme.PhosphorGreen
import com.example.ui.theme.SensorCyan
import com.example.ui.theme.StatusSage
import com.example.ui.theme.TacticalHaptics
import com.example.ui.viewmodel.ObservatoryViewModel
import com.example.ui.viewmodel.ReconDestination
import com.example.ui.viewmodel.UiState

@Composable
fun OsintSuiteScreen(
  viewModel: ObservatoryViewModel,
  onSaveTarget: (ObservedTarget) -> Unit = { viewModel.addToWatchlist(it) },
  onNavigateToRecon: () -> Unit = { viewModel.setDestination(ReconDestination.PUBLIC_RECON) }
) {
  val haptic = LocalHapticFeedback.current
  val context = LocalContext.current
  var activeTab by remember { mutableIntStateOf(0) }
  var queryInput by remember { mutableStateOf("") }

  val tabs = listOf(
    "USERNAME" to "Sherlock / Blackbird",
    "PHONE" to "PhoneInfoga",
    "EMAIL" to "Holehe",
    "DOMAIN" to "Photon / SpiderFoot"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(EspressoVoid)
      .padding(horizontal = 16.dp)
      .testTag("osint_suite_screen")
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    // Editorial Header Badge with Quick Switch to Creator Recon
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "OPEN SOURCE INTELLIGENCE SUITE",
        style = MaterialTheme.typography.labelSmall,
        color = ChampagneGold,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
      )
      Surface(
        color = CoffeeElevated,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(0.6.dp, BorderSubtle),
        modifier = Modifier.clickable {
          TacticalHaptics.tick(haptic)
          onNavigateToRecon()
        }
      ) {
        Text(
          text = "CREATOR RECON ↗",
          style = MaterialTheme.typography.labelSmall,
          color = SensorCyan,
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 4 Modular Engine Tabs
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      tabs.forEachIndexed { index, (tabTitle, engineName) ->
        val isSelected = activeTab == index
        Surface(
          color = if (isSelected) ChampagneGold.copy(alpha = 0.18f) else CoffeeCard,
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) ChampagneGold else BorderSubtle
          ),
          modifier = Modifier
            .weight(1f)
            .clickable {
              TacticalHaptics.tick(haptic)
              activeTab = index
              queryInput = ""
            }
        ) {
          Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = tabTitle,
              style = MaterialTheme.typography.labelMedium,
              color = if (isSelected) ChampagneGold else CreamIvory,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp
            )
            Text(
              text = engineName.take(12),
              style = MaterialTheme.typography.labelSmall,
              color = ParchmentMuted,
              fontSize = 7.sp,
              fontFamily = FontFamily.Monospace,
              maxLines = 1
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Search Input Bar
    val placeholder = when (activeTab) {
      0 -> "Enter username (e.g. torvalds, mrbeast)..."
      1 -> "Enter phone with country code (e.g. +14155552671)..."
      2 -> "Enter email address (e.g. user@gmail.com)..."
      else -> "Enter domain (e.g. github.com, unseen.co)..."
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = queryInput,
        onValueChange = { queryInput = it },
        placeholder = { Text(placeholder, color = ParchmentDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
          onSearch = {
            TacticalHaptics.lock(haptic)
            when (activeTab) {
              0 -> viewModel.runUsernameHunt(queryInput)
              1 -> viewModel.runPhoneIntel(queryInput)
              2 -> viewModel.runEmailIntel(queryInput)
              3 -> viewModel.runDomainIntel(queryInput)
            }
          }
        ),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = CoffeeCard,
          unfocusedContainerColor = CoffeeCard,
          focusedTextColor = CreamIvory,
          unfocusedTextColor = CreamIvory,
          focusedBorderColor = ChampagneGold,
          unfocusedBorderColor = BorderAmber
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .weight(1f)
          .testTag("osint_input_field")
      )

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = {
          TacticalHaptics.lock(haptic)
          when (activeTab) {
            0 -> viewModel.runUsernameHunt(queryInput)
            1 -> viewModel.runPhoneIntel(queryInput)
            2 -> viewModel.runEmailIntel(queryInput)
            3 -> viewModel.runDomainIntel(queryInput)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = ChampagneGold,
          contentColor = EspressoVoid
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("osint_execute_button")
      ) {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Run Probe", modifier = Modifier.size(18.dp))
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Content Display Area according to Active Tab
    when (activeTab) {
      0 -> UsernameHuntContent(viewModel, onSaveTarget)
      1 -> PhoneIntelContent(viewModel)
      2 -> EmailIntelContent(viewModel)
      3 -> DomainIntelContent(viewModel)
    }
  }
}

@Composable
private fun UsernameHuntContent(
  viewModel: ObservatoryViewModel,
  onSaveTarget: (ObservedTarget) -> Unit
) {
  val huntState by viewModel.usernameHuntState.collectAsState()
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current

  when (val state = huntState) {
    is UiState.Loading -> {
      Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          CircularProgressIndicator(color = ChampagneGold)
          Spacer(modifier = Modifier.height(10.dp))
          Text("PROBING 18+ SOCIAL NETWORKS (SHERLOCK & BLACKBIRD)...", color = ChampagneGold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
      }
    }
    is UiState.Success -> {
      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("PROBE RESULTS (${state.data.size} TARGETS CHECKED)", style = MaterialTheme.typography.labelSmall, color = ParchmentMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text("${state.data.count { it.exists }} DETECTED", style = MaterialTheme.typography.labelSmall, color = StatusSage, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
          }
        }
        items(state.data) { account ->
          Surface(
            color = CoffeeCard,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, if (account.exists) BorderAmberStrong else BorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(if (account.exists) StatusSage else ParchmentDim, CircleShape)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = account.platform,
                  style = MaterialTheme.typography.labelMedium,
                  color = CreamIvory,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  fontFamily = FontFamily.Monospace
                )
                Text(
                  text = account.profileUrl,
                  style = MaterialTheme.typography.bodySmall,
                  color = SensorCyan,
                  fontSize = 9.sp,
                  fontFamily = FontFamily.Monospace,
                  maxLines = 1
                )
              }
              IconButton(
                onClick = {
                  TacticalHaptics.click(haptic)
                  context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(account.profileUrl)))
                }
              ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open Link", tint = ChampagneGold, modifier = Modifier.size(16.dp))
              }
              IconButton(
                onClick = {
                  TacticalHaptics.lock(haptic)
                  onSaveTarget(
                    ObservedTarget(
                      platform = account.platform.uppercase(),
                      username = account.profileUrl.substringAfterLast("/"),
                      displayName = "${account.platform} Recon",
                      followerCount = 1000L,
                      metricLabel = "VERIFIED PROFILE",
                      status = "VERIFIED",
                      bio = "Discovered via Sherlock/Blackbird probe: ${account.profileUrl}"
                    )
                  )
                }
              ) {
                Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = "Save to Dossier", tint = AmberLight, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
        item { Spacer(modifier = Modifier.height(90.dp)) }
      }
    }
    is UiState.Error -> {
      Text("Error: ${state.message}", color = Color.Red, fontSize = 11.sp)
    }
    null -> {
      EmptyEnginePrompt(
        title = "SHERLOCK & BLACKBIRD RECON ENGINE",
        description = "Rapid account hunting across 18+ high-priority networks: GitHub, Reddit, Twitter/X, Instagram, YouTube, Roblox, TikTok, Steam, Telegram, Spotify, Dev.to."
      )
    }
  }
}

@Composable
private fun PhoneIntelContent(viewModel: ObservatoryViewModel) {
  val phoneResult by viewModel.phoneIntelState.collectAsState()
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current

  phoneResult?.let { result ->
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      item {
        Surface(
          color = CoffeeCard,
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderAmberStrong),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text("PHONEINFOGA TELECOMMUNICATION DOSSIER", style = MaterialTheme.typography.labelSmall, color = ChampagneGold, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("E.164 STANDARDIZED: ${result.formattedE164}", color = CreamIvory, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(6.dp))
            Text("COUNTRY: ${result.countryName} (${result.countryCode})", color = SensorCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Text("CARRIER / REGION: ${result.carrierEstimate}", color = LinenWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Text("LINE TYPE: ${result.lineType}", color = StatusSage, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          }
        }
      }

      item {
        Text("AUTOMATED GOOGLE DORKS (PHONE FOOTPRINT)", style = MaterialTheme.typography.labelSmall, color = ParchmentMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
      }

      items(result.googleDorks) { (label, dorkQuery) ->
        Surface(
          color = CoffeeElevated,
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(0.8.dp, BorderSubtle),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              TacticalHaptics.click(haptic)
              val searchUrl = "https://www.google.com/search?q=" + Uri.encode(dorkQuery)
              context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl)))
            }
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(label, style = MaterialTheme.typography.labelMedium, color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              Text(dorkQuery, style = MaterialTheme.typography.bodySmall, color = ParchmentMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Execute Dork", tint = ChampagneGold, modifier = Modifier.size(16.dp))
          }
        }
      }
      item { Spacer(modifier = Modifier.height(90.dp)) }
    }
  } ?: EmptyEnginePrompt(
    title = "PHONEINFOGA RECONNAISSANCE ENGINE",
    description = "International telephony intelligence: E.164 normalization, carrier prefix estimation, line type classification, and automated Google Dork generator."
  )
}

@Composable
private fun EmailIntelContent(viewModel: ObservatoryViewModel) {
  val emailState by viewModel.emailIntelState.collectAsState()
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current

  when (val state = emailState) {
    is UiState.Loading -> {
      Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          CircularProgressIndicator(color = ChampagneGold)
          Spacer(modifier = Modifier.height(10.dp))
          Text("PROBING EMAIL REGISTRATIONS (HOLEHE ENGINE)...", color = ChampagneGold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
      }
    }
    is UiState.Success -> {
      val result = state.data
      LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
          Surface(
            color = CoffeeCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderAmberStrong),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (result.gravatarAvatarUrl != null) {
                  AsyncImage(
                    model = result.gravatarAvatarUrl,
                    contentDescription = "Gravatar",
                    modifier = Modifier.size(42.dp).clip(CircleShape).border(1.dp, ChampagneGold, CircleShape),
                    contentScale = ContentScale.Crop
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                }
                Column {
                  Text(result.email, color = CreamIvory, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                  Text("DOMAIN: ${result.domain}", color = SensorCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text("GRAVATAR ACCOUNT: ${if (result.gravatarFound) "AVATAR DETECTED & EXTRACTED" else "NOT REGISTERED"}", color = if (result.gravatarFound) StatusSage else ParchmentDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }

        item {
          Text("SUSPECTED REGISTERED SERVICES (HOLEHE MODULE)", style = MaterialTheme.typography.labelSmall, color = ParchmentMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }

        items(result.platformFootprint) { (platform, detected) ->
          Surface(
            color = CoffeeElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
              Text(platform, color = CreamIvory, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              Text(if (detected) "PROBABLE REGISTRATION" else "AVAILABLE", color = if (detected) StatusSage else ParchmentDim, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
          }
        }

        item {
          Text("AUTOMATED EMAIL LEAK & PASTE DORKS", style = MaterialTheme.typography.labelSmall, color = ParchmentMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }

        items(result.dorks) { (label, dorkQuery) ->
          Surface(
            color = CoffeeElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().clickable {
              TacticalHaptics.click(haptic)
              val searchUrl = "https://www.google.com/search?q=" + Uri.encode(dorkQuery)
              context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl)))
            }
          ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
              Column(modifier = Modifier.weight(1f)) {
                Text(label, color = ChampagneGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(dorkQuery, color = ParchmentMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
              }
              Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(14.dp))
            }
          }
        }

        item { Spacer(modifier = Modifier.height(90.dp)) }
      }
    }
    is UiState.Error -> {
      Text("Error: ${state.message}", color = Color.Red, fontSize = 11.sp)
    }
    null -> {
      EmptyEnginePrompt(
        title = "HOLEHE EMAIL FOOTPRINT ENGINE",
        description = "Discover linked accounts across 120+ platforms from an email address without alerting the target, with automatic Gravatar MD5 avatar extraction."
      )
    }
  }
}

@Composable
private fun DomainIntelContent(viewModel: ObservatoryViewModel) {
  val domainState by viewModel.domainIntelState.collectAsState()
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current

  when (val state = domainState) {
    is UiState.Loading -> {
      Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          CircularProgressIndicator(color = ChampagneGold)
          Spacer(modifier = Modifier.height(10.dp))
          Text("SCANNING CLOUD BUCKETS & SUBDOMAINS (PHOTON & SPIDERFOOT)...", color = ChampagneGold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
      }
    }
    is UiState.Success -> {
      val result = state.data
      LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
          Surface(
            color = CoffeeCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderAmberStrong),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("PHOTON & SPIDERFOOT DOMAIN RECON", style = MaterialTheme.typography.labelSmall, color = ChampagneGold, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
              Spacer(modifier = Modifier.height(6.dp))
              Text("TARGET DOMAIN: ${result.domain}", color = CreamIvory, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
              Text("IP INFRASTRUCTURE: ${result.ipEstimate}", color = SensorCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }

        item {
          Text("CLOUD STORAGE BUCKET PROBE (S3 / GCP / AZURE)", style = MaterialTheme.typography.labelSmall, color = ParchmentMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }

        items(result.cloudBucketsChecked) { (bucketUrl, active) ->
          Surface(
            color = CoffeeElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().clickable {
              TacticalHaptics.click(haptic)
              context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(bucketUrl.substringAfter("(").substringBefore(")"))))
            }
          ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
              Column(modifier = Modifier.weight(1f)) {
                Text(bucketUrl, color = CreamIvory, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
              }
              Text(if (active) "EXISTS / ACCESSIBLE" else "NOT OPEN", color = if (active) StatusSage else ParchmentDim, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
          }
        }

        item {
          Text("PROXIMATE SUBDOMAINS DETECTED", style = MaterialTheme.typography.labelSmall, color = ParchmentMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }

        items(result.commonSubdomains) { (subdomain, exists) ->
          Surface(
            color = CoffeeElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().clickable {
              TacticalHaptics.click(haptic)
              context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://$subdomain")))
            }
          ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
              Text(subdomain, color = SensorCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
              Text(if (exists) "RESOLVED" else "NO DNS", color = if (exists) StatusSage else ParchmentDim, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
          }
        }

        item { Spacer(modifier = Modifier.height(90.dp)) }
      }
    }
    is UiState.Error -> {
      Text("Error: ${state.message}", color = Color.Red, fontSize = 11.sp)
    }
    null -> {
      EmptyEnginePrompt(
        title = "PHOTON & SPIDERFOOT DOMAIN RECON",
        description = "Automated reconnaissance of target domains: AWS S3, Google Cloud, and Azure Blob storage bucket discovery, DNS subdomains, and security headers."
      )
    }
  }
}

@Composable
private fun EmptyEnginePrompt(title: String, description: String) {
  Surface(
    color = CoffeeCard,
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.labelMedium, color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(description, style = MaterialTheme.typography.bodyMedium, color = LinenWhite, fontSize = 11.sp, lineHeight = 16.sp)
    }
  }
}
