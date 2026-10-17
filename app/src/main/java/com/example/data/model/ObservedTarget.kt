package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class ObservedTarget(
  @DocumentId val id: String = "",
  val userId: String = "",
  val username: String = "",
  val platform: String = "YOUTUBE",
  val displayName: String = "",
  val avatarUrl: String? = null,
  val bio: String? = null,
  val followerCount: Long = 0L,
  val metricLabel: String = "Followers",
  val status: String? = "ACTIVE",
  val notes: String? = null,
  val tags: List<String> = emptyList(),
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
) {
  fun toMap(includeTimestamps: Boolean = true): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>(
      "userId" to userId,
      "username" to username,
      "platform" to platform,
      "displayName" to displayName,
      "avatarUrl" to avatarUrl,
      "bio" to bio,
      "followerCount" to followerCount,
      "metricLabel" to metricLabel,
      "status" to status,
      "notes" to notes,
      "tags" to tags
    )
    if (includeTimestamps) {
      if (createdAt != null) map["createdAt"] = createdAt
      if (updatedAt != null) map["updatedAt"] = updatedAt
    }
    return map.filterValues { it != null }
  }
}
