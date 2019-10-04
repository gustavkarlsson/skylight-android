package se.gustavkarlsson.skylight.android.lib.okhttp

import okhttp3.OkHttpClient
import org.koin.dsl.module.module
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.extensions.seconds
import java.util.concurrent.TimeUnit

val libOkHttpModule = module {

	single {
		val timeout = 30.seconds
		OkHttpClient.Builder()
			.connectTimeout(timeout)
			.readTimeout(timeout)
			.writeTimeout(timeout)
			.build()
	}
}

private fun OkHttpClient.Builder.connectTimeout(timeout: Duration): OkHttpClient.Builder =
	this.connectTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

private fun OkHttpClient.Builder.readTimeout(timeout: Duration): OkHttpClient.Builder =
	this.readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

private fun OkHttpClient.Builder.writeTimeout(timeout: Duration): OkHttpClient.Builder =
	this.writeTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
