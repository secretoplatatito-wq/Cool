package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.ObservedTarget
import com.example.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TargetRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun addTarget_authenticatedUser_createsDocumentSuccessfully() = runBlocking {
    val uid = signInTestUser("alice_rule@test.com")
    val repository = FirestoreRepository(firestore)

    val target = ObservedTarget(
      username = "mrbeast",
      platform = "YOUTUBE",
      displayName = "MrBeast",
      avatarUrl = "https://example.com/avatar.jpg",
      bio = "Top creator",
      followerCount = 330000000L,
      metricLabel = "Subscribers",
      status = "ACTIVE",
      notes = "Verified target",
      tags = listOf("youtube", "creator")
    )

    val docId = withTimeout(10000L) {
      repository.addTarget(target)
    }

    assertNotNull(docId)
    assertTrue(docId.isNotBlank())

    val targets = withTimeout(10000L) {
      repository.observeTargets().first()
    }

    assertEquals(1, targets.size)
    assertEquals("mrbeast", targets[0].username)
    assertEquals("YOUTUBE", targets[0].platform)
    assertEquals(uid, targets[0].userId)
  }

  @Test
  fun updateAndDeleteTarget_authenticatedUser_modifiesAndRemovesDocument() = runBlocking {
    signInTestUser("bob_rule@test.com")
    val repository = FirestoreRepository(firestore)

    val target = ObservedTarget(
      username = "torvalds",
      platform = "GITHUB",
      displayName = "Linus Torvalds",
      followerCount = 225000L,
      metricLabel = "Followers"
    )

    val docId = withTimeout(10000L) { repository.addTarget(target) }

    val savedTarget = repository.observeTargets().first()[0]
    withTimeout(10000L) {
      repository.updateTarget(savedTarget.copy(notes = "Kernel Master"))
    }

    val updatedTarget = repository.observeTargets().first()[0]
    assertEquals("Kernel Master", updatedTarget.notes)

    withTimeout(10000L) {
      repository.deleteTarget(docId)
    }

    val emptyTargets = repository.observeTargets().first()
    assertEquals(0, emptyTargets.size)
  }
}
