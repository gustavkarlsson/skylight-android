# Kotlinx serialization: https://github.com/Kotlin/kotlinx.serialization#android
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class se.gustavkarlsson.skylight.android.lib.kpindex.**$$serializer { *; }
-keepclassmembers class se.gustavkarlsson.skylight.android.lib.kpindex.** {
    *** Companion;
}
-keepclasseswithmembers class se.gustavkarlsson.skylight.android.lib.kpindex.** {
    kotlinx.serialization.KSerializer serializer(...);
}
