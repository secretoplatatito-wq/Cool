# God's Eye Observatory: Tactical Geospatial & Public Intelligence Dashboard

An advanced, high-contrast observatory platform uniting an interactive 3D tactical globe, multi-platform public creator and developer analytics (YouTube, TikTok, Roblox, Twitch, Kick, GitHub), Firebase cloud synchronization, and Gemini multimodal intelligence with Search & Maps grounding.

### User Review & Critical Decisions

> [!IMPORTANT]
> The architectural direction below incorporates all confirmed choices from Phase 1. Review the key modules and proceed when ready to begin phased implementation.

- **Confirmed Core Experience**: Tactical dashboard combining interactive 3D globe navigation with public creator & streamer statistics.
- **Confirmed Public Intelligence Scope**: Multi-platform public metrics lookup covering YouTube channels, TikTok public bios/stats, Roblox public user & game metrics, Twitch & Kick streamer statistics, and GitHub public developer repositories/activity.
- **Confirmed Visual Aesthetic**: High-contrast minimalist observatory theme with deep obsidian backgrounds (`#0A0D12`), amber holographic accents (`#FFB300`), phosphor telemetry highlights, and monospaced coordinate telemetry.
- **Data Privacy & Policy Compliance**: Strictly adheres to public OSINT norms and Google Play developer policies. Analyzes exclusively public metrics (subscribers, follower counts, verified badges, repository stars, public game visits, public descriptions) with zero invasive device monitoring or private credential extraction.

---

### 1. Overview & Core Concept

- **What It Does**: Provides an integrated command center for digital creators, researchers, and tech enthusiasts. Users can explore a responsive 3D satellite/wireframe globe with coordinate telemetry, inspect public performance metrics across major content platforms, save monitored creators to an authenticated cloud watchlist, and query Gemini AI analysts for public trend analysis and geographic grounding.
- **Target Audience / Persona**: Content strategists, open-source researchers, esports analysts, and developers looking for a unified tactical hub to track public channel milestones and geospatial points of interest.
- **Key Value**: Replaces scattered browser lookups and clunky static tools with a cohesive, cinematic Android application featuring tactile 3D orbital controls, instant public metrics aggregation, and cloud-synced target dossiers.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Orbital Command (Home View)**:
   - Full-bleed tactical 3D vector globe with interactive touch gestures (drag to rotate, pinch to zoom, tap to lock coordinates).
   - Real-time telemetry overlay showing simulated orbital pass, latitude/longitude reticle, and active tracking indicators.
   - Quick platform switcher bar allowing one-tap jumps to creator intelligence streams.
2. **Public Platform Reconnaissance**:
   - Universal search bar supporting YouTube handles, TikTok usernames, Roblox user IDs/usernames, Twitch/Kick streamers, and GitHub developers.
   - Comprehensive metric cards: subscriber/follower count, total views/plays, top public repositories/experiences, bio overview, account creation year, and engagement indicators.
   - "Add to Watchlist" action syncing the target dossier to Cloud Firestore.
3. **Observatory Watchlist & Dossier Viewer**:
   - Filterable grid of saved creators categorized by platform.
   - Historical milestone notes and public status tags (Active, Streaming, Milestone Alert).
4. **Gemini Tactical Analyst & Grounded Chat**:
   - Multi-turn AI intelligence terminal powered by `gemini-3.8-flash` (with Search & Maps grounding) and `gemini-4-argon` (for deep thinking queries).
   - Allows users to analyze creator audience patterns, compare platform reaches, or search location coordinates with Maps grounding.
5. **Secure Authentication Gate**:
   - Google Sign-In via Android Jetpack `CredentialManager` gating persistent Cloud Firestore sync.

#### Visual Identity & Theme
- **Color Palette & Atmospheric Accents**:
  - `Background`: Deep Obsidian `#0A0D12` / Surface Dark `#121721`
  - `Primary Accent`: High-Intensity Amber `#FFB300` / Gold Accent `#FFC107`
  - `Secondary Accent`: Tactical Cyan `#00E5FF` (for sensor overlays & coordinates)
  - `Surface Outlines`: Semi-transparent amber/slate border `#FFB30022`
  - `Text`: Ultra-crisp high-contrast White `#F5F7FA` with Muted Amber `#FFD54F` subheadings
- **Typography**:
  - Monospaced telemetry headers (`JetBrains Mono` / Monospace) for coordinates, platform stats, and system status.
  - Clean M3 sans-serif body for readable bios, descriptions, and analysis threads.
- **Tactical Component Styling**:
  - Angular card borders, HUD bracket corners, scanline effects, and illuminated badge indicators.
  - Touch targets strictly exceeding 48dp with tactile ripple animations.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Interactive 3D Globe Implementation**
  - *Chosen Approach*: Hardware-accelerated Jetpack Compose Canvas 3D spherical projection with matrix math, orbital graticules, rotating landmass arcs, and interactive touch raycasting.
  - *Why*: Delivers a smooth 60fps cinematic experience with zero heavy external C++ engine dependencies, ensuring fast APK compile times and rock-solid emulator stability.
  - *Alternatives Considered*: Heavy WebGL / WebView wrappers (prone to sluggish touch latency and high memory overhead on Android).

