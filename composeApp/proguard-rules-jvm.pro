# Kotlin 序列化
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class org.example.project.data.json.**$$serializer { *; }
-keepclassmembers class org.example.project.data.json.** {
    *** Companion;
}
-keepclasseswithmembers class org.example.project.data.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.serialization.** { *; }
-keep class io.ktor.util.date.** { *; }