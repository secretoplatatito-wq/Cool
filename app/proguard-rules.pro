# God's Eye Observatory Production ProGuard / R8 Hardening Rules

# 1. Code Optimization and Obfuscation
-repackageclasses 'com.example.obfuscated'
-allowaccessmodification
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod

# 2. Prevent Information Leakage: Strip verbose and debug logs
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# 3. Keep Data Models for Firestore and Moshi
-keep class com.example.data.model.** { *; }
-keepclassmembers class com.example.data.model.** {
    <fields>;
    <init>(...);
}

# 4. Moshi Reflection and CodeGen Keep Rules
-keepattributes *Annotation*
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# 5. Firebase and Google Services Keep Rules
-keepattributes *Annotation*,Signature
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

# 6. OkHttp and Retrofit Rules
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
