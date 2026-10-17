# Hardened Release E_0.0.1: Security Audit, Database Protection & Code Hardening

A comprehensive security hardening and release preparation plan for God's Eye Observatory (vE_0.0.1), focusing on impenetrable zero-trust database isolation, ProGuard R8 bytecode obfuscation, compiler warning elimination, and production release artifact generation.

### User Review & Critical Decisions

> [!IMPORTANT]
> The plan below incorporates all confirmed choices from Phase 1. Review the security measures and proceed to execute the hardening phase.

- **Confirmed Version Identifier**: Bump app version to `E_0.0.1` (`versionName = "E_0.0.1"` in `app/build.gradle.kts`).
- **Confirmed Database Security Posture**: Zero-trust user isolation with strict schema validation. All Firestore read/write operations must strictly assert `request.auth != null` and `request.auth.uid == userId`, blocking cross-tenant snooping or unauthorized query injection.
- **Confirmed Code Protection**: Enable ProGuard/R8 code shrinking, resource shrinking, and bytecode obfuscation to protect API integrations, data models, and logic from reverse-engineering or tamper backdoors.
- **Release Preparation**: Fix compiler deprecation warnings (`@param:Json` annotations), run automated security rules test suite, tag `vE_0.0.1`, and build the production APK.

---

### 1. Overview & Threat Model Analysis

- **Threat Model & Backdoor Mitigation**:
  - *Risk 1 (Unauthorized Database Access / Data Breach)*: Attackers extracting the Firebase Project ID or Web Client ID and querying Firestore directly via REST or SDK without client-side filters.
    - *Defense*: Firestore security rules evaluate server-side independent of the client. Rule `match /users/{userId}/targets/{targetId}` strictly enforces `request.auth != null && request.auth.uid == userId` and `incoming().userId == request.auth.uid`. A rogue client attempting to query or list another user's documents is rejected with `PERMISSION_DENIED` at the database engine level.
  - *Risk 2 (APK Reverse-Engineering & Decompilation)*: Attackers decompiling the APK to extract API endpoints, reverse-engineer proprietary algorithms, or modify application logic.
    - *Defense*: Enable ProGuard R8 with full optimization, class/method obfuscation, and stripping of diagnostic debug logs in release builds while preserving Moshi JSON adapters and Firebase reflection.
  - *Risk 3 (Resource & Parameter Poisoning)*: Malicious payloads injecting oversized strings or unlisted fields into target dossiers.
    - *Defense*: Strict validation helper `isValidTarget(incoming())` verifying string lengths (`size() <= 100`), enum values (`platform in [...]`), and field allowlists (`hasOnly([...])`).

---

### 2. User Experience & Visual Design

- **Release Stability & Visual Polish**:
  - Complete zero-glitch UI experience across Compact, Medium, and Expanded screen sizes.
  - Graceful error boundaries and sanitized user notifications (no raw exception stack traces or sensitive database IDs exposed to the user interface).
  - Explicit release badge (`vE_0.0.1 [HARDENED]`) displayed in the Observatory Command Header for clear version verification.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: ProGuard / R8 Obfuscation & Minification**
  - *Chosen Approach*: Enable `isMinifyEnabled = true` and `isShrinkResources = true` in `release` build type with customized `proguard-rules.pro`. Keep `@JsonClass` models, Firebase descriptors, and Credential Manager entities safe from over-stripping.
  - *Why*: Reduces APK size, speeds up execution, and makes decompiled bytecode virtually unreadable to malicious actors.
  - *Alternatives Considered*: Leaving minification disabled (makes the APK trivial to decompile with tools like JADX).

- **Decision 2: Compiler Warning Resolution**
  - *Chosen Approach*: Update `@Json(name = ...)` annotations to `@param:Json(name = ...)` across data classes to align with Kotlin 2.2 strict annotation targeting rules (`KT-73255`), eliminating all compiler warnings.
  - *Why*: Guarantees pristine compile logs and prevents future serialization breakage.

- **Decision 3: Zero-Trust Rules & Blueprint Verification**
  - *Chosen Approach*: Execute both Tier 1 JavaScript unit tests and Tier 2 native Robolectric rule tests to mathematically prove that unauthorized reads, spoofed user IDs, and cross-user updates fail.
  - *Why*: Validates the security fortress before tagging the release.

---

### 4. Technical Architecture & Hardening Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        R8 Obfuscated Bytecode Layer                    │
│   (Classes & Methods Renamed, Unused Code Stripped, Dead Code Removed) │
├────────────────────────────────────────────────────────────────────────┤
│                          Application Layer                             │
│   - Version: E_0.0.1                                                   │
│   - Sanitized Error Handling (No raw paths / internal tokens leaked)   │
│   - Jetpack CredentialManager Auth Gate (Token Signature Validated)    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Encrypted HTTPS / TLS 1.3
┌───────────────────────────────────▼────────────────────────────────────┐
│                  Firebase Firestore Cloud Security Gate                │
├────────────────────────────────────────────────────────────────────────┤
│  Rule 1: match /{document=**} { allow read, write: if false; }         │
│  Rule 2: match /users/{userId}/targets/{targetId}                      │
│          - allow get, list: if isSignedIn() && auth.uid == userId      │
│          - allow create: if auth.uid == userId && incoming.uid == auth │
│          - allow update: if auth.uid == userId && hasOnly([...])       │
│          - allow delete: if auth.uid == userId                         │
│  Rule 3: isValidTarget() schema validator enforcing types & limits     │
└────────────────────────────────────────────────────────────────────────┘
```

#### Step-by-Step Execution Plan
1. **Version Update**: Update `versionName = "E_0.0.1"` and `versionCode = 1` in `app/build.gradle.kts`.
2. **Compiler Warning Fixes**: Update `@Json` annotations in `GeminiService.kt` to `@param:Json`.
3. **ProGuard / R8 Hardening**:
   - Configure `app/proguard-rules.pro` with keep rules for Moshi, Firebase Firestore, Firebase Auth, GoogleId, and Retrofit.
   - Configure `app/build.gradle.kts` release build type.
4. **Security Verification**:
   - Re-run `node --test firestore.test.js` to verify zero-trust rules.
   - Re-run `gradle :app:testDebugUnitTest --tests '*Rule*'` to verify Android repository tests.
5. **Git Release Tagging**: Commit changes and tag `vE_0.0.1`.
6. **Production Build**: Run `compile_applet` and verify successful compilation.
