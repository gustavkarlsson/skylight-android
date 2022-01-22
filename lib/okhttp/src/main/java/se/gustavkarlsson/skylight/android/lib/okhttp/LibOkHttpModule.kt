package se.gustavkarlsson.skylight.android.lib.okhttp

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.utils.seconds
import java.util.concurrent.TimeUnit

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

private fun OkHttpClient.Builder.connectTimeout(timeout: Duration): OkHttpClient.Builder =
    this.connectTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

private fun OkHttpClient.Builder.readTimeout(timeout: Duration): OkHttpClient.Builder =
    this.readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

private fun OkHttpClient.Builder.writeTimeout(timeout: Duration): OkHttpClient.Builder =
    this.writeTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

private val TIMEOUT = 30.seconds
