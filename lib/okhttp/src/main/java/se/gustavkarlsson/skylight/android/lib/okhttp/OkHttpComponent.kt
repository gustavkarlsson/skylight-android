package se.gustavkarlsson.skylight.android.lib.okhttp

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import okhttp3.OkHttpClient
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
@OkHttpScope
abstract class OkHttpComponent {

    abstract val okHttpClient: OkHttpClient

    @Provides
    @OkHttpScope
    internal fun okHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT)
            .readTimeout(TIMEOUT)
            .writeTimeout(TIMEOUT)
            .build()

    companion object {
        val instance: OkHttpComponent = OkHttpComponent::class.create()
    }
}

private val TIMEOUT = 30.seconds.toJavaDuration()

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class OkHttpScope
