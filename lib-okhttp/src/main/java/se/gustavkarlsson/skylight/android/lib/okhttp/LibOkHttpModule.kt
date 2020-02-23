package se.gustavkarlsson.skylight.android.lib.okhttp

import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.extensions.seconds
import java.util.concurrent.TimeUnit

@Module
class LibOkHttpModule {

    @Provides
    @Reusable
    internal fun okHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT)
            .readTimeout(TIMEOUT)
            .writeTimeout(TIMEOUT)
            .build()
}

private fun OkHttpClient.Builder.connectTimeout(timeout: Duration): OkHttpClient.Builder =
    this.connectTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

private fun OkHttpClient.Builder.readTimeout(timeout: Duration): OkHttpClient.Builder =
    this.readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

private fun OkHttpClient.Builder.writeTimeout(timeout: Duration): OkHttpClient.Builder =
    this.writeTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

private val TIMEOUT = 30.seconds
