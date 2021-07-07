# Kotlinx serialization: https://github.com/Kotlin/kotlinx.serialization#android
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class se.gustavkarlsson.skylight.android.lib.weather.**$$serializer { *; }
-keepclassmembers class se.gustavkarlsson.skylight.android.lib.weather.** {
    *** Companion;
}
-keepclasseswithmembers class se.gustavkarlsson.skylight.android.lib.weather.** {
    kotlinx.serialization.KSerializer serializer(...);
}
