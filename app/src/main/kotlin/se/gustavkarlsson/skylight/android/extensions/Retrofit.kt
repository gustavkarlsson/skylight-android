package se.gustavkarlsson.skylight.android.extensions

import okhttp3.OkHttpClient
import org.threeten.bp.Duration
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit.MILLISECONDS

// Removes need to supply class when creating service
inline fun <reified T: Any> Retrofit.create(): T = this.create(T::class.java)

fun OkHttpClient.Builder.connectTimeout(timeout: Duration): OkHttpClient.Builder =
	this.connectTimeout(timeout.toMillis(), MILLISECONDS)

fun OkHttpClient.Builder.readTimeout(timeout: Duration): OkHttpClient.Builder =
	this.readTimeout(timeout.toMillis(), MILLISECONDS)

fun OkHttpClient.Builder.writeTimeout(timeout: Duration): OkHttpClient.Builder =
	this.writeTimeout(timeout.toMillis(), MILLISECONDS)
