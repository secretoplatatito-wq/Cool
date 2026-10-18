![God's Eye Observatory Banner](Images/logo.png)





# God's Eye Observatory 🛰️

> Tactical Geospatial & Public Intelligence Dashboard for Android (vE_0.0.1)

God's Eye Observatory unites an interactive 3D tactical vector globe, real-time public creator & streamer intelligence (YouTube, Roblox, GitHub, TikTok, Twitch, Kick), persistent zero-trust Cloud Firestore synchronization with Google Sign-In, and multimodal Gemini AI intelligence with Google Search grounding.

---

## 🎨 Aesthetic: Unseen.co Refined Luxury & Tactical Minimalism
Inspired by the digital craftsmanship and quiet luxury of **unseen.co**:
- **Palette**: Velvet espresso void (`#140F0D`), rich roasted coffee cards (`#2E2420`), luminous creamy ivory (`#FBF8F3`), and metallic champagne gold (`#D4AF37`)
- **Celestial Starfield**: Interactive canvas backdrop with touch-responsive gravitational drift and ambient micro-glows
- **Haptic Tactility**: Precise vibration feedback on target lock, probe executions, and tactical navigation
- **Fine Details**: Hairline gold borders (`BorderGold`), monospace telemetry coordinates, and glassmorphic depth

---

## ⚡ Unified Intelligence & OSINT Engines
God's Eye unites open-source intelligence frameworks into one cohesive, zero-trust mobile command suite:

1. **Sherlock & Blackbird (Username Intelligence)**:
   - Automated account reconnaissance across 18+ high-priority developer and social networks: GitHub, Reddit, Twitter/X, Instagram, YouTube, Roblox, TikTok, Steam, Telegram, Spotify, Dev.to, Replit, and Kick.
   - Verified endpoint detection with 1-tap Dossier saving.
2. **PhoneInfoga (Telephony Reconnaissance)**:
   - E.164 international phone number normalization and carrier routing detection.
   - Automatic Google Dork generation for public paste dumps, directories, and social footprints.
3. **Holehe (Email Footprint & Gravatar)**:
   - Account presence reconnaissance across global platforms without alerting targets.
   - MD5 hash computation and Gravatar profile avatar extraction.
4. **Photon & SpiderFoot (Domain, Subdomains & Cloud Storage)**:
   - Cloud storage bucket discovery across AWS S3, Google Cloud Storage, and Azure Blob.
   - DNS subdomain scanning (api, dev, admin, staging, mail, cdn) and HTTP security header evaluation.
5. **Interactive 3D Tactical Globe**:
   - Hardware-accelerated 3D spherical projection with pitch/yaw drag rotation and global observation nodes.
6. **Creator Recon HUD**:
   - Live public creator and streamer lookup across YouTube, Roblox, GitHub, TikTok, Twitch, and Kick.
7. **Cloud Dossier & Watchlist (Firebase Firestore)**:
   - Scoped user isolation under `/users/{uid}/targets/{targetId}` with verified zero-trust rules.
8. **AI Tactical Analyst (Gemini Intelligence)**:
   - Powered by `gemini-3.8-flash` with Google Search Grounding for real-time facts and verification.

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
