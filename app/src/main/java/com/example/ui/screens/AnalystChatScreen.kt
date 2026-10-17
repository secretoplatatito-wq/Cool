package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.BorderAmber
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.PhosphorGreen
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.ObservatoryViewModel

@Composable
fun AnalystChatScreen(
  viewModel: ObservatoryViewModel,
  initialPrompt: String? = null
) {
  val messages by viewModel.messages.collectAsState()
  val isThinking by viewModel.isAnalystThinking.collectAsState()
  var inputPrompt by remember { mutableStateOf(initialPrompt ?: "") }
  var useSearchGrounding by remember { mutableStateOf(true) }
  var useHighThinking by remember { mutableStateOf(false) }

  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val promptSuggestions = listOf(
    "Compare YouTube vs TikTok reach",
    "Analyze Roblox top game creators",
    "Geospatial lookup: Tokyo tech district",
    "GitHub open-source trend report"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBlack)
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    // Terminal Header
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
          imageVector = Icons.Default.Psychology,
          contentDescription = null,
          tint = AmberPrimary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "TACTICAL AI ANALYST // TERMINAL",
            style = MaterialTheme.typography.labelSmall,
            color = AmberLight,
            fontSize = 11.sp
          )
          Text(
            text = if (useHighThinking) "ENGINE: GEMINI 4 ARGON [DEEP THINKING MODE]" else "ENGINE: GEMINI 3.8 FLASH [SEARCH GROUNDED]",
            style = MaterialTheme.typography.labelSmall,
            color = if (useHighThinking) PhosphorGreen else TacticalCyan,
            fontSize = 9.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Intelligence Mode Controls (Search Grounding & High Thinking)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      FilterChip(
        selected = useSearchGrounding,
        onClick = {
          useSearchGrounding = !useSearchGrounding
          if (useSearchGrounding) useHighThinking = false
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Public,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
        },
        label = { Text("Search Grounded", fontSize = 11.sp) },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = TacticalCyan,
          selectedLabelColor = ObsidianBlack,
          containerColor = ObsidianCard,
          labelColor = TextPrimary
        )
      )

      FilterChip(
        selected = useHighThinking,
        onClick = {
          useHighThinking = !useHighThinking
          if (useHighThinking) useSearchGrounding = false
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
        },
        label = { Text("Gemini 4 Thinking", fontSize = 11.sp) },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = PhosphorGreen,
          selectedLabelColor = ObsidianBlack,
          containerColor = ObsidianCard,
          labelColor = TextPrimary
        )
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Scrollable Chat Thread
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        ChatBubble(msg = msg)
      }

      if (isThinking) {
        item {
          Surface(
            color = ObsidianCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFB300)),
            modifier = Modifier.fillMaxWidth(0.85f)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = AmberPrimary,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = if (useHighThinking) "Synthesizing deep tactical analysis..." else "Querying search intelligence...",
                style = MaterialTheme.typography.labelSmall,
                color = AmberLight
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Prompt Suggestions Bar
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      items(promptSuggestions) { suggestion ->
        Surface(
          color = ObsidianCard,
          shape = RoundedCornerShape(6.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFB300)),
          modifier = Modifier.clip(RoundedCornerShape(6.dp))
        ) {
          Text(
            text = suggestion,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 10.sp,
            modifier = Modifier
              .padding(horizontal = 8.dp, vertical = 5.dp)
              .testTag("prompt_suggestion_${suggestion.take(8)}")
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Input Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputPrompt,
        onValueChange = { inputPrompt = it },
        placeholder = { Text("Ask analyst (e.g., analyze MrBeast reach)...", fontSize = 12.sp) },
        modifier = Modifier
          .weight(1f)
          .testTag("analyst_chat_input"),
        maxLines = 3,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(onSend = {
          if (inputPrompt.isNotBlank() && !isThinking) {
            viewModel.sendAnalystPrompt(inputPrompt, useSearchGrounding, useHighThinking)
            inputPrompt = ""
          }
        }),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = AmberPrimary,
          unfocusedBorderColor = Color(0x44FFB300),
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
          focusedContainerColor = ObsidianCard,
          unfocusedContainerColor = ObsidianCard
        ),
        shape = RoundedCornerShape(10.dp)
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = {
          if (inputPrompt.isNotBlank() && !isThinking) {
            viewModel.sendAnalystPrompt(inputPrompt, useSearchGrounding, useHighThinking)
            inputPrompt = ""
          }
        },
        enabled = inputPrompt.isNotBlank() && !isThinking,
        modifier = Modifier
          .size(48.dp)
          .background(
            if (inputPrompt.isNotBlank() && !isThinking) AmberPrimary else Color(0xFF332A00),
            RoundedCornerShape(10.dp)
          )
          .testTag("send_analyst_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send",
          tint = if (inputPrompt.isNotBlank() && !isThinking) ObsidianBlack else Color(0xFF666666)
        )
      }
    }
  }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
  val isUser = msg.sender == "COMMANDER"

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = msg.sender,
        style = MaterialTheme.typography.labelSmall,
        color = if (isUser) TacticalCyan else AmberPrimary,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = msg.timestamp,
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        fontSize = 9.sp
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Surface(
      color = if (isUser) Color(0xFF142436) else ObsidianCard,
      shape = RoundedCornerShape(
        topStart = if (isUser) 12.dp else 2.dp,
        topEnd = if (isUser) 2.dp else 12.dp,
        bottomStart = 12.dp,
        bottomEnd = 12.dp
      ),
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        if (isUser) Color(0x5500E5FF) else Color(0x33FFB300)
      ),
      modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.95f)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = msg.text,
          style = MaterialTheme.typography.bodyMedium,
          color = TextPrimary,
          fontSize = 13.sp,
          lineHeight = 19.sp
        )

        if (msg.isGrounded && !isUser) {
          Spacer(modifier = Modifier.height(6.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Public,
              contentDescription = null,
              tint = TacticalCyan,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Live Google Grounded Intelligence",
              style = MaterialTheme.typography.labelSmall,
              color = TacticalCyan,
              fontSize = 9.sp
            )
          }
        }
      }
    }
  }
}
