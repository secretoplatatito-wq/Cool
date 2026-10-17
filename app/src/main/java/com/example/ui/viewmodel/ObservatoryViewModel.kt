package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ObservedTarget
import com.example.data.network.GeminiClient
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.PublicPlatformRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ReconDestination {
  ORBITAL_GLOBE,
  PUBLIC_RECON,
  DOSSIER_WATCHLIST,
  ANALYST_AI
}

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String) : UiState<Nothing>
}

data class ChatMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val sender: String,
  val text: String,
  val isGrounded: Boolean = false,
  val timestamp: String = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date())
)

class ObservatoryViewModel(application: Application) : AndroidViewModel(application) {
  private val firestoreRepository = FirestoreRepository(application)
  private val platformRepository = PublicPlatformRepository()

  val currentUserId: String?
    get() = Firebase.auth.currentUser?.uid

  private val _activeDestination = MutableStateFlow(ReconDestination.ORBITAL_GLOBE)
  val activeDestination: StateFlow<ReconDestination> = _activeDestination.asStateFlow()

  fun setDestination(dest: ReconDestination) {
    _activeDestination.value = dest
  }

  // Public Recon HUD State
  private val _selectedPlatform = MutableStateFlow("YOUTUBE")
  val selectedPlatform: StateFlow<String> = _selectedPlatform.asStateFlow()

  fun setPlatform(platform: String) {
    _selectedPlatform.value = platform
  }

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  private val _activeTargetState = MutableStateFlow<UiState<ObservedTarget>?>(null)
  val activeTargetState: StateFlow<UiState<ObservedTarget>?> = _activeTargetState.asStateFlow()

  val curatedPresets: List<ObservedTarget> = platformRepository.getCuratedPresets()

  fun searchTarget(platform: String = _selectedPlatform.value, query: String = _searchQuery.value) {
    if (query.isBlank()) return
    viewModelScope.launch {
      _activeTargetState.value = UiState.Loading
      val result = platformRepository.resolvePublicTarget(platform, query)
      result.fold(
        onSuccess = { target ->
          _activeTargetState.value = UiState.Success(target)
        },
        onFailure = { err ->
          _activeTargetState.value = UiState.Error(err.localizedMessage ?: "Target resolution failed")
        }
      )
    }
  }

  fun selectPreset(target: ObservedTarget) {
    _selectedPlatform.value = target.platform
    _searchQuery.value = target.username
    _activeTargetState.value = UiState.Success(target)
  }

  // Cloud Watchlist Flow
  val watchlistState: StateFlow<UiState<List<ObservedTarget>>> = firestoreRepository.observeTargets()
    .map<List<ObservedTarget>, UiState<List<ObservedTarget>>> { UiState.Success(it) }
    .catch { emit(UiState.Error(it.localizedMessage ?: "Failed to observe watchlist")) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = UiState.Loading
    )

  private val _watchlistActionStatus = MutableStateFlow<String?>(null)
  val watchlistActionStatus: StateFlow<String?> = _watchlistActionStatus.asStateFlow()

  fun clearStatusMessage() {
    _watchlistActionStatus.value = null
  }

  fun addToWatchlist(target: ObservedTarget) {
    viewModelScope.launch {
      try {
        firestoreRepository.addTarget(target)
        _watchlistActionStatus.value = "Target '@${target.username}' added to Cloud Dossier."
      } catch (e: Exception) {
        _watchlistActionStatus.value = "Failed to save target: ${e.localizedMessage}"
      }
    }
  }

  fun deleteFromWatchlist(targetId: String) {
    viewModelScope.launch {
      try {
        firestoreRepository.deleteTarget(targetId)
        _watchlistActionStatus.value = "Target removed from Cloud Dossier."
      } catch (e: Exception) {
        _watchlistActionStatus.value = "Failed to delete: ${e.localizedMessage}"
      }
    }
  }

  fun updateTargetNotes(target: ObservedTarget, newNotes: String) {
    viewModelScope.launch {
      try {
        firestoreRepository.updateTarget(target.copy(notes = newNotes))
        _watchlistActionStatus.value = "Dossier notes updated."
      } catch (e: Exception) {
        _watchlistActionStatus.value = "Failed to update notes: ${e.localizedMessage}"
      }
    }
  }

  // AI Analyst Chat State
  private val _messages = MutableStateFlow<List<ChatMessage>>(
    listOf(
      ChatMessage(
        sender = "GOD'S EYE AI",
        text = "Observatory Intelligence Link Established. Ready to assist with public creator analytics, cross-platform performance comparisons, or geospatial coordinate telemetry.",
        isGrounded = true
      )
    )
  )
  val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

  private val _isAnalystThinking = MutableStateFlow(false)
  val isAnalystThinking: StateFlow<Boolean> = _isAnalystThinking.asStateFlow()

  fun sendAnalystPrompt(prompt: String, useSearchGrounding: Boolean = true, useHighThinking: Boolean = false) {
    if (prompt.isBlank() || _isAnalystThinking.value) return

    val userMessage = ChatMessage(sender = "COMMANDER", text = prompt)
    _messages.value = _messages.value + userMessage
    _isAnalystThinking.value = true

    viewModelScope.launch {
      val history = _messages.value.dropLast(1).chunked(2).mapNotNull { pair ->
        if (pair.size == 2) Pair(pair[0].text, pair[1].text) else null
      }

      val replyText = GeminiClient.queryAnalyst(
        prompt = prompt,
        history = history,
        useSearchGrounding = useSearchGrounding,
        useHighThinking = useHighThinking
      )

      val modelMessage = ChatMessage(
        sender = if (useHighThinking) "GEMINI 4 ARGON [DEEP THINKING]" else "GEMINI 3.8 FLASH [SEARCH GROUNDED]",
        text = replyText,
        isGrounded = useSearchGrounding
      )

      _messages.value = _messages.value + modelMessage
      _isAnalystThinking.value = false
    }
  }
}
