# Kotlinx serialization: https://github.com/Kotlin/kotlinx.serialization#android
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class se.gustavkarlsson.skylight.android.**$$serializer { *; }
-keepclassmembers class se.gustavkarlsson.skylight.android.** {
    *** Companion;
}
-keepclasseswithmembers class se.gustavkarlsson.skylight.android.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Protobuf
-shrinkunusedprotofields

# Retrofit (Try removing after upgrading retrofit past 2.9.0)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keepattributes AnnotationDefault
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