- **Decision 2: Public Creator & Platform Intelligence Architecture**
  - *Chosen Approach*: Multi-provider public metrics resolver querying official public endpoints (GitHub public REST, Roblox public user/games API, YouTube public data, and intelligent fallback search grounding for TikTok and Kick).
  - *Why*: Guarantees live, verifiable public data while eliminating brittle scraping hacks and respecting platform terms of service.
  - *Alternatives Considered*: Direct web scraping of login-walled pages (breaks frequently and violates terms of service).

- **Decision 3: Cloud Persistence & User Isolation**
  - *Chosen Approach*: Google Sign-In via `CredentialManager` backed by Firebase Cloud Firestore using custom database ID and hardened security rules scoped to `request.auth.uid`.
  - *Why*: Satisfies all cloud persistence mandates, guarantees zero data loss across device installs, and enforces zero-trust isolation between users.

- **Decision 4: Multimodal AI & Grounding Strategy**
  - *Chosen Approach*: Dual-model tiering using `gemini-3.8-flash` with Google Search and Maps grounding tools for real-time live facts, alongside `gemini-4-argon` with high thinking level for deep comparative research.
  - *Why*: Optimizes latency and token efficiency while providing the user with cutting-edge analytical capabilities.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├──────────────────┬──────────────────┬─────────────────┬────────────────┤
│  Globe3DCanvas   │  ReconSearchHUD  │ WatchlistDossier│  AnalystChat   │
│ (3D Canvas + HUD)│ (Multi-Platform) │ (Cloud Sync)    │ (Gemini + Ground)│
└─────────┬────────┴─────────┬────────┴────────┬────────┴────────┬───────┘
          │                  │                 │                 │
┌─────────▼──────────────────▼─────────────────▼─────────────────▼────────┐
│                        ObservatoryViewModel                            │
│  - Active Target State    - Search Query State   - 3D Camera Angles    │
│  - Multi-Platform Flow    - Watchlist Flow       - AI Message Stream   │
└─────────┬──────────────────┬─────────────────┬─────────────────┬────────┘
          │                  │                 │                 │
┌─────────▼────────┐ ┌───────▼────────┐ ┌──────▼────────┐ ┌──────▼────────┐
│ PublicMetricsRepo│ │ FirestoreRepo  │ │ AuthRepository│ │  GeminiService│
│ - YouTube API    │ │ - users/{uid}/ │ │ - Credential  │ │ - 3.8-flash   │
│ - Roblox API     │ │   targets      │ │   Manager     │ │   (Search/Maps│
│ - GitHub API     │ │ - custom DB ID │ │ - GoogleID    │ │ - 4-argon     │
│ - Twitch/Kick    │ │ - rules tested │ │   Provider    │ │   (Thinking)  │
└──────────────────┘ └────────────────┘ └───────────────┘ └───────────────┘
```

#### Core Data Models
- **`ObservedTarget`**:
  - `id: String` (Document ID)
  - `userId: String` (Owner UID)
  - `username: String`
  - `platform: String` (YOUTUBE, TIKTOK, ROBLOX, TWITCH, KICK, GITHUB)
  - `displayName: String`
  - `avatarUrl: String`
  - `bio: String`
  - `followerCount: Long`
  - `metricLabel: String` (e.g., "Subscribers", "Followers", "Stars")
  - `additionalStats: Map<String, String>` (e.g., {"Public Repos": "42", "Total Views": "1.2M"})
  - `tags: List<String>`
  - `createdAt: Timestamp?`
  - `updatedAt: Timestamp?`

- **`GlobeCoordinate`**:
  - `latitude: Double`
  - `longitude: Double`
  - `label: String`
  - `regionType: String`

#### Implementation Steps
1. **Firebase Backend Provisioning**: Execute `set_up_firebase` with `platform: "web"` (or Android config) and `requestedProducts: ["firestore"]` to provision Firestore and Auth.
2. **Schema & Security Rules**: Write `firebase-blueprint.json` and hardened `firestore.rules`, validate via automated emulator testing, and deploy with `deploy_firebase`.
3. **Dependencies & Permissions**: Activate Firebase, Credential Manager, Retrofit, OkHttp, and Kotlinx Serialization in `app/build.gradle.kts`. Add `INTERNET` and `ACCESS_NETWORK_STATE` to `AndroidManifest.xml`.
4. **Repositories & Network Client**: Build `PublicPlatformRepository`, `FirestoreRepository`, and `GeminiApiService`.
5. **Observatory Theme & HUD Components**: Implement high-contrast dark theme with amber accent tokens, HUD cards, reticles, and typography.
6. **3D Tactical Globe Engine**: Create Compose Canvas 3D sphere with rotation gestures, latitude/longitude graticules, landmark pins, and coordinate HUD.
7. **Multi-Platform Reconnaissance Screen**: Build search, platform tabs, metric cards, and watchlist sync actions.
8. **Gemini Grounded Intelligence Terminal**: Implement multi-turn conversational AI with Search/Maps grounding and high-thinking mode.
9. **Verification**: Run `compile_applet` and verify end-to-end functionality.
