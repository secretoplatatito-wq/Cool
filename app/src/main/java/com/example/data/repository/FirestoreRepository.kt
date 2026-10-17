package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.ObservedTarget
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  fun observeTargets(): Flow<List<ObservedTarget>> = flow {
    val uid = requireUserId()
    val path = "users/$uid/targets"
    emitAll(
      db.collection("users").document(uid).collection("targets")
        .snapshots()
        .map { snapshot -> snapshot.toObjects(ObservedTarget::class.java) }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun addTarget(target: ObservedTarget): String {
    val uid = requireUserId()
    val targetsRef = db.collection("users").document(uid).collection("targets")
    val payload = target.toMap(includeTimestamps = false).toMutableMap()
    payload["userId"] = uid
    payload["createdAt"] = FieldValue.serverTimestamp()
    payload["updatedAt"] = FieldValue.serverTimestamp()

    try {
      val docRef = targetsRef.add(payload).await()
      return docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, targetsRef.path)
      throw e
    }
  }

  suspend fun updateTarget(target: ObservedTarget) {
    val uid = requireUserId()
    require(target.id.isNotBlank()) { "Target ID must not be blank for update" }
    val docRef = db.collection("users").document(uid).collection("targets").document(target.id)
    try {
      val payload = mapOf(
        "displayName" to target.displayName,
        "avatarUrl" to target.avatarUrl,
        "bio" to target.bio,
        "followerCount" to target.followerCount,
        "metricLabel" to target.metricLabel,
        "status" to target.status,
        "notes" to target.notes,
        "tags" to target.tags,
        "updatedAt" to FieldValue.serverTimestamp()
      ).filterValues { it != null }

      docRef.update(payload).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteTarget(targetId: String) {
    val uid = requireUserId()
    require(targetId.isNotBlank()) { "Target ID must not be blank for delete" }
    val docRef = db.collection("users").document(uid).collection("targets").document(targetId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }
}
