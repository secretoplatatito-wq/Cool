package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Observatory Cosmic Dark Foundation (High Contrast)
val ObservatoryVoid = Color(0xFF06090E)           // Ultra-deep space black
val ObservatorySurface = Color(0xFF0B1017)        // Dark station hull
val ObservatorySurfaceVariant = Color(0xFF121924) // Elevated tactical panel
val ObservatorySurfaceContainer = Color(0xFF17202D) // Card / module background
val ObservatorySurfaceContainerHigh = Color(0xFF1D293A) // Highlighted panel
val ObservatorySurfaceContainerHighest = Color(0xFF243348)

// Vibrant Amber Spectrum (Observatory Primary Accent)
val AmberPrimary = Color(0xFFFFB300)             // Core luminous amber #FFB300
val AmberLight = Color(0xFFFFD54F)               // High-intensity phosphor gold #FFD54F
val AmberDark = Color(0xFFFF8F00)                // Deep warning amber #FF8F00
val AmberContainer = Color(0xFF382600)           // Dark warm amber container
val OnAmberContainer = Color(0xFFFFE082)         // Bright amber text on container
val AmberGlow = Color(0x66FFB300)                // Holographic amber glow
val AmberHighlight = Color(0xFFFFE57F)           // Intense laser point

// Tactical Secondary & Sensor Telemetry
val TacticalCyan = Color(0xFF00E5FF)             // Radar / coordinate cyan #00E5FF
val TacticalCyanContainer = Color(0xFF003840)
val OnTacticalCyanContainer = Color(0xFF80F4FF)
val PhosphorGreen = Color(0xFF00E676)            // Satellite lock / active status
val PhosphorGreenContainer = Color(0xFF00381B)
val OnPhosphorGreenContainer = Color(0xFF80FFBA)
val CrimsonAlert = Color(0xFFFF1744)             // Alert / danger red
val CrimsonAlertContainer = Color(0xFF40000C)
val OnCrimsonAlertContainer = Color(0xFFFF8A9E)

// Typography & Content Contrast
val TextPrimary = Color(0xFFF6F8FB)              // Pure crisp white text
val TextSecondary = Color(0xFFB0BEC5)            // Cool telemetry silver
val TextMuted = Color(0xFF6A7B8C)                // Muted coordinate text

// Reticles & Borders
val BorderAmber = Color(0x44FFB300)              // Translucent amber HUD line
val BorderAmberStrong = Color(0x88FFB300)        // Focused target border
val BorderSubtle = Color(0x22FFFFFF)             // Grid boundary line

// Backward-compatible aliases for existing screen references
val ObsidianBlack = ObservatoryVoid
val ObsidianSurface = ObservatorySurface
val ObsidianElevated = ObservatorySurfaceVariant
val ObsidianCard = ObservatorySurfaceContainer
