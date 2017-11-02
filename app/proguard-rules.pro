# Retrofit2
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions


# okio
-dontwarn okio.**


# gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer


# gson serialized classes
-keep class se.gustavkarlsson.aurora_notifier.common.domain.** { *; }
-keep class se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.** { *; }


# dualcache
-keepnames class com.fasterxml.jackson.annotation.** { *; }
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-dontwarn java.beans.Transient
-dontwarn java.beans.ConstructorProperties
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}


# dualcache serialized classes
-keep public class se.gustavkarlsson.skylight.android.entities.* {
    public void set*(*);
    public ** get*();
}


# dagger
-dontwarn com.google.errorprone.annotations.*


# rxjava
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontwarn sun.misc.Unsafe


# don't remember what this is for
-dontwarn javax.annotation.**

