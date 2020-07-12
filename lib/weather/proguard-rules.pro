# Kotlinx serialization: https://github.com/Kotlin/kotlinx.serialization#androidjvm
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class se.gustavkarlsson.skylight.android.lib.weather.**$$serializer { *; }
-keepclassmembers class se.gustavkarlsson.skylight.android.lib.weather.** {
    *** Companion;
}
-keepclasseswithmembers class se.gustavkarlsson.skylight.android.lib.weather.** {
    kotlinx.serialization.KSerializer serializer(...);
}
