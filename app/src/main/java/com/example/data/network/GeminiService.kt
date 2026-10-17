package com.example.data.network

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiRequest(
  @param:Json(name = "contents") val contents: List<GeminiContent>,
  @param:Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
  @param:Json(name = "tools") val tools: List<Map<String, Any>>? = null,
  @param:Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
  @param:Json(name = "parts") val parts: List<GeminiPart>,
  @param:Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
  @param:Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
  @param:Json(name = "temperature") val temperature: Float? = null,
  @param:Json(name = "thinkingConfig") val thinkingConfig: GeminiThinkingConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiThinkingConfig(
  @param:Json(name = "thinkingLevel") val thinkingLevel: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
  @param:Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
  @param:Json(name = "content") val content: GeminiContent? = null
)

interface GeminiApi {
  @POST("v1beta/models/{model}:generateContent")
  suspend fun generateContent(
    @Path("model") model: String,
    @Query("key") apiKey: String,
    @Body request: GeminiRequest
  ): GeminiResponse
}

object GeminiClient {
  private const val BASE_URL = "https://generativelanguage.googleapis.com/"

  private val moshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  val api: GeminiApi by lazy {
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(GeminiApi::class.java)
  }

  suspend fun queryAnalyst(
    prompt: String,
    history: List<Pair<String, String>> = emptyList(),
    useSearchGrounding: Boolean = true,
    useHighThinking: Boolean = false
  ): String = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext "Observatory Notice: Gemini API Key is not set. Please configure GEMINI_API_KEY in the Secrets panel to activate live AI satellite analysis."
    }

    val model = if (useHighThinking) "gemini-4-argon" else "gemini-3.8-flash"

    val contentList = mutableListOf<GeminiContent>()
    history.forEach { (userMsg, modelMsg) ->
      contentList.add(GeminiContent(parts = listOf(GeminiPart(text = userMsg)), role = "user"))
      contentList.add(GeminiContent(parts = listOf(GeminiPart(text = modelMsg)), role = "model"))
    }
    contentList.add(GeminiContent(parts = listOf(GeminiPart(text = prompt)), role = "user"))

    val toolsList = if (useSearchGrounding && !useHighThinking) {
      listOf(mapOf("googleSearch" to emptyMap<String, Any>()))
    } else null

    val generationConfig = if (useHighThinking) {
      GeminiGenerationConfig(
        thinkingConfig = GeminiThinkingConfig(thinkingLevel = "HIGH")
      )
    } else {
      GeminiGenerationConfig(temperature = 0.5f)
    }

    val systemInstruction = GeminiContent(
      parts = listOf(
        GeminiPart(
          text = "You are God's Eye Tactical Observatory Analyst. You provide precise, verified open-source intelligence analysis, public creator performance statistics (YouTube, TikTok, Roblox, Twitch, Kick, GitHub), and geospatial location coordinates. Maintain a calm, analytical, and tactical HUD tone. Format key data points with bullet points, subscriber/follower estimates, and verified milestones."
        )
      )
    )

    val request = GeminiRequest(
      contents = contentList,
      generationConfig = generationConfig,
      tools = toolsList,
      systemInstruction = systemInstruction
    )

    try {
      val response = api.generateContent(model, apiKey, request)
      val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
      reply ?: "No telemetry returned from orbital satellite analysis."
    } catch (e: Exception) {
      "Observatory Link Error: ${e.localizedMessage ?: e.message}"
    }
  }
}
