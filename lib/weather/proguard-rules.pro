-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class se.gustavkarlsson.skylight.android.lib.weather.**$$serializer { *; }
-keepclassmembers class se.gustavkarlsson.skylight.android.lib.weather.** {
    *** Companion;
}
-keepclasseswithmembers class se.gustavkarlsson.skylight.android.lib.weather.** {
    kotlinx.serialization.KSerializer serializer(...);
}
