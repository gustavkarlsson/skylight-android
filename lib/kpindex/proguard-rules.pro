-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class se.gustavkarlsson.skylight.android.lib.kpindex.**$$serializer { *; }
-keepclassmembers class se.gustavkarlsson.skylight.android.lib.kpindex.** {
    *** Companion;
}
-keepclasseswithmembers class se.gustavkarlsson.skylight.android.lib.kpindex.** {
    kotlinx.serialization.KSerializer serializer(...);
}
