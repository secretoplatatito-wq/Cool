![God's Eye Observatory Banner](Images/logo.png)





# God's Eye Observatory 🛰️

> Tactical Geospatial & Public Intelligence Dashboard for Android (vE_0.0.1)

God's Eye Observatory unites an interactive 3D tactical vector globe, real-time public creator & streamer intelligence (YouTube, Roblox, GitHub, TikTok, Twitch, Kick), persistent zero-trust Cloud Firestore synchronization with Google Sign-In, and multimodal Gemini AI intelligence with Google Search grounding.

---

## 🎨 Theme: Observatory Palette
Designed with Material 3 in a high-contrast cosmic observatory aesthetic:
- **Cosmic Void**: Deep space obsidian `#06090E` and hull slate `#0B1017`
- **Vibrant Amber Accents**: High-intensity HUD laser amber `#FFB300` and phosphor gold `#FFD54F`
- **Tactical Sensor Overlays**: Cyberpunk cyan `#00E5FF` and active telemetry green `#00E676`

---

## ⚡ Features
1. **Interactive 3D Tactical Globe**:
   - Hardware-accelerated 3D spherical projection with pitch/yaw drag rotation.
   - Real-time orbital rings with rotating recon satellite beacon.
   - Global observation nodes (Tokyo, London, New York, San Francisco, Singapore, Sydney, Dubai, Berlin, São Paulo, Seoul) with coordinate telemetry.
2. **Multi-Platform Public Recon (Recon HUD)**:
   - Universal search and public metric tracking across **YouTube**, **Roblox**, **GitHub**, **TikTok**, **Twitch**, and **Kick**.
   - Curated 1-tap presets (*MrBeast*, *builderman*, *Linus Torvalds*, *xQc*, *Khaby Lame*).
3. **Cloud Dossier & Watchlist (Firebase Firestore)**:
   - Scoped user isolation under `/users/{uid}/targets/{targetId}`.
   - Zero-trust security rules mathematically tested against injection and cross-user leaks.
   - Custom observation logs and milestone notes.
4. **AI Tactical Analyst (Gemini Intelligence)**:
   - Powered by `gemini-3.8-flash` with Google Search Grounding for live facts.
   - Deep reasoning mode powered by `gemini-4-argon` with high-thinking level.

---

## 📦 How to Find and Publish Releases on GitHub

If you pushed your repository to GitHub and cannot find the release under your repo's **Releases** tab (`https://github.com/USER/REPO/releases`):

### Why it happens
By default, running `git push` only pushes commits on branches (e.g. `main`), but **git tags are not automatically pushed unless specified**, and a git tag alone is not yet an official GitHub Release with an attached APK binary.

### Option A: Automatic Release via GitHub Actions (Recommended)
This repository includes `.github/workflows/release.yml`. Whenever you push the tag, GitHub will automatically compile the APK and publish the official Release:

1. Push your tag from the terminal:
   ```bash
   git push origin vE_0.0.1
   # Or push all tags:
   git push origin --tags
   ```
2. Navigate to your repository on GitHub:
   - Click the **Actions** tab to watch the build.
   - Once completed (~2 minutes), click the **Releases** tab on the right sidebar.
   - You will see **God's Eye Observatory Release vE_0.0.1** with the downloadable `gods-eye-observatory-vE_0.0.1.apk` attached!

### Option B: Create the Release Manually in GitHub Web UI
1. Go to your repository on GitHub: `https://github.com/<your-username>/<repo-name>`.
2. On the right sidebar, click **Releases** (or go to `/releases/new`).
3. Click **Draft a new release**.
4. In **Choose a tag**, type or select `vE_0.0.1`.
5. Enter Title: `God's Eye Observatory Release E_0.0.1`.
6. Attach the pre-compiled `.apk` from `.build-outputs/app-debug.apk` (or drag and drop it into the binaries box).
7. Click **Publish release**.

---

## 🔒 Security
- Default-deny catch-all in `firestore.rules`.
- Authenticated user isolation gating read/write operations to `request.auth.uid == userId`.
- ProGuard R8 bytecode obfuscation and log stripping configured in `app/proguard-rules.pro`.
