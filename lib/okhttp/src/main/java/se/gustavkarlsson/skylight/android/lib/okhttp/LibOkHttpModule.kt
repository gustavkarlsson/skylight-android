package se.gustavkarlsson.skylight.android.lib.okhttp

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Module
@ContributesTo(AppScopeMarker::class)
object LibOkHttpModule {

    @Provides
    @Reusable
    internal fun okHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT)
            .readTimeout(TIMEOUT)
            .writeTimeout(TIMEOUT)
            .build()
}

private val TIMEOUT = 30.seconds.toJavaDuration()
