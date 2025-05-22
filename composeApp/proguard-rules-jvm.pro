# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Kotlin 序列化
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class org.example.**

# Don't warn
-dontwarn androidx.**
-dontwarn io.ktor.**
-dontwarn kotlin.**
-dontwarn kotlinx.**
-dontwarn org.slf4j.helpers.SubstituteLogger
-dontwarn okhttp3.internal.platform.**
