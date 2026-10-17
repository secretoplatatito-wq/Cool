const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read targets", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("targets").get());
});

test("Alice can create target in her own collection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const targetRef = aliceDb.collection("users").doc(ALICE_UID).collection("targets").doc("target_1");
  await assertSucceeds(
    targetRef.set({
      userId: ALICE_UID,
      username: "mrbeast",
      platform: "YOUTUBE",
      displayName: "MrBeast",
      avatarUrl: "https://example.com/avatar.jpg",
      bio: "Creator of epic videos",
      followerCount: 300000000,
      metricLabel: "Subscribers",
      status: "ACTIVE",
      notes: "Top monitored YouTube channel",
      tags: ["creator", "entertainment"],
      createdAt: new Date(),
      updatedAt: null,
    })
  );
});

test("Bob cannot read or create target in Alice's collection", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const aliceTargetRef = bobDb.collection("users").doc(ALICE_UID).collection("targets").doc("target_1");
  await assertFails(aliceTargetRef.get());
  await assertFails(
    aliceTargetRef.set({
      userId: BOB_UID,
      username: "hacker",
      platform: "YOUTUBE",
      displayName: "Hacker",
      followerCount: 1,
      metricLabel: "Subscribers",
      createdAt: new Date(),
    })
  );
});

test("Alice cannot create target with spoofed userId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const targetRef = aliceDb.collection("users").doc(ALICE_UID).collection("targets").doc("target_spoof");
  await assertFails(
    targetRef.set({
      userId: BOB_UID,
      username: "mrbeast",
      platform: "YOUTUBE",
      displayName: "MrBeast",
      followerCount: 300000000,
      metricLabel: "Subscribers",
      createdAt: new Date(),
    })
  );
});

test("Alice can update her own target mutable fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const targetRef = aliceDb.collection("users").doc(ALICE_UID).collection("targets").doc("target_1");
  const createDate = new Date();
  await targetRef.set({
    userId: ALICE_UID,
    username: "mrbeast",
    platform: "YOUTUBE",
    displayName: "MrBeast",
    followerCount: 300000000,
    metricLabel: "Subscribers",
    createdAt: createDate,
  });

  await assertSucceeds(
    targetRef.update({
      followerCount: 310000000,
      notes: "Updated milestone",
      updatedAt: new Date(),
    })
  );
});

test("Alice can delete her target", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const targetRef = aliceDb.collection("users").doc(ALICE_UID).collection("targets").doc("target_1");
  await targetRef.set({
    userId: ALICE_UID,
    username: "mrbeast",
    platform: "YOUTUBE",
    displayName: "MrBeast",
    followerCount: 300000000,
    metricLabel: "Subscribers",
    createdAt: new Date(),
  });

  await assertSucceeds(targetRef.delete());
});
